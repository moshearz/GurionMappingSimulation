package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick = 0; // Current Time
    private STATUS status = STATUS.UP;
    private final List<Pose> poseList = new ArrayList<>(); // List of the robot's position with time stamps

    public GPSIMU(String filePath) {
        try (JsonReader reader = new JsonReader(new FileReader(filePath))) {
            Gson gson = new Gson();
            reader.beginArray();
            while (reader.hasNext()) {
                poseList.add(gson.fromJson(reader, Pose.class));
            }
            reader.endArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Pose getCurrentPose(int tick) {
        for (Pose pose : poseList) {
            if (pose.getTime() == tick) {
                return pose;
            }
        }
        return null; // No pose found for the given tick
    }

    public int getCurrentTick() { return currentTick; }

    public int updateCurrentTick() {
        return ++currentTick;
    }

    public STATUS getStatus() { return status; }

    public void setStatus(STATUS status) { this.status = status;}

    public List<Pose> getPoseList() { return poseList; }


}
