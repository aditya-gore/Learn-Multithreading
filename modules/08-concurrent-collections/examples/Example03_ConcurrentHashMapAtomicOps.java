/**
 * Example 03: ConcurrentHashMap Atomic Operations
 *
 * Demonstrates computeIfAbsent, compute, merge for thread-safe updates.
 */

import java.util.concurrent.*;
import java.util.*;

public class Example03_ConcurrentHashMapAtomicOps {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ConcurrentHashMap Atomic Ops ===\n");

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // merge: atomic add
        map.merge("count", 1, Integer::sum);
        map.merge("count", 1, Integer::sum);
        map.merge("count", 1, Integer::sum);
        System.out.println("count after merge: " + map.get("count"));

        // compute: update or create
        map.compute("key", (k, v) -> (v == null) ? 10 : v + 5);
        map.compute("key", (k, v) -> (v == null) ? 10 : v + 5);
        System.out.println("key after compute: " + map.get("key"));

        // Word-count style with merge
        String[] words = {"a", "b", "a", "c", "b", "a"};
        ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
        for (String w : words) {
            counts.merge(w, 1, Integer::sum);
        }
        System.out.println("Word counts: " + counts);
        System.out.println();
    }
}
