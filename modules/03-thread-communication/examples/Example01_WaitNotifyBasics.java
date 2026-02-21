/**
 * Example 01: Wait/Notify Basics
 * 
 * Demonstrates:
 * 1. Basic wait() and notify() usage
 * 2. Why wait() must be in synchronized block
 * 3. The importance of the while loop pattern
 */
public class Example01_WaitNotifyBasics {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Wait/Notify Basics ===\n");

        basicWaitNotify();
        demonstrateWhileLoop();
    }

    /**
     * Basic wait/notify demonstration.
     * One thread waits for a signal from another.
     */
    private static void basicWaitNotify() throws InterruptedException {
        System.out.println("--- Basic Wait/Notify ---\n");

        final Object lock = new Object();
        final boolean[] ready = {false};

        // Waiter thread
        Thread waiter = new Thread(() -> {
            synchronized (lock) {
                System.out.println("[Waiter] Waiting for signal...");
                
                while (!ready[0]) {  // Always use while, not if!
                    try {
                        lock.wait();  // Releases lock and waits
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                
                System.out.println("[Waiter] Got signal! Proceeding...");
            }
        }, "Waiter");

        // Signaler thread
        Thread signaler = new Thread(() -> {
            try {
                Thread.sleep(1000);  // Simulate some work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            synchronized (lock) {
                System.out.println("[Signaler] Sending signal...");
                ready[0] = true;
                lock.notify();  // Wake up waiter
            }
        }, "Signaler");

        waiter.start();
        Thread.sleep(100);  // Ensure waiter starts first
        signaler.start();

        waiter.join();
        signaler.join();
        System.out.println();
    }

    /**
     * Demonstrates why while loop is essential.
     * Multiple threads waiting, only one should proceed.
     */
    private static void demonstrateWhileLoop() throws InterruptedException {
        System.out.println("--- Why While Loop Matters ---\n");

        final Object lock = new Object();
        final int[] itemCount = {0};

        // Create multiple consumer threads
        Thread[] consumers = new Thread[3];
        for (int i = 0; i < consumers.length; i++) {
            final int id = i + 1;
            consumers[i] = new Thread(() -> {
                synchronized (lock) {
                    System.out.println("[Consumer-" + id + "] Waiting for item...");
                    
                    while (itemCount[0] == 0) {  // WHILE, not IF!
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                        System.out.println("[Consumer-" + id + "] Woke up, checking...");
                    }
                    
                    // Consume item
                    itemCount[0]--;
                    System.out.println("[Consumer-" + id + "] Consumed! Items left: " + itemCount[0]);
                }
            }, "Consumer-" + id);
        }

        // Start all consumers
        for (Thread c : consumers) {
            c.start();
        }
        Thread.sleep(500);  // Let them all wait

        // Producer adds ONE item and notifies ALL
        Thread producer = new Thread(() -> {
            synchronized (lock) {
                System.out.println("\n[Producer] Adding 1 item and notifying ALL...");
                itemCount[0] = 1;
                lock.notifyAll();  // Wake ALL consumers
            }
        }, "Producer");

        producer.start();
        producer.join();

        for (Thread c : consumers) {
            c.join();
        }

        System.out.println("\nOnly ONE consumer got the item because of while loop!");
        System.out.println("Others woke up, found itemCount==0, and went back to wait.\n");
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Wait/Notify Basics ===
 * 
 * --- Basic Wait/Notify ---
 * 
 * [Waiter] Waiting for signal...
 * [Signaler] Sending signal...
 * [Waiter] Got signal! Proceeding...
 * 
 * --- Why While Loop Matters ---
 * 
 * [Consumer-1] Waiting for item...
 * [Consumer-2] Waiting for item...
 * [Consumer-3] Waiting for item...
 * 
 * [Producer] Adding 1 item and notifying ALL...
 * [Consumer-1] Woke up, checking...
 * [Consumer-1] Consumed! Items left: 0
 * [Consumer-2] Woke up, checking...
 * [Consumer-3] Woke up, checking...
 * 
 * Only ONE consumer got the item because of while loop!
 * Others woke up, found itemCount==0, and went back to wait.
 * 
 * 
 * KEY INSIGHTS:
 * 
 * 1. wait() RELEASES the lock - other threads can enter synchronized
 * 2. When woken, thread REACQUIRES lock before continuing
 * 3. notifyAll() wakes ALL waiters, but only one can proceed at a time
 * 4. WHILE loop ensures re-checking condition after wakeup
 * 5. Without WHILE, threads might proceed when they shouldn't
 */
