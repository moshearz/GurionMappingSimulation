package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

public class Future<T> {

	private T result;
	private boolean status;
	private final Object lock = new Object();

	/**
	 * This should be the only public constructor in this class.
	 */
	public Future() {
		this.result = null;
		this.status = false;
	}

	/**
	 * Retrieves the result the Future object holds if it has been resolved.
	 * This is a blocking method! It waits for the computation if it has
	 * not been completed.
	 *
	 * @return the result of type T if it is available, if not wait until it is available.
	 */
	public T get() {
		synchronized (lock) {
			while (!status) {
				try {
					lock.wait();
				} catch (InterruptedException e) {}
			}
			return result;
		}
	}

	/**
	 * Resolves the result of this Future object.
	 */
	public void resolve(T result) {
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
	 * Retrieves the result the Future object holds if it has been resolved,
	 * This method is non-blocking, it has a limited amount of time determined
	 * by {@code timeout}
	 *
	 * @param timeout  the maximal amount of time units to wait for the result.
	 * @param unit     the {@link TimeUnit} time units to wait.
	 * @return the result of type T if it is available; if not,
	 *         wait for {@code timeout} TimeUnits {@code unit}. If time has
	 *         elapsed, return null.
	 */
	public T get(long timeout, TimeUnit unit) {
		synchronized (lock) {
			if (status) { return result; }

			long timeLeft = unit.toMillis(timeout);
			long endTime  = System.currentTimeMillis() + timeLeft;

			// Wait until status is true, or time has elapsed
			while (!status && timeLeft > 0) {
				try {
					lock.wait(timeLeft);
				} catch (InterruptedException e){}
				timeLeft = endTime  - System.currentTimeMillis();
			}

			return status ? result : null;
		}
	}
}
