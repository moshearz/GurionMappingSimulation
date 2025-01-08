package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import com.google.gson.reflect.TypeToken;
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
        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (camera.isEmpty()) {
                terminate();
                camera.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast(new TypeToken<CameraService>() {}.getType()));
            } else {
                StampedDetectedObjects stampedDetectedObjects = camera.getStampedDetectedObjects(tick.getTick());
                if (camera.getStatus() == STATUS.ERROR) {
                    terminate();
                    sendBroadcast(new CrashedBroadcast(stampedDetectedObjects.getDetectedObjects().get(0).getDescription(), "Camera " + camera.getId()));
                }
                if (camera.getStatus() == STATUS.UP & stampedDetectedObjects != null) {
                    StatisticalFolder.getInstance().updateDetectedObjectsTotal(stampedDetectedObjects.size());
                    sendEvent(new DetectObjectsEvent(stampedDetectedObjects));
                }
            }
        });

        subscribeBroadcast(TerminatedBroadcast.class, termination -> {
            if (termination.getMicroServiceType() == new TypeToken<TimeService>() {}.getType()) {
                sendBroadcast(new TerminatedBroadcast(new TypeToken<CameraService>() {}.getType()));
                terminate();
            }
        });

        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            CrashReport instance = CrashReport.getInstance();
            instance.addLastCameraFrame("Camera " + camera.getId(), camera.getLastDetectedObjects());
            sendBroadcast(new CrashedBroadcast(instance.getErrorMessage(), instance.getFaultySensor()));
            terminate();
        });
    }
}
