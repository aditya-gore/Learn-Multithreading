/**
 * Example 01: Race Condition Demonstration
 * 
 * This example shows a classic race condition bug.
 * Multiple threads increment a shared counter, but the final count
 * is often LESS than expected due to lost updates.
 * 
 * Run this multiple times - you'll likely get different results each time!
 */
public class Example01_RaceCondition {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Race Condition Demo ===\n");

        for (int trial = 1; trial <= 5; trial++) {
            runTrial(trial);
        }

        System.out.println("\nNotice: Results vary and are often < 20000!");
        System.out.println("This is a RACE CONDITION bug.");
    }

    private static void runTrial(int trialNum) throws InterruptedException {
        UnsafeCounter counter = new UnsafeCounter();

        // Create two threads, each incrementing 10000 times
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                counter.increment();
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                counter.increment();
            }
        }, "Thread-2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.printf("Trial %d: Expected=20000, Actual=%d %s%n",
            trialNum,
            counter.getCount(),
            counter.getCount() == 20000 ? "✓" : "✗ RACE CONDITION!");
    }
}

/**
 * This counter is NOT thread-safe!
 * 
 * The increment() operation looks atomic but it's actually:
 * 1. READ the current value of count
 * 2. ADD 1 to the value
 * 3. WRITE the result back to count
 * 
 * If two threads read the same value before either writes,
 * one increment is lost.
 */
class UnsafeCounter {
    private int count = 0;

    public void increment() {
        count++;  // NOT ATOMIC!
    }

    public int getCount() {
        return count;
    }
}

/*
 * SAMPLE OUTPUT (your results will vary):
 * 
 * === Race Condition Demo ===
 * 
 * Trial 1: Expected=20000, Actual=18965 ✗ RACE CONDITION!
 * Trial 2: Expected=20000, Actual=19234 ✗ RACE CONDITION!
 * Trial 3: Expected=20000, Actual=20000 ✓
 * Trial 4: Expected=20000, Actual=17892 ✗ RACE CONDITION!
 * Trial 5: Expected=20000, Actual=19567 ✗ RACE CONDITION!
 * 
 * Notice: Results vary and are often < 20000!
 * This is a RACE CONDITION bug.
 * 
 * HOW THE BUG HAPPENS:
 * 
 * Time    Thread 1           Thread 2           count (memory)
 * ─────   ─────────          ─────────          ──────────────
 * T1      READ count (5)                        5
 * T2                         READ count (5)     5
 * T3      ADD 1 → 6                             5
 * T4                         ADD 1 → 6          5
 * T5      WRITE 6                               6
 * T6                         WRITE 6            6
 * 
 * Both threads read 5, both write 6.
 * Expected: 7, Actual: 6 ← One increment lost!
 * 
 * SOLUTION: See Example02_SynchronizedFix.java
 */
