package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

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
    private List<LandMark> landMarks;

    private StatisticalFolder() {}

    public static synchronized StatisticalFolder getInstance() {
        if (instance == null) {
            instance = new StatisticalFolder();
        }
        return instance;
    }

    public void updateSystemRuntimeTotal() {
        systemRuntime++;
    }

    public void updateDetectedObjectsTotal(int num) {
        numDetectedObjects += num;
    }

    public void updateTrackedObjectsTotal(int num) {
        numTrackedObjects += num;
    }

    public void updateLandmarksTotal() {
        numLandmarks++;
    }

    public void setLandMarks(List<LandMark> landMarks) {
        this.landMarks = landMarks;
    }

    // Used for testing
    public String toString() {
        return "systemRunTime: " + systemRuntime +
                "\n numDetectedObjects: " + numDetectedObjects +
                "\n numTrackedObjects: " + numTrackedObjects +
                "\n numLandmarks: " + numLandmarks + "\n";
    }
}
