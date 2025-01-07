package spl.bgu.mics.application.objects;


import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.TrackedObject;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;


class FusionSlamUnitTest {
    private FusionSlam fusionSlam;

    @BeforeEach
    void setUp() {
        fusionSlam = FusionSlam.getInstance();
    }

    @Test
    void testSingletonBehavior(){
        FusionSlam slam1 = FusionSlam.getInstance();
        FusionSlam slam2 = FusionSlam.getInstance();
        assertSame(slam1, slam2);
    }

    @Test
    void testAddAndVerifyPose() throws Exception{
        //Load pose data from pose_data.json
        Reader reader = Files.newBufferedReader(Paths.get("example input/pose_data.json"));
        Gson gson = new Gson();
        Pose[] poses = gson.fromJson(reader, Pose[].class);

        // Add all poses from JSON and verify existence
        for (Pose pose : poses) {
            fusionSlam.addPose(pose);
            assertTrue(fusionSlam.poseExist(pose.getTime()), "Pose with tickTime " + pose.getTime() + " should exist in FusionSlam");
        }

        assertFalse(fusionSlam.poseExist(50), "Pose with tickTime 50 should not exist in FusionSlam"); // Verify a pose that does mot exist in the JSON

    }

    @Test
    void testNewLandMarkFromJson() throws Exception{
        //Load pose data + tracked object data from json(pose_data.json, lidar_data.json)
        Reader readerPD = Files.newBufferedReader(Paths.get("example input/pose_data.json"));
        Reader readerLD = Files.newBufferedReader(Paths.get("example input/lidar_data.json"));
        Gson gson = new Gson();
        Pose[] poses = gson.fromJson(readerPD, Pose[].class);
        TrackedObject[] trackedObjects = gson.fromJson(readerLD, TrackedObject[].class);
        //from example input (first), we take match pose and trackedObject (time 2...)
        fusionSlam.addPose(poses[1]); // Pose at time=2 (poses[1] has time=2)
        fusionSlam.updateLandMark(trackedObjects[0]); // Tracked object at time=2 (trackedObjects[0])
        //Reads all the data from the Fusion Slam, (with stream) we convert the list of lm into stream for processing,
        LandMark landMark = fusionSlam.getLandmarks().stream().filter(lm -> lm.getId().equals(trackedObjects[0].getId())).findFirst().orElse(null);
        //taking here only one point and not sure its correct
    }











}
