package charStackManagement;

import java.io.FileWriter;
import java.io.IOException;

import charStackExceptions.CharStackEmptyException;
import charStackExceptions.CharStackFullException;
import charStackExceptions.CharStackInvalidAceessException;

public class StackManager {
	private static CharStack stack = new CharStack();
	private static final int NUM_ACQREL = 4; // Number of Producer/Consumer threads
	private static final int NUM_PROBERS = 1; // Number of threads dumping stack
	private static int iThreadSteps = 3; // Number of steps they take

	private static StringBuilder stringBuilder = new StringBuilder();

	private static Semaphore mutex = new Semaphore(1);
	private static Semaphore full = new Semaphore(stack.getTop() + 1);
	private static Semaphore empty = new Semaphore(stack.getSize() - (stack.getTop() + 1));

	public static void main(String[] argv) {
		try {
			System.out.println("Main thread starts executing.");
			System.out.println("Initial value of top = " + stack.getTop() + ".");
			System.out.println("Initial value of stack top = " + stack.pick() + ".");
			System.out.println("Main thread will now fork several threads.");

			stringBuilder.append("Main thread starts executing.\n" + "Initial value of top = " + stack.getTop() + ".\n"
					+ "Initial value of stack top =" + stack.pick() + ".\n"
					+ "Main thread will now fork several threads.\n");

		} catch (CharStackEmptyException e) {
			System.out.println("Caught exception: StackCharEmptyException");
			System.out.println("Message : " + e.getMessage());
			System.out.println("Stack Trace : ");
			e.printStackTrace();
		}

		Consumer ab1 = new Consumer();
		Consumer ab2 = new Consumer();
		System.out.println("Two Consumer threads have been created.");

		stringBuilder.append("Two Consumer threads have been created.\n");

		Producer rb1 = new Producer();
		Producer rb2 = new Producer();
		System.out.println("Two Producer threads have been created.");

		stringBuilder.append("Two Producer threads have been created.\n");

		CharStackProber csp = new CharStackProber();
		System.out.println("One CharStackProber thread has been created.");

		stringBuilder.append("One CharStackProber thread has been created.\n");

		ab1.start();
		rb1.start();
		ab2.start();
		rb2.start();
		csp.start();
		/*
		 * Wait by here for all forked threads to die
		 */
		try {
			ab1.join();
			ab2.join();
			rb1.join();
			rb2.join();
			csp.join();
			// Some final stats after all the child threads terminated...
			System.out.println("System terminates normally.");
			System.out.println("Final value of top = " + stack.getTop() + ".");
			System.out.println("Final value of stack top = " + stack.pick() + ".");
			System.out.println("Final value of stack top-1 = " + stack.getAt(stack.getTop() - 1) + ".");

			stringBuilder.append("System terminates normally.\n" + "Final value of top = " + stack.getTop() + ".\n"
					+ "Final value of stack top = " + stack.pick() + ".\n" + "Final value of stack top-1 = "
					+ stack.getAt(stack.getTop() - 1) + ".\n");

		} catch (InterruptedException e) {
			System.out.println("Caught InterruptedException: " + e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			System.out.println("Caught exception: " + e.getClass().getName());
			System.out.println("Message : " + e.getMessage());
			System.out.println("Stack Trace : ");
			e.printStackTrace();
		}

		String outputString = stringBuilder.toString();

		try (FileWriter fw = new FileWriter("Output_.txt")) {
			fw.write(outputString);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	static class Consumer extends BaseThread {
		private char copy;

		public void run() {
			System.out.println("Consumer thread [TID=" + this.iTID + "] starts executing.");

			stringBuilder.append("Consumer thread [TID=" + this.iTID + "] starts executing.\n");

			for (int i = 0; i < StackManager.iThreadSteps; i++) {
				full.P();
				mutex.P();
				try {
					copy = stack.pop();
				} catch (CharStackEmptyException e) {
					e.printStackTrace();
				}

				System.out.println("Consumer thread [TID=" + this.iTID + "] pops character =" + this.copy);

				stringBuilder.append("Consumer thread [TID=" + this.iTID + "] pops character =" + this.copy + "\n");

				mutex.V();
				empty.Signal();
			}
			System.out.println("Consumer thread [TID=" + this.iTID + "] terminates.");

			stringBuilder.append("Consumer thread [TID=" + this.iTID + "] terminates.\n");
		}
	}

	static class Producer extends BaseThread {
		private char block;

		public void run() {
			System.out.println("Producer thread [TID=" + this.iTID + "] starts executing.");

			stringBuilder.append("Producer thread [TID=" + this.iTID + "] starts executing.\n");

			for (int i = 0; i < StackManager.iThreadSteps; i++) {
				empty.P();
				mutex.P();
				try {

					if (stack.getTop() == -1) {
						block = 'a';
						stack.push('a');
					} else {
						block = (char) (stack.pick() + 1);
						stack.push(block);
					}

				} catch (CharStackEmptyException e) { // check this
					e.printStackTrace();
				} catch (CharStackFullException e) {
					e.printStackTrace();
				}

				System.out.println("Producer thread [TID=" + this.iTID + "] pushes character =" + this.block);

				stringBuilder.append("Producer thread [TID=" + this.iTID + "] pushes character =" + this.block + "\n");

				mutex.V();
				full.V();
			}
			System.out.println("Producer thread [TID=" + this.iTID + "] terminates.");

			stringBuilder.append("Producer thread [TID=" + this.iTID + "] terminates.\n");

		}
	}

	static class CharStackProber extends BaseThread {
		public void run() {
			System.out.println("CharStackProber thread [TID=" + this.iTID + "] starts executing.");

			stringBuilder.append("CharStackProber thread [TID=" + this.iTID + "] starts executing.\n");

			for (int i = 0; i < 2 * StackManager.iThreadSteps; i++) {
				mutex.P();
				String stackString = "Stack S= (";
				for (int j = 0; j < stack.getSize(); j++) {
					stackString += "[";
					if (j <= stack.getTop()) {
						try {
							stackString += stack.getAt(j);
						} catch (CharStackInvalidAceessException e) {
							e.printStackTrace();
						}
					} else {
						stackString += "$";
					}
					if (j < stack.getSize() - 1) {
						stackString += "],";
					} else {
						stackString += "])";
					}
				}
				System.out.println(stackString);

				stringBuilder.append(stackString + "\n");

				mutex.V();
			}
		}
	}
}
