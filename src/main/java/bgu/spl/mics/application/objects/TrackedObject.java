package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private final String id;
    private final int time;
    private final String description;
    private final List<CloudPoint> coordinates; // List of coordinates (cloud points)


    public TrackedObject(DetectedObject object, StampedCloudPoints cloudPoints) {
        this.id = object.getId();
        this.time = cloudPoints.getTime();
        this.description = object.getDescription();
        this.coordinates = cloudPoints.getCloudPoints();
    }

    public String getId() { return id; }

    public int getTime() { return time; }

    public String getDescription() { return description; }

    public List<CloudPoint> getCoordinates() { return coordinates;}
}
