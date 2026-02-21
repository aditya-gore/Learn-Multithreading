/**
 * Example 03: Callable and Future
 * 
 * Demonstrates:
 * 1. Callable for returning values
 * 2. Future for async results
 * 3. invokeAll and invokeAny
 */

import java.util.concurrent.*;
import java.util.*;

public class Example03_CallableAndFuture {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Callable and Future Demo ===\n");

        futureBasicsDemo();
        invokeAllDemo();
        invokeAnyDemo();
    }

    private static void futureBasicsDemo() throws Exception {
        System.out.println("--- Future Basics ---\n");

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Submit Callable that returns a value
        Callable<Integer> task = () -> {
            System.out.println("Computing...");
            Thread.sleep(1000);
            return 42;
        };

        Future<Integer> future = executor.submit(task);

        System.out.println("Task submitted, isDone: " + future.isDone());

        // Do other work while task executes
        System.out.println("Doing other work...");

        // Get result (blocks if not ready)
        Integer result = future.get();
        System.out.println("Result: " + result);
        System.out.println("isDone: " + future.isDone());

        // Future with timeout
        Future<String> slowFuture = executor.submit(() -> {
            Thread.sleep(5000);
            return "slow result";
        });

        try {
            String slowResult = slowFuture.get(1, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.out.println("Timed out waiting for slow task");
            slowFuture.cancel(true);
        }

        executor.shutdown();
        System.out.println();
    }

    private static void invokeAllDemo() throws Exception {
        System.out.println("--- invokeAll Demo ---\n");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        List<Callable<Integer>> tasks = Arrays.asList(
            () -> { Thread.sleep(1000); return 1; },
            () -> { Thread.sleep(500);  return 2; },
            () -> { Thread.sleep(1500); return 3; }
        );

        System.out.println("Submitting 3 tasks...");
        long start = System.currentTimeMillis();

        // invokeAll waits for ALL tasks to complete
        List<Future<Integer>> futures = executor.invokeAll(tasks);

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("All completed in " + elapsed + "ms");

        for (int i = 0; i < futures.size(); i++) {
            System.out.println("Task " + (i+1) + " result: " + futures.get(i).get());
        }

        executor.shutdown();
        System.out.println();
    }

    private static void invokeAnyDemo() throws Exception {
        System.out.println("--- invokeAny Demo ---\n");

        ExecutorService executor = Executors.newFixedThreadPool(3);

        List<Callable<String>> tasks = Arrays.asList(
            () -> { Thread.sleep(3000); return "slow"; },
            () -> { Thread.sleep(1000); return "medium"; },
            () -> { Thread.sleep(500);  return "fast"; }
        );

        System.out.println("Submitting 3 tasks, waiting for first...");
        long start = System.currentTimeMillis();

        // invokeAny returns FIRST result, cancels others
        String result = executor.invokeAny(tasks);

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("First result: " + result + " (in " + elapsed + "ms)");

        executor.shutdown();
        System.out.println();
    }
}
