package bgu.spl.mics.application.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CameraTest {
    private Camera camera;
    private List<StampedDetectedObjects> detectedObjectsList;

    @BeforeEach
    void setUp() {
        // Create mock DetectedObjects for the test
        List<DetectedObject> objectsAtTime1 = new ArrayList<>();
        objectsAtTime1.add(new DetectedObject("Wall_1", "Wall"));
        objectsAtTime1.add(new DetectedObject("Wall_3", "Wall"));
        List<DetectedObject> objectsAtTime2 = new ArrayList<>();
        objectsAtTime2.add(new DetectedObject("Chair_Base_1", "Chair_Base"));
        objectsAtTime2.add(new DetectedObject("Door", "Door"));

        // Create StampedDetectedObjects with timestamps
        detectedObjectsList = new ArrayList<>();
        detectedObjectsList.add(new StampedDetectedObjects(1, objectsAtTime1));
        detectedObjectsList.add(new StampedDetectedObjects(3, objectsAtTime2));

        // Initialize Camera with mock data
        camera = new Camera(1, 2, detectedObjectsList);
    }

    @Test
    void getId() {
        assertEquals(1, camera.getId(), "Camera ID should be 1");
    }

    @Test
    void getFrequency() {
        assertEquals(2, camera.getFrequency(), "Camera frequency should be 2");
    }

    @Test
    void getStatus() {
        assertEquals(STATUS.UP, camera.getStatus(), "Camera status should be UP initially");
    }

    @Test
    void setStatus() {
        camera.setStatus(STATUS.DOWN);
        assertEquals(STATUS.DOWN, camera.getStatus(), "Camera status should be updated to DOWN");
    }

    @Test
    void getDetectedObjectsList() {
        assertEquals(2, camera.getDetectedObjectsList().size(), "DetectedObjectsList should contain 2 entries");
        assertEquals(1, camera.getDetectedObjectsList().get(0).getTime(), "First entry should have timestamp 1");
        assertEquals(3, camera.getDetectedObjectsList().get(1).getTime(), "Second entry should have timestamp 3");
    }

    @Test
    void getStampedDetectedObjects_ValidTick() {
        StampedDetectedObjects result = camera.getStampedDetectedObjects(3); // Tick 3, frequency is 2
        assertNotNull(result, "Detected objects should not be null for valid tick");
        assertEquals(2, result.getDetectedObjects().size(), "Detected objects list should contain 2 objects");
        assertEquals("Wall", result.getDetectedObjects().get(0).getDescription(), "First detected object should be 'Person'");
    }

    @Test
    void getStampedDetectedObjects_InvalidTick() {
        StampedDetectedObjects result = camera.getStampedDetectedObjects(1); // Tick 1, not within frequency
        assertNull(result, "Detected objects should be null for invalid tick");
    }

    @Test
    void isEmpty_Initially() {
        assertFalse(camera.isEmpty(), "Detected objects list should not be empty initially");
    }

    @Test
    void isEmpty_AfterRemoval() {
        camera.getStampedDetectedObjects(3); // Remove first object
        camera.getStampedDetectedObjects(5); // Remove second object
        assertTrue(camera.isEmpty(), "Detected objects list should be empty after removing all objects");
    }
}
