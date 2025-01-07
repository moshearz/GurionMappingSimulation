package bgu.spl.mics.application.objects;

import java.util.List;

public class CrashReport {
    private String errorMessage;
    private String faultySensor;
    private Object lastCameraFrames;
    private Object lastLidarFrames;
    private List<Pose> poses;
    private StatisticalFolder statistics;

    public CrashReport(String errorMessage, String faultySensor, Object lastCameraFrames, Object lastLidarFrames, List<Pose> poses, StatisticalFolder statistics) {
        this.errorMessage = errorMessage;
        this.faultySensor = faultySensor;
        this.lastCameraFrames = lastCameraFrames;
        this.lastLidarFrames = lastLidarFrames;
        this.poses = poses;
        this.statistics = statistics;
    }
}
