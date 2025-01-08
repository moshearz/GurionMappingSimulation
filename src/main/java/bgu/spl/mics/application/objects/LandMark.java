package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private final String id;
    private final String description;
    private final List<CloudPoint> coordinates;

    public LandMark(String id, String description, List<CloudPoint> coordinates) {
        this.id = id;
        this.description = description;
        this.coordinates = coordinates;
    }

    public final String getId() {
        return id;
    }

    public final List<CloudPoint> getCoordinates() {
        return coordinates;
    }

    public final String getDescription() {
        return description;
    }

    /**
     * @param newList updates each CloudPoint of the list by averaging the two CloudPoints in each spot.
     *                Adds the remaining CloudPoints to the list if it contains more than the original.
     */
    public void updateCoordinates(List<CloudPoint> newList) {
        int minSize = Integer.min(newList.size(), coordinates.size());
        for (int i = 0; i < minSize; i++) {
            coordinates.get(i).update(newList.get(i));
        }
        if (newList.size() > minSize) {
            for (int i = minSize; i < newList.size(); i++) {
                System.out.println(Thread.currentThread().getName() + " has added the landmark " + newList.get(i));
                coordinates.add(newList.get(i));
            }
        }
    }
}
