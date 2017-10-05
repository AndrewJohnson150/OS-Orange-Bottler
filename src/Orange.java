/**
 * This is the class which represents a virtual Orange
 * 
 * @author Nate Williams
 * @author Andrew Johnson
 */
public class Orange {
	/**
	 * this enum contains all the states that the orange should be in.
	 */
	public enum State {
		Fetched(15), Peeled(38), Squeezed(40), Bottled(45), Processed(1);

		private static final int finalIndex = State.values().length - 1;

		final int timeToComplete;

		State(int timeToComplete) {
			this.timeToComplete = timeToComplete;
		}

		State getNext() {
			int currIndex = this.ordinal();
			if (currIndex >= finalIndex) {
				throw new IllegalStateException("Already at final state");
			}
			return State.values()[currIndex + 1];
		}
	}

	private State state;

	/**
	 * important note about this constructor is that it calls doWork. This means
	 * there must be special implementation in worker for the first worker.
	 * 
	 * @see #doWork()
	 * @see Worker
	 */
	public Orange() {
		state = State.Fetched;
		doWork();
	}

	public State getState() {
		return state;
	}

	/**
	 * this method checks if the orange is already processed, then sets the
	 * state to the next state and calls the doWork() method
	 * 
	 * @see #doWork()
	 */
	public void runProcess() {
		// Don't attempt to process an already completed orange
		if (state == State.Processed) {
			throw new IllegalStateException("This orange has already been processed");
		}
		state = state.getNext();
		doWork();
	}

	/**
	 * This is a simple method that just waits for the amount of time it takes
	 * to process the orange as designated by state.timeToComplete
	 * 
	 */
	private void doWork() {
		// Sleep for the amount of time necessary to do the work
		try {
			Thread.sleep(state.timeToComplete);
		} catch (InterruptedException e) {
			System.err.println("Incomplete orange processing, juice may be bad");
		}
	}
}