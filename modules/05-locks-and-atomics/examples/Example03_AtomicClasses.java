/**
 * Example 03: Atomic Classes
 * 
 * Demonstrates lock-free programming with:
 * - AtomicInteger operations
 * - AtomicReference for objects
 * - AtomicStampedReference for ABA problem
 * - Performance comparison with locks
 */

import java.util.concurrent.atomic.*;

public class Example03_AtomicClasses {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Atomic Classes Demo ===\n");

        atomicIntegerDemo();
        atomicReferenceDemo();
        abaProlemDemo();
        performanceComparison();
    }

    /**
     * AtomicInteger provides thread-safe numeric operations.
     */
    private static void atomicIntegerDemo() throws InterruptedException {
        System.out.println("--- AtomicInteger Demo ---\n");

        AtomicInteger counter = new AtomicInteger(0);

        // Demonstrate various operations
        System.out.println("Initial value: " + counter.get());
        System.out.println("incrementAndGet(): " + counter.incrementAndGet());  // 1
        System.out.println("getAndIncrement(): " + counter.getAndIncrement());  // 1 (returns old)
        System.out.println("Current value: " + counter.get());  // 2
        System.out.println("addAndGet(10): " + counter.addAndGet(10));  // 12
        System.out.println("compareAndSet(12, 100): " + counter.compareAndSet(12, 100));  // true
        System.out.println("Current value: " + counter.get());  // 100
        System.out.println("updateAndGet(x -> x * 2): " + counter.updateAndGet(x -> x * 2));  // 200

        // Thread-safe counting
        System.out.println("\nThread-safe counting test:");
        counter.set(0);
        
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    counter.incrementAndGet();
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println("Expected: 10000, Actual: " + counter.get());
        System.out.println();
    }

    /**
     * AtomicReference for atomic object updates.
     */
    private static void atomicReferenceDemo() {
        System.out.println("--- AtomicReference Demo ---\n");

        AtomicReference<String> ref = new AtomicReference<>("initial");

        System.out.println("Initial: " + ref.get());

        // Compare and set
        boolean updated = ref.compareAndSet("initial", "updated");
        System.out.println("CAS(initial -> updated): " + updated + ", value: " + ref.get());

        // Fails because current value is no longer "initial"
        updated = ref.compareAndSet("initial", "another");
        System.out.println("CAS(initial -> another): " + updated + ", value: " + ref.get());

        // Update with function
        String oldValue = ref.getAndUpdate(s -> s.toUpperCase());
        System.out.println("getAndUpdate(toUpperCase): old=" + oldValue + ", new=" + ref.get());
        System.out.println();
    }

    /**
     * The ABA problem and solution with AtomicStampedReference.
     */
    private static void abaProlemDemo() throws InterruptedException {
        System.out.println("--- ABA Problem Demo ---\n");

        // Without stamp - vulnerable to ABA
        AtomicReference<String> ref = new AtomicReference<>("A");

        Thread aba = new Thread(() -> {
            ref.compareAndSet("A", "B");
            ref.compareAndSet("B", "A");  // Back to A
            System.out.println("[ABA Thread] Changed A→B→A");
        });

        Thread victim = new Thread(() -> {
            String expected = ref.get();  // Gets "A"
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            // Value is still "A" but it changed in between!
            boolean success = ref.compareAndSet(expected, "C");
            System.out.println("[Victim] CAS(A→C): " + success + " (should this succeed?)");
        });

        victim.start();
        Thread.sleep(10);
        aba.start();

        victim.join();
        aba.join();

        // With stamp - protected from ABA
        System.out.println("\nWith AtomicStampedReference:");
        AtomicStampedReference<String> stampedRef = new AtomicStampedReference<>("A", 0);

        Thread aba2 = new Thread(() -> {
            int stamp = stampedRef.getStamp();
            stampedRef.compareAndSet("A", "B", stamp, stamp + 1);
            stamp = stampedRef.getStamp();
            stampedRef.compareAndSet("B", "A", stamp, stamp + 1);
            System.out.println("[ABA Thread] Changed A→B→A with stamps");
        });

        Thread safe = new Thread(() -> {
            int[] stampHolder = new int[1];
            String val = stampedRef.get(stampHolder);
            int stamp = stampHolder[0];
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            // Stamp has changed even though value is same!
            boolean success = stampedRef.compareAndSet(val, "C", stamp, stamp + 1);
            System.out.println("[Safe] CAS with stamp: " + success + " (correctly fails)");
        });

        safe.start();
        Thread.sleep(10);
        aba2.start();

        safe.join();
        aba2.join();
        System.out.println();
    }

    /**
     * Compare performance of AtomicInteger vs synchronized.
     */
    private static void performanceComparison() throws InterruptedException {
        System.out.println("--- Performance Comparison ---\n");

        int threads = 4;
        int iterations = 1_000_000;

        // Atomic
        AtomicInteger atomicCounter = new AtomicInteger(0);
        long atomicTime = timeExecution(threads, iterations, () -> {
            atomicCounter.incrementAndGet();
        });

        // Synchronized
        int[] syncCounter = {0};
        Object lock = new Object();
        long syncTime = timeExecution(threads, iterations, () -> {
            synchronized (lock) {
                syncCounter[0]++;
            }
        });

        System.out.println("Atomic time: " + atomicTime + "ms");
        System.out.println("Synchronized time: " + syncTime + "ms");
        System.out.println("Atomic is " + String.format("%.1f", (double)syncTime/atomicTime) + 
            "x faster");
        System.out.println();
    }

    private static long timeExecution(int threadCount, int iterationsPerThread, 
                                       Runnable operation) throws InterruptedException {
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < iterationsPerThread; j++) {
                    operation.run();
                }
            });
        }

        long start = System.currentTimeMillis();
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        return System.currentTimeMillis() - start;
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Atomic Classes Demo ===
 * 
 * --- AtomicInteger Demo ---
 * Initial value: 0
 * incrementAndGet(): 1
 * getAndIncrement(): 1
 * Current value: 2
 * addAndGet(10): 12
 * compareAndSet(12, 100): true
 * Current value: 100
 * updateAndGet(x -> x * 2): 200
 * 
 * Thread-safe counting test:
 * Expected: 10000, Actual: 10000
 * 
 * --- AtomicReference Demo ---
 * Initial: initial
 * CAS(initial -> updated): true, value: updated
 * CAS(initial -> another): false, value: updated
 * getAndUpdate(toUpperCase): old=updated, new=UPDATED
 * 
 * --- ABA Problem Demo ---
 * [ABA Thread] Changed A→B→A
 * [Victim] CAS(A→C): true (should this succeed?)
 * 
 * With AtomicStampedReference:
 * [ABA Thread] Changed A→B→A with stamps
 * [Safe] CAS with stamp: false (correctly fails)
 * 
 * --- Performance Comparison ---
 * Atomic time: ~50ms
 * Synchronized time: ~200ms
 * Atomic is 4.0x faster
 * 
 * 
 * KEY INSIGHTS:
 * 
 * 1. ATOMIC OPERATIONS:
 *    All operations are lock-free using hardware CAS instructions.
 * 
 * 2. COMPARE-AND-SET:
 *    The fundamental operation - only update if current value matches expected.
 * 
 * 3. ABA PROBLEM:
 *    Value can change A→B→A, making CAS think nothing changed.
 *    Solution: AtomicStampedReference adds a version stamp.
 * 
 * 4. PERFORMANCE:
 *    Atomic classes are significantly faster than locks under contention
 *    because they don't involve context switches or thread blocking.
 */
