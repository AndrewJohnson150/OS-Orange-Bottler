/**
 * This class is represents a virtual worker. It is meant primarily to get an
 * orange, implement work, and hand it off.
 * 
 * @author Andrew Johnson
 *
 */
public class Worker implements Runnable {
	private BlockedQ get;
	private BlockedQ give;
	private Orange currentOrange;
	private final Thread thread;
	private boolean iAmFirst;
	private volatile boolean timeToWork;
	private int orangesProvided;
	
	/**
	 * This is the constructor.
	 * 
	 * @param GetFrom
	 *            Queue that the worker gets oranges from
	 * @param GiveTo
	 *            Queue that the worker puts oranges in
	 * @param num
	 *            number of the worker (Mostly for debugging purpose)
	 */
	public Worker(BlockedQ getFrom, BlockedQ giveTo, int num) {
		get = getFrom;
		give = giveTo;
		iAmFirst = false;
		thread = new Thread(this, "Worker " + num);
	}
	
	/**
	 * This is an alternative constructor for the first worker who will function
	 * differently than the others. Most notably, this constructor takes two
	 * less parameters and sets the boolean iAmFirst to true.
	 *
	 * @param giveTo
	 *            Queue that the worker puts oranges in
	 */
	public Worker(BlockedQ giveTo) {
		orangesProvided = 0;
		get = null;
		give = giveTo;
		iAmFirst = true;
		thread = new Thread(this, "First Worker");
	}
	
	/**
	 * @return number of oranges provided
	 */
	public int getOrangesProvided() {
		return orangesProvided;
	}

	/**
	 * In this run() method, the worker will work while it is timeToWork. If it
	 * is the first worker, then it will fetch oranges and give them to the next
	 * worker. If it is not the first, it will get an orange, do work on it,
	 * and give it to the next.
	 */
	@Override
	public synchronized void run() {
		while (timeToWork) {
			if (iAmFirst)
				fetchAnOrange();
			else {
				getWork();
				doWork();
			}
			giveWork();
		}
	}

	/**
	 * this method allows the workers to start working. We could run these commands in the constructor,
	 * but that would mean that the workers are starting when they are created, and not when the plant
	 * starts running
	 */
	public void startWork() {
		timeToWork = true; 
		thread.start();
	}
	
	/**
	 * joins thread
	 */
	public void waitToStop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			System.err.println(thread.getName() + " stop malfunction");
		}
	}
	
	/**
	 * This is a method for the first worker to call, it creates a new orange
	 * and increments orangesProvided
	 * 
	 * @see Orange
	 * @see #Worker(BlockedQ)
	 */
	private void fetchAnOrange() {
		currentOrange = new Orange();
		orangesProvided++;
	}

	/**
	 * This method dequeues from the synchronized queue that was passed to it in
	 * the constructor.
	 * 
	 * @see #Worker(BlockedQ, BlockedQ, int)
	 */
	public void getWork() {
		currentOrange = get.dequeue();
	}

	/**
	 * This method does work on an orange
	 * 
	 * @see Orange
	 */
	private void doWork() {
		currentOrange.runProcess();
	}

	/**
	 * This method enqueues to the synchronized queue that was passed to it in
	 * the constructor.
	 * 
	 * @see #Worker(BlockedQ)
	 * @see #Worker(BlockedQ, BlockedQ, int)
	 */
	private void giveWork() {
		give.enqueue(currentOrange);
		currentOrange = null;
	}

	/**
	 * This method is called when it is time to stop. This could be a static implementation of timeToWork
	 * so that only one worker would need to have it called, but that would restrict further development
	 * like if we wanted to stop the first worker before the others
	 */
	public void stopWork() {
		timeToWork = false;
	}

}
