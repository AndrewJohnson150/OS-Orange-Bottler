/**
 * This class was written last year in CS120 by myself (some of it was probably
 * Ted Wendt but I don't recall) and was modified to work for Oranges and
 * implement synchronization and blocking
 * 
 * @author Andrew Johnson
 * @author Theodore Wendt
 */
public class BlockedQ {

	private Node frontNode;
	private Node backNode;

	/**
	 * constructor. Sets back and front node to null.
	 */
	public BlockedQ() {
		frontNode = null;
		backNode = null;
	}

	/**
	 * this puts an Orange into the queue. If the size of the queue is 1, it
	 * will wake up threads who are waiting for an orange to be enqueued. This
	 * method is synchronized to prevent race conditions.
	 * 
	 * @param newEntry
	 *            An orange which should be enqueued.
	 * @see #enqueue(Orange)
	 */
	public synchronized void enqueue(Orange newEntry) {
		Node newNode = new Node(newEntry, null);
		if (isEmpty())
			frontNode = newNode;
		else
			backNode.setNextNode(newNode);
		backNode = newNode;

		// only when size is 1 because if the size is two there isn't anything
		// waiting.
		if (getSize() == 1) {
			notifyAll();
		}
	}

	/**
	 * This takes an orange from the queue. If the queue is empty, the thread
	 * attempting to access this will block (wait), and be notified when the
	 * first orange is enqueued.
	 * 
	 * @return An orange from the queue.
	 * @see #enqueue(Orange)
	 */
	public synchronized Orange dequeue() {
		try {
			// if this was an if statement, all threads would wake up but only
			// one would be able
			// to take an orange. That would allow other threads to wake up
			// without actually
			// being able to get an orange. To avoid that, we use while.
			while (isEmpty())
				wait();
		} catch (InterruptedException ignore) {
			ignore.printStackTrace();
		}

		Orange front = getFront();
		frontNode.setData(null);
		frontNode = frontNode.getNextNode();
		if (frontNode == null)
			backNode = null;
		return front;
	}

	/**
	 * helper method for dequeue() method
	 * 
	 * @return the data that the front node points to
	 * @see #dequeue()
	 */
	public Orange getFront() {
		if (isEmpty())
			return null;
		else
			return frontNode.getData();
	}

	/**
	 * This method simply checks if the front node is null. If so, the queue is
	 * empty.
	 * 
	 * @return boolean which is true if queue is empty
	 */
	public synchronized boolean isEmpty() {
		return frontNode == null;
	}

	/**
	 * loops through the nodes linked to the front node and counts the total
	 * number of nodes.
	 * 
	 * @return number of Oranges in the queue
	 */
	public int getSize() {
		Node currentNode = frontNode;
		int counter = 0;
		while (currentNode != null) {
			counter++;
			currentNode = currentNode.getNextNode();
		}
		return counter;
	}

	/**
	 * This is a Node class that is utilized for the queue class. It essentially
	 * is just two pointers, one to the next node, and one to relevant data. It
	 * then has quite self explanatory method names that I will not go into
	 * detail about, because they are all simple get, set, and constructor
	 * methods.
	 * 
	 * @author Andrew Johnson, Theodore Wendt
	 */
	private class Node {
		private Orange data;
		private Node next;

		public Node() {
			this(null);
		}

		public Node(Orange data) {
			this.data = data;
			this.next = null;
		}

		public Node(Orange data, Node next) {
			this.data = data;
			this.next = next;
		}

		public Orange getData() {
			return data;
		}

		public void setData(Orange newData) {
			data = newData;
		}

		public Node getNextNode() {
			return next;
		}

		public void setNextNode(Node newNext) {
			next = newNext;
		}
	}

}
