package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * broadcast message sent by the TimeService to notify other services about the current simulation tick.
 * Sent by: TimeService  other services for timing purposes.
 * Used for: Timing message publications and processing.
 */
public class TickBroadcast implements Broadcast {
    private final int tick;

    public TickBroadcast(int tick) { this.tick = tick; }

    public int getTick() { return tick; }
}
