package charStackExceptions;

public class SemaphoreNegativeValueException extends Exception {
	public SemaphoreNegativeValueException() {
		super("Invalid (Negative) Semaphore Value");
	}

}
