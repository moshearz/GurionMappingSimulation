package spl.bgu.mics.application.services;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

//Libary of JSON parser for loading the data from the JSON file into the test
import bgu.spl.mics.application.services.CameraService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spl.bgu.mics.application.JsonUtils;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List; // Maybe ArrayList?
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//Test Case the method should: 1. Retrieve raw data 2. convert ot into the right format 3. return the processed format
class CameraServiceUnitTest {
    private CameraService cameraService;
    private Camera testCamera;

    void setUp() throws Exception {
        Path filePath = Paths.get("example input", "camera_data.json"); //path to the json file. relative to the config file.
        assertTrue(Files.exists(filePath), "json file does not exist or not found: " + filePath.toAbsolutePath()); // Ensure the file path wxist in te specified location and send err msg if not

        try (Reader reader = Files.newBufferedReader(filePath)) // opens BufferedReader for the file - used to parse the json file.
        {
            Type type = new TypeToken<Map<String, List<StampedDetectedObjects>>>() {}.getType(); //TypeToken used to define the specific generic type we expect from the json file
            Map<String, List<StampedDetectedObjects>> cameraData = new Gson().fromJson(reader, type); //Parse the JSON data

            List<StampedDetectedObjects> detectedObjectsList = cameraData.get("camera1"); //testing "camera1"
            testCamera = new Camera(1, 0, detectedObjectsList); // Initialize Camera with the parsed data
            cameraService = new CameraService(testCamera); // Initialize CameraService with the test Camera

        }



    }








}




