package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

/**
 * (Represents an event) sent by: LiDar workers
 * Handled by: Fusion-SLAM
 * includes a list of TrackedObjects to be processed by Fusion-SLAM.
 */
public class TrackedObjectsEvent implements Event<Boolean> {
    private final List<TrackedObject> trackedObjects;

    public TrackedObjectsEvent(List<TrackedObject> trackedObjects) {this.trackedObjects = trackedObjects;}

    public List<TrackedObject> getTrackedObjects() {return trackedObjects;}

    public int getTrackedTickTime() {
        return trackedObjects.get(0).getTime();
    }
}
