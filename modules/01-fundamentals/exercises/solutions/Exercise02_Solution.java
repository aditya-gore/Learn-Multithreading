/**
 * Solution for Exercise 02: Thread Coordination
 */
public class Exercise02_Solution {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting workflow...\n");
        long startTime = System.currentTimeMillis();

        // Create threads with dependencies
        Thread downloadThread = new Thread(new DownloadTask(), "Download");
        Thread processThread = new Thread(new ProcessTask(downloadThread), "Process");
        Thread saveThread = new Thread(new SaveTask(processThread), "Save");

        // Start all threads
        downloadThread.start();
        processThread.start();  // Will wait for download internally
        saveThread.start();     // Will wait for process internally

        // Wait for final thread to complete
        saveThread.join();

        long endTime = System.currentTimeMillis();
        System.out.println("\nWorkflow completed in " + (endTime - startTime) + "ms");
    }
}

class DownloadTask implements Runnable {
    @Override
    public void run() {
        System.out.println("[Download] Starting download...");
        try {
            Thread.sleep(2000); // Simulate 2 second download
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[Download] Interrupted!");
            return;
        }
        System.out.println("[Download] Download complete!");
    }
}

class ProcessTask implements Runnable {
    private final Thread prerequisite;

    public ProcessTask(Thread prerequisite) {
        this.prerequisite = prerequisite;
    }

    @Override
    public void run() {
        try {
            // Wait for download to complete first
            prerequisite.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        System.out.println("[Process] Starting processing...");
        try {
            Thread.sleep(1000); // Simulate 1 second processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[Process] Interrupted!");
            return;
        }
        System.out.println("[Process] Processing complete!");
    }
}

class SaveTask implements Runnable {
    private final Thread prerequisite;

    public SaveTask(Thread prerequisite) {
        this.prerequisite = prerequisite;
    }

    @Override
    public void run() {
        try {
            // Wait for processing to complete first
            prerequisite.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        System.out.println("[Save] Starting save...");
        try {
            Thread.sleep(500); // Simulate 0.5 second save
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[Save] Interrupted!");
            return;
        }
        System.out.println("[Save] Save complete!");
    }
}

/*
 * KEY LEARNINGS:
 * 
 * 1. Sequential Dependencies: Even though we start all threads immediately,
 *    they execute sequentially because of join() calls.
 * 
 * 2. Total time ~3.5s: 2s (download) + 1s (process) + 0.5s (save)
 *    This is NOT parallel - each task waits for the previous one.
 * 
 * 3. Why this pattern? Sometimes tasks have true dependencies.
 *    You can't process data before downloading it!
 * 
 * ALTERNATIVE DESIGN FOR PARALLELISM:
 * If tasks were independent, you could run them in parallel:
 * 
 *   downloadThread.start();
 *   processThread.start();  // Different data
 *   saveThread.start();     // Different data
 *   
 *   downloadThread.join();
 *   processThread.join();
 *   saveThread.join();
 *   
 * Total time would be ~2s (max of all tasks).
 * 
 * BONUS SOLUTION - Error Handling:
 * See Exercise02_BonusSolution.java for a version with error handling.
 */
