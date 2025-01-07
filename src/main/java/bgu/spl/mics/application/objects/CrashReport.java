package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.Map;

public class CrashReport {
    private static CrashReport instance;
    private String errorMessage;
    private String faultySensor;
    private Map<String, StampedDetectedObjects> lastCameraFrames;
    private Map<String, List<TrackedObject>> lastLidarFrames;
    private List<Pose> poses;
    private StatisticalFolder statistics = StatisticalFolder.getInstance();

    private CrashReport() {}

    public static CrashReport getInstance() {
        if (instance == null) {
            instance = new CrashReport();
        }
        return instance;
    }


    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setFaultySensor(String faultySensor) {
        this.faultySensor = faultySensor;
    }

    public void addLastCameraFrame(String name, StampedDetectedObjects lastFrame) {
        lastCameraFrames.put(name, lastFrame);
    }

    public void addLastLidarFrames(String name, List<TrackedObject> lastFrame) {
        lastLidarFrames.put(name, lastFrame);
    }

    public void setPoses(List<Pose> poses) {
        this.poses = poses;
    }

    public void setStatistics(StatisticalFolder statistics) {
        this.statistics = statistics;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getFaultySensor() {
        return faultySensor;
    }
}
