/**
 * Example 03: Using volatile for Flags
 * 
 * This example demonstrates:
 * 1. The visibility problem without volatile
 * 2. How volatile ensures visibility across threads
 * 3. When volatile is sufficient (and when it's not)
 */
public class Example03_VolatileFlag {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Volatile Flag Demo ===\n");

        demonstrateVisibilityProblem();
        demonstrateVolatileFix();
        demonstrateVolatileNotEnough();
    }

    /**
     * WITHOUT volatile: The worker might run forever!
     * The JVM may cache 'running' in the worker thread's CPU cache.
     * 
     * NOTE: This bug is timing/JVM dependent. It may or may not manifest.
     * With JIT compilation and optimizations, the loop might become infinite.
     */
    private static void demonstrateVisibilityProblem() throws InterruptedException {
        System.out.println("--- Visibility Problem (without volatile) ---");

        NonVolatileWorker worker = new NonVolatileWorker();
        Thread t = new Thread(worker, "Worker");

        t.start();
        Thread.sleep(100);

        System.out.println("Main: Requesting stop...");
        worker.stop();

        // Wait max 2 seconds
        t.join(2000);

        if (t.isAlive()) {
            System.out.println("Main: Worker didn't stop! (visibility issue)");
            System.out.println("Main: Interrupting to force stop...");
            t.interrupt();
            t.join();
        } else {
            System.out.println("Main: Worker stopped (got lucky this time)");
        }
        System.out.println();
    }

    /**
     * WITH volatile: Worker always sees the updated value.
     */
    private static void demonstrateVolatileFix() throws InterruptedException {
        System.out.println("--- Volatile Fix ---");

        VolatileWorker worker = new VolatileWorker();
        Thread t = new Thread(worker, "Worker");

        t.start();
        Thread.sleep(100);

        System.out.println("Main: Requesting stop...");
        worker.stop();

        t.join(2000);

        if (t.isAlive()) {
            System.out.println("Main: Unexpected - worker didn't stop!");
            t.interrupt();
            t.join();
        } else {
            System.out.println("Main: Worker stopped correctly ✓");
        }
        System.out.println();
    }

    /**
     * volatile is NOT sufficient for compound operations like count++
     */
    private static void demonstrateVolatileNotEnough() throws InterruptedException {
        System.out.println("--- volatile is NOT enough for count++ ---");

        VolatileCounter counter = new VolatileCounter();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                counter.increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                counter.increment();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.printf("Expected: 20000, Actual: %d%n", counter.getCount());
        System.out.println("volatile alone doesn't prevent race conditions on count++!");
        System.out.println("Use synchronized or AtomicInteger instead.\n");
    }
}

/**
 * Worker WITHOUT volatile flag.
 * May not see updates to 'running' from other threads.
 */
class NonVolatileWorker implements Runnable {
    private boolean running = true;  // No volatile!

    @Override
    public void run() {
        int count = 0;
        while (running) {  // May be cached and never re-read!
            count++;
            // Busy loop - maximizes chance of caching issue
        }
        System.out.println("Worker: Stopped after " + count + " iterations");
    }

    public void stop() {
        running = false;
    }
}

/**
 * Worker WITH volatile flag.
 * Always reads 'running' from main memory.
 */
class VolatileWorker implements Runnable {
    private volatile boolean running = true;  // volatile!

    @Override
    public void run() {
        int count = 0;
        while (running) {  // Always reads from main memory
            count++;
        }
        System.out.println("Worker: Stopped after " + count + " iterations");
    }

    public void stop() {
        running = false;
    }
}

/**
 * Counter with volatile - still has race condition!
 * volatile ensures visibility but NOT atomicity of count++
 */
class VolatileCounter {
    private volatile int count = 0;  // volatile doesn't help here!

    public void increment() {
        count++;  // STILL NOT ATOMIC! (read-modify-write)
    }

    public int getCount() {
        return count;
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Volatile Flag Demo ===
 * 
 * --- Visibility Problem (without volatile) ---
 * Main: Requesting stop...
 * Main: Worker didn't stop! (visibility issue)
 * Main: Interrupting to force stop...
 * Worker: Stopped after xxxxxx iterations
 * 
 * --- Volatile Fix ---
 * Main: Requesting stop...
 * Worker: Stopped after xxxxxx iterations
 * Main: Worker stopped correctly ✓
 * 
 * --- volatile is NOT enough for count++ ---
 * Expected: 20000, Actual: 17856
 * volatile alone doesn't prevent race conditions on count++!
 * Use synchronized or AtomicInteger instead.
 * 
 * 
 * KEY TAKEAWAYS:
 * 
 * 1. volatile ensures VISIBILITY - changes are seen by all threads
 * 2. volatile does NOT ensure ATOMICITY - compound operations still race
 * 3. Use volatile for:
 *    - Boolean flags
 *    - Single writer, multiple readers
 *    - Writes that don't depend on current value
 * 4. Use synchronized or Atomic classes for:
 *    - count++, count += value
 *    - Check-then-act patterns
 *    - Any read-modify-write operation
 */
