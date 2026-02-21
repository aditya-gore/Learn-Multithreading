/**
 * Example 03: Thread join() and sleep() Methods
 * 
 * This example demonstrates:
 * - sleep(): Pauses current thread for specified time
 * - join(): Waits for another thread to complete
 * - join(timeout): Waits with a maximum time limit
 * 
 * These are fundamental methods for thread coordination.
 */
public class Example03_JoinAndSleep {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Join and Sleep Demo ===\n");

        demonstrateSleep();
        demonstrateJoin();
        demonstrateJoinWithTimeout();
        demonstrateMultipleJoins();
    }

    /**
     * sleep() pauses the current thread for a specified duration.
     * The thread does NOT release any locks it holds during sleep.
     */
    private static void demonstrateSleep() throws InterruptedException {
        System.out.println("--- sleep() Demo ---");
        System.out.println("Starting countdown...");

        for (int i = 3; i >= 1; i--) {
            System.out.println(i + "...");
            Thread.sleep(1000); // Sleep for 1 second
        }
        System.out.println("Go!\n");
    }

    /**
     * join() makes the current thread wait until the target thread completes.
     * Without join(), main thread would continue immediately.
     */
    private static void demonstrateJoin() throws InterruptedException {
        System.out.println("--- join() Demo ---");

        Thread worker = new Thread(() -> {
            System.out.println("Worker: Starting task...");
            try {
                Thread.sleep(2000); // Simulate work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Worker: Task completed!");
        }, "Worker");

        System.out.println("Main: Starting worker thread");
        long startTime = System.currentTimeMillis();
        
        worker.start();
        
        System.out.println("Main: Waiting for worker to finish...");
        worker.join(); // Block until worker completes
        
        long endTime = System.currentTimeMillis();
        System.out.println("Main: Worker finished! Waited " + (endTime - startTime) + "ms\n");
    }

    /**
     * join(timeout) waits for at most the specified time.
     * If the thread doesn't finish in time, we continue anyway.
     */
    private static void demonstrateJoinWithTimeout() throws InterruptedException {
        System.out.println("--- join(timeout) Demo ---");

        Thread slowWorker = new Thread(() -> {
            System.out.println("SlowWorker: Starting long task...");
            try {
                Thread.sleep(5000); // 5 second task
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("SlowWorker: Finally done!");
        }, "SlowWorker");

        slowWorker.start();
        
        System.out.println("Main: Waiting max 2 seconds for slow worker...");
        slowWorker.join(2000); // Wait at most 2 seconds
        
        if (slowWorker.isAlive()) {
            System.out.println("Main: Timeout! Worker still running, moving on...");
        } else {
            System.out.println("Main: Worker finished in time!");
        }
        
        // Wait for thread to actually finish for clean demo
        slowWorker.join();
        System.out.println();
    }

    /**
     * Multiple threads can be joined to wait for all of them.
     * This is a common pattern for parallel processing.
     */
    private static void demonstrateMultipleJoins() throws InterruptedException {
        System.out.println("--- Multiple Joins Demo ---");

        int numWorkers = 3;
        Thread[] workers = new Thread[numWorkers];

        // Create and start all workers
        for (int i = 0; i < numWorkers; i++) {
            final int workerId = i + 1;
            workers[i] = new Thread(() -> {
                System.out.println("Worker-" + workerId + ": Started");
                try {
                    // Each worker takes different time
                    Thread.sleep(workerId * 500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Worker-" + workerId + ": Completed");
            }, "Worker-" + workerId);
            workers[i].start();
        }

        System.out.println("Main: All workers started, waiting for completion...\n");

        // Join all workers - wait for ALL to complete
        for (Thread worker : workers) {
            worker.join();
        }

        System.out.println("\nMain: All workers have completed!");
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Join and Sleep Demo ===
 * 
 * --- sleep() Demo ---
 * Starting countdown...
 * 3...
 * 2...
 * 1...
 * Go!
 * 
 * --- join() Demo ---
 * Main: Starting worker thread
 * Main: Waiting for worker to finish...
 * Worker: Starting task...
 * Worker: Task completed!
 * Main: Worker finished! Waited ~2000ms
 * 
 * --- join(timeout) Demo ---
 * Main: Waiting max 2 seconds for slow worker...
 * SlowWorker: Starting long task...
 * Main: Timeout! Worker still running, moving on...
 * SlowWorker: Finally done!
 * 
 * --- Multiple Joins Demo ---
 * Main: All workers started, waiting for completion...
 * Worker-1: Started
 * Worker-2: Started
 * Worker-3: Started
 * Worker-1: Completed
 * Worker-2: Completed
 * Worker-3: Completed
 * Main: All workers have completed!
 * 
 * KEY INSIGHT: join() is essential for coordinating thread completion order!
 */
