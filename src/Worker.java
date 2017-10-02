

public class Worker implements Runnable {
	private BlockedQ get;
	private BlockedQ give;
	private Orange currentOrange;
	private final Thread thread;
	private boolean iAmFirst;
	private static volatile boolean timeToWork;
	private int orangesProvided;
	
	public Worker (BlockedQ getFrom, BlockedQ giveTo, int num) {
		get = getFrom;
		give = giveTo;
		iAmFirst = false;
		thread = new Thread(this, "Worker " + num);
		thread.start();
	}
	
	public Worker (BlockedQ giveTo) {
		orangesProvided = 0;
		get = null;
		give = giveTo;
		iAmFirst = true;
		timeToWork =true; // only need the first worker to change this cause its static
		thread = new Thread(this, "First Worker");
		thread.start();
	}
	
	private void fetchAnOrange() {
		currentOrange = new Orange();
		orangesProvided++;
	}
	
	public void getWork() {
		currentOrange = get.dequeue();
	}
	
	public void doWork() {
		currentOrange.runProcess();
	}
	
	public void giveWork() {
		give.enqueue(currentOrange);
		currentOrange = null;
	}
	
	public void stopWork() {
		timeToWork = false;
	}
	
	public int getOrangesProvided() {
		return orangesProvided;
	}

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
		//overtime, we don't like waste so finish everything that's in your q before you leave.
		if (!iAmFirst) { 
			while (!get.isEmpty()) { // I feel like this shouldn't work since they all have diff. times
				getWork();
				doWork();
				giveWork();
			}
		}
	}
}
