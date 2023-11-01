package ProgrammingAssignment2;

//Source code for semaphore class:       

public class Semaphore {
	private int value;
	private int counter = 0;

	public Semaphore(int value) {
		this.value = value;
	}

	public Semaphore() {
		this(0);
	}

	public synchronized void Wait() {
//		this.value--; // __________________________________________++++
		if (this.value < 0)
			counter++;
		while (this.value <= 0) // _______________make it < 0
		{
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println("Semaphore::Wait() - caught InterruptedException: " + e.getMessage());
				e.printStackTrace();
			}
		}
		this.value--;
		counter--;
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

//Task 1: Refer to the Semaphore class supplied and answer the following: according to the classical
//definition of a semaphore, it can be initialized only to a non-negative value. If the value of a
//semaphore becomes negative (via Wait() operation), then its absolute value indicates the number of
//processes/threads on its wait queue. Does the given semaphore class implementation satisfy these
//requirements? If not, modify the semaphore class implementation accordingly. Submit the modified
//code.
