package bgu.spl.mics.application.objects;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private int systemRuntime = 0;
    private int numDetectedObjects = 0;
    private int numTrackedObjects = 0;
    private int numLandmarks = 0;

    public void finalUpTime(int tickCount) {
        systemRuntime = tickCount;
    }

    public void updateDetectedObjectsTotal() {
        numDetectedObjects++;
    }

    public void updateTrackedObjectsTotal() {
        numTrackedObjects++;
    }

    public void updateLandmakrsTotal() {
        numLandmarks++;
    }

    public final String result() {
        return "systemRunTime: " + systemRuntime +
                "\n numDetectedObjects: " + numDetectedObjects +
                "\n numTrackedObjects: " + numTrackedObjects +
                "\n numLandmarks: " + numLandmarks;
    }
}
