/**
 * Solution for Exercise 01: Fetch Two URLs in Parallel and Combine Results
 */

import java.util.concurrent.*;

public class Exercise01_Solution {

    public static void main(String[] args) throws Exception {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            sleep(200);
            return "Data A";
        });
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            sleep(150);
            return "Data B";
        });

        CompletableFuture<String> combined = f1.thenCombine(f2, (a, b) -> a + " | " + b);
        System.out.println("Combined: " + combined.get());
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
