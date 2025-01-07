package bgu.spl.mics.application.objects;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private final List<LandMark> landmarks = new ArrayList<>(); // List of landmark on the map
    private final List<Pose> poses = new ArrayList<>();
    private final List<TrackedObject> waitingList = new ArrayList<>();

    private int totalMicroServices;
    private final Object Lock_landmarks = new Object();
    private final Object Lock_Poses_WaitingList = new Object();

    private FusionSlam() {}

    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }

    public void setTotalMS(int totalMicroServices) {
        this.totalMicroServices = totalMicroServices;
    }

    public Boolean updateTotal() {
        return --totalMicroServices == 0;
    }

    public List<LandMark> getLandmarks() {
        return landmarks;
    }

    public void addPose(Pose pose) {
        synchronized (Lock_Poses_WaitingList) {
            List<TrackedObject> waitingListCopy = new ArrayList<>(waitingList);
            for (TrackedObject trackedObject : waitingListCopy) {
                if (trackedObject.getTime() == pose.getTime()) {
                    if (waitingList.remove(trackedObject)) {
                        updateLandMark(trackedObject, pose);
                    }
                }
            }
            poses.add(pose);
        }
    }

    public void addTrackedObject(TrackedObject trackedObject) {
        synchronized (Lock_Poses_WaitingList) {
            for (Pose pose : poses) {
                if (pose.getTime() == trackedObject.getTime()) {
                    updateLandMark(trackedObject, pose);
                    return;
                }
            }
            waitingList.add(trackedObject);
        }
    }

    // Function is called only when both trackedObject and Pose are present
    public void updateLandMark(TrackedObject object, Pose pose) {
        double sin = Math.sin(Math.toRadians(pose.getYaw()));
        double cos = Math.cos(Math.toRadians(pose.getYaw()));
        List<CloudPoint> additionalData = new ArrayList<>();
        for (CloudPoint cloudPoint : object.getCoordinates()) {
            additionalData.add(new CloudPoint(
                    cos * cloudPoint.getX() - sin * cloudPoint.getY() + pose.getX()
                    ,sin * cloudPoint.getX() - cos * cloudPoint.getY() + pose.getY())
            );
        }
        LandMark currentLandMark = null;
        synchronized (Lock_landmarks) {
            for (LandMark selected : landmarks) {
                if (Objects.equals(selected.getId(), object.getId())) {
                    currentLandMark = selected;
                    break;
                }
            }
            if (currentLandMark == null) { // When object with specific ID doesn't exist
                landmarks.add( new LandMark(object.getId(), object.getDescription(), additionalData));
                StatisticalFolder.getInstance().updateLandmarksTotal();
            } else { // When the object exists and needs to update the CloudPoints
                currentLandMark.updateCoordinates(additionalData);
            }
        }
    }

    private static class FusionSlamHolder {
        // Singleton instance holder
        private static final FusionSlam instance = new FusionSlam();
    }

}
