package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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
    void testUpdateLandMark(){
        Pose pose = new Pose(1, 3, 4, 90.0);
        List<Double> cP = new ArrayList<>();
        cP.add(0.0);
        cP.add(0.0);
        List<List<Double>> cPS = new ArrayList<List<Double>>();
        cPS.add(cP);
        StampedCloudPoints stmp = new StampedCloudPoints("1", 1, cPS);
        DetectedObject DO = new DetectedObject("1", "aaaaa");
        TrackedObject tO = new TrackedObject(DO, stmp);
        fusionSlam.updateLandMark(tO, pose);
        assertEquals(new CloudPoint(3, 4).getX() , fusionSlam.getLandmarks().get(0).getCoordinates().get(0).getX());
        assertEquals(new CloudPoint(3, 4).getY() , fusionSlam.getLandmarks().get(0).getCoordinates().get(0).getY());
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
