/**
 * Example 02: Chaining and Error Handling with CompletableFuture
 *
 * Demonstrates:
 * - thenApply, thenCompose (flatten nested futures)
 * - exceptionally, handle, whenComplete
 */

import java.util.concurrent.*;

public class Example02_ChainingAndErrorHandling {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Chaining and Error Handling ===\n");

        thenComposeDemo();
        errorHandlingDemo();
    }

    private static void thenComposeDemo() throws Exception {
        System.out.println("--- thenCompose (async step returns future) ---\n");

        CompletableFuture<String> result = CompletableFuture
            .supplyAsync(() -> {
                sleep(200);
                return "user-123";
            })
            .thenCompose(userId -> fetchUserDetails(userId));

        System.out.println("User details: " + result.get());
        System.out.println();
    }

    private static CompletableFuture<String> fetchUserDetails(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return "User(" + userId + "): Alice, admin";
        });
    }

    private static void errorHandlingDemo() throws Exception {
        System.out.println("--- Error handling: exceptionally vs handle ---\n");

        CompletableFuture<String> withExceptionally = CompletableFuture
            .supplyAsync(() -> {
                if (System.currentTimeMillis() % 2 == 0) throw new RuntimeException("Service down");
                return "data";
            })
            .exceptionally(ex -> "Fallback: " + ex.getMessage());

        System.out.println("exceptionally result: " + withExceptionally.get());

        CompletableFuture<String> withHandle = CompletableFuture
            .supplyAsync(() -> "success")
            .handle((res, ex) -> {
                if (ex != null) return "error: " + ex.getMessage();
                return "ok: " + res;
            });

        System.out.println("handle result: " + withHandle.get());

        CompletableFuture<String> whenComplete = CompletableFuture
            .supplyAsync(() -> "done")
            .whenComplete((res, ex) -> {
                if (ex != null) System.out.println("  whenComplete saw exception: " + ex.getMessage());
                else System.out.println("  whenComplete saw result: " + res);
            });

        whenComplete.join();
        System.out.println();
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
