package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.objects.CrashReport;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.STATUS;
import com.google.gson.reflect.TypeToken;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private final GPSIMU gpsimu;

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick ->{
            Pose currPose = gpsimu.getCurrentPose(gpsimu.updateCurrentTick());
            if (currPose != null) {
                sendEvent(new PoseEvent(currPose));
            } else {
                gpsimu.setStatus(STATUS.DOWN);
                terminate();
                sendBroadcast(new TerminatedBroadcast(new TypeToken<PoseService>() {}.getType()));
            }
        });

        subscribeBroadcast(TerminatedBroadcast.class, termination -> {
            if (termination.getMicroServiceType() == new TypeToken<TimeService>() {}.getType()) {
                terminate();
                sendBroadcast(new TerminatedBroadcast(new TypeToken<PoseService>() {}.getType()));
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, crash -> {
            terminate();
            CrashReport.getInstance().setPoses(gpsimu.getPoseList().subList(0, gpsimu.getCurrentTick()));
        });
    }
}
