package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.reflect.TypeToken;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");
        try (FileReader reader = new FileReader(args[0])) {
            Gson gson = new Gson();
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            JsonObject CamerasJSON = root.getAsJsonObject("Cameras");
            JsonArray CamerasConfigurationsJSON = CamerasJSON.getAsJsonArray("CamerasConfigurations");
            String cameraDataPathJSON = CamerasJSON.getAsJsonPrimitive("camera_datas_path").getAsString();
            Path CameraDataPath = Paths.get(args[0]).toAbsolutePath().normalize().getParent().resolve(cameraDataPathJSON).normalize();
            List<CameraService> cameraServiceList = new ArrayList<>();
            try (FileReader camReader = new FileReader(CameraDataPath.toString())) {
                JsonObject cameraRoot = JsonParser.parseReader(camReader).getAsJsonObject();
                for (int i = 1; i <= CamerasConfigurationsJSON.size(); i++) {
                    JsonArray camera_i = cameraRoot.getAsJsonArray("camera" + i);
                    cameraServiceList.add(new CameraService(new Camera(
                            CamerasConfigurationsJSON.get(i - 1).getAsJsonObject().get("id").getAsInt(),
                            CamerasConfigurationsJSON.get(i - 1).getAsJsonObject().get("frequency").getAsInt(),
                            gson.fromJson(camera_i, new TypeToken<List<StampedDetectedObjects>>(){}.getType())
                    )));
                }
            }

            JsonObject LidarWorkersJSON = root.getAsJsonObject("LiDarWorkers");
            JsonArray LidarConfigurationsJSON = LidarWorkersJSON.getAsJsonArray("LidarConfigurations");
            String lidarDataPathJSON = LidarWorkersJSON.getAsJsonPrimitive("lidars_data_path").getAsString();
            List<LiDarService> liDarWorkerTrackerList = new ArrayList<>();
            for (int i = 0; i < LidarConfigurationsJSON.size(); i++) {
                liDarWorkerTrackerList.add(new LiDarService(new LiDarWorkerTracker(
                        LidarConfigurationsJSON.get(i).getAsJsonObject().get("id").getAsInt(),
                        LidarConfigurationsJSON.get(i).getAsJsonObject().get("frequency").getAsInt()
                )));
            }

            Path LidarDataPath = Paths.get(args[0]).toAbsolutePath().normalize().getParent().resolve(lidarDataPathJSON).normalize();
            List<StampedCloudPoints> cloudPointList;
            try (FileReader lidarReader = new FileReader(LidarDataPath.toString())) {
                Type cloudPointsListType = new TypeToken<List<StampedCloudPoints>>() {}.getType();
                cloudPointList = gson.fromJson(lidarReader, cloudPointsListType);
            }
            LiDarDataBase lidarDB = LiDarDataBase.getInstance();
            lidarDB.setDataBase(cloudPointList);

            String poseJsonFile = root.getAsJsonPrimitive("poseJsonFile").getAsString();
            Path PoseDataPath = Paths.get(args[0]).toAbsolutePath().normalize().getParent().resolve(poseJsonFile).normalize();
            PoseService poseService = new PoseService(new GPSIMU(PoseDataPath.toString()));

            int TickTime = root.getAsJsonPrimitive("TickTime").getAsInt();
            int Duration = root.getAsJsonPrimitive("Duration").getAsInt();
            TimeService globalClock = new TimeService(TickTime, Duration);

            FusionSlamService fusionSlamService = new FusionSlamService(FusionSlam.getInstance());

            MessageBusImpl simulation = MessageBusImpl.getInstance();

            StatisticalFolder stats = StatisticalFolder.getInstance();

            for (CameraService microService : cameraServiceList) {
                microService.run();
            }
            for (LiDarService microService : liDarWorkerTrackerList) {
                microService.run();
            }
            poseService.run();
            fusionSlamService.run();

            FusionSlam.getInstance().setTotalMS(cameraServiceList.size() + liDarWorkerTrackerList.size());

            globalClock.run();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
