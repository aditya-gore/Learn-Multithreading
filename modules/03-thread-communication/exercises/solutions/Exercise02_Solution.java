/**
 * Solution for Exercise 02: Bounded Buffer with Timeout
 */

import java.util.LinkedList;
import java.util.Queue;

public class Exercise02_Solution {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Bounded Buffer with Timeout ===\n");

        TimeoutBoundedBuffer<Integer> buffer = new TimeoutBoundedBuffer<>(3);

        testNormalOperation(buffer);
        testPutTimeout(buffer);
        testTakeTimeout(buffer);
    }

    private static void testNormalOperation(TimeoutBoundedBuffer<Integer> buffer) 
            throws InterruptedException {
        System.out.println("--- Normal Operation ---");
        
        buffer.put(1);
        buffer.put(2);
        System.out.println("Added 1 and 2");
        
        System.out.println("Took: " + buffer.take());
        System.out.println("Took: " + buffer.take());
        System.out.println();
    }

    private static void testPutTimeout(TimeoutBoundedBuffer<Integer> buffer) 
            throws InterruptedException {
        System.out.println("--- Put Timeout Test ---");
        
        // Fill the buffer
        buffer.put(1);
        buffer.put(2);
        buffer.put(3);
        System.out.println("Buffer full (capacity 3)");

        // Try to put with timeout
        System.out.println("Trying put with 1 second timeout...");
        long start = System.currentTimeMillis();
        boolean success = buffer.put(4, 1000);
        long elapsed = System.currentTimeMillis() - start;
        
        System.out.println("Result: " + success + " (took " + elapsed + "ms)");
        
        // Clean up
        buffer.take();
        buffer.take();
        buffer.take();
        System.out.println();
    }

    private static void testTakeTimeout(TimeoutBoundedBuffer<Integer> buffer) 
            throws InterruptedException {
        System.out.println("--- Take Timeout Test ---");
        System.out.println("Buffer is empty");

        // Try to take with timeout
        System.out.println("Trying take with 1 second timeout...");
        long start = System.currentTimeMillis();
        Integer result = buffer.take(1000);
        long elapsed = System.currentTimeMillis() - start;
        
        System.out.println("Result: " + result + " (took " + elapsed + "ms)");
        System.out.println();
    }
}

class TimeoutBoundedBuffer<E> {
    private final Queue<E> queue = new LinkedList<>();
    private final int capacity;

    public TimeoutBoundedBuffer(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Blocking put - waits indefinitely until space is available.
     */
    public synchronized void put(E item) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();
        }
        queue.add(item);
        notifyAll();
    }

    /**
     * Blocking take - waits indefinitely until item is available.
     */
    public synchronized E take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        E item = queue.poll();
        notifyAll();
        return item;
    }

    /**
     * Put with timeout.
     * @return true if item was added, false if timeout expired
     */
    public synchronized boolean put(E item, long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        
        while (queue.size() == capacity) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) {
                return false;  // Timeout expired
            }
            wait(remaining);  // Wait for remaining time
        }
        
        queue.add(item);
        notifyAll();
        return true;
    }

    /**
     * Take with timeout.
     * @return the item, or null if timeout expired
     */
    public synchronized E take(long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        
        while (queue.isEmpty()) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) {
                return null;  // Timeout expired
            }
            wait(remaining);  // Wait for remaining time
        }
        
        E item = queue.poll();
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Bounded Buffer with Timeout ===
 * 
 * --- Normal Operation ---
 * Added 1 and 2
 * Took: 1
 * Took: 2
 * 
 * --- Put Timeout Test ---
 * Buffer full (capacity 3)
 * Trying put with 1 second timeout...
 * Result: false (took ~1000ms)
 * 
 * --- Take Timeout Test ---
 * Buffer is empty
 * Trying take with 1 second timeout...
 * Result: null (took ~1000ms)
 * 
 * 
 * KEY INSIGHTS:
 * 
 * 1. DEADLINE PATTERN:
 *    Calculate the deadline upfront: deadline = now + timeout
 *    After each wakeup: remaining = deadline - now
 *    This handles spurious wakeups correctly!
 * 
 * 2. EARLY EXIT:
 *    If remaining <= 0, exit immediately without waiting
 * 
 * 3. PARTIAL WAIT:
 *    wait(remaining) waits for at most the remaining time
 *    If notified early, we re-check the condition
 *    If we wake up (spuriously or from timeout), we recalculate remaining
 * 
 * 4. RETURN VALUES:
 *    - put returns boolean (success/failure)
 *    - take returns E or null (got item / timed out)
 *    This is the standard pattern used by BlockingQueue.offer/poll
 * 
 * 
 * WHY System.currentTimeMillis() IN LOOP?
 * 
 * Spurious wakeups can happen! We might wake up:
 * - From notify() but condition still false
 * - Spuriously (JVM/OS reasons)
 * - From timeout
 * 
 * Each time we wake, we must:
 * 1. Check if condition is now true
 * 2. If not, check if we have time left
 * 3. If we do, wait again for remaining time
 */
