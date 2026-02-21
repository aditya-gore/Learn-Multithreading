/**
 * Example 01: CountDownLatch
 * 
 * Demonstrates using CountDownLatch for:
 * 1. Waiting for multiple services to start
 * 2. Coordinating test scenarios
 * 3. One-time gate pattern
 */

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Example01_CountDownLatch {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== CountDownLatch Demo ===\n");

        serviceStartupExample();
        racingStartExample();
    }

    /**
     * Common use case: Wait for multiple services to initialize.
     */
    private static void serviceStartupExample() throws InterruptedException {
        System.out.println("--- Service Startup Example ---\n");

        int serviceCount = 3;
        CountDownLatch latch = new CountDownLatch(serviceCount);

        // Start services
        String[] services = {"Database", "Cache", "MessageQueue"};
        for (String serviceName : services) {
            new Thread(new ServiceStarter(serviceName, latch)).start();
        }

        System.out.println("[Main] Waiting for all services to start...\n");

        // Wait for all services (with timeout)
        boolean allStarted = latch.await(10, TimeUnit.SECONDS);

        if (allStarted) {
            System.out.println("\n[Main] All services started! Application ready.");
        } else {
            System.out.println("\n[Main] Timeout! Some services failed to start.");
        }
        System.out.println();
    }

    /**
     * Use case: Start multiple threads at exactly the same time.
     * Useful for testing race conditions.
     */
    private static void racingStartExample() throws InterruptedException {
        System.out.println("--- Racing Start Example ---\n");

        int racerCount = 5;
        CountDownLatch readyLatch = new CountDownLatch(racerCount);  // Racers signal ready
        CountDownLatch startLatch = new CountDownLatch(1);           // Start signal

        for (int i = 1; i <= racerCount; i++) {
            new Thread(new Racer(i, readyLatch, startLatch)).start();
        }

        // Wait for all racers to be ready
        System.out.println("[Starter] Waiting for racers to be ready...");
        readyLatch.await();

        // All ready - give start signal
        System.out.println("[Starter] All ready! GO!\n");
        startLatch.countDown();  // Release all racers simultaneously

        Thread.sleep(1000);  // Let race complete
        System.out.println();
    }
}

class ServiceStarter implements Runnable {
    private final String serviceName;
    private final CountDownLatch latch;

    public ServiceStarter(String serviceName, CountDownLatch latch) {
        this.serviceName = serviceName;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            System.out.println("[" + serviceName + "] Starting...");
            
            // Simulate startup time (different for each service)
            long startupTime = (long) (Math.random() * 2000) + 500;
            Thread.sleep(startupTime);
            
            System.out.println("[" + serviceName + "] Started! (took " + startupTime + "ms)");
        } catch (InterruptedException e) {
            System.out.println("[" + serviceName + "] Startup interrupted!");
            Thread.currentThread().interrupt();
        } finally {
            latch.countDown();  // Signal completion even if interrupted
        }
    }
}

class Racer implements Runnable {
    private final int id;
    private final CountDownLatch readyLatch;
    private final CountDownLatch startLatch;

    public Racer(int id, CountDownLatch readyLatch, CountDownLatch startLatch) {
        this.id = id;
        this.readyLatch = readyLatch;
        this.startLatch = startLatch;
    }

    @Override
    public void run() {
        try {
            System.out.println("[Racer-" + id + "] Ready!");
            readyLatch.countDown();  // Signal ready

            startLatch.await();      // Wait for start signal

            // Race!
            long raceTime = (long) (Math.random() * 500) + 100;
            Thread.sleep(raceTime);
            System.out.println("[Racer-" + id + "] Finished! (time: " + raceTime + "ms)");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === CountDownLatch Demo ===
 * 
 * --- Service Startup Example ---
 * 
 * [Main] Waiting for all services to start...
 * 
 * [Database] Starting...
 * [Cache] Starting...
 * [MessageQueue] Starting...
 * [Cache] Started! (took 756ms)
 * [MessageQueue] Started! (took 1234ms)
 * [Database] Started! (took 1567ms)
 * 
 * [Main] All services started! Application ready.
 * 
 * --- Racing Start Example ---
 * 
 * [Starter] Waiting for racers to be ready...
 * [Racer-1] Ready!
 * [Racer-2] Ready!
 * [Racer-3] Ready!
 * [Racer-4] Ready!
 * [Racer-5] Ready!
 * [Starter] All ready! GO!
 * 
 * [Racer-3] Finished! (time: 145ms)
 * [Racer-1] Finished! (time: 267ms)
 * ...
 * 
 * 
 * KEY INSIGHTS:
 * 
 * 1. ONE-TIME USE: CountDownLatch cannot be reset. Once count reaches 0,
 *    it stays at 0. Use CyclicBarrier or Phaser if you need reset.
 * 
 * 2. FLEXIBLE SIGNALING: Any thread can call countDown(). The threads
 *    signaling don't have to be the same as the ones waiting.
 * 
 * 3. TIMEOUT SUPPORT: await(timeout, unit) returns false if timeout
 *    expires before count reaches 0.
 * 
 * 4. RACING START PATTERN: Using two latches:
 *    - One for participants to signal ready
 *    - One for coordinator to signal start
 *    This ensures all start at exactly the same moment.
 */
