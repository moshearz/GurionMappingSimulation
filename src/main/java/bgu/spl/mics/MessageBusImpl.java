package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.*;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {

	// Separate maps for Event and Broadcast subscriptions
	//map for Event. f we use round-robin indices to implement round-robin behavior, the eventSubscriptions field must be a List and not a Queue.
	//Because A List allows direct access to elements using their index and this is necessary for implementing round-robin via an external field like roundRobinIndices
	// (A Queue operates on a FIFO (First-In-First-Out) principle.)
	private final Map<Class<? extends Event<?>>, Queue<MicroService>> eventSubscriptions;

	//map for Broadcast
	private final Map<Class<? extends Broadcast>, List<MicroService>> broadcastSubscriptions;

	// Mapping from each MicroService to its own message queue
	private final Map<MicroService, BlockingQueue<Message>> queues;
	//BlockingQueue is a java Queue that support operations that wait for the queue to become non-empty when retrieving and removing an element, and wait for space to become available in the queue when adding an element.

	// Mapping from events to their associated Futures
	private final Map<Event<?>, Future<?>> eventFutures;

	// Singleton instance of the MessageBusImpl
	private static MessageBusImpl instance = null;


	private MessageBusImpl() {
		eventSubscriptions = new ConcurrentHashMap<>(); // Thread-safe mapping for Event subscriptions
		broadcastSubscriptions = new ConcurrentHashMap<>(); // Thread-safe mapping for Broadcast subscriptions
		queues = new ConcurrentHashMap<>();        // Thread-safe mapping for MicroService queues
		eventFutures = new ConcurrentHashMap<>();  // Thread-safe mapping for event Futures
	}

	// Singleton instance
	//private static MessageBusImpl instance_ = null;
	// duplicate?

	/**
	 * Public method to get the Singleton instance of the MessageBusImpl.
	 * Ensures thread-safe instantiation.
	 * @return the Singleton instance of MessageBusImpl.
	 */
	public static synchronized MessageBusImpl getInstance() {
		if (instance == null) {
			instance = new MessageBusImpl();
		}
		return instance;
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// Use of ConcurrentHashMap's merge function which is an atomic thread-safe function
		// merge tries to add a new event type to the map if it doesn't exist yet, otherwise using the built-in biFunction m gets added to the already existing queue
//		eventSubscriptions.merge(type, new ConcurrentLinkedQueue<>(), (existingQueue, placeHolder) -> {
//			existingQueue.add(m);
//			return existingQueue;
//		});

		eventSubscriptions.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// Use of ConcurrentHashMap's merge function which is an atomic thread-safe function
		// merge tries to add a new event type to the map if it doesn't exist yet, otherwise using the built-in biFunction m gets added to the already existing list
//		broadcastSubscriptions.merge(type, new CopyOnWriteArrayList<>(), (existingList, placeHolder) -> {
//			existingList.add(m);
//			return existingList;
//		});

		broadcastSubscriptions.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		try { // Test case if event doesn't exist
			((Future<T>) eventFutures.get(e)).resolve(result); // Uses safe casting since eventFutures is maintained in a type-consistent manner
		} catch (NullPointerException ignore) {}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		try { // Test case if at least one microService has subscribed to broadcast type b
			synchronized (broadcastSubscriptions.get(b.getClass())) {
				for (MicroService subscriber : broadcastSubscriptions.get(b.getClass())) {
					queues.get(subscriber).add(b);
				}
			}
		} catch (NoSuchElementException ignored) {}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		try { // Test case if at least one microService has subscribed to event type e
			synchronized (eventSubscriptions.get(e.getClass())) {
				Queue<MicroService> eventQueue = eventSubscriptions.get(e.getClass());
				try { // Test case if all the subscribed microServices have terminated
					MicroService selectedMicroService = eventQueue.remove();
					eventQueue.add(selectedMicroService);
					queues.get(selectedMicroService).add(e);
				} catch (NoSuchElementException E) {
					return null;
				}
			}
			Future<T> future = new Future<>();
			eventFutures.put(e, future);
			return future;
		} catch (NullPointerException E) {
			return null;
		}
	}

	@Override
	public void register(MicroService m) {
		// register a microservice in the sys
		// init msg queue
		queues.putIfAbsent(m, new LinkedBlockingQueue<>());
		//The putIfAbsent() method writes an entry into the map. If an entry with the same key already exists and its value is not null then the map is not changed.
	}

	@Override
	public void unregister(MicroService m) {
		// removes (unregister..) a microservice from the sys
		// cleaning up: msg queue and its subscription
		synchronized (queues) {
			Queue<Message> queue = queues.remove(m);
			if (queue != null) {
				queue.clear();
			} // clear all messages in the queue
		}
		synchronized (eventSubscriptions){ for( Queue<MicroService> subscribers : eventSubscriptions.values()) subscribers.remove(m);}

		synchronized (broadcastSubscriptions){ for( List<MicroService> subscribers : broadcastSubscriptions.values()) subscribers.remove(m);}

		//Only if necessary, removed associated futures
		//entrySet() returns a set view of the key-value pairs in the eventFutures map. then "removeIf(...)" iterates over the entries in the Set and removes any entry where the provided condition in the lambda
		//eventFutures.entrySet().removeIf(entry -> entry.getValue().equals(m));

		// I think this part is unnecessary ^

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		while (queues.get(m) != null) {
			try {
				return queues.get(m).take();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		return null;
	}

}
