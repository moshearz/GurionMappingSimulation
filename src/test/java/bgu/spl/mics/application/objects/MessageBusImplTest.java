package bgu.spl.mics.application.objects;

import bgu.spl.mics.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private MessageBusImpl messageBus;
    private MicroService testService1;
    private MicroService testService2;
    private TestEvent testEvent;
    private TestBroadcast testBroadcast;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        testService1 = new MicroService("TestService1") {
            @Override
            protected void initialize() {}
        };
        testService2 = new MicroService("TestService2") {
            @Override
            protected void initialize() {}
        };
        messageBus.register(testService1);
        messageBus.register(testService2);
        testEvent = new TestEvent();
        testBroadcast = new TestBroadcast();
    }

    @Test
    void testSubscribeEvent() {
        messageBus.subscribeEvent(TestEvent.class, testService1);
        Future<String> future = messageBus.sendEvent(testEvent);
        assertNotNull(future, "Future should not be null when an event is sent.");

        new Thread(() -> {
            try {
                Message message = messageBus.awaitMessage(testService1);
                assertEquals(testEvent, message, "The received message should match the sent event.");
                future.resolve("Completed");
            } catch (InterruptedException e) {
                fail("Thread interrupted unexpectedly.");
            }
        }).start();

        assertEquals("Completed", future.get(2, TimeUnit.SECONDS), "The future result should be resolved correctly.");
    }

    @Test
    void testSubscribeBroadcast() throws InterruptedException {
        messageBus.subscribeBroadcast(TestBroadcast.class, testService1);
        messageBus.subscribeBroadcast(TestBroadcast.class, testService2);

        messageBus.sendBroadcast(testBroadcast);

        Message message1 = messageBus.awaitMessage(testService1);
        Message message2 = messageBus.awaitMessage(testService2);

        assertEquals(testBroadcast, message1, "testService1 should receive the broadcast.");
        assertEquals(testBroadcast, message2, "testService2 should receive the broadcast.");
    }

    @Test
    void testComplete() {
        messageBus.subscribeEvent(TestEvent.class, testService1);
        Future<String> future = messageBus.sendEvent(testEvent);
        assertNotNull(future, "Future should not be null when an event is sent.");

        messageBus.complete(testEvent, "Completed");
        assertEquals("Completed", future.get(), "The future result should be resolved correctly.");
    }

    @Test
    void testSendBroadcast() throws InterruptedException {
        messageBus.subscribeBroadcast(TestBroadcast.class, testService1);
        messageBus.subscribeBroadcast(TestBroadcast.class, testService2);

        messageBus.sendBroadcast(testBroadcast);

        Message message1 = messageBus.awaitMessage(testService1);
        Message message2 = messageBus.awaitMessage(testService2);

        assertEquals(testBroadcast, message1, "testService1 should receive the broadcast.");
        assertEquals(testBroadcast, message2, "testService2 should receive the broadcast.");
    }

    @Test
    void testSendEvent() throws InterruptedException {
        messageBus.subscribeEvent(TestEvent.class, testService1);
        Future<String> future = messageBus.sendEvent(testEvent);
        assertNotNull(future, "Future should not be null when an event is sent.");

        new Thread(() -> {
            try {
                Message message = messageBus.awaitMessage(testService1);
                assertEquals(testEvent, message, "The received message should match the sent event.");
            } catch (InterruptedException e) {
                fail("Thread interrupted unexpectedly.");
            }
        }).start();

        messageBus.complete(testEvent, "Success");
        assertEquals("Success", future.get(2, TimeUnit.SECONDS), "The future result should be resolved correctly.");
    }

    @Test
    void testRegisterAndUnregister() {
        messageBus.unregister(testService1);

        assertThrows(IllegalStateException.class, () -> {
            messageBus.awaitMessage(testService1);
        }, "Awaiting message for an unregistered service should throw an exception.");

        messageBus.register(testService1);
        messageBus.subscribeEvent(TestEvent.class, testService1);

        Future<String> future = messageBus.sendEvent(testEvent);
        assertNotNull(future, "Future should not be null when an event is sent.");

        messageBus.unregister(testService1);
        assertThrows(IllegalStateException.class, () -> {
            messageBus.awaitMessage(testService1);
        }, "Awaiting message for an unregistered service should throw an exception.");
    }

    @Test
    void testAwaitMessageBlocks() throws InterruptedException {
        Thread testThread = new Thread(() -> {
            try {
                messageBus.awaitMessage(testService1);
                fail("AwaitMessage should block when no message is available.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        testThread.start();

        Thread.sleep(1000); // Give the thread time to block
        testThread.interrupt();
    }

    private static class TestEvent implements Event<String> {}

    private static class TestBroadcast implements Broadcast {}
}
