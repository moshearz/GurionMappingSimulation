package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {

    private final int id;
    private final int frequency;
    private STATUS status;
    private final List<StampedDetectedObjects> detectedObjectsList; // List of objects with time stamp that identified by the Camera

    public Camera(int id, int frequency, List<StampedDetectedObjects> detectedObjectsList) {
        this.id = id;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.detectedObjectsList = detectedObjectsList;
    }

    public int getId() {return id;}

    public int getFrequency() {return frequency;}

    public STATUS getStatus() { return status; }

    public void setStatus(STATUS status) { this.status = status; }

    public List<StampedDetectedObjects> getDetectedObjectsList() { return detectedObjectsList; }

    public StampedDetectedObjects getStampedDetectedObjects(int tick) {
        for (StampedDetectedObjects current : detectedObjectsList) {
            if (current.getTime() == tick) {
                return current;
            }
        }
        return null;
    }

    public void addDetectedObject(StampedDetectedObjects detectedObject) {
        this.detectedObjectsList.add(detectedObject);
        System.out.println("Camera " + id + " added detected object: " + detectedObject); // for debugging
    }
}
