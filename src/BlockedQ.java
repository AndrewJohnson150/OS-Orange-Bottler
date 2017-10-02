package multiThreaded;

public class BlockedQ{ 
	/*
	 * This class was written last year in CS120 by myself 
	 * (some of it was probably Ted Wendt but I don't recall)
	 * and was modified to work for Oranges and implement synchronization and blocking
	*/

		
	private Node frontNode;
	private Node backNode;
	
	public BlockedQ()
	{
		frontNode = null;
		backNode = null;
	}
	
	
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

	
	public Orange getFront() 
	{
		if(isEmpty())
			return null;
		else
			return frontNode.getData();
	}

	
	public synchronized boolean isEmpty() 
	{
		return frontNode == null;
	}

	
	public void clear() 
	{
		while(!isEmpty())
			dequeue();
	}

		
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
