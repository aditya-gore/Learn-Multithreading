/**
 * Exercise 01: Thread-Safe Word Count Using ConcurrentHashMap
 *
 * TASK:
 * Implement a word counter that multiple threads can update concurrently.
 * Given a list of words (or lines of text), count occurrences of each word
 * using ConcurrentHashMap atomic operations (merge or compute).
 *
 * REQUIREMENTS:
 * 1. Use ConcurrentHashMap<String, Integer> for counts
 * 2. Use merge(key, 1, Integer::sum) or compute() for thread-safe increment
 * 3. Simulate multiple threads each processing a subset of words
 * 4. Print final word counts
 *
 * HINTS:
 * - map.merge(word, 1, (old, one) -> old + one) is atomic
 * - Split a list of words among threads; each thread updates the shared map
 *
 * When done, compare with: solutions/Exercise01_Solution.java
 */

import java.util.concurrent.*;
import java.util.*;

public class Exercise01_WordCount {

    public static void main(String[] args) throws InterruptedException {
        // TODO: Create ConcurrentHashMap for word counts
        // TODO: Create list of words (e.g. "a", "b", "a", "c", "b", "a" ...)
        // TODO: Use executor or threads to update counts in parallel
        // TODO: Print final counts

        System.out.println("Implement thread-safe word count!");
    }
}
