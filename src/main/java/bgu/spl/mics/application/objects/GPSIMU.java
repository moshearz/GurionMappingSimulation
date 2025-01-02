package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick = 0; // Current Time
    private STATUS status = STATUS.UP;
    private final List<Pose> poseList; // List of the robot's position with time stamps

    public GPSIMU(List<Pose> poseList) {
        this.poseList = poseList;
    }

    public final Pose getNextPose() {
        try {
            return poseList.get(currentTick++);
        } catch (IndexOutOfBoundsException e) {
            status = STATUS.DOWN;
        }
        return null;
    }

    public final STATUS getStatus() {
        return status;
    }
}
