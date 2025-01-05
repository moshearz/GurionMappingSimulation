package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.TrackedObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private final FusionSlam instance;
    private final Map<Integer ,Future<PoseEvent>> timedPoses = new ConcurrentHashMap<>();
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("Fusion-SLAM");
        instance = fusionSlam;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeEvent(TrackedObjectsEvent.class, objectsEvent -> {
            // Sets up a Future Pose matching the tick time of the TrackedObjects
            Future<PoseEvent> poseFuture = new Future<>();
            timedPoses.put(objectsEvent.getTrackedTickTime(), poseFuture);
            poseFuture.get();

            for (TrackedObject object : objectsEvent.getTrackedObjects()) {
                instance.updateLandMark(object);
            }
        });

        subscribeEvent(PoseEvent.class, poseEvent -> {
            instance.addPose(poseEvent.getCurrPose());
            complete(poseEvent, poseEvent.getCurrPose());
            timedPoses.get(poseEvent.getCurrPose().getTime()).resolve(poseEvent);
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminatedEvent -> {
            System.out.println(getName() + " received termination signal. Terminating.");
            terminate();
        });

        subscribeBroadcast(CrashedBroadcast.class, crashedEvent -> {
            System.out.println(getName() + " crashed");
            terminate();
        });
    }
}
