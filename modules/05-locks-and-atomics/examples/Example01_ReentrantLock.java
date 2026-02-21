/**
 * Example 01: ReentrantLock Features
 * 
 * Demonstrates:
 * 1. Basic lock usage
 * 2. tryLock() for non-blocking
 * 3. tryLock(timeout) for timed acquisition
 * 4. lockInterruptibly() for interruptible waiting
 * 5. Condition objects
 */

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;

public class Example01_ReentrantLock {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ReentrantLock Demo ===\n");

        basicLockUsage();
        tryLockDemo();
        tryLockWithTimeoutDemo();
        conditionDemo();
    }

    /**
     * Basic lock/unlock pattern.
     * ALWAYS unlock in finally block!
     */
    private static void basicLockUsage() throws InterruptedException {
        System.out.println("--- Basic Lock Usage ---\n");

        Lock lock = new ReentrantLock();
        int[] counter = {0};

        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    lock.lock();  // Acquire lock
                    try {
                        counter[0]++;
                    } finally {
                        lock.unlock();  // ALWAYS in finally!
                    }
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println("Counter (expected 5000): " + counter[0]);
        System.out.println();
    }

    /**
     * tryLock() returns immediately if lock is not available.
     */
    private static void tryLockDemo() throws InterruptedException {
        System.out.println("--- tryLock() Demo ---\n");

        Lock lock = new ReentrantLock();

        // Thread that holds the lock for 2 seconds
        Thread holder = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("[Holder] Acquired lock, holding for 2 seconds...");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                System.out.println("[Holder] Releasing lock");
                lock.unlock();
            }
        });

        // Thread that tries to acquire with tryLock()
        Thread tryLocker = new Thread(() -> {
            try {
                Thread.sleep(500);  // Start after holder
            } catch (InterruptedException e) {
                return;
            }

            System.out.println("[TryLocker] Trying to acquire lock...");
            if (lock.tryLock()) {
                try {
                    System.out.println("[TryLocker] Got lock!");
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println("[TryLocker] Lock not available, doing something else");
            }
        });

        holder.start();
        tryLocker.start();
        holder.join();
        tryLocker.join();
        System.out.println();
    }

    /**
     * tryLock(timeout) waits up to the specified time.
     */
    private static void tryLockWithTimeoutDemo() throws InterruptedException {
        System.out.println("--- tryLock(timeout) Demo ---\n");

        Lock lock = new ReentrantLock();

        Thread holder = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("[Holder] Holding lock for 3 seconds...");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        });

        Thread timedTryLocker = new Thread(() -> {
            try {
                Thread.sleep(500);
                System.out.println("[TimedTryLocker] Trying to acquire (1 second timeout)...");
                
                long start = System.currentTimeMillis();
                if (lock.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        System.out.println("[TimedTryLocker] Got lock!");
                    } finally {
                        lock.unlock();
                    }
                } else {
                    long waited = System.currentTimeMillis() - start;
                    System.out.println("[TimedTryLocker] Timeout after " + waited + "ms");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        holder.start();
        timedTryLocker.start();
        holder.join();
        timedTryLocker.join();
        System.out.println();
    }

    /**
     * Condition objects for more flexible wait/notify.
     */
    private static void conditionDemo() throws InterruptedException {
        System.out.println("--- Condition Demo ---\n");

        Lock lock = new ReentrantLock();
        Condition dataReady = lock.newCondition();
        String[] data = {null};

        // Consumer - waits for data
        Thread consumer = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("[Consumer] Waiting for data...");
                while (data[0] == null) {
                    dataReady.await();  // Like wait()
                }
                System.out.println("[Consumer] Got data: " + data[0]);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        });

        // Producer - produces data after delay
        Thread producer = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }

            lock.lock();
            try {
                data[0] = "Hello from Producer!";
                System.out.println("[Producer] Data ready, signaling...");
                dataReady.signal();  // Like notify()
            } finally {
                lock.unlock();
            }
        });

        consumer.start();
        producer.start();
        consumer.join();
        producer.join();
        System.out.println();
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === ReentrantLock Demo ===
 * 
 * --- Basic Lock Usage ---
 * Counter (expected 5000): 5000
 * 
 * --- tryLock() Demo ---
 * [Holder] Acquired lock, holding for 2 seconds...
 * [TryLocker] Trying to acquire lock...
 * [TryLocker] Lock not available, doing something else
 * [Holder] Releasing lock
 * 
 * --- tryLock(timeout) Demo ---
 * [Holder] Holding lock for 3 seconds...
 * [TimedTryLocker] Trying to acquire (1 second timeout)...
 * [TimedTryLocker] Timeout after ~1000ms
 * 
 * --- Condition Demo ---
 * [Consumer] Waiting for data...
 * [Producer] Data ready, signaling...
 * [Consumer] Got data: Hello from Producer!
 * 
 * 
 * KEY TAKEAWAYS:
 * 
 * 1. ALWAYS UNLOCK IN FINALLY:
 *    Unlike synchronized, forgetting unlock causes permanent lock.
 * 
 * 2. tryLock() PATTERNS:
 *    - tryLock(): Immediate return, useful for try-else patterns
 *    - tryLock(timeout): Wait with deadline, useful for time-sensitive ops
 * 
 * 3. CONDITIONS:
 *    - More flexible than wait/notify
 *    - Multiple conditions per lock (e.g., notFull, notEmpty)
 *    - Must hold lock when calling await/signal
 */
