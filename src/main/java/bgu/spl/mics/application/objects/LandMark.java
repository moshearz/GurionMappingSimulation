package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private final String id;
    private final String description;
    private List<CloudPoint> coordinates;
    private CloudPoint trueCoordinates = new CloudPoint(0, 0);

    public LandMark(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public final String getId() {
        return id;
    }

    public final List<CloudPoint> getCoordinatesList() {
        return coordinates;
    }

    public void addCoordinates(CloudPoint newCoordinates) {
        coordinates.add(newCoordinates);
        trueCoordinates = new CloudPoint(trueCoordinates.x + newCoordinates.x, trueCoordinates.y + newCoordinates.y);
    }
}
