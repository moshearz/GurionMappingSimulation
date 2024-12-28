package bgu.spl.mics.application.objects;

import java.util.List;


/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private final int time; // time of detection
    private final List<DetectedObject> detectedObjects; // list of objects detected in the same time

    public StampedDetectedObjects(int time, List<DetectedObject> detectedObjects) {
        this.time = time;
        this.detectedObjects = detectedObjects;
    }
}
