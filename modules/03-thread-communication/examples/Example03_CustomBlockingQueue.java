/**
 * Example 03: Custom Blocking Queue Implementation
 * 
 * This example shows how to build a full-featured blocking queue
 * similar to java.util.concurrent.ArrayBlockingQueue.
 * 
 * Features:
 * - put() blocks when full
 * - take() blocks when empty
 * - offer() returns false when full (non-blocking)
 * - poll() returns null when empty (non-blocking)
 * - peek() views without removing
 */

import java.util.LinkedList;
import java.util.Queue;

public class Example03_CustomBlockingQueue {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Custom Blocking Queue Demo ===\n");

        MyBlockingQueue<Integer> queue = new MyBlockingQueue<>(3);

        demonstrateBlockingPut(queue);
        demonstrateBlockingTake(queue);
        demonstrateNonBlocking(queue);
    }

    private static void demonstrateBlockingPut(MyBlockingQueue<Integer> queue) 
            throws InterruptedException {
        System.out.println("--- Blocking put() Demo ---\n");

        // Producer that fills the queue
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    System.out.println("[Producer] Putting " + i + "...");
                    queue.put(i);
                    System.out.println("[Producer] Put " + i + " (size=" + queue.size() + ")");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Slow consumer
        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(1000);  // Start late
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(500);
                    Integer item = queue.take();
                    System.out.println("[Consumer] Took " + item);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println();
    }

    private static void demonstrateBlockingTake(MyBlockingQueue<Integer> queue) 
            throws InterruptedException {
        System.out.println("--- Blocking take() Demo ---\n");

        // Consumer starts first, waits for data
        Thread consumer = new Thread(() -> {
            try {
                System.out.println("[Consumer] Waiting for data...");
                Integer item = queue.take();  // Will block
                System.out.println("[Consumer] Got: " + item);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumer.start();
        Thread.sleep(1000);  // Let consumer wait

        // Producer adds data after delay
        System.out.println("[Producer] Adding item...");
        queue.put(42);

        consumer.join();
        queue.take();  // Clean up
        System.out.println();
    }

    private static void demonstrateNonBlocking(MyBlockingQueue<Integer> queue) {
        System.out.println("--- Non-blocking offer()/poll() Demo ---\n");

        // offer() - non-blocking put
        System.out.println("Offering items to queue (capacity 3):");
        for (int i = 1; i <= 5; i++) {
            boolean success = queue.offer(i);
            System.out.println("  offer(" + i + ") = " + success + " (size=" + queue.size() + ")");
        }

        System.out.println("\nPolling items from queue:");
        Integer item;
        while ((item = queue.poll()) != null) {
            System.out.println("  poll() = " + item + " (size=" + queue.size() + ")");
        }
        System.out.println("  poll() = null (empty)");
        System.out.println();
    }
}

/**
 * A custom implementation of a blocking queue.
 * 
 * This is a learning implementation. In production, use
 * java.util.concurrent.ArrayBlockingQueue or LinkedBlockingQueue.
 */
class MyBlockingQueue<E> {
    private final Queue<E> queue = new LinkedList<>();
    private final int capacity;

    public MyBlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
    }

    /**
     * Inserts element, blocking if queue is full.
     */
    public synchronized void put(E element) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();  // Release lock and wait for space
        }
        queue.add(element);
        notifyAll();  // Wake up any waiting consumers
    }

    /**
     * Removes and returns element, blocking if queue is empty.
     */
    public synchronized E take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();  // Release lock and wait for data
        }
        E element = queue.poll();
        notifyAll();  // Wake up any waiting producers
        return element;
    }

    /**
     * Non-blocking put. Returns true if successful, false if full.
     */
    public synchronized boolean offer(E element) {
        if (queue.size() == capacity) {
            return false;
        }
        queue.add(element);
        notifyAll();
        return true;
    }

    /**
     * Non-blocking take. Returns element or null if empty.
     */
    public synchronized E poll() {
        if (queue.isEmpty()) {
            return null;
        }
        E element = queue.poll();
        notifyAll();
        return element;
    }

    /**
     * Returns but does not remove the head element.
     */
    public synchronized E peek() {
        return queue.peek();
    }

    /**
     * Returns current number of elements.
     */
    public synchronized int size() {
        return queue.size();
    }

    /**
     * Returns true if queue is empty.
     */
    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Returns true if queue is full.
     */
    public synchronized boolean isFull() {
        return queue.size() == capacity;
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Custom Blocking Queue Demo ===
 * 
 * --- Blocking put() Demo ---
 * 
 * [Producer] Putting 1...
 * [Producer] Put 1 (size=1)
 * [Producer] Putting 2...
 * [Producer] Put 2 (size=2)
 * [Producer] Putting 3...
 * [Producer] Put 3 (size=3)
 * [Producer] Putting 4...
 * (producer blocks here - queue is full!)
 * [Consumer] Took 1
 * [Producer] Put 4 (size=3)
 * [Producer] Putting 5...
 * [Consumer] Took 2
 * [Producer] Put 5 (size=3)
 * [Consumer] Took 3
 * [Consumer] Took 4
 * [Consumer] Took 5
 * 
 * --- Blocking take() Demo ---
 * 
 * [Consumer] Waiting for data...
 * [Producer] Adding item...
 * [Consumer] Got: 42
 * 
 * --- Non-blocking offer()/poll() Demo ---
 * 
 * Offering items to queue (capacity 3):
 *   offer(1) = true (size=1)
 *   offer(2) = true (size=2)
 *   offer(3) = true (size=3)
 *   offer(4) = false (size=3)
 *   offer(5) = false (size=3)
 * 
 * Polling items from queue:
 *   poll() = 1 (size=2)
 *   poll() = 2 (size=1)
 *   poll() = 3 (size=0)
 *   poll() = null (empty)
 * 
 * 
 * COMPARISON WITH java.util.concurrent:
 * 
 * BlockingQueue Method  | Our Method
 * ─────────────────────────────────────
 * put(e)                | put(e)        - blocks if full
 * take()                | take()        - blocks if empty
 * offer(e)              | offer(e)      - returns false if full
 * poll()                | poll()        - returns null if empty
 * offer(e, timeout)     | (not implemented)
 * poll(timeout)         | (not implemented)
 * 
 * In production, use java.util.concurrent.BlockingQueue implementations:
 * - ArrayBlockingQueue - bounded, array-backed
 * - LinkedBlockingQueue - optionally bounded, linked-list backed
 * - PriorityBlockingQueue - unbounded, priority-ordered
 */
