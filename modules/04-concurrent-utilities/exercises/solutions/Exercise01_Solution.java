/**
 * Solution for Exercise 01: Rate Limiter with Semaphore
 */

import java.util.concurrent.Semaphore;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exercise01_Solution {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Rate Limiter Test ===\n");

        SimpleRateLimiter limiter = new SimpleRateLimiter(5);  // 5 per second

        long startTime = System.currentTimeMillis();
        Thread[] threads = new Thread[20];

        for (int i = 0; i < 20; i++) {
            final int requestId = i + 1;
            threads[i] = new Thread(() -> {
                try {
                    limiter.acquire();  // Block until allowed
                    long elapsed = System.currentTimeMillis() - startTime;
                    System.out.printf("[%4dms] Request %d processed%n", elapsed, requestId);
                    Thread.sleep(50);  // Simulate request processing
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    limiter.release();
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("\nTotal time: " + totalTime + "ms");
        System.out.println("Expected: ~4000ms (20 requests at 5/second)");

        limiter.shutdown();
    }
}

/**
 * Simple rate limiter using Semaphore.
 * Allows N requests per second by replenishing permits every second.
 */
class SimpleRateLimiter {
    private final Semaphore semaphore;
    private final int permitsPerSecond;
    private final ScheduledExecutorService scheduler;

    public SimpleRateLimiter(int permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
        this.semaphore = new Semaphore(permitsPerSecond);
        this.scheduler = Executors.newScheduledThreadPool(1);

        // Replenish permits every second
        scheduler.scheduleAtFixedRate(() -> {
            int permitsToRelease = permitsPerSecond - semaphore.availablePermits();
            if (permitsToRelease > 0) {
                semaphore.release(permitsToRelease);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * Acquire a permit, blocking if rate limit is exceeded.
     */
    public void acquire() throws InterruptedException {
        semaphore.acquire();
    }

    /**
     * Try to acquire a permit without blocking.
     */
    public boolean tryAcquire() {
        return semaphore.tryAcquire();
    }

    /**
     * Release a permit back.
     * Note: In this implementation, we don't actually need to call release
     * because permits are replenished automatically every second.
     * This method is here for API consistency.
     */
    public void release() {
        // Permits are replenished by the scheduler, not by release
        // This is intentional to maintain the rate limit
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Rate Limiter Test ===
 * 
 * [   0ms] Request 1 processed
 * [   0ms] Request 2 processed
 * [   0ms] Request 3 processed
 * [   1ms] Request 4 processed
 * [   2ms] Request 5 processed
 * [1001ms] Request 6 processed
 * [1002ms] Request 7 processed
 * [1002ms] Request 8 processed
 * [1003ms] Request 9 processed
 * [1004ms] Request 10 processed
 * [2001ms] Request 11 processed
 * ...
 * 
 * Total time: ~4000ms
 * 
 * 
 * KEY INSIGHTS:
 * 
 * 1. PERMIT REPLENISHMENT: Instead of tracking individual request times,
 *    we replenish permits every second using a ScheduledExecutorService.
 * 
 * 2. NO RELEASE NEEDED: The caller doesn't need to release permits because
 *    they're replenished automatically. This is simpler to use.
 * 
 * 3. BURST HANDLING: This allows bursts up to N requests instantly,
 *    then throttles until the next second.
 * 
 * 
 * ALTERNATIVE: Token Bucket Rate Limiter
 * 
 * A more sophisticated approach that:
 * - Allows smooth request spacing
 * - Supports configurable burst capacity
 * - Uses timestamps instead of scheduled replenishment
 */
class TokenBucketRateLimiter {
    private final long capacity;
    private final double refillRate;  // permits per millisecond
    private double tokens;
    private long lastRefillTime;

    public TokenBucketRateLimiter(int permitsPerSecond, int burstCapacity) {
        this.capacity = burstCapacity;
        this.refillRate = permitsPerSecond / 1000.0;
        this.tokens = burstCapacity;
        this.lastRefillTime = System.currentTimeMillis();
    }

    public synchronized boolean tryAcquire() {
        refill();
        if (tokens >= 1) {
            tokens--;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double tokensToAdd = (now - lastRefillTime) * refillRate;
        tokens = Math.min(capacity, tokens + tokensToAdd);
        lastRefillTime = now;
    }
}
