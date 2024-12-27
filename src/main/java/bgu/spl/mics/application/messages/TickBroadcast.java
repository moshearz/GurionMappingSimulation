package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private final String microServiceID;

    public TickBroadcast(String microServiceID) { this.microServiceID = microServiceID; }

    public String getMicroServiceID() { return microServiceID; }
}
