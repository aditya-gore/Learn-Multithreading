/**
 * Example 05: Thread Interruption
 * 
 * This example demonstrates:
 * - How to interrupt a thread using interrupt()
 * - How threads should handle interruption
 * - The difference between interrupt flag and InterruptedException
 * 
 * Interruption is the cooperative mechanism for stopping threads in Java.
 * It's a REQUEST to stop - the thread must check and respond.
 */
public class Example05_ThreadInterrupt {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Thread Interruption Demo ===\n");

        demonstrateInterruptFlag();
        demonstrateInterruptedException();
        demonstrateProperInterruptHandling();
        demonstrateIgnoringInterrupt(); // Bad practice!
    }

    /**
     * Threads should check the interrupt flag periodically in loops.
     */
    private static void demonstrateInterruptFlag() throws InterruptedException {
        System.out.println("--- Checking Interrupt Flag ---\n");

        Thread worker = new Thread(() -> {
            int count = 0;
            // GOOD: Check interrupt flag in loop condition
            while (!Thread.currentThread().isInterrupted()) {
                count++;
                if (count % 1000000 == 0) {
                    System.out.println("  [Worker] Count: " + count);
                }
            }
            System.out.println("  [Worker] Interrupted! Final count: " + count);
        }, "Worker");

        worker.start();
        
        Thread.sleep(100); // Let worker run for a bit
        System.out.println("[Main] Interrupting worker...");
        worker.interrupt();
        
        worker.join();
        System.out.println("[Main] Worker stopped cleanly\n");
    }

    /**
     * When a thread is sleeping/waiting and gets interrupted,
     * it throws InterruptedException.
     */
    private static void demonstrateInterruptedException() throws InterruptedException {
        System.out.println("--- InterruptedException ---\n");

        Thread sleeper = new Thread(() -> {
            System.out.println("  [Sleeper] Going to sleep for 10 seconds...");
            try {
                Thread.sleep(10000);
                System.out.println("  [Sleeper] Woke up naturally");
            } catch (InterruptedException e) {
                // InterruptedException clears the interrupt flag!
                System.out.println("  [Sleeper] Sleep was interrupted!");
                System.out.println("  [Sleeper] Interrupt flag is now: " + 
                    Thread.currentThread().isInterrupted());
            }
        }, "Sleeper");

        sleeper.start();
        
        Thread.sleep(1000); // Let it start sleeping
        System.out.println("[Main] Interrupting sleeper...");
        sleeper.interrupt();
        
        sleeper.join();
        System.out.println();
    }

    /**
     * Best practice: Restore interrupt status after catching InterruptedException.
     * This allows calling code to also detect the interruption.
     */
    private static void demonstrateProperInterruptHandling() throws InterruptedException {
        System.out.println("--- Proper Interrupt Handling ---\n");

        Thread worker = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Do some work...
                    System.out.println("  [Worker] Working...");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // IMPORTANT: Restore the interrupt status!
                    Thread.currentThread().interrupt();
                    System.out.println("  [Worker] Interrupted during sleep, cleaning up...");
                    // Now the while condition will see the interrupt
                }
            }
            System.out.println("  [Worker] Exiting gracefully");
        }, "Worker");

        worker.start();
        
        Thread.sleep(1200);
        System.out.println("[Main] Requesting worker to stop...");
        worker.interrupt();
        
        worker.join();
        System.out.println("[Main] Worker stopped cleanly\n");
    }

    /**
     * BAD PRACTICE: Ignoring interruption
     * This is shown only to demonstrate what NOT to do!
     */
    private static void demonstrateIgnoringInterrupt() throws InterruptedException {
        System.out.println("--- BAD: Ignoring Interrupt (Don't do this!) ---\n");

        Thread badWorker = new Thread(() -> {
            int count = 0;
            while (count < 5) {
                try {
                    System.out.println("  [BadWorker] Working... " + count);
                    Thread.sleep(300);
                    count++;
                } catch (InterruptedException e) {
                    // BAD: Just ignoring the interrupt!
                    System.out.println("  [BadWorker] Ignoring interrupt (BAD!)");
                    // Thread continues running despite interrupt request
                }
            }
            System.out.println("  [BadWorker] Finished normally");
        }, "BadWorker");

        badWorker.start();
        
        Thread.sleep(500);
        System.out.println("[Main] Trying to interrupt bad worker...");
        badWorker.interrupt();
        
        badWorker.join();
        System.out.println("[Main] BadWorker ignored our interrupt request!\n");
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Thread Interruption Demo ===
 * 
 * --- Checking Interrupt Flag ---
 *   [Worker] Count: 1000000
 *   [Worker] Count: 2000000
 *   ...
 * [Main] Interrupting worker...
 *   [Worker] Interrupted! Final count: xxxxxxx
 * [Main] Worker stopped cleanly
 * 
 * --- InterruptedException ---
 *   [Sleeper] Going to sleep for 10 seconds...
 * [Main] Interrupting sleeper...
 *   [Sleeper] Sleep was interrupted!
 *   [Sleeper] Interrupt flag is now: false
 * 
 * --- Proper Interrupt Handling ---
 *   [Worker] Working...
 *   [Worker] Working...
 *   [Worker] Working...
 * [Main] Requesting worker to stop...
 *   [Worker] Interrupted during sleep, cleaning up...
 *   [Worker] Exiting gracefully
 * [Main] Worker stopped cleanly
 * 
 * --- BAD: Ignoring Interrupt (Don't do this!) ---
 *   [BadWorker] Working... 0
 *   [BadWorker] Working... 1
 * [Main] Trying to interrupt bad worker...
 *   [BadWorker] Ignoring interrupt (BAD!)
 *   [BadWorker] Working... 2
 *   ...
 * [Main] BadWorker ignored our interrupt request!
 * 
 * KEY TAKEAWAYS:
 * 1. Interruption is a cooperative mechanism - threads must check and respond
 * 2. Check isInterrupted() in loop conditions
 * 3. InterruptedException clears the interrupt flag
 * 4. Always restore interrupt status after catching InterruptedException
 * 5. NEVER ignore interrupts - it makes threads unstoppable
 */
