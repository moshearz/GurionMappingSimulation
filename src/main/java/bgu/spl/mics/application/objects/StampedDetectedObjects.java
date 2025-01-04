package bgu.spl.mics.application.objects;

import java.util.List;


/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private final String id; // ID of the detection (unique identifier for the detection group)
    private final int time; // time of detection
    private final List<DetectedObject> detectedObjects; // list of objects detected in the same time

    public StampedDetectedObjects(String id, int time, List<DetectedObject> detectedObjects) {
        this.id = id;
        this.time = time;
        this.detectedObjects = detectedObjects;
    }

    public String getId() { return id; } // of the detection group

    public int getTime() { return time; } //of the detection

    public List<DetectedObject> getDetectedObjectsStamped() { return detectedObjects; }
}
