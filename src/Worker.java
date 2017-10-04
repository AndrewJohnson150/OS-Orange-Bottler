//This code is written by Andrew Johnson, and is meant to get an orange, implement work, and hand it off.
public class Worker implements Runnable {
	private BlockedQ get;
	private BlockedQ give;
	private Orange currentOrange;
	private final Thread thread;
	private boolean iAmFirst;
	private static volatile boolean timeToWork;
	private int orangesProvided;
	
	/**
	 * This is the constructor.
	 * 
	 * @param GetFrom Queue that the worker gets oranges from
	 * @param GiveTo Queue that the worker puts oranges in
	 * @param num number of the worker (Mostly for debugging purpose)
	 */
	public Worker (BlockedQ getFrom, BlockedQ giveTo, int num) {
		get = getFrom;
		give = giveTo;
		iAmFirst = false;
		thread = new Thread(this, "Worker " + num);
		thread.start();
	}
	/**
	 * This is an alternative constructor for the first worker who 
	 * will function differently than the others.
	 * Most notably, this constructor takes two less parameters and sets the boolean iAmFirst to true.
	 *
	 * @param giveTo Queue that the worker puts oranges in
	 */
	public Worker (BlockedQ giveTo) {
		orangesProvided = 0;
		get = null;
		give = giveTo;
		iAmFirst = true;
		timeToWork =true; // only need the first worker to change this because its static
		thread = new Thread(this, "First Worker");
		thread.start();
	}
	
	/**
	 * This is a method for the first worker to call, it creates a new 
	 * orange and increments orangesProvided
	 * 
	 * @see Orange
	 */
	private void fetchAnOrange() {
		currentOrange = new Orange();
		orangesProvided++;
	}
	
	/**
	 * This method dequeues from the synchronized queue that was passed to it in the constructor.
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
	public void doWork() {
		currentOrange.runProcess();
	}
	
	/**
	 * This method enqueues to the synchronized queue that was passed to it in the constructor.
	 * 
	 * @see #Worker(BlockedQ)
	 * @see #Worker(BlockedQ, BlockedQ, int)
	 */
	public void giveWork() {
		give.enqueue(currentOrange);
		currentOrange = null;
	}
	
	/**
	 * This signal is called for the first worker when it is time to stop. Because timeToWork is static,
	 * only the first worker needs to have this called.
	 */
	public void stopWork() {
		timeToWork = false;
	}
	
	/**
	 * @return number of oranges provided
	 */
	public int getOrangesProvided() {
		return orangesProvided;
	}

	/**
	 * In this run() method, the worker will work while it is timeToWork. If it is the first worker,
	 * then it will fetch oranges and give them to the next worker. If it is not the first,
	 * it will get an orange , do work on it, and give it to the next.
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
}
