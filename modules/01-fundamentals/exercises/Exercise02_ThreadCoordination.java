/**
 * Exercise 02: Thread Coordination
 * 
 * TASK:
 * Simulate a simple workflow where tasks must complete in a specific order:
 * 1. DownloadThread - simulates downloading data (takes 2 seconds)
 * 2. ProcessThread - processes the data (takes 1 second) - MUST wait for download
 * 3. SaveThread - saves the result (takes 0.5 seconds) - MUST wait for processing
 * 
 * The main thread should print the total time taken when all tasks complete.
 * 
 * EXPECTED OUTPUT:
 * Starting workflow...
 * [Download] Starting download...
 * [Download] Download complete!
 * [Process] Starting processing...
 * [Process] Processing complete!
 * [Save] Starting save...
 * [Save] Save complete!
 * Workflow completed in ~3500ms
 * 
 * HINTS:
 * 1. Use join() to make one thread wait for another
 * 2. Pass the thread to wait for as a constructor parameter
 * 3. Use System.currentTimeMillis() to measure time
 * 
 * BONUS: Add error handling - if download fails, skip processing and saving.
 */
public class Exercise02_ThreadCoordination {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting workflow...\n");
        long startTime = System.currentTimeMillis();

        // TODO: Create three threads: downloadThread, processThread, saveThread
        // - processThread must wait for downloadThread to complete before starting
        // - saveThread must wait for processThread to complete before starting

        // YOUR CODE HERE

        long endTime = System.currentTimeMillis();
        System.out.println("\nWorkflow completed in " + (endTime - startTime) + "ms");
    }

    // TODO: Create a DownloadTask class that simulates downloading (2 seconds)

    // TODO: Create a ProcessTask class that:
    // - Takes a Thread to wait for in constructor
    // - Joins on that thread before doing its work
    // - Simulates processing (1 second)

    // TODO: Create a SaveTask class similar to ProcessTask (0.5 seconds)
}

/*
 * LEARNING GOALS:
 * - Understand thread dependencies
 * - Use join() for sequential coordination
 * - Measure and understand concurrent vs sequential execution time
 * 
 * QUESTION TO PONDER:
 * Why does this take ~3.5 seconds even though threads run "concurrently"?
 * How could you redesign this to be truly parallel?
 * 
 * When you're done, compare with: solutions/Exercise02_Solution.java
 */
