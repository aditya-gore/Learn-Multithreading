/**
 * Solution for Exercise 02: Thread-Safe Counter with Multiple Operations
 */
public class Exercise02_Solution {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Thread-Safe Counter Test ===\n");

        testBasicOperations();
        testConcurrentIncrements();
        testCompareAndSet();
    }

    private static void testBasicOperations() {
        System.out.println("--- Basic Operations ---");
        ThreadSafeCounter counter = new ThreadSafeCounter();

        counter.increment();
        System.out.println("After increment: " + counter.getValue());  // 1

        counter.add(10);
        System.out.println("After add(10): " + counter.getValue());    // 11

        counter.decrement();
        System.out.println("After decrement: " + counter.getValue());  // 10

        int oldValue = counter.getAndIncrement();
        System.out.println("getAndIncrement returned: " + oldValue);   // 10
        System.out.println("Value is now: " + counter.getValue());     // 11

        int newValue = counter.incrementAndGet();
        System.out.println("incrementAndGet returned: " + newValue);   // 12
        System.out.println("Value is now: " + counter.getValue());     // 12

        System.out.println();
    }

    private static void testConcurrentIncrements() throws InterruptedException {
        System.out.println("--- Concurrent Increments ---");
        ThreadSafeCounter counter = new ThreadSafeCounter();

        int numThreads = 4;
        int incrementsPerThread = 10000;
        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });
        }

        long startTime = System.currentTimeMillis();

        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }

        long endTime = System.currentTimeMillis();

        int expected = numThreads * incrementsPerThread;
        int actual = counter.getValue();
        System.out.println("Expected: " + expected);
        System.out.println("Actual: " + actual);
        System.out.println("Result: " + (expected == actual ? "✓ CORRECT" : "✗ WRONG"));
        System.out.println("Time: " + (endTime - startTime) + "ms");
        System.out.println();
    }

    private static void testCompareAndSet() {
        System.out.println("--- Compare And Set ---");
        ThreadSafeCounter counter = new ThreadSafeCounter();
        counter.add(100);

        boolean success1 = counter.compareAndSet(100, 200);
        System.out.println("CAS(100, 200): " + success1 + ", value=" + counter.getValue());
        // true, 200

        boolean success2 = counter.compareAndSet(100, 300);
        System.out.println("CAS(100, 300): " + success2 + ", value=" + counter.getValue());
        // false, still 200 (expected didn't match)

        boolean success3 = counter.compareAndSet(200, 300);
        System.out.println("CAS(200, 300): " + success3 + ", value=" + counter.getValue());
        // true, 300

        System.out.println();
    }
}

class ThreadSafeCounter {
    private int value = 0;

    public synchronized void increment() {
        value++;
    }

    public synchronized void decrement() {
        value--;
    }

    public synchronized void add(int delta) {
        value += delta;
    }

    public synchronized int getValue() {
        return value;
    }

    /**
     * Atomically sets value to newValue if current value equals expected.
     * This is the foundation of many lock-free algorithms.
     * 
     * @return true if successful, false if current value != expected
     */
    public synchronized boolean compareAndSet(int expected, int newValue) {
        if (value == expected) {
            value = newValue;
            return true;
        }
        return false;
    }

    /**
     * Atomically increments and returns the NEW value.
     */
    public synchronized int incrementAndGet() {
        return ++value;
    }

    /**
     * Atomically returns the OLD value and then increments.
     */
    public synchronized int getAndIncrement() {
        return value++;
    }
}

/*
 * KEY LEARNINGS:
 * 
 * 1. ALL METHODS MUST BE SYNCHRONIZED:
 *    Even getValue() needs synchronized to ensure visibility
 *    of the most recent writes.
 * 
 * 2. ATOMIC COMPOUND OPERATIONS:
 *    incrementAndGet() and getAndIncrement() combine two
 *    operations atomically. This is important for building
 *    correct concurrent code.
 * 
 * 3. COMPARE AND SET (CAS):
 *    This is a fundamental primitive for lock-free programming.
 *    AtomicInteger provides this without locks using CPU instructions.
 * 
 * PERFORMANCE NOTE:
 * 
 * This synchronized implementation works but has overhead:
 * - Lock acquisition/release on every operation
 * - Threads block waiting for the lock
 * 
 * java.util.concurrent.atomic.AtomicInteger provides the same
 * operations using hardware CAS instructions, which are much
 * faster under contention. See Module 5!
 * 
 * PREVIEW - How AtomicInteger does it:
 * 
 * public final int incrementAndGet() {
 *     return U.getAndAddInt(this, VALUE, 1) + 1;
 * }
 * 
 * This uses sun.misc.Unsafe to access CPU's atomic instructions.
 */
