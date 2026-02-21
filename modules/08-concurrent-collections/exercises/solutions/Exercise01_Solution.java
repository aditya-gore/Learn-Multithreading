/**
 * Solution for Exercise 01: Thread-Safe Word Count Using ConcurrentHashMap
 */

import java.util.concurrent.*;
import java.util.*;

public class Exercise01_Solution {

    public static void main(String[] args) throws InterruptedException {
        ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
        List<String> words = Arrays.asList("a", "b", "a", "c", "b", "a", "c", "a", "b");

        int numThreads = 3;
        int chunk = (words.size() + numThreads - 1) / numThreads;
        Thread[] threads = new Thread[numThreads];

        for (int t = 0; t < numThreads; t++) {
            final int start = t * chunk;
            final int end = Math.min(start + chunk, words.size());
            threads[t] = new Thread(() -> {
                for (int i = start; i < end; i++) {
                    String w = words.get(i);
                    counts.merge(w, 1, Integer::sum);
                }
            });
            threads[t].start();
        }

        for (Thread th : threads) th.join();
        System.out.println("Word counts: " + counts);
    }
}
