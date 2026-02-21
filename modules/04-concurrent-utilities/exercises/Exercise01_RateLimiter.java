/**
 * Exercise 01: Rate Limiter with Semaphore
 * 
 * TASK:
 * Implement a rate limiter that allows at most N requests per second.
 * 
 * REQUIREMENTS:
 * 1. Allow up to N concurrent requests
 * 2. After each request completes, wait until the current second is over
 *    before releasing the permit
 * 3. tryAcquire() should return false if rate limit is reached
 * 
 * TEST SCENARIO:
 * - Rate limit: 5 requests per second
 * - Send 20 requests
 * - Should take approximately 4 seconds (5 requests each second)
 * 
 * HINTS:
 * 1. Use Semaphore with N permits
 * 2. Track time to ensure permits are released at second boundaries
 * 3. Consider using ScheduledExecutorService for permit release
 * 
 * BONUS: Implement a token bucket rate limiter (allows bursts)
 */

import java.util.concurrent.Semaphore;

public class Exercise01_RateLimiter {

    public static void main(String[] args) throws InterruptedException {
        // TODO: Implement SimpleRateLimiter class

        // Test:
        // SimpleRateLimiter limiter = new SimpleRateLimiter(5);  // 5 per second
        
        // Send 20 requests
        // long startTime = System.currentTimeMillis();
        // for (int i = 1; i <= 20; i++) {
        //     final int requestId = i;
        //     new Thread(() -> {
        //         if (limiter.tryAcquire()) {
        //             try {
        //                 System.out.println("Request " + requestId + " processed");
        //             } finally {
        //                 limiter.release();
        //             }
        //         } else {
        //             System.out.println("Request " + requestId + " rejected");
        //         }
        //     }).start();
        // }

        System.out.println("Implement SimpleRateLimiter and uncomment the test!");
    }
}

// TODO: Implement this class
// class SimpleRateLimiter {
//     private final Semaphore semaphore;
//     private final int permitsPerSecond;
//     
//     public SimpleRateLimiter(int permitsPerSecond) { }
//     
//     // Returns true if request is allowed, false if rate limited
//     public boolean tryAcquire() { }
//     
//     // Release permit (must be called after request completes)
//     public void release() { }
// }

/*
 * LEARNING GOALS:
 * - Use Semaphore for resource limiting
 * - Handle time-based permit replenishment
 * - Understand rate limiting patterns
 * 
 * When done, compare with: solutions/Exercise01_Solution.java
 */
