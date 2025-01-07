package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FusionSlamTest {

    private FusionSlam fusionSlam;

    @BeforeEach
    void setUp() {
        fusionSlam = FusionSlam.getInstance();
    }

    @Test
    void testSingletonInstance() {
        FusionSlam instance1 = FusionSlam.getInstance();
        FusionSlam instance2 = FusionSlam.getInstance();
        assertSame(instance1, instance2, "FusionSlam should implement the Singleton pattern.");
    }

    @Test
    void testSetTotalMSAndUpdateTotal() {
        fusionSlam.setTotalMS(3);
        assertFalse(fusionSlam.updateTotal(), "updateTotal should return false when totalMicroServices is greater than zero.");
        assertFalse(fusionSlam.updateTotal(), "updateTotal should return false when totalMicroServices is greater than zero.");
        assertTrue(fusionSlam.updateTotal(), "updateTotal should return true when totalMicroServices reaches zero.");
    }

    @Test
    void testAddPoseAndGetLandmarks() {
        Pose pose = new Pose(1, 10.0, 15.0, 45.0);
        fusionSlam.addPose(pose);

        List<LandMark> landmarks = fusionSlam.getLandmarks();
        assertNotNull(landmarks, "Landmarks list should not be null.");
        assertTrue(landmarks.isEmpty(), "Landmarks list should initially be empty if no tracked objects are added.");
    }



}
