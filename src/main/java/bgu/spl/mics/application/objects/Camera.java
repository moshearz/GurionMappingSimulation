package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {

    private final int id;
    private final int frequency;
    private STATUS status;
    private final List<StampedDetectedObjects> detectedObjectsList; // List of objects with time stamp that identified by the Camera
    private StampedDetectedObjects lastDetectedObjects;

    public Camera(int id, int frequency, List<StampedDetectedObjects> detectedObjectsList) {
        this.id = id;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.detectedObjectsList = detectedObjectsList;
    }

    public int getId() {return id;}

    public int getFrequency() {return frequency;}

    public STATUS getStatus() { return status; }

    public StampedDetectedObjects getLastDetectedObjects() {
        return lastDetectedObjects;
    }

    public List<StampedDetectedObjects> getDetectedObjectsList() { return detectedObjectsList; }

    public StampedDetectedObjects getStampedDetectedObjects(int tick) {
        try {
            if (detectedObjectsList.get(0).getTime() + frequency <= tick) {
                lastDetectedObjects = detectedObjectsList.remove(0);
                return lastDetectedObjects;
            }
        } catch (IndexOutOfBoundsException ignored) {}
        return null;
    }

    public void setStatus(STATUS status) { this.status = status; }

    public Boolean isEmpty() {
        return detectedObjectsList.isEmpty();
    }
}
