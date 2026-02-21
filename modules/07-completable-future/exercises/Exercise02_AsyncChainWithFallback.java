/**
 * Exercise 02: Chain 3 Async Steps with Error Handling and Fallback
 *
 * TASK:
 * Implement a chain of 3 async steps:
 * 1. Step 1: supplyAsync -> returns a value (e.g. "step1")
 * 2. Step 2: thenApply -> transforms it (e.g. append "-step2")
 * 3. Step 3: thenApply -> transforms again (e.g. append "-step3")
 *
 * Add error handling so that if any step throws, the result is "fallback" (or similar).
 * Use exceptionally() or handle().
 *
 * REQUIREMENTS:
 * 1. Use supplyAsync for first step, thenApply for next two
 * 2. Add exceptionally or handle to return "fallback" on any exception
 * 3. Print final result (either the chain result or "fallback")
 *
 * BONUS: Make step 2 randomly throw to test the fallback.
 *
 * When done, compare with: solutions/Exercise02_Solution.java
 */

import java.util.concurrent.*;

public class Exercise02_AsyncChainWithFallback {

    public static void main(String[] args) throws Exception {
        // TODO: Build chain: supplyAsync -> thenApply -> thenApply
        // TODO: Add exceptionally or handle for fallback
        // TODO: Print result

        System.out.println("Implement async chain with fallback!");
    }
}
