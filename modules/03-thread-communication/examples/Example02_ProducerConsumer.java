/**
 * Example 02: Producer-Consumer Pattern
 * 
 * This classic pattern demonstrates:
 * - Producers creating work items
 * - Consumers processing work items
 * - A bounded buffer between them
 * - Proper wait/notify coordination
 */

import java.util.LinkedList;
import java.util.Queue;

public class Example02_ProducerConsumer {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Producer-Consumer Pattern ===\n");

        // Shared buffer with capacity 5
        BoundedBuffer buffer = new BoundedBuffer(5);

        // Create producers
        Thread producer1 = new Thread(new Producer(buffer, "P1"), "Producer-1");
        Thread producer2 = new Thread(new Producer(buffer, "P2"), "Producer-2");

        // Create consumers
        Thread consumer1 = new Thread(new Consumer(buffer, "C1"), "Consumer-1");
        Thread consumer2 = new Thread(new Consumer(buffer, "C2"), "Consumer-2");

        // Start all threads
        producer1.start();
        producer2.start();
        consumer1.start();
        consumer2.start();

        // Let them run for a while
        Thread.sleep(3000);

        // Interrupt to stop
        producer1.interrupt();
        producer2.interrupt();
        consumer1.interrupt();
        consumer2.interrupt();

        producer1.join();
        producer2.join();
        consumer1.join();
        consumer2.join();

        System.out.println("\n=== All threads stopped ===");
    }
}

/**
 * Bounded buffer using wait/notify.
 * - Producers wait when full
 * - Consumers wait when empty
 */
class BoundedBuffer {
    private final Queue<String> queue = new LinkedList<>();
    private final int capacity;

    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Add item to buffer. Blocks if buffer is full.
     */
    public synchronized void put(String item) throws InterruptedException {
        // Wait while buffer is full
        while (queue.size() == capacity) {
            System.out.println("  [Buffer] Full! Producer waiting...");
            wait();
        }

        queue.add(item);
        System.out.println("  [Buffer] Added: " + item + " (size=" + queue.size() + ")");

        // Notify consumers that data is available
        notifyAll();
    }

    /**
     * Remove item from buffer. Blocks if buffer is empty.
     */
    public synchronized String take() throws InterruptedException {
        // Wait while buffer is empty
        while (queue.isEmpty()) {
            System.out.println("  [Buffer] Empty! Consumer waiting...");
            wait();
        }

        String item = queue.poll();
        System.out.println("  [Buffer] Removed: " + item + " (size=" + queue.size() + ")");

        // Notify producers that space is available
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }
}

/**
 * Producer that generates items and puts them in the buffer.
 */
class Producer implements Runnable {
    private final BoundedBuffer buffer;
    private final String name;

    public Producer(BoundedBuffer buffer, String name) {
        this.buffer = buffer;
        this.name = name;
    }

    @Override
    public void run() {
        int count = 0;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String item = name + "-Item-" + (++count);
                System.out.println("[" + name + "] Producing: " + item);
                buffer.put(item);

                // Simulate production time
                Thread.sleep((long) (Math.random() * 500));
            }
        } catch (InterruptedException e) {
            System.out.println("[" + name + "] Interrupted, stopping.");
            Thread.currentThread().interrupt();
        }
    }
}

/**
 * Consumer that takes items from the buffer and processes them.
 */
class Consumer implements Runnable {
    private final BoundedBuffer buffer;
    private final String name;

    public Consumer(BoundedBuffer buffer, String name) {
        this.buffer = buffer;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String item = buffer.take();
                System.out.println("[" + name + "] Consumed: " + item);

                // Simulate processing time
                Thread.sleep((long) (Math.random() * 800));
            }
        } catch (InterruptedException e) {
            System.out.println("[" + name + "] Interrupted, stopping.");
            Thread.currentThread().interrupt();
        }
    }
}

/*
 * SAMPLE OUTPUT (order varies):
 * 
 * === Producer-Consumer Pattern ===
 * 
 * [P1] Producing: P1-Item-1
 *   [Buffer] Added: P1-Item-1 (size=1)
 * [P2] Producing: P2-Item-1
 *   [Buffer] Added: P2-Item-1 (size=2)
 * [C1] Consumed: P1-Item-1
 *   [Buffer] Removed: P1-Item-1 (size=1)
 * [C2] Consumed: P2-Item-1
 *   [Buffer] Removed: P2-Item-1 (size=0)
 * [P1] Producing: P1-Item-2
 *   [Buffer] Added: P1-Item-2 (size=1)
 * ...
 *   [Buffer] Full! Producer waiting...
 * ...
 *   [Buffer] Empty! Consumer waiting...
 * ...
 * 
 * === All threads stopped ===
 * 
 * 
 * KEY POINTS:
 * 
 * 1. BOUNDED BUFFER: Limited size prevents unbounded memory growth
 * 
 * 2. BACK PRESSURE: Producers slow down when buffer is full
 *    (they block on wait(), giving consumers time to catch up)
 * 
 * 3. DECOUPLING: Producers and consumers work at their own pace
 *    The buffer absorbs bursts from either side
 * 
 * 4. notifyAll() vs notify():
 *    We use notifyAll() because both producers (waiting for space)
 *    and consumers (waiting for items) might be waiting.
 *    notify() might wake the wrong type of thread.
 * 
 * 5. GRACEFUL SHUTDOWN: We use interrupt() to signal threads to stop
 *    Threads check isInterrupted() and catch InterruptedException
 */
