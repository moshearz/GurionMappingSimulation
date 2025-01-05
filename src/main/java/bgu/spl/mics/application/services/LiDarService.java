package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;



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
            worker.updateTick();
        });

        subscribeEvent(DetectObjectsEvent.class, event -> {
            if (worker.getStatus() == STATUS.UP) {
                if (worker.getTick() < event.getDetectedObjects().getTime() + worker.getFrequency()) {
                    sendEvent(event);
                } else {
                    List<TrackedObject> trackedObjectsList = new ArrayList<>();
                    for (DetectedObject detectedObject : event.getDetectedObjects().getDetectedObjects()) {
                        StampedCloudPoints matchingCloudPoints = LiDarDataBase.getInstance().getStampedCloudPoints(detectedObject, worker.getTick());
                        if (Objects.equals(matchingCloudPoints.getId(), "ERROR")) {
                            sendBroadcast(new CrashedBroadcast(matchingCloudPoints.getId()));
                            worker.setStatus(STATUS.ERROR);
                            terminate();
                        } else {
                            TrackedObject trackedObject = new TrackedObject(detectedObject, matchingCloudPoints);
                            trackedObjectsList.add(trackedObject);
                        }
                    }
                    worker.setLastTrackedObjects(trackedObjectsList);
                    sendEvent(new TrackedObjectsEvent(trackedObjectsList));
                }
            } else {
                terminate();
            }
        });






        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {

        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            terminate();
            //??
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