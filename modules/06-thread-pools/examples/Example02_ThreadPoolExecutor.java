/**
 * Example 02: Custom ThreadPoolExecutor Configuration
 * 
 * Demonstrates:
 * 1. ThreadPoolExecutor parameters
 * 2. Different queue types
 * 3. Rejection policies
 */

import java.util.concurrent.*;

public class Example02_ThreadPoolExecutor {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ThreadPoolExecutor Demo ===\n");

        customPoolDemo();
        rejectionPolicyDemo();
    }

    private static void customPoolDemo() throws InterruptedException {
        System.out.println("--- Custom Pool Configuration ---\n");

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,                      // corePoolSize
            4,                      // maximumPoolSize
            30, TimeUnit.SECONDS,   // keepAliveTime
            new ArrayBlockingQueue<>(2),  // Bounded queue
            new ThreadPoolExecutor.CallerRunsPolicy()
        );

        System.out.println("Core pool size: " + executor.getCorePoolSize());
        System.out.println("Max pool size: " + executor.getMaximumPoolSize());
        System.out.println("Queue capacity: 2");

        // Submit more tasks than pool can handle
        for (int i = 1; i <= 10; i++) {
            final int taskId = i;
            System.out.println("Submitting task " + taskId);
            executor.execute(() -> {
                System.out.println("  [" + Thread.currentThread().getName() + 
                    "] Task " + taskId + " running");
                sleep(1000);
            });
            
            System.out.println("  Pool size: " + executor.getPoolSize() + 
                ", Queue size: " + executor.getQueue().size() +
                ", Active: " + executor.getActiveCount());
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        System.out.println("\nCompleted tasks: " + executor.getCompletedTaskCount());
        System.out.println();
    }

    private static void rejectionPolicyDemo() throws InterruptedException {
        System.out.println("--- Rejection Policies Demo ---\n");

        // Small pool that will reject tasks
        ThreadPoolExecutor abortExecutor = new ThreadPoolExecutor(
            1, 1, 0, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.AbortPolicy()
        );

        // Submit tasks until rejection
        System.out.println("AbortPolicy (throws exception):");
        try {
            for (int i = 1; i <= 10; i++) {
                final int taskId = i;
                abortExecutor.execute(() -> sleep(1000));
                System.out.println("  Task " + taskId + " submitted");
            }
        } catch (RejectedExecutionException e) {
            System.out.println("  Task rejected: " + e.getMessage());
        }
        abortExecutor.shutdownNow();

        // CallerRunsPolicy
        System.out.println("\nCallerRunsPolicy (caller runs the task):");
        ThreadPoolExecutor callerRunsExecutor = new ThreadPoolExecutor(
            1, 1, 0, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );

        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            System.out.println("  Submitting task " + taskId);
            callerRunsExecutor.execute(() -> {
                System.out.println("    Task " + taskId + " runs in: " + 
                    Thread.currentThread().getName());
                sleep(200);
            });
        }
        callerRunsExecutor.shutdown();
        callerRunsExecutor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
