package charStackManagement;

import charStackExceptions.SemaphoreNegativeValueException;

//Source code for semaphore class:       

public class Semaphore {
	private int value;

	public Semaphore(int value) throws SemaphoreNegativeValueException {
		if (value < 0) {
			throw new SemaphoreNegativeValueException();
		} else {
			this.value = value;
		}
	}

	public Semaphore() throws SemaphoreNegativeValueException {
		this(0);
	}

	public synchronized void Wait() {
		while (this.value <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("Semaphore::Wait() - caught InterruptedException: " + e.getMessage());
				e.printStackTrace();
			}
		}
		this.value--;
	}

	public synchronized void Signal() {
		++this.value;
		notify();
	}

	public synchronized void P() {
		this.Wait();
	}

	public synchronized void V() {
		this.Signal();
	}
}
