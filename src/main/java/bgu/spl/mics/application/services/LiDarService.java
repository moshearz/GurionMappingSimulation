package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 *
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private final LiDarWorkerTracker worker;

    /**
     * Constructor for LiDarService.
     *
     * @param worker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker worker) {
        super("LiDarWorker" + worker.getId());
        this.worker = worker;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick -> {
            List<TrackedObject> trackedObjectList = worker.readyTrackedList(tick.getTick());
            while (trackedObjectList != null) {
                StatisticalFolder.getInstance().updateTrackedObjectsTotal(trackedObjectList.size());
                sendEvent(new TrackedObjectsEvent(trackedObjectList));
                trackedObjectList = worker.readyTrackedList(tick.getTick());
            }
            if (worker.getStatus() == STATUS.ERROR) {
                terminate();
                sendBroadcast(new CrashedBroadcast("LiDarTrackerWorker " + worker.getId() + " disconnected", "LiDarTrackerWorker " + worker.getId()));
            } else if (worker.getStatus() == STATUS.DOWN) {
                terminate();
                sendBroadcast(new TerminatedBroadcast(new TypeToken<LiDarService>() {}.getType()));
            }
        });

        subscribeEvent(DetectObjectsEvent.class, event -> {
            worker.addDetectedObject(event.getStampedDetectedObjects());
        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, termination -> {
            if (termination.getMicroServiceType() == new TypeToken<TimeService>() {}.getType()) {
                terminate();
                sendBroadcast(new TerminatedBroadcast(new TypeToken<LiDarService>() {}.getType()));
            }
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            terminate();
            CrashReport instance = CrashReport.getInstance();
            instance.addLastLidarFrames("LiDarTrackerWorker " + worker.getId(), worker.getLastTrackedObjects());
            sendBroadcast(new CrashedBroadcast(instance.getErrorMessage(), instance.getFaultySensor()));
        });

        //System.out.println(getName() + " initialized.");
    }

    // Converts raw cloud point data (List<List<Double>>) into List<CloudPoint>.
    private List<CloudPoint> convertToCloudPoints(List<List<Double>> rawPoints) {
        if (rawPoints == null) {
            return null;
        }

        List<CloudPoint> cloudPoints = new ArrayList<>();
        for (List<Double> rawPoint : rawPoints) {
            if (rawPoint.size() >= 2) { // Ensure valid x and y
                cloudPoints.add(new CloudPoint(rawPoint.get(0), rawPoint.get(1)));
            }
        }
        return cloudPoints;
    }
}