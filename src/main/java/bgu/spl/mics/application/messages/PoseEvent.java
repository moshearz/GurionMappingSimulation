package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Pose;

/**
 * (Represents an event) sent by: PoseService
 * Handled by: Fusion-SLAM.
 * Contains the robot's current pose info.
 * Used by Fusion-SLAM for calculations based on received TrackedObjectEvents
 */
public class PoseEvent implements Event<Pose> {

    private final Pose currPose;

    public PoseEvent(final Pose currPose) {this.currPose = currPose;}

    public Pose getCurrPose() {return currPose;}
}
