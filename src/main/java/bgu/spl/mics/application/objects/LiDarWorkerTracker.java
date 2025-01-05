package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private final int id;
    private final int frequency;
    private STATUS status = STATUS.UP;
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

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public void updateTick() {
        tick++;
    }

    public void setLastTrackedObjects(List<TrackedObject> lastTrackedObjects) {
        this.lastTrackedObjects = lastTrackedObjects;
    }
}
