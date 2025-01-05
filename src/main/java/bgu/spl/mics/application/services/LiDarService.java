package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.TrackedObject;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;



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
    private final int id; // Unique LiDAR sensor ID
    private final int frequency; // Frequency of operation
    private final LiDarDataBase lidarDB; // Reference to the LiDAR database
    private int currentTick; // Tracks the current tick
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(int id ,int frequency, LiDarDataBase lidarDB) {
        super("LiDarService" + id);
        this.id = id;
        this.frequency = frequency;
        this.lidarDB = lidarDB;
        this.currentTick = 0;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        // Subscribe to TickBroadcast(tracks the global tick count)
        // TimeServiece sends TickBroadcast msg at every tick ( the callback updates the currenttick so it keep track the simulation time)
        subscribeBroadcast(TickBroadcast.class, tick -> {
            currentTick = tick.getTick();
        });
        // Subscribe to DetectObjectsEvent ( which sent by CameraService)
        subscribeEvent(DetectObjectsEvent.class, event -> {
            // Ensures that enough time has passed since the detection ( the timestamp from "StampedDetectedObjects" object ) to process the data
            if(currentTick >= event.getDetectedObjects().getTime() + frequency) {
                List<TrackedObject> trackedObjectsList = new ArrayList<>();
                for (DetectedObject detectedObject : event.getDetectedObjects().getDetectedObjectsStamped()){
                    List<List<Double>> cloudPointsBeforeConvert = lidarDB.getCloudPoints(detectedObject.getId(), event.getDetectedObjects().getTime());
                    List<CloudPoint> cloudPoints = convertToCloudPoints(cloudPointsBeforeConvert);
                    if (cloudPoints != null) {
                        TrackedObject trackedObject = new TrackedObject(detectedObject.getId(), event.getDetectedObjects().getTime(), detectedObject.getDescription(), cloudPoints);
                        trackedObjectsList.add(trackedObject);
                    }
                    // Send TrackedObjectsEvent to Fusion-SLAM
                    if (!trackedObjectsList.isEmpty()) {
                        sendEvent(new TrackedObjectsEvent(trackedObjectsList));
                    }
                }
            }
        });
        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, terminated -> terminate());

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