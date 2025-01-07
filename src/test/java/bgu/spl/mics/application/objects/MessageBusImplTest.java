package bgu.spl.mics.application.objects;

import bgu.spl.mics.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private MessageBus messageBus;
    private MicroService testService1;
    private MicroService testService2;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance(); //Singelton instance of messageBus
        testService1 = new MicroService("TestService1") {
            @Override
            protected void initialize() {} // init method is empty for this test
        };

        testService2 = new MicroService("TestService2") {
            @Override
            protected void initialize() {}
        };
        messageBus.register(testService1);
        messageBus.register(testService2);
    }

    @Test
    void unregisterMS() {
        messageBus.unregister(testService1);
        messageBus.unregister(testService2);

        // Ensures that calling awaitMessage on an unregistered microservice throws IllegalArgumentException.
        assertThrows(IllegalArgumentException.class, () -> messageBus.awaitMessage(testService1));
        assertThrows(IllegalArgumentException.class, () -> messageBus.awaitMessage(testService2));
    }

    @Test
    void testSubscribeAndSendEvent() throws InterruptedException {
        messageBus.subscribeEvent(TestEvent.class, testService1); //Register testservice1 to handle TestEvent
        TestEvent event = new TestEvent();
        Future<String> future = messageBus.sendEvent(event); //sends a testevent to MS testservice1 and return a Future
        assertNotNull(future); // ensures future returned

        new Thread( () -> {
            try{
                Message message = messageBus.awaitMessage(testService1); // the ms (use it) waits to the events in his queue
                assertEquals(event, message);
            } catch (InterruptedException e){
                fail("Thread Interrupted");
            }
        }).start();

        String res = future.get(2, TimeUnit.SECONDS); // ??

    }

    @Test
    void testSubscribeAndSendBroadcast() throws InterruptedException {
        //Subscribe ts1 and ts2 to TestBroadcast (using the messageBus)
        messageBus.subscribeBroadcast(TestBroadcast.class, testService1);
        messageBus.subscribeBroadcast(TestBroadcast.class, testService2);
        TestBroadcast broadcast = new TestBroadcast();

        messageBus.sendBroadcast(broadcast);

        Message msg1 = messageBus.awaitMessage(testService1);
        assertInstanceOf(TestBroadcast.class, msg1, "Received message type should be TestBroadcast");
        assertEquals(broadcast, msg1); // Verify content of the broadcast
        Message msg2 = messageBus.awaitMessage(testService2);
        assertInstanceOf(TestBroadcast.class, msg2, "Received message type should be TestBroadcast");
        assertEquals(broadcast, msg2);
    }

    @Test
    void testRoundRobinEventDistribution() throws InterruptedException {
         messageBus.subscribeEvent(TestEvent.class, testService1);
         messageBus.subscribeEvent(TestEvent.class, testService2);
         TestEvent event1 = new TestEvent();
         TestEvent event2 = new TestEvent();
        //Sends two events
         messageBus.sendEvent(event1);
         messageBus.sendEvent(event2);
        //Confirm ts1 gets event1 & ts2 gets event2
         Message msg1 = messageBus.awaitMessage(testService1);
         assertEquals(event1, msg1);
         Message msg2 = messageBus.awaitMessage(testService2);
         assertEquals(event2, msg2);
    }

    @Test
    void testAwaitMessageBlocks() throws InterruptedException {
        //Ensures awaitMeassage blocks when no message is availaiblee
        //NOTICE,we wont send an event in this part
        //We create a tread to simulate ms calling awaitmessage, which attempts to retrieve a msg from the queue of ts1
        Thread testThread = new Thread( () -> {
           try {
               messageBus.awaitMessage(testService1); // The queue for ts1 is initially empty,so the awaitMessage call clocks(waits)
               fail("Should have blocked,"); //unexpected behavior
           } catch (InterruptedException e) {}
        });
        testThread.start();
        testThread.sleep(1000);// Give Thread time to block
        testThread.interrupt();
    }
    //Instead of create class files of java
    private static class TestEvent implements Event<String> {}
    private static class TestBroadcast implements Broadcast {}
}
