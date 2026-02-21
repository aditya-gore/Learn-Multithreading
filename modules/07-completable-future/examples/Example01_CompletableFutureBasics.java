/**
 * CompletableFuture Examples
 */

import java.util.concurrent.*;
import java.util.*;

public class Example01_CompletableFutureBasics {

    public static void main(String[] args) throws Exception {
        System.out.println("=== CompletableFuture Demo ===\n");

        // Basic creation and chaining
        CompletableFuture<String> future = CompletableFuture
            .supplyAsync(() -> {
                System.out.println("Fetching data...");
                sleep(500);
                return "Hello";
            })
            .thenApply(s -> {
                System.out.println("Transforming...");
                return s + " World";
            })
            .thenApply(String::toUpperCase);

        System.out.println("Result: " + future.get());
        System.out.println();

        // Combining two futures
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return "Price: $100";
        });
        
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            sleep(200);
            return "Stock: 50 units";
        });

        CompletableFuture<String> combined = f1.thenCombine(f2, 
            (price, stock) -> price + ", " + stock);
        System.out.println("Combined: " + combined.get());
        System.out.println();

        // Error handling
        CompletableFuture<String> withError = CompletableFuture
            .supplyAsync(() -> {
                if (true) throw new RuntimeException("Service unavailable");
                return "data";
            })
            .exceptionally(ex -> "Fallback: " + ex.getMessage());

        System.out.println("With error handling: " + withError.get());
        System.out.println();

        // Waiting for all
        List<CompletableFuture<Integer>> futures = Arrays.asList(
            CompletableFuture.supplyAsync(() -> { sleep(300); return 1; }),
            CompletableFuture.supplyAsync(() -> { sleep(200); return 2; }),
            CompletableFuture.supplyAsync(() -> { sleep(100); return 3; })
        );

        CompletableFuture<Void> allDone = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        allDone.thenRun(() -> {
            int sum = futures.stream()
                .mapToInt(CompletableFuture::join)
                .sum();
            System.out.println("Sum of all: " + sum);
        }).join();
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
}
