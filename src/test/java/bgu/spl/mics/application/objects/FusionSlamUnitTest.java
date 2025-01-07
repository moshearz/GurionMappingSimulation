package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FusionSlamTest {

    private FusionSlam fusionSlam;

    @BeforeEach
    void setUp() {
        fusionSlam = FusionSlam.getInstance();
        fusionSlam.setTotalMS(5); // Set an arbitrary total microservices count
    }

    @Test
    void getInstance() {
        FusionSlam instance1 = FusionSlam.getInstance();
        FusionSlam instance2 = FusionSlam.getInstance();
        assertNotNull(instance1);
        assertSame(instance1, instance2, "FusionSlam should follow the Singleton pattern.");
    }

    @Test
    void setTotalMS() {
        fusionSlam.setTotalMS(10);
        assertFalse(fusionSlam.updateTotal(), "Total should not reach zero after one update.");
        for (int i = 0; i < 9; i++) {
            fusionSlam.updateTotal();
        }
        assertTrue(fusionSlam.updateTotal(), "Total should reach zero after 10 updates.");
    }

    @Test
    void getLandmarks() {
        List<LandMark> landmarks = fusionSlam.getLandmarks();
        assertNotNull(landmarks, "Landmarks list should not be null.");
        assertTrue(landmarks.isEmpty(), "Landmarks list should initially be empty.");
    }

    @Test
    void addPose() {
        Pose pose = new Pose(1, 10.0, 15.0, 45.0);
        fusionSlam.addPose(pose);

        // Create a DetectedObject and StampedCloudPoints
        DetectedObject detectedObject = new DetectedObject("1", "Test Landmark");
        List<CloudPoint> cloudPoints = new ArrayList<>();
        cloudPoints.add(new CloudPoint(1.0, 2.0));
        StampedCloudPoints stampedCloudPoints = new StampedCloudPoints(1, cloudPoints);

        // Create a TrackedObject
        TrackedObject object = new TrackedObject(detectedObject, stampedCloudPoints);
        fusionSlam.addTrackedObject(object);

        List<LandMark> landmarks = fusionSlam.getLandmarks();
        assertEquals(1, landmarks.size(), "A new landmark should be created after adding a matching TrackedObject and Pose.");
        assertEquals("1", landmarks.get(0).getId(), "The landmark ID should match the TrackedObject ID.");
    }

    @Test
    void poseExist() {
        Pose pose = new Pose(2, 20.0, 25.0, 90.0);
        fusionSlam.addPose(pose);

        // Create a DetectedObject and StampedCloudPoints
        DetectedObject detectedObject = new DetectedObject("2", "Test Object");
        List<CloudPoint> cloudPoints = new ArrayList<>();
        cloudPoints.add(new CloudPoint(1.0, 1.0));
        StampedCloudPoints stampedCloudPoints = new StampedCloudPoints(2, cloudPoints);

        // Create a TrackedObject
        TrackedObject object = new TrackedObject(detectedObject, stampedCloudPoints);
        fusionSlam.addTrackedObject(object);

        assertEquals(1, fusionSlam.getLandmarks().size(), "A landmark should be added when Pose and TrackedObject timestamps match.");
    }

    @Test
    void addTrackedObject() {
        // Create a DetectedObject and StampedCloudPoints
        DetectedObject detectedObject = new DetectedObject("3", "Test Object");
        List<CloudPoint> cloudPoints = new ArrayList<>();
        cloudPoints.add(new CloudPoint(2.0, 3.0));
        StampedCloudPoints stampedCloudPoints = new StampedCloudPoints(3, cloudPoints);

        // Create a TrackedObject
        TrackedObject trackedObject = new TrackedObject(detectedObject, stampedCloudPoints);
        fusionSlam.addTrackedObject(trackedObject);

        Pose matchingPose = new Pose(3, 30.0, 35.0, 180.0);
        fusionSlam.addPose(matchingPose);

        assertEquals(1, fusionSlam.getLandmarks().size(), "Landmark should be created when tracked object matches a pose.");
    }

    @Test
    void updateLandMark() {
        Pose pose = new Pose(4, 40.0, 50.0, 270.0);

        // Create a DetectedObject and StampedCloudPoints
        DetectedObject detectedObject = new DetectedObject("4", "Object Update Test");
        List<CloudPoint> cloudPoints = new ArrayList<>();
        cloudPoints.add(new CloudPoint(5.0, 5.0));
        StampedCloudPoints stampedCloudPoints = new StampedCloudPoints(4, cloudPoints);

        // Create a TrackedObject
        TrackedObject trackedObject = new TrackedObject(detectedObject, stampedCloudPoints);

        fusionSlam.addPose(pose);
        fusionSlam.addTrackedObject(trackedObject);

        List<LandMark> landmarks = fusionSlam.getLandmarks();
        assertEquals(1, landmarks.size(), "A landmark should be created after updating.");

        LandMark landMark = landmarks.get(0);
        assertEquals("4", landMark.getId(), "Landmark ID should match the tracked object ID.");
    }

    @Test
    void testUpdateTotal() {
        for (int i = 0; i < 4; i++) {
            assertFalse(fusionSlam.updateTotal(), "Total should not reach zero yet.");
        }
        assertTrue(fusionSlam.updateTotal(), "Total should reach zero after the last update.");
    }
}
