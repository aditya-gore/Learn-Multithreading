/**
 * Example 03: Combining Multiple CompletableFutures
 *
 * Demonstrates:
 * - allOf: wait for all futures, then combine results
 * - anyOf: use result of first to complete
 */

import java.util.concurrent.*;
import java.util.*;

public class Example03_CombiningFutures {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Combining Futures ===\n");

        allOfDemo();
        anyOfDemo();
    }

    private static void allOfDemo() throws Exception {
        System.out.println("--- allOf: wait for all, then combine ---\n");

        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return "A";
        });
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            sleep(200);
            return "B";
        });
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> {
            sleep(100);
            return "C";
        });

        CompletableFuture<Void> allDone = CompletableFuture.allOf(f1, f2, f3);
        allDone.thenRun(() -> {
            String combined = f1.join() + ", " + f2.join() + ", " + f3.join();
            System.out.println("All results: " + combined);
        }).join();
        System.out.println();
    }

    private static void anyOfDemo() throws Exception {
        System.out.println("--- anyOf: first to complete wins ---\n");

        CompletableFuture<String> slow = CompletableFuture.supplyAsync(() -> {
            sleep(500);
            return "slow";
        });
        CompletableFuture<String> fast = CompletableFuture.supplyAsync(() -> {
            sleep(100);
            return "fast";
        });

        CompletableFuture<Object> first = CompletableFuture.anyOf(slow, fast);
        System.out.println("First result: " + first.get());
        System.out.println();
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
