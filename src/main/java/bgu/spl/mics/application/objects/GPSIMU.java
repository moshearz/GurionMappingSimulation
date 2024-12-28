package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick; // Current Time
    private STATUS status;
    private final List<Pose> poseList; // List of the robot's position with time stamps

    public GPSIMU(int currentTick, STATUS status, List<Pose> poseList) {
        this.currentTick = currentTick;
        this.status = status;
        this.poseList = poseList;
    }
}
