/**
 * Example 02: Thread Lifecycle and States
 * 
 * This example demonstrates all thread states:
 * - NEW: Thread created but not started
 * - RUNNABLE: Thread is running or ready to run
 * - BLOCKED: Thread waiting to acquire a lock
 * - WAITING: Thread waiting indefinitely (wait(), join())
 * - TIMED_WAITING: Thread waiting for specified time (sleep(), wait(timeout))
 * - TERMINATED: Thread has finished execution
 */
public class Example02_ThreadLifecycle {

    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Thread Lifecycle Demo ===\n");

        demonstrateNewState();
        demonstrateRunnableState();
        demonstrateTimedWaitingState();
        demonstrateWaitingState();
        demonstrateBlockedState();
        demonstrateTerminatedState();
    }

    /**
     * NEW State: Thread is created but start() not called yet
     */
    private static void demonstrateNewState() {
        System.out.println("--- NEW State ---");
        Thread thread = new Thread(() -> {});
        System.out.println("Thread created, state: " + thread.getState());
        // Output: NEW
        System.out.println();
    }

    /**
     * RUNNABLE State: Thread is executing or ready to execute
     */
    private static void demonstrateRunnableState() throws InterruptedException {
        System.out.println("--- RUNNABLE State ---");
        Thread thread = new Thread(() -> {
            // Busy loop to stay in RUNNABLE state
            long count = 0;
            while (count < 1_000_000_000L) {
                count++;
            }
        });

        thread.start();
        Thread.sleep(10); // Give thread time to start
        System.out.println("Thread running, state: " + thread.getState());
        // Output: RUNNABLE
        thread.join();
        System.out.println();
    }

    /**
     * TIMED_WAITING State: Thread is sleeping or waiting with timeout
     */
    private static void demonstrateTimedWaitingState() throws InterruptedException {
        System.out.println("--- TIMED_WAITING State ---");
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000); // Sleep for 2 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        thread.start();
        Thread.sleep(100); // Let thread enter sleep
        System.out.println("Thread sleeping, state: " + thread.getState());
        // Output: TIMED_WAITING
        thread.join();
        System.out.println();
    }

    /**
     * WAITING State: Thread waiting for another thread (join without timeout)
     */
    private static void demonstrateWaitingState() throws InterruptedException {
        System.out.println("--- WAITING State ---");
        
        Thread longRunning = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "LongRunning");

        Thread waiter = new Thread(() -> {
            try {
                longRunning.join(); // Wait indefinitely for longRunning
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Waiter");

        longRunning.start();
        waiter.start();
        Thread.sleep(100); // Let waiter enter join()
        
        System.out.println("Waiter thread state: " + waiter.getState());
        // Output: WAITING
        
        longRunning.join();
        waiter.join();
        System.out.println();
    }

    /**
     * BLOCKED State: Thread waiting to acquire a monitor lock
     */
    private static void demonstrateBlockedState() throws InterruptedException {
        System.out.println("--- BLOCKED State ---");

        Thread holder = new Thread(() -> {
            synchronized (lock) {
                try {
                    Thread.sleep(2000); // Hold lock for 2 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "LockHolder");

        Thread blocked = new Thread(() -> {
            synchronized (lock) {
                // Will be blocked until holder releases the lock
                System.out.println("Blocked thread acquired lock!");
            }
        }, "BlockedThread");

        holder.start();
        Thread.sleep(100); // Let holder acquire lock
        blocked.start();
        Thread.sleep(100); // Let blocked thread attempt to acquire lock

        System.out.println("Blocked thread state: " + blocked.getState());
        // Output: BLOCKED

        holder.join();
        blocked.join();
        System.out.println();
    }

    /**
     * TERMINATED State: Thread has completed execution
     */
    private static void demonstrateTerminatedState() throws InterruptedException {
        System.out.println("--- TERMINATED State ---");
        Thread thread = new Thread(() -> {
            System.out.println("Thread executing...");
        });

        thread.start();
        thread.join(); // Wait for completion
        System.out.println("Thread finished, state: " + thread.getState());
        // Output: TERMINATED
        System.out.println();
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Thread Lifecycle Demo ===
 * 
 * --- NEW State ---
 * Thread created, state: NEW
 * 
 * --- RUNNABLE State ---
 * Thread running, state: RUNNABLE
 * 
 * --- TIMED_WAITING State ---
 * Thread sleeping, state: TIMED_WAITING
 * 
 * --- WAITING State ---
 * Waiter thread state: WAITING
 * 
 * --- BLOCKED State ---
 * Blocked thread state: BLOCKED
 * Blocked thread acquired lock!
 * 
 * --- TERMINATED State ---
 * Thread executing...
 * Thread finished, state: TERMINATED
 * 
 * KEY INSIGHT: Understanding thread states helps debug concurrency issues!
 */
