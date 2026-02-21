/**
 * Example 02: Fixing Race Conditions with synchronized
 * 
 * This example shows multiple ways to fix the race condition from Example01:
 * 1. Synchronized method
 * 2. Synchronized block
 * 3. Synchronized on dedicated lock object
 */
public class Example02_SynchronizedFix {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Synchronized Fix Demo ===\n");

        testCounter("Method Synchronized", new MethodSyncCounter());
        testCounter("Block Synchronized", new BlockSyncCounter());
        testCounter("Lock Object Sync", new LockObjectCounter());
    }

    private static void testCounter(String name, Counter counter) throws InterruptedException {
        System.out.println("--- " + name + " ---");

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                counter.increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                counter.increment();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.printf("Expected: 200000, Actual: %d %s%n%n",
            counter.getCount(),
            counter.getCount() == 200000 ? "✓ CORRECT" : "✗ WRONG");
    }
}

interface Counter {
    void increment();
    int getCount();
}

/**
 * Solution 1: Synchronized Method
 * 
 * The entire method is synchronized on 'this'.
 * Simple but may be too coarse-grained for complex classes.
 */
class MethodSyncCounter implements Counter {
    private int count = 0;

    @Override
    public synchronized void increment() {
        count++;
    }

    @Override
    public synchronized int getCount() {
        return count;
    }
}

/**
 * Solution 2: Synchronized Block
 * 
 * Only the critical section is synchronized.
 * More flexible - can sync on different objects.
 */
class BlockSyncCounter implements Counter {
    private int count = 0;

    @Override
    public void increment() {
        synchronized (this) {
            count++;
        }
    }

    @Override
    public int getCount() {
        synchronized (this) {
            return count;
        }
    }
}

/**
 * Solution 3: Dedicated Lock Object (RECOMMENDED)
 * 
 * Benefits:
 * - Lock is private, can't be acquired externally
 * - Final ensures lock reference never changes
 * - Clear separation of what the lock protects
 */
class LockObjectCounter implements Counter {
    private int count = 0;
    private final Object lock = new Object();

    @Override
    public void increment() {
        synchronized (lock) {
            count++;
        }
    }

    @Override
    public int getCount() {
        synchronized (lock) {
            return count;
        }
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Synchronized Fix Demo ===
 * 
 * --- Method Synchronized ---
 * Expected: 200000, Actual: 200000 ✓ CORRECT
 * 
 * --- Block Synchronized ---
 * Expected: 200000, Actual: 200000 ✓ CORRECT
 * 
 * --- Lock Object Sync ---
 * Expected: 200000, Actual: 200000 ✓ CORRECT
 * 
 * 
 * WHICH APPROACH TO USE?
 * 
 * 1. Synchronized Method:
 *    + Simple
 *    - Locks on 'this', which external code could also lock on
 *    - Locks entire method even if only part needs protection
 * 
 * 2. Synchronized Block:
 *    + More control over what's locked and for how long
 *    - Still locks on 'this' by default
 * 
 * 3. Private Lock Object (BEST):
 *    + Lock is private - external code can't interfere
 *    + Can have multiple locks for different data
 *    + Clearest about what the lock protects
 *    - Slightly more verbose
 * 
 * RULE OF THUMB: Use private lock objects for production code.
 */
