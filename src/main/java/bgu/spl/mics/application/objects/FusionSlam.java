package bgu.spl.mics.application.objects;

import java.util.*;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private List<LandMark> landmarks = new ArrayList<>(); // List of landmark on the map
    private List<Pose> poses = new ArrayList<>(); // List of Robot locations for calculations
    private FusionSlamHolder instanceHolder;

    private final Object Lock_landmarks = new Object();

    public FusionSlam getInstance() {
        if (instanceHolder == null) {
            instanceHolder = new FusionSlamHolder();
        }
        return instanceHolder.instance;
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
                LandMark currentLandMark = null;
                synchronized (Lock_landmarks) {
                    for (LandMark selected : landmarks) {
                        if (Objects.equals(selected.getId(), object.getId())) {
                            currentLandMark = selected;
                            break;
                        }
                    }
                    double newGlobalX = cos * object.getLocalX() - sin * object.getLocalY() + currentPose.getX();
                    double newGlobalY = sin * object.getLocalX() - cos * object.getLocalY() + currentPose.getY();
                    if (currentLandMark != null) {
                        currentLandMark.updateCoordinates(newGlobalX, newGlobalY);
                    } else {
                        landmarks.add(new LandMark(object.getId(), object.getDescription(), newGlobalX, newGlobalY));
                    }
                }
            }
        }
    }

    private static class FusionSlamHolder {
        // Singleton instance holder
        public FusionSlam instance = new FusionSlam();
    }

}
