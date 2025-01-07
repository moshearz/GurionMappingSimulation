package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.CrashReport;

/**
 * (Represents a broadcast message) sent by:  all the sensors (broadcast message sent by a sensor to notify other services)
 * used for: Notifying all other services that the sender service has crashed
 */
public class CrashedBroadcast implements Broadcast {

    public CrashedBroadcast(String error, String faultySensor) {
        CrashReport crash = CrashReport.getInstance();
        crash.setErrorMessage(error);
        crash.setFaultySensor(faultySensor);
    }
}
