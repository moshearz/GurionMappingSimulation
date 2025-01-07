package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * (Represents a broadcast message) sent by:  all the sensors (broadcast message sent by a sensor to notify other services)
 * used for: Notifying all other services that the sender service has crashed
 */
public class CrashedBroadcast implements Broadcast {

    private final String error;
    private final String faultySensor;

    public CrashedBroadcast(String error, String faultySensor) {
        this.error = error;
        this.faultySensor = faultySensor;
    }
}
