/**
 * Example 03: Semaphore
 * 
 * Demonstrates using Semaphore for:
 * 1. Limiting concurrent access (connection pool)
 * 2. Rate limiting
 * 3. Binary semaphore (mutex)
 */

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Example03_Semaphore {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Semaphore Demo ===\n");

        connectionPoolExample();
        binarySemaphoreExample();
        tryAcquireExample();
    }

    /**
     * Use case: Database connection pool.
     * Only N connections can be used simultaneously.
     */
    private static void connectionPoolExample() throws InterruptedException {
        System.out.println("--- Connection Pool Example ---\n");

        int maxConnections = 3;
        ConnectionPool pool = new ConnectionPool(maxConnections);

        // Simulate 6 clients trying to use 3 connections
        Thread[] clients = new Thread[6];
        for (int i = 0; i < clients.length; i++) {
            final int clientId = i + 1;
            clients[i] = new Thread(() -> {
                try {
                    pool.useConnection(clientId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Client-" + clientId);
        }

        // Start all clients
        for (Thread client : clients) {
            client.start();
            Thread.sleep(100);  // Stagger starts
        }

        // Wait for completion
        for (Thread client : clients) {
            client.join();
        }

        System.out.println();
    }

    /**
     * Use case: Binary semaphore as a mutex.
     */
    private static void binarySemaphoreExample() throws InterruptedException {
        System.out.println("--- Binary Semaphore (Mutex) Example ---\n");

        Semaphore mutex = new Semaphore(1);  // Only 1 permit
        int[] counter = {0};

        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                try {
                    mutex.acquire();
                    try {
                        // Critical section
                        int current = counter[0];
                        Thread.sleep(50);  // Simulate work
                        counter[0] = current + 1;
                        System.out.println("[Thread-" + id + "] Counter = " + counter[0]);
                    } finally {
                        mutex.release();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println("Final counter: " + counter[0] + "\n");
    }

    /**
     * Use case: Non-blocking acquire with tryAcquire.
     */
    private static void tryAcquireExample() throws InterruptedException {
        System.out.println("--- tryAcquire Example ---\n");

        Semaphore semaphore = new Semaphore(2);

        // Acquire all permits
        semaphore.acquire(2);

        // Try to acquire without blocking
        System.out.println("tryAcquire() immediate: " + semaphore.tryAcquire());

        // Try to acquire with timeout
        System.out.print("tryAcquire(1 second): ");
        long start = System.currentTimeMillis();
        boolean acquired = semaphore.tryAcquire(1, TimeUnit.SECONDS);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println(acquired + " (waited " + elapsed + "ms)");

        // Release permits
        semaphore.release(2);

        System.out.println("After release, tryAcquire(): " + semaphore.tryAcquire());
        System.out.println();
    }
}

class ConnectionPool {
    private final Semaphore semaphore;
    private final int maxConnections;

    public ConnectionPool(int maxConnections) {
        this.maxConnections = maxConnections;
        this.semaphore = new Semaphore(maxConnections, true);  // Fair!
    }

    public void useConnection(int clientId) throws InterruptedException {
        System.out.println("[Client-" + clientId + "] Requesting connection... " +
            "(available: " + semaphore.availablePermits() + "/" + maxConnections + ")");

        semaphore.acquire();  // Block until a connection is available
        try {
            System.out.println("[Client-" + clientId + "] Got connection! " +
                "(available: " + semaphore.availablePermits() + "/" + maxConnections + ")");

            // Simulate using the connection
            Thread.sleep((long) (Math.random() * 1000) + 500);

            System.out.println("[Client-" + clientId + "] Releasing connection.");
        } finally {
            semaphore.release();  // Always release!
        }
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Semaphore Demo ===
 * 
 * --- Connection Pool Example ---
 * 
 * [Client-1] Requesting connection... (available: 3/3)
 * [Client-1] Got connection! (available: 2/3)
 * [Client-2] Requesting connection... (available: 2/3)
 * [Client-2] Got connection! (available: 1/3)
 * [Client-3] Requesting connection... (available: 1/3)
 * [Client-3] Got connection! (available: 0/3)
 * [Client-4] Requesting connection... (available: 0/3)
 * [Client-5] Requesting connection... (available: 0/3)
 * [Client-6] Requesting connection... (available: 0/3)
 * [Client-1] Releasing connection.
 * [Client-4] Got connection! (available: 0/3)
 * ...
 * 
 * --- Binary Semaphore (Mutex) Example ---
 * 
 * [Thread-0] Counter = 1
 * [Thread-1] Counter = 2
 * [Thread-2] Counter = 3
 * [Thread-3] Counter = 4
 * [Thread-4] Counter = 5
 * Final counter: 5
 * 
 * --- tryAcquire Example ---
 * 
 * tryAcquire() immediate: false
 * tryAcquire(1 second): false (waited 1001ms)
 * After release, tryAcquire(): true
 * 
 * 
 * KEY INSIGHTS:
 * 
 * 1. PERMIT COUNTING: Semaphore maintains a count of available permits.
 *    acquire() decrements, release() increments.
 * 
 * 2. FAIRNESS: Fair semaphore (true in constructor) ensures FIFO ordering.
 *    Non-fair is more performant but can cause starvation.
 * 
 * 3. ACQUIRE MULTIPLE: acquire(n) gets n permits at once.
 *    Useful for reserving multiple resources atomically.
 * 
 * 4. NO OWNERSHIP: Unlike locks, any thread can release a permit.
 *    Be careful not to release more than acquired!
 * 
 * 5. tryAcquire(): Non-blocking alternative that returns immediately
 *    or after timeout. Useful for try-else patterns.
 */
