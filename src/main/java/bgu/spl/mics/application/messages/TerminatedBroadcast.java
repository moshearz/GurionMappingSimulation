package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import java.lang.reflect.Type;


/**
 * A broadcast message indicating that a specific service is terminating.
 * Sent by: all the sensors
 * used for: notifying all other services that the service sending the broadcast will terminate
 */
public class TerminatedBroadcast implements Broadcast {
    private final Type microServiceType;

    public TerminatedBroadcast(Type microServiceType) { this.microServiceType = microServiceType; }

    public Type getMicroServiceType() { return microServiceType; }
}
