package spl.bgu.mics.application.services;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

//Libary of JSON parser for loading the data from the JSON file into the test
import bgu.spl.mics.application.services.CameraService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List; // Maybe ArrayList?
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//Test Case the method should: 1. Retrieve raw data 2. convert ot into the right format 3. return the processed format
class CameraServiceUnitTest {
    private CameraService cameraService;
    private Camera testCamera;
    private Map<Integer, StampedDetectedObjects> stampedDetectedObjectsFromJSONData; // Map of timestamp to detected objects

    void setUp() throws Exception {
        // load JSON Data
        try
        {

        }
    }








}




