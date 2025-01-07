package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
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
import java.util.Objects;

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

        });
// detectevent saves to a list in worker while tickbroadcast checks when to send trackedevent

        subscribeEvent(DetectObjectsEvent.class, event -> {
            worker.addDetectedObject(event.getStampedDetectedObjects());
        });

        subscribeEvent(DetectObjectsEvent.class, event -> {
            if (worker.getTick() < event.getStampedDetectedObjects().getTime() + worker.getFrequency()) {
                sendEvent(event);
            } else {
                List<TrackedObject> trackedObjectsList = worker.trackObjects(event.getStampedDetectedObjects());
                if (worker.getStatus() == STATUS.ERROR) {
                    terminate();
                    CrashReport.getInstance().addLastLidarFrames("LiDarTrackerWorker " + worker.getId(), worker.getLastTrackedObjects());
                    sendBroadcast(new CrashedBroadcast("LiDarTrackerWorker " + worker.getId() + " disconnected", "LiDarTrackerWorker " + worker.getId()));
                } else if (worker.getStatus() == STATUS.UP) {
                    StatisticalFolder.getInstance().updateTrackedObjectsTotal(trackedObjectsList.size());
                    sendEvent(new TrackedObjectsEvent(trackedObjectsList));
                } else { // STATUS.DOWN
                    terminate();
                    sendBroadcast(new TerminatedBroadcast(new TypeToken<LiDarService>() {}.getType()));
                }
            }
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
            System.out.println(getName() + " received crash signal.");
            terminate();
            CrashReport.getInstance().addLastLidarFrames("LiDarTrackerWorker " + worker.getId(), worker.getLastTrackedObjects());
        });
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