<<<<<<<< HEAD:src/charStackManagement/BaseThread.java
package charStackManagement;
========
package chatStackManagement;
>>>>>>>> 51944b7012ba84aaecff61740a136c9f6984b098:src/chatStackManagement/BaseThread.java

class BaseThread extends Thread {
	/*
	 * Data members
	 */
	public static int iNextTID = 1; // Preserves value across all instances
	protected int iTID;

	public BaseThread() {
		this.iTID = iNextTID;
		iNextTID++;
	}

	public BaseThread(int piTID) {
		this.iTID = piTID;
	}
}
