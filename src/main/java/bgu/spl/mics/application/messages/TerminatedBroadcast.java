package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;



/**
 * A broadcast message indicating that a specific service is terminating.
 * Sent by: all the sensors
 * used for: notifying all other services that the service sending the broadcast will terminate
 */
public class TerminatedBroadcast implements Broadcast {
    private final String serviceName;

    public TerminatedBroadcast(String serviceName) { this.serviceName = serviceName; }

    public String getServiceName() { return serviceName; }
}
