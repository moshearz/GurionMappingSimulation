package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private List<LandMark> landmarks = new ArrayList<>(); // List of landmark on the map
    private List<Pose> poses = new ArrayList<>(); // List of Robot locations for calculations
    private FusionSlamHolder instanceHolder;

    public FusionSlam getInstance() {
        if (instanceHolder == null) {
            instanceHolder = new FusionSlamHolder();
        }
        return instanceHolder.instance;
    }

    public void updateLandMark(String objectId, Pose pose) {

//        LandMark selectedLandMark = null;
//        for (LandMark currentLM : landmarks) {
//            if (Objects.equals(currentLM.getId(), landMark.getId())) {
//                selectedLandMark = currentLM;
//            }
//        }
//        if (selectedLandMark == null) {
//            landmarks.add(landMark);
//        } else {
//            List<CloudPoint> updatedCoordinates = selectedLandMark.getCoordinates();
//            for (int i = 0; i < ; i++) {
//
//            }
//            selectedLandMark.setCoordinates();
//        }
    }

    private static class FusionSlamHolder {
        // Singleton instance holder
        public FusionSlam instance;
    }

}
