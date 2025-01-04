package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

/**
 * An event representing the detection of objects by a camera at a specific time.
 * sent by: CameraService
 * handled by: LiDar workers.
 */
public class DetectObjectsEvent  implements Event<DetectedObject> {
    private final StampedDetectedObjects detectedObjects;

    public DetectObjectsEvent(StampedDetectedObjects detectedObjects) {this.detectedObjects = detectedObjects;}

    public StampedDetectedObjects getDetectedObjects() {return detectedObjects;}
}
