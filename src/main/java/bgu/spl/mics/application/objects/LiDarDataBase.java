package bgu.spl.mics.application.objects;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private final List<StampedCloudPoints> cloudPointsList;
    // Singleton instance. declared static because this ensures that there is only one instance of the LiDarDataBase class across the entire application
    // By setting it to null, we indicate that the LiDarDataBase instance has not yet been created. This allows us to lazily initialize it in the getInstance method
    private static LiDarDataBase instance = null;

    private LiDarDataBase() { this.cloudPointsList = new ArrayList<>(); }
    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static synchronized LiDarDataBase getInstance(String filePath) {
        if (instance == null) {
            instance = new LiDarDataBase();
            System.out.println("(LiDarDataBase) initialized with: " + filePath);
        }
        return instance;
    }

    //Adds new cloud points to the database
    public synchronized void addCloudPoints(StampedCloudPoints point) { cloudPointsList.add(point); }

    // Get cloud points by ID and time
    public synchronized List<List<Double>> getCloudPoints(String id, int time) {
        synchronized (cloudPointsList) { // Synchronize for safe iteration
            for (StampedCloudPoints point : cloudPointsList) {
                if (point.getId().equals(id) && point.getTime() == time) {
                    return point.getCloudPoints();
                }
            }
        }
        return null; // if not found
    }
    //Removes cloud points for a specific object ID and time-stamp. If the cloud points were REMOVED we will return True, else we will return False.
    public synchronized boolean removeCloudPoints(String id, int time) { return cloudPointsList.removeIf(point -> point.getId().equals(id) && point.getTime() == time); }

    //Updates cloud points for a specific object ID and time-stamp. If the cloud points were UPDATED we will return True, else we will return False.
    public synchronized boolean updateCloudPoints(String id, int time, List<List<Double>> cloudPointsNEW) {
        for (StampedCloudPoints point : cloudPointsList) {
            if (point.getId().equals(id) && point.getTime() == time) {
                point.setCloudPoints(cloudPointsNEW);
                return true;
            }
        }
        return false;
    }

    public synchronized List<StampedCloudPoints> getAllCloudPoints() { return new ArrayList<>(cloudPointsList); }
}



