package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.LiDarService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private final int id;
    private final int frequency;
    private STATUS status = STATUS.UP;
    private final List<StampedDetectedObjects> waitingList = new ArrayList<>();
    private List<TrackedObject> lastTrackedObjects = new ArrayList<>();
    private int tick = 0;

    public LiDarWorkerTracker(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
    }

    public final int getId() {
        return id;
    }

    public final int getFrequency() {
        return frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    public int getTick() {
        return tick;
    }

    public List<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }

    public void updateTick() {
        tick++;
    }

    public void addDetectedObject(StampedDetectedObjects stampedDetectedObjects) {
        waitingList.add(stampedDetectedObjects);
    }

    public List<TrackedObject> trackObjects(StampedDetectedObjects stampedDetectedObjects) {
        List<TrackedObject> trackedObjectList = new ArrayList<>();
        for (DetectedObject detectedObject : stampedDetectedObjects.getDetectedObjects()) {
            StampedCloudPoints matchingCloudPoints = LiDarDataBase.getInstance().getStampedCloudPoints(detectedObject, stampedDetectedObjects.getTime());
            if (Objects.equals(matchingCloudPoints.getId(), "ERROR")) {
                status = STATUS.ERROR;
                break;
            } else {
                trackedObjectList.add(new TrackedObject(detectedObject, matchingCloudPoints));
            }
        }
        if (status == STATUS.UP) {
            lastTrackedObjects = trackedObjectList;
            return trackedObjectList;
        }
        if (LiDarDataBase.getInstance().isDone()) {
            status = STATUS.DOWN;
        }
        return null;
    }
}
