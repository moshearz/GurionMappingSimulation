package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private final String id;
    private final String description;
    private CloudPoint coordinates;

    public LandMark(String id, String description, double globalX, double globalY) {
        this.id = id;
        this.description = description;
        coordinates = new CloudPoint(globalX, globalY);
    }

    public final String getId() {
        return id;
    }

    public final CloudPoint getCoordinates() {
        return coordinates;
    }

    public void updateCoordinates(double x, double y) {
        coordinates.setX((coordinates.getX() + x)/2);
        coordinates.setY((coordinates.getY() + y)/2);
    }
}
