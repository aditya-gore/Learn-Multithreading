/**
 * Solution for Exercise 02: Chain 3 Async Steps with Error Handling and Fallback
 */

import java.util.concurrent.*;

public class Exercise02_Solution {

    public static void main(String[] args) throws Exception {
        CompletableFuture<String> chain = CompletableFuture
            .supplyAsync(() -> {
                sleep(100);
                return "step1";
            })
            .thenApply(s -> s + "-step2")
            .thenApply(s -> s + "-step3")
            .exceptionally(ex -> "fallback");

        System.out.println("Result: " + chain.get());
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
