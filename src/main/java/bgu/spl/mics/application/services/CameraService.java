package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.messages.TerminatedBroadcast;

import java.util.Objects;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("Camera: " + camera.getId());
        this.camera = camera;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        // First type of msg - TickBroadcast: Tracks system ticks and determines when to perform object detection
        subscribeBroadcast(TickBroadcast.class, tick ->{
            if (camera.getStatus() == STATUS.UP) {
                StampedDetectedObjects stampedDetectedObjects = camera.getStampedDetectedObjects(tick.getTick() - camera.getFrequency());
                if (stampedDetectedObjects != null) {
                    for (DetectedObject detectedObject : stampedDetectedObjects.getDetectedObjects()) {
                        if (Objects.equals(detectedObject.getId(), "ERROR")) {
                            sendBroadcast(new CrashedBroadcast("camera" + camera.getId()));
                            camera.setStatus(STATUS.ERROR);
                            terminate();
                        }
                    }
                    sendEvent(new DetectObjectsEvent(stampedDetectedObjects));
                }
            } else {
                terminate();
            }
        });
        // Second type of msg - TerminatedBroadcast: Listens for termination signals from other services
        subscribeBroadcast(TerminatedBroadcast.class, termination -> {
            System.out.println(getName() + " received termination signal. Terminating.(I'll be back!)");
            terminate(); // stop the service’s event loop
        });
        //Ensures that the service sends a TerminatedBroadcast before it terminates.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> { // Adds a shutdown hook, which is a thread that runs when the JVM is shutting down.
            sendBroadcast(new TerminatedBroadcast(getName()));
        }));
        System.out.println(getName() + " initialized.");

    }
}
