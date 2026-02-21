/**
 * Exercise 02: Thread-Safe Counter with Multiple Operations
 * 
 * TASK:
 * Implement a thread-safe counter that supports:
 * - increment()
 * - decrement()
 * - add(value)
 * - getValue()
 * - compareAndSet(expected, newValue) - only sets if current value == expected
 * - incrementAndGet() - returns the new value after incrementing
 * - getAndIncrement() - returns the old value before incrementing
 * 
 * REQUIREMENTS:
 * 1. All operations must be thread-safe
 * 2. Operations that return values must return the correct value relative to the operation
 * 3. compareAndSet must be atomic
 * 
 * TEST SCENARIO:
 * - Start counter at 0
 * - 4 threads each call increment() 10000 times
 * - Final value should be 40000
 * 
 * CHALLENGE:
 * After implementing with synchronized, think about:
 * - Could this be implemented without locks? (Preview of Module 5 - AtomicInteger)
 * - What's the performance impact of synchronized?
 */
public class Exercise02_ThreadSafeCounter {

    public static void main(String[] args) throws InterruptedException {
        // TODO: Implement ThreadSafeCounter class

        // Test your implementation:
        // ThreadSafeCounter counter = new ThreadSafeCounter();
        
        // Create 4 threads, each incrementing 10000 times
        // int numThreads = 4;
        // int incrementsPerThread = 10000;
        // ...
        
        // Verify final value is 40000
        
        System.out.println("Implement the ThreadSafeCounter class and uncomment the test code!");
    }
}

// TODO: Implement this class
// class ThreadSafeCounter {
//     private int value = 0;
//     
//     public synchronized void increment() { }
//     
//     public synchronized void decrement() { }
//     
//     public synchronized void add(int delta) { }
//     
//     public synchronized int getValue() { }
//     
//     // Returns true if value was updated, false otherwise
//     public synchronized boolean compareAndSet(int expected, int newValue) { }
//     
//     // Increments and returns the NEW value
//     public synchronized int incrementAndGet() { }
//     
//     // Returns the OLD value, then increments
//     public synchronized int getAndIncrement() { }
// }

/*
 * LEARNING GOALS:
 * - Practice implementing common counter operations
 * - Understand the importance of atomic compound operations
 * - Prepare for AtomicInteger which provides these operations lock-free
 * 
 * When done, compare with: solutions/Exercise02_Solution.java
 */
