package concurrency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Autolock implements AutoCloseable {

	private final Lock lock = new ReentrantLock();

	public static void main(String... args) {
		Autolock myLock = new Autolock();
		try (Autolock tmp = myLock.lock()) {
			System.out.println("locked " + myLock);
		}
		System.out.println("Unlocked? " + myLock);


	}

	public Autolock lock() {
		lock.lock();
		return this;
	}

	@Override
	public void close() {
		lock.unlock();
	}

	@Override
	public String toString() {
		return "Autolock{" +
			"lock=" + lock +
			'}';
	}
}
