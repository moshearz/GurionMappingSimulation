package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public STATUS getStatus() { return status; }

    public int getId() {
        return id;
    }

    public StampedDetectedObjects getLastDetectedObjects() {
        return lastDetectedObjects;
    }

    public StampedDetectedObjects getStampedDetectedObjects(int tick) {
        try {
            if (detectedObjectsList.get(0).getTime() + frequency <= tick) {
                StampedDetectedObjects temp = detectedObjectsList.remove(0);
                for (DetectedObject detectedObject : temp.getDetectedObjects()) {
                    if (Objects.equals(detectedObject.getId(), "ERROR")) {
                        CrashReport.getInstance().addLastCameraFrame("Camera " + id, lastDetectedObjects);
                        status = STATUS.ERROR;
                        List<DetectedObject> badMessage = new ArrayList<>();
                        badMessage.add(detectedObject);
                        return new StampedDetectedObjects(0, badMessage);
                    }
                }
                lastDetectedObjects = temp;
                return lastDetectedObjects;
            }
        } catch (IndexOutOfBoundsException ignored) {}
        return null;
    }

    public void setStatus(STATUS status) { this.status = status; }

    public boolean isEmpty() {
        return detectedObjectsList.isEmpty();
    }

    public int getFrequency() { return frequency; }

    public StampedDetectedObjects getDetectedObjectsList() { return lastDetectedObjects; }
}
