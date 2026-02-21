/**
 * Example 04: Scheduled Executor Service
 * 
 * Demonstrates:
 * 1. Delayed execution
 * 2. Fixed-rate scheduling
 * 3. Fixed-delay scheduling
 */

import java.util.concurrent.*;
import java.time.LocalTime;

public class Example04_ScheduledExecutor {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Scheduled Executor Demo ===\n");

        delayedExecutionDemo();
        fixedRateDemo();
        fixedDelayDemo();
    }

    private static void delayedExecutionDemo() throws Exception {
        System.out.println("--- Delayed Execution ---\n");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        System.out.println(time() + " Scheduling task with 2 second delay...");

        ScheduledFuture<String> future = scheduler.schedule(
            () -> {
                System.out.println(time() + " Task executed!");
                return "result";
            },
            2, TimeUnit.SECONDS
        );

        System.out.println(time() + " Waiting for result...");
        String result = future.get();
        System.out.println(time() + " Got result: " + result);

        scheduler.shutdown();
        System.out.println();
    }

    private static void fixedRateDemo() throws Exception {
        System.out.println("--- Fixed Rate Scheduling ---\n");
        System.out.println("Task runs every 1 second (regardless of task duration)");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
            () -> {
                System.out.println(time() + " Fixed-rate task running");
                sleep(300);  // Task takes 300ms
            },
            0,           // Initial delay
            1,           // Period
            TimeUnit.SECONDS
        );

        // Let it run a few times
        Thread.sleep(4500);
        future.cancel(false);
        scheduler.shutdown();
        System.out.println();
    }

    private static void fixedDelayDemo() throws Exception {
        System.out.println("--- Fixed Delay Scheduling ---\n");
        System.out.println("Task runs 1 second AFTER previous completion");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(
            () -> {
                System.out.println(time() + " Fixed-delay task running");
                sleep(300);  // Task takes 300ms
                System.out.println(time() + " Task finished, delay starts now");
            },
            0,           // Initial delay
            1,           // Delay between end and next start
            TimeUnit.SECONDS
        );

        // Let it run a few times
        Thread.sleep(5500);
        future.cancel(false);
        scheduler.shutdown();
        System.out.println();
    }

    private static String time() {
        return "[" + LocalTime.now().toString().substring(0, 12) + "]";
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
 * FIXED RATE vs FIXED DELAY:
 * 
 * Fixed Rate (scheduleAtFixedRate):
 * - Runs at: t=0, t=1000ms, t=2000ms, t=3000ms...
 * - If task takes 300ms, next starts at t=1000ms (not t=1300ms)
 * - If task takes longer than period, next starts immediately
 * 
 * Fixed Delay (scheduleWithFixedDelay):
 * - Runs at: t=0, finishes at t=300ms, next at t=1300ms
 * - Delay starts AFTER task completion
 * - Interval = task_duration + delay
 * 
 * Use Fixed Rate for: Metrics collection, heartbeats (consistent timing matters)
 * Use Fixed Delay for: Cleanup tasks, polling (avoid overlap matters)
 */
