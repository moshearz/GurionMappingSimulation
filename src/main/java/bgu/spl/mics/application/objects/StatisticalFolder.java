package bgu.spl.mics.application.objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private static StatisticalFolder instance;
    private int systemRuntime = 0;
    private int numDetectedObjects = 0;
    private int numTrackedObjects = 0;
    private int numLandmarks = 0;
    private Map<String, LandMark> landMarks = new HashMap<>();

    private StatisticalFolder() {}

    public static synchronized StatisticalFolder getInstance() {
        if (instance == null) {
            instance = new StatisticalFolder();
        }
        return instance;
    }

    public synchronized void updateSystemRuntimeTotal() {
        systemRuntime++;
        //System.out.println("total run time: " + systemRuntime);
    }

    public synchronized void updateDetectedObjectsTotal(int num) {
        numDetectedObjects += num;
        //System.out.println("total detected objects: " + numDetectedObjects);
    }

    public synchronized void updateTrackedObjectsTotal(int num) {
        numTrackedObjects += num;
        //System.out.println("total tracked objects: " + numTrackedObjects);
    }

    public synchronized void updateLandmarksTotal() {
        numLandmarks++;
        //System.out.println("total landmarks: " + numLandmarks);
    }

    public void setLandMarks(List<LandMark> landMarks) {
        for (LandMark landMark : landMarks) {
            this.landMarks.put(landMark.getId(), landMark);
        }
    }

    // Used for testing
    public String toString() {
        return "systemRunTime: " + systemRuntime +
                "\n numDetectedObjects: " + numDetectedObjects +
                "\n numTrackedObjects: " + numTrackedObjects +
                "\n numLandmarks: " + numLandmarks + "\n";
    }
}
