/**
 * Exercise 01: Fetch Two URLs in Parallel and Combine Results
 *
 * TASK:
 * Simulate fetching data from two "URLs" in parallel using CompletableFuture,
 * then combine the two results into a single string (e.g. "Result1 | Result2").
 *
 * REQUIREMENTS:
 * 1. Use supplyAsync() for each fetch (simulate with sleep + return a string)
 * 2. Combine results using thenCombine() or allOf() + join()
 * 3. Print the combined result
 *
 * Simulate URLs: "https://api.service-a.com" returns "Data A", "https://api.service-b.com" returns "Data B"
 *
 * HINTS:
 * - CompletableFuture.supplyAsync(() -> { sleep(200); return "Data A"; })
 * - f1.thenCombine(f2, (a, b) -> a + " | " + b)
 *
 * When done, compare with: solutions/Exercise01_Solution.java
 */

import java.util.concurrent.*;

public class Exercise01_ParallelFetch {

    public static void main(String[] args) throws Exception {
        // TODO: Create two CompletableFutures that fetch in parallel
        // TODO: Combine their results and print

        System.out.println("Implement parallel fetch and combine!");
    }
}
