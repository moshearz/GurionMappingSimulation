package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {

	private T result;
	private boolean status; // Boolean flag that says if problem "resolved"
	private final Object lock = new Object();

	/**
	 * This should be the only public constructor in this class.
	 */
	public Future() {
		this.result = null;
		this.status = false;
	}

	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * /@return return the result of type T if it is available, if not wait until it is available.
     *
	 */
	public T get() {
		synchronized (lock) { // JVM attaches to each object at runtime it own monitor which has two states: possession or availaible
			while (!status) { // waits until the futere object is resolved and eventually returns the result
				try {
					lock.wait(); // waits for resolve() calls to notifyAll()
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			return result;
		}
	}

	/**
     * Resolves the result of this Future object.
     */
	public void resolve (T result) {
		synchronized (lock) {
			this.result = result;
			this.status = true;
			lock.notifyAll();
		}
	}

	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		synchronized (lock) {
			return status;
		}
	}

	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {/@code timeout}
     * <p>
     * /@param timout     the maximal amount of time units to wait for the result.
     * /@param unit        the {/@link TimeUnit} time units to wait.
     * /@return return the result of type T if it is available, if not,
     * 	       wait for {/@code timeout} TimeUnits {/@code unit}. If time has
     *         elapsed, return null.
     */
	public T get(long timeout, TimeUnit unit) {
		synchronized (lock) {
			if (!status) { // waits until: Future is resolved OR specified timout duration past
				try {
					long endTime = System.currentTimeMillis() + unit.toMillis(timeout); // calculates the absolute "timestamp" of when the waiting period should end.
					long timeLeft = unit.toMillis(timeout); // How much time left to wait' initially to full timeout duration
					while (!status && timeLeft > 0) { // Because of Spurious wakeups we need thw while loop. this can happen when a thread calls the wait() method on an object and subsequently gets woken up without any actual notification or interruption.
						lock.wait(timeLeft);
						timeLeft = endTime - System.currentTimeMillis();
					}
				} catch (InterruptedException e) {
					// Ensures that the thread itself (or other parts of the program), can detect and respond to interruption.
					Thread.currentThread().interrupt();
				}
			}
			return result;
		}
	}

}
