/**
 * Exercise 02: Bounded Task Queue with BlockingQueue and Worker Threads
 *
 * TASK:
 * Implement a simple task processor:
 * - One BlockingQueue (e.g. ArrayBlockingQueue<Runnable>) with fixed capacity
 * - N worker threads that take tasks from the queue and run them
 * - A method submit(Runnable task) that puts the task on the queue (blocks if full)
 * - shutdown() to stop workers after queue is drained
 *
 * REQUIREMENTS:
 * 1. Constructor: TaskProcessor(int numWorkers, int queueCapacity)
 * 2. void submit(Runnable task) throws InterruptedException
 * 3. void shutdown() throws InterruptedException
 * 4. Workers loop: take() from queue, run task (or exit when shutdown)
 *
 * HINTS:
 * - Use ArrayBlockingQueue<Runnable>
 * - Workers: while (true) { Runnable r = queue.take(); if (r == POISON) break; r.run(); }
 * - For shutdown, you can use a poison task or a volatile boolean
 *
 * When done, compare with: solutions/Exercise02_Solution.java
 */

import java.util.concurrent.*;
import java.util.*;

public class Exercise02_BoundedTaskQueue {

    public static void main(String[] args) throws InterruptedException {
        // TODO: Create TaskProcessor(2, 5)
        // TODO: Submit several tasks, then shutdown
        // TaskProcessor processor = new TaskProcessor(2, 5);
        // for (int i = 0; i < 6; i++) { final int id = i; processor.submit(() -> System.out.println("Task " + id)); }
        // processor.shutdown();

        System.out.println("Implement TaskProcessor with BlockingQueue and workers!");
    }
}
