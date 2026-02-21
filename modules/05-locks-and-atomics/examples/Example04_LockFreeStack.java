/**
 * Example 04: Lock-Free Stack Implementation
 * 
 * Demonstrates building a thread-safe data structure using only CAS operations.
 * No locks are used - this is "lock-free" programming.
 */

import java.util.concurrent.atomic.AtomicReference;

public class Example04_LockFreeStack {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Lock-Free Stack Demo ===\n");

        LockFreeStack<Integer> stack = new LockFreeStack<>();

        // Test basic operations
        System.out.println("--- Basic Operations ---");
        stack.push(1);
        stack.push(2);
        stack.push(3);
        System.out.println("Pushed: 1, 2, 3");
        System.out.println("Pop: " + stack.pop());  // 3
        System.out.println("Pop: " + stack.pop());  // 2
        System.out.println("Pop: " + stack.pop());  // 1
        System.out.println("Pop (empty): " + stack.pop());  // null
        System.out.println();

        // Concurrent test
        System.out.println("--- Concurrent Test ---\n");
        LockFreeStack<Integer> concurrentStack = new LockFreeStack<>();
        int numThreads = 4;
        int opsPerThread = 10000;

        // Producer threads push values
        Thread[] producers = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            producers[i] = new Thread(() -> {
                for (int j = 0; j < opsPerThread; j++) {
                    concurrentStack.push(threadId * opsPerThread + j);
                }
            }, "Producer-" + i);
        }

        // Consumer threads pop values
        Thread[] consumers = new Thread[numThreads];
        int[] popCounts = new int[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            consumers[i] = new Thread(() -> {
                int count = 0;
                while (count < opsPerThread) {
                    Integer value = concurrentStack.pop();
                    if (value != null) {
                        count++;
                    }
                    // If null, stack was empty - try again
                }
                popCounts[threadId] = count;
            }, "Consumer-" + i);
        }

        // Start producers
        long startTime = System.currentTimeMillis();
        for (Thread t : producers) t.start();
        
        // Start consumers after small delay
        Thread.sleep(10);
        for (Thread t : consumers) t.start();

        // Wait for all
        for (Thread t : producers) t.join();
        for (Thread t : consumers) t.join();
        long endTime = System.currentTimeMillis();

        int totalPopped = 0;
        for (int count : popCounts) totalPopped += count;

        System.out.println("Pushed: " + (numThreads * opsPerThread));
        System.out.println("Popped: " + totalPopped);
        System.out.println("Stack empty: " + concurrentStack.isEmpty());
        System.out.println("Time: " + (endTime - startTime) + "ms");
        System.out.println("\nNo locks used - pure CAS operations!");
    }
}

/**
 * A thread-safe, lock-free stack implementation.
 * 
 * Uses AtomicReference for the head pointer and CAS for updates.
 * 
 * Lock-free guarantee: At least one thread makes progress in a finite
 * number of steps, even if other threads are delayed.
 */
class LockFreeStack<E> {
    private final AtomicReference<Node<E>> head = new AtomicReference<>(null);

    private static class Node<E> {
        final E value;
        Node<E> next;

        Node(E value) {
            this.value = value;
        }
    }

    /**
     * Push an element onto the stack.
     * 
     * Algorithm:
     * 1. Create new node
     * 2. Read current head
     * 3. Point new node's next to current head
     * 4. CAS head to new node
     * 5. If CAS fails (another thread modified head), retry from step 2
     */
    public void push(E value) {
        Node<E> newNode = new Node<>(value);
        Node<E> currentHead;
        do {
            currentHead = head.get();
            newNode.next = currentHead;
        } while (!head.compareAndSet(currentHead, newNode));
    }

    /**
     * Pop an element from the stack.
     * 
     * Algorithm:
     * 1. Read current head
     * 2. If null, stack is empty
     * 3. Get next node (new head)
     * 4. CAS head to next node
     * 5. If CAS fails (another thread modified head), retry from step 1
     * 
     * @return the popped value, or null if stack is empty
     */
    public E pop() {
        Node<E> currentHead;
        Node<E> newHead;
        do {
            currentHead = head.get();
            if (currentHead == null) {
                return null;  // Stack is empty
            }
            newHead = currentHead.next;
        } while (!head.compareAndSet(currentHead, newHead));
        return currentHead.value;
    }

    /**
     * Peek at the top element without removing it.
     */
    public E peek() {
        Node<E> currentHead = head.get();
        return currentHead != null ? currentHead.value : null;
    }

    /**
     * Check if the stack is empty.
     */
    public boolean isEmpty() {
        return head.get() == null;
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Lock-Free Stack Demo ===
 * 
 * --- Basic Operations ---
 * Pushed: 1, 2, 3
 * Pop: 3
 * Pop: 2
 * Pop: 1
 * Pop (empty): null
 * 
 * --- Concurrent Test ---
 * 
 * Pushed: 40000
 * Popped: 40000
 * Stack empty: true
 * Time: ~50ms
 * 
 * No locks used - pure CAS operations!
 * 
 * 
 * HOW IT WORKS:
 * 
 *   PUSH(3)                         HEAD
 *                                    │
 *   ┌─────┐                         ▼
 *   │  3  │─────────────────────▶ ┌─────┐    ┌─────┐
 *   └─────┘                       │  2  │───▶│  1  │───▶ null
 *      │                          └─────┘    └─────┘
 *      │
 *      └── CAS(head, [2], [3])
 *          If head is still [2], set to [3]
 * 
 *   If CAS fails (another thread pushed/popped), retry with new head.
 * 
 * 
 * KEY INSIGHTS:
 * 
 * 1. LOCK-FREE:
 *    No locks means no blocking. Threads might retry CAS multiple times,
 *    but they never sleep waiting for a lock.
 * 
 * 2. CAS LOOP PATTERN:
 *    do {
 *        read current state
 *        compute new state
 *    } while (!CAS(current, new))
 * 
 * 3. PROGRESS GUARANTEE:
 *    Even if one thread is suspended mid-operation, other threads can
 *    still make progress. This is the "lock-free" guarantee.
 * 
 * 4. ABA CAUTION:
 *    This simple implementation is vulnerable to ABA problem.
 *    For production, use AtomicStampedReference or other techniques.
 * 
 * 5. PERFORMANCE:
 *    Lock-free structures scale better under high contention because
 *    threads don't block each other.
 */
