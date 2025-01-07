package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;
import com.google.gson.reflect.TypeToken;
import java.util.concurrent.TimeUnit;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    private final int TickTime;
    private final int Duration;

    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in seconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        this.TickTime = TickTime;
        this.Duration = Duration;
    }

    public synchronized void tickWait() {
        long endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(TickTime);
        while (System.currentTimeMillis() < endTime) {
            try {
                this.wait(endTime - System.currentTimeMillis());
            } catch (InterruptedException ignored) {}
        }
        StatisticalFolder.getInstance().updateSystemRuntimeTotal();
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TerminatedBroadcast.class, termination -> {
            if (termination.getMicroServiceType() == new TypeToken<FusionSlamService>() {}.getType()) {
                terminate();
                sendBroadcast(new TerminatedBroadcast(TimeService.class));
            }
        });

        subscribeBroadcast(TickBroadcast.class, tick -> {
            if (tick.getTick() <= Duration) {
                tickWait();
                sendBroadcast(new TickBroadcast(tick.getTick() + 1));
            } else {
                terminate();
                sendBroadcast(new TerminatedBroadcast(TimeService.class));
            }
        });
        tickWait();
        sendBroadcast(new TickBroadcast(1));

        //System.out.println(getName() + " initialized.");
    }
}