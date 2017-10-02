//This code is written mostly by Nate Williams but was modified by Andrew Johnson to utilize
//Multiple workers per plant.
public class Plant implements Runnable {
    // How long do we want to run the juice processing
    public static final long PROCESSING_TIME = 5 * 1000;
    
    private static final int NUM_PLANTS = 2;
    
    private int orangesInLine;
    
    private Worker[] workers = new Worker[5]; 
    private Worker[] extraWorkers = new Worker[5];
    private BlockedQ[] buffers = new BlockedQ[5];
    
    public static void main(String[] args) {
        // Startup the plants
        Plant[] plants = new Plant[NUM_PLANTS];
        for (int i = 0; i < NUM_PLANTS; i++) {
           plants[i] = new Plant();
           plants[i].startPlant();
        }

        // Give the plants time to do work
        delay(PROCESSING_TIME, "Plant malfunction");

        // Stop the plant, and wait for it to shutdown
        for (Plant p : plants) {
           p.stopPlant();
        }
        for (Plant p : plants) {
           p.waitToStop();
        }

        // Summarize the results
        int totalProvided = 0;
        int totalProcessed = 0;
        int totalBottles = 0;
        int totalWasted = 0;
        int totalInLine = 0;
        for (Plant p : plants) {
            totalProvided += p.getProvidedOranges();
            totalProcessed += p.getProcessedOranges();
            totalBottles += p.getBottles();
            totalWasted += p.getWaste();
            totalInLine += p.orangesStillInLine();
        }
       System.out.println("Total provided/processed = " + totalProvided + "/" + totalProcessed);
       System.out.println("Created " + totalBottles +
                           ", wasted " + (totalWasted+totalInLine) + " oranges");

    }

    private static void delay(long time, String errMsg) {
        long sleepTime = Math.max(1, time);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println(errMsg);
        }
    }

   public final int ORANGES_PER_BOTTLE = 4;

    private final Thread thread;
    private int orangesProvided;
    private volatile boolean timeToWork;

    Plant() {
    	orangesInLine = 0;
    	
    	System.out.println("Making conveyer belts");
    	for (int i = 0; i<5; i++) { 
    		buffers[i] = new BlockedQ();
    	}
    	System.out.println("Hiring Workers");
    	
    	workers[0] = new Worker(buffers[0]);
    	
    	for (int i = 1; i<5; i++) {
    		workers[i] = new Worker(buffers[i-1],buffers[i], i);
    	}
    	
        orangesProvided = 0;
        thread = new Thread(this, "Plant");
        
    }

    public void startPlant() {
        timeToWork = true;
        thread.start();
    }

    public void stopPlant() {
        timeToWork = false;
        workers[0].stopWork(); //only need one call since its a static variable 
    }

    public void waitToStop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println(thread.getName() + " stop malfunction");
        }
    }

    public void run() {
        System.out.print(Thread.currentThread().getName() + " Processing oranges");
        while (timeToWork) {
        	System.out.print(".");
        	try {
        		Thread.sleep(500); 
        	}
        	catch (Exception ignored) {}
        }
        System.out.print("\n");
    }


    public int getProvidedOranges() {
        return workers[0].getOrangesProvided();
    }

    public int getProcessedOranges() {
        return buffers[4].getSize();
    }

    
    //returns the amount of full bottles.
    public int getBottles() {
        return buffers[4].getSize() / ORANGES_PER_BOTTLE;
    }
    
    //
    public int getWaste() {
        return buffers[4].getSize() % ORANGES_PER_BOTTLE;
    }
    
    public int orangesStillInLine() {
		int sum = 0;
		for (int i = 0; i<buffers.length-1; i++){
			sum+=buffers[i].getSize();
		}
    	return sum;
    }
}