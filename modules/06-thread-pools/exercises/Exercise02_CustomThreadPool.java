/**
 * Exercise 02: Build a Mini Thread Pool
 *
 * TASK:
 * Implement a simple thread pool from scratch that:
 * - Has a fixed number of worker threads
 * - Accepts Runnable tasks via submit()
 * - Uses a bounded blocking queue for the task queue
 * - Supports shutdown() to stop accepting new tasks and wait for completion
 * - Supports shutdownNow() to interrupt running workers
 *
 * REQUIREMENTS:
 * 1. Constructor: MiniThreadPool(int numThreads, int queueCapacity)
 * 2. void submit(Runnable task) - blocks if queue is full (or use offer with timeout)
 * 3. void shutdown() - no new tasks; wait for queue to drain
 * 4. List<Runnable> shutdownNow() - interrupt workers, return unexecuted tasks
 *
 * HINTS:
 * - Use a BlockingQueue (e.g. ArrayBlockingQueue) for the task queue
 * - Each worker thread runs in a loop: take task from queue, run it (or exit if shutdown)
 * - Use volatile boolean or AtomicBoolean for shutdown state
 * - On shutdownNow(), interrupt all worker threads
 */

import java.util.concurrent.*;
import java.util.*;

public class Exercise02_CustomThreadPool {

    public static void main(String[] args) throws InterruptedException {
        // TODO: Create MiniThreadPool with 2 threads, queue capacity 10

        // MiniThreadPool pool = new MiniThreadPool(2, 10);
        // for (int i = 0; i < 5; i++) {
        //     final int id = i;
        //     pool.submit(() -> System.out.println("Task " + id + " by " + Thread.currentThread().getName()));
        // }
        // pool.shutdown();

        System.out.println("Implement MiniThreadPool and uncomment the test!");
    }
}

// TODO: Implement this class
// class MiniThreadPool {
//     private final BlockingQueue<Runnable> workQueue;
//     private final List<Thread> workers;
//     private volatile boolean shutdown = false;
//
//     public MiniThreadPool(int numThreads, int queueCapacity) { }
//
//     public void submit(Runnable task) throws InterruptedException { }
//
//     public void shutdown() throws InterruptedException { }
//
//     public List<Runnable> shutdownNow() { }
// }

/*
 * LEARNING GOALS:
 * - Understand how a thread pool works internally
 * - Use BlockingQueue for producer-consumer between submit and workers
 * - Handle graceful vs abrupt shutdown
 *
 * When done, compare with: solutions/Exercise02_Solution.java
 */
