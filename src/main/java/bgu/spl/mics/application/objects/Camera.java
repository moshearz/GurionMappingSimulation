package bgu.spl.mics.application.objects;

import java.util.ArrayList;
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

    public Camera(int id, int frequency, List<StampedDetectedObjects> detectedObjectsList) {
        this.id = id;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.detectedObjectsList = (detectedObjectsList != null) ? detectedObjectsList : new ArrayList<>();
    }

    public int getId() {return id;}

    public int getFrequency() {return frequency;}

    public STATUS getStatus() { return status; }

    public void setStatus(STATUS status) { this.status = status; }

    public List<StampedDetectedObjects> getDetectedObjectsList() { return new ArrayList<>(detectedObjectsList); } // A copy of the list of detected objects. "Snapshot" as we learned in class.

    public void addDetectedObject(StampedDetectedObjects detectedObject) {
        this.detectedObjectsList.add(detectedObject);
        System.out.println("Camera " + id + " added detected object: " + detectedObject); // for debugging
    }

    public StampedDetectedObjects getNextDetectedObjects() {
        if (!detectedObjectsList.isEmpty()) {
            return detectedObjectsList.remove(0); // Remove and return the first object
        }
        return null; // Return null if the list is empty
    }

    public boolean hasDetectedObjects() { return !detectedObjectsList.isEmpty(); }

    public void clearDetectedObjects() { detectedObjectsList.clear(); }

}
