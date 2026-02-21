/**
 * Exercise 02: Bounded Buffer with Timeout
 * 
 * TASK:
 * Implement a bounded buffer that supports:
 * - put(item) - blocks until space available
 * - take() - blocks until item available  
 * - put(item, timeout) - blocks max timeout ms, returns false if timeout
 * - take(timeout) - blocks max timeout ms, returns null if timeout
 * 
 * REQUIREMENTS:
 * 1. Thread-safe using wait/notify
 * 2. Proper handling of spurious wakeups
 * 3. Timeout versions should return early if timeout expires
 * 
 * TEST SCENARIOS:
 * 1. Normal producer-consumer (should work like Example03)
 * 2. Producer timeout when buffer stays full
 * 3. Consumer timeout when buffer stays empty
 * 
 * HINTS:
 * 1. For timeout, track elapsed time in the while loop
 * 2. Use wait(remainingTime) instead of wait()
 * 3. Re-check time after each wakeup
 */
public class Exercise02_BoundedBuffer {

    public static void main(String[] args) throws InterruptedException {
        // TODO: Implement TimeoutBoundedBuffer class

        // Test scenarios:
        // 1. Normal operation
        // 2. put() timeout test - fill buffer, try put with timeout
        // 3. take() timeout test - empty buffer, try take with timeout

        System.out.println("Implement TimeoutBoundedBuffer and uncomment tests!");
    }
}

// TODO: Implement this class
// class TimeoutBoundedBuffer<E> {
//     private final int capacity;
//     // ...
//     
//     public synchronized void put(E item) throws InterruptedException { }
//     
//     public synchronized E take() throws InterruptedException { }
//     
//     // Returns false if timeout expires before space is available
//     public synchronized boolean put(E item, long timeoutMs) throws InterruptedException { }
//     
//     // Returns null if timeout expires before item is available
//     public synchronized E take(long timeoutMs) throws InterruptedException { }
// }

/*
 * LEARNING GOALS:
 * - Implement timeout-based waiting
 * - Handle spurious wakeups with timeout tracking
 * - Practice the full blocking queue pattern
 * 
 * When done, compare with: solutions/Exercise02_Solution.java
 */
