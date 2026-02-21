/**
 * Example 01: Executor Framework Basics
 * 
 * Demonstrates:
 * 1. Different executor types
 * 2. Submitting tasks
 * 3. Proper shutdown
 */

import java.util.concurrent.*;

public class Example01_ExecutorBasics {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Executor Basics Demo ===\n");

        fixedThreadPoolDemo();
        cachedThreadPoolDemo();
        singleThreadExecutorDemo();
    }

    private static void fixedThreadPoolDemo() throws InterruptedException {
        System.out.println("--- Fixed Thread Pool ---\n");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Submit 6 tasks to 3 threads
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            executor.execute(() -> {
                String thread = Thread.currentThread().getName();
                System.out.println("[" + thread + "] Task " + taskId + " starting");
                sleep(500);
                System.out.println("[" + thread + "] Task " + taskId + " completed");
            });
        }

        // Shutdown and wait
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("All tasks completed!\n");
    }

    private static void cachedThreadPoolDemo() throws InterruptedException {
        System.out.println("--- Cached Thread Pool ---\n");

        ExecutorService executor = Executors.newCachedThreadPool();

        // Submit tasks quickly - new threads created as needed
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            executor.execute(() -> {
                String thread = Thread.currentThread().getName();
                System.out.println("[" + thread + "] Task " + taskId);
                sleep(100);
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("Cached pool done!\n");
    }

    private static void singleThreadExecutorDemo() throws InterruptedException {
        System.out.println("--- Single Thread Executor ---\n");

        ExecutorService executor = Executors.newSingleThreadExecutor();

        // All tasks run sequentially on one thread
        for (int i = 1; i <= 3; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println("Task " + taskId + " - Thread: " + 
                    Thread.currentThread().getName());
                sleep(200);
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("Single thread done!\n");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Executor Basics Demo ===
 * 
 * --- Fixed Thread Pool ---
 * [pool-1-thread-1] Task 1 starting
 * [pool-1-thread-2] Task 2 starting
 * [pool-1-thread-3] Task 3 starting
 * [pool-1-thread-1] Task 1 completed
 * [pool-1-thread-1] Task 4 starting
 * [pool-1-thread-2] Task 2 completed
 * [pool-1-thread-2] Task 5 starting
 * ...
 * All tasks completed!
 * 
 * Note: With 3 threads and 6 tasks, threads are reused.
 */
