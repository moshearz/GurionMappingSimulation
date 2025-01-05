package bgu.spl.mics.application.objects;

import java.util.*;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private final List<LandMark> landmarks = new ArrayList<>(); // List of landmark on the map
    private final List<Pose> poses = new ArrayList<>(); // List of Robot locations for calculations

    private final Object Lock_landmarks = new Object();

    private FusionSlam() {}

    public static FusionSlam getInstance() {
        return FusionSlamHolder.instance;
    }

    public void addPose(Pose pose) {
        poses.add(pose);
    }

    public Boolean poseExist(int tickTime) {
        for (Pose pose : poses) {
            if (pose.getTime() == tickTime) {
                return true;
            }
        }
        return false;
    }

    // Function is called only when the needed pose is saved
    public void updateLandMark(TrackedObject object) {
        for (Pose currentPose : poses) {
            if (currentPose.getTime() == object.getTime()) {
                double sin = Math.sin(Math.toRadians(currentPose.getYaw()));
                double cos = Math.cos(Math.toRadians(currentPose.getYaw()));
                List<CloudPoint> additionalData = new ArrayList<>();
                for (CloudPoint cloudPoint : object.getCoordinates()) {
                    additionalData.add(new CloudPoint(
                            cos * cloudPoint.getX() - sin * cloudPoint.getY() + currentPose.getX()
                            ,sin * cloudPoint.getX() - cos * cloudPoint.getY() + currentPose.getY())
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
                    } else { // When the object exists and needs to update the CloudPoints
                        currentLandMark.updateCoordinates(additionalData);
                    }
                }
            }
        }
    }

    private static class FusionSlamHolder {
        // Singleton instance holder
        private static final FusionSlam instance = new FusionSlam();
    }

}
