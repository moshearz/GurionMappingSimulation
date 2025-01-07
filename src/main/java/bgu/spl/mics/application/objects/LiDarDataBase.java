package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.Objects;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    // Singleton instance. declared static because this ensures that there is only one instance of the LiDarDataBase class across the entire application
    // By setting it to null, we indicate that the LiDarDataBase instance has not yet been created. This allows us to lazily initialize it in the getInstance method
    private static LiDarDataBase instance;
    private List<StampedCloudPoints> cloudPointsList;
    private int totalLeft;

    private LiDarDataBase() {}
    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance() {
        if (instance == null) {
            instance = new LiDarDataBase();
        }
        return instance;
    }

    public void setDataBase(List<StampedCloudPoints> cloudPointsList) {
        this.cloudPointsList = cloudPointsList;
        totalLeft = cloudPointsList.size();
    }

    // Get cloud points by ID and time
    public StampedCloudPoints getStampedCloudPoints(DetectedObject object, int tick) {
        for (StampedCloudPoints cloudPoints : cloudPointsList) {
            if (Objects.equals(cloudPoints.getId(), object.getId()) & cloudPoints.getTime() == tick) {
                totalLeft--;
                return cloudPoints;
            }
        }
        return null; // will never reach here
    }

    public Boolean isDone() {
        return totalLeft == 0;
    }
}



