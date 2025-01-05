package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private final String id;
    private final int time;
    private List<List<Double>> cloudPoints;

    public StampedCloudPoints(String id, int time, List<List<Double>> cloudPoints) {
        this.id = id;
        this.time = time;
        this.cloudPoints = cloudPoints;
    }

    public String getId() { return id; }

    public int getTime() { return time; }

    public List<CloudPoint> getCloudPoints() {
        List<CloudPoint> transformed = new ArrayList<>();
        for (List<Double> point : cloudPoints) {
            transformed.add(new CloudPoint(point.get(0), point.get(1)));
        }
        return transformed;
    }
    // mY

    public void setCloudPoints(List<List<Double>> cloudPoints) { this.cloudPoints = cloudPoints; }
}
