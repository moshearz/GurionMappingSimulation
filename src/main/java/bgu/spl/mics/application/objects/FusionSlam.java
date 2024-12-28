package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    // Singleton instance holder
    private final List landmarks; // List of landmark on the map
    private final List poses; // List of Robot locations for calculations

    public FusionSlam(List landmarks, List poses) {
        this.landmarks = landmarks;
        this.poses = poses;
    }

    private static class FusionSlamHolder {
        // TODO: Implement singleton instance logic.
    }
}
