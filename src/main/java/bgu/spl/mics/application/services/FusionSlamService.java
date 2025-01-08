package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private final FusionSlam instance;
    private final File filePath;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam, String filePath) {
        super("Fusion-SLAM");
        instance = fusionSlam;
        this.filePath = new File(filePath);
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeEvent(TrackedObjectsEvent.class, objectsEvent -> {
            for (TrackedObject trackedObject : objectsEvent.getTrackedObjects()) {
                instance.addTrackedObject(trackedObject);
            }
        });

        subscribeEvent(PoseEvent.class, poseEvent -> {
            instance.addPose(poseEvent.getCurrPose());
        });

        subscribeBroadcast(TerminatedBroadcast.class, termination -> {
            if (instance.updateTotal()) {
                terminate();
                sendBroadcast(new TerminatedBroadcast(this.getClass()));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                StatisticalFolder.getInstance().setLandMarks(instance.getLandmarks());
                String jsonString = gson.toJson(StatisticalFolder.getInstance());
                try (FileWriter writer = new FileWriter(new File(filePath.getParent(),"output_file.json"))) {
                    gson.toJson(StatisticalFolder.getInstance(), writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            if (instance.updateTotal()) {
                terminate();
                sendBroadcast(new TerminatedBroadcast(this.getClass()));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                StatisticalFolder.getInstance().setLandMarks(instance.getLandmarks());
                String jsonString = gson.toJson(CrashReport.getInstance());
                try (FileWriter writer = new FileWriter(new File(filePath.getParent(),"error_output.json"))) {
                    gson.toJson(CrashReport.getInstance(), writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String getOutputFilePath(String inputFilePath, String outputFileName) {
        File inputFile = new File(inputFilePath);
        return new File(inputFile.getParent(), outputFileName).getPath();
    }
}