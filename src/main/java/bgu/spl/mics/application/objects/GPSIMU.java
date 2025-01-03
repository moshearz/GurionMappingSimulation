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

    public Pose getCurrentPose(int tick) {
        for (Pose pose : poseList) {
            if (pose.getTime() == tick) {
                return pose;
            }
        }
        return null; // No pose found for the given tick
    }

    public int getCurrentTick() { return currentTick; }

    public STATUS getStatus() { return status; }

    public void setStatus(STATUS status) { this.status = status;}

    public List<Pose> getPoseList() { return poseList; }


}
