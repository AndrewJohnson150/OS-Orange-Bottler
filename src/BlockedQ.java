/**
 * This class was written last year in CS120 by myself 
 * (some of it was probably Ted Wendt but I don't recall)
 * and was modified to work for Oranges and implement synchronization and blocking
 * 
 * @author Andrew Johnson and Theodore Wendt
*/
public class BlockedQ{ 

		
	private Node frontNode;
	private Node backNode;
	
	/**
	 * constructor. Sets back and front node to null.
	 */
	public BlockedQ()
	{
		frontNode = null;
		backNode = null;
	}
	
	/**
	 * this puts an Orange into the queue. If the size of the queue is 1, it will wake up functions
	 * who are waiting for an orange to be enqueued. This method is synchronized to prevent race conditions.
	 * @param newEntry An orange which should be enqueued.
	 * @see #enqueue(Orange)
	 */
	public synchronized void enqueue(Orange newEntry) 
	{
		Node newNode = new Node(newEntry,null);
		if(isEmpty())
			frontNode = newNode;
		else
			backNode.setNextNode(newNode);
		backNode = newNode;
		
		if(getSize() == 1) {
			notifyAll();
		}
	}

	/**
	 * This takes an orange from the queue. If the queue is empty, the thread attempting to access this
	 * will wait, and be notified when the first orange is enqueued.
	 * @return An orange from the queue.
	 * @see #enqueue(Orange)
	 */
	public synchronized Orange dequeue() 
	{
		try {
			while(isEmpty())
				wait();
		} catch (InterruptedException ignore) {ignore.printStackTrace();}
		
		Orange front = getFront();
		frontNode.setData(null);
		frontNode = frontNode.getNextNode();
		if(frontNode == null)
			backNode = null;
		return front;
	}

	/**
	 * helper method for dequeue() method
	 * @return the data that the front node points to
	 * @see #dequeue()
	 */
	public Orange getFront() 
	{
		if(isEmpty())
			return null;
		else
			return frontNode.getData();
	}

	/**
	 * This method simply checks if the front node is null. If so, the queue is empty.
	 * @return boolean which is true if queue is empty
	 */
	public synchronized boolean isEmpty() 
	{
		return frontNode == null;
	}


	/**
	 * 
	 * @return number of Oranges in the queue
	 */
	public int getSize()
	{
		Node currentNode = frontNode;
		int counter = 0;
		while(currentNode != null)
		{
			counter++;
			currentNode = currentNode.getNextNode();
		}
		return counter;
	}

	/**
	 * This is a Node class that is utilized for the queue class. It essentially is just two pointers,
	 * one to the next node, and one to relevant data.
	 * 
	 * @author Andrew Johnson, Theodore Wendt 
	 */
	private class Node
	{
		private Orange data;
		private Node next;
		
		public Node()
		{
			this(null);
		}
		
		public Node(Orange data)
		{
			this.data = data;
			this.next = null;
		}
		
		public Node(Orange data, Node next)
		{
			this.data = data;
			this.next = next;
		}
		
		public Orange getData()
		{
			return data;
		}
		
		public void setData(Orange newData)
		{
			data = newData;
		}
		
		public Node getNextNode()
		{
			return next;
		}
		
		public void setNextNode(Node newNext)
		{
			next = newNext;
		}
	}
	

}
