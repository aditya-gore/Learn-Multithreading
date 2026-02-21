/**
 * Exercise 02: ThreadLocal Usage and Cleanup
 *
 * TASK:
 * Use ThreadLocal to store a per-thread "request ID" (e.g. a string or UUID).
 * Simulate 3 threads each setting their own request ID, doing work (e.g. print the ID),
 * then removing the value in a finally block to avoid leaks (important when using
 * thread pools).
 *
 * REQUIREMENTS:
 * 1. ThreadLocal<String> for request ID
 * 2. Each thread: set("request-" + threadId), do work (get and print), finally remove()
 * 3. Demonstrate that each thread sees only its own ID
 *
 * HINTS:
 * - ThreadLocal.withInitial(() -> ...) or new ThreadLocal<>() and set in thread
 * - try { ... } finally { threadLocal.remove(); }
 *
 * When done, compare with: solutions/Exercise02_Solution.java
 */

public class Exercise02_ThreadLocalUsage {

    public static void main(String[] args) throws InterruptedException {
        // TODO: Create ThreadLocal<String> for request ID
        // TODO: Start 3 threads, each sets its ID, prints it, removes in finally

        System.out.println("Implement ThreadLocal request ID with remove()!");
    }
}
