/**
 * Exercise 02: Concurrent Cache with ReadWriteLock
 * 
 * TASK:
 * Build a thread-safe cache with:
 * - Concurrent reads (using ReadWriteLock)
 * - Exclusive writes
 * - Automatic expiration of entries
 * - Maximum size with LRU eviction
 * 
 * REQUIREMENTS:
 * 1. get() - returns value or null, updates access time for LRU
 * 2. put() - adds entry, evicts oldest if at capacity
 * 3. putIfAbsent() - atomic put-if-not-present
 * 4. computeIfAbsent() - atomic compute-and-put
 * 5. remove() - removes entry
 * 6. Entries expire after configurable time
 * 
 * BONUS: Implement periodic cleanup of expired entries.
 */

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.TimeUnit;

public class Exercise02_ConcurrentCache {

    public static void main(String[] args) throws InterruptedException {
        // TODO: Implement ConcurrentCache class
        
        // Test:
        // ConcurrentCache<String, Integer> cache = 
        //     new ConcurrentCache<>(100, 5, TimeUnit.SECONDS);
        
        // Test concurrent reads
        // Test expiration
        // Test LRU eviction
        
        System.out.println("Implement ConcurrentCache and uncomment the tests!");
    }
}

// TODO: Implement this class
// class ConcurrentCache<K, V> {
//     private final int maxSize;
//     private final long expirationNanos;
//     private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
//     
//     // Entry that tracks creation and access time
//     private static class CacheEntry<V> {
//         V value;
//         long createdAt;
//         long accessedAt;
//     }
//     
//     public ConcurrentCache(int maxSize, long expiration, TimeUnit unit) { }
//     
//     public V get(K key) { }
//     
//     public void put(K key, V value) { }
//     
//     public V putIfAbsent(K key, V value) { }
//     
//     public V computeIfAbsent(K key, Function<K, V> mappingFunction) { }
//     
//     public V remove(K key) { }
//     
//     public int size() { }
//     
//     // Remove expired entries (call periodically)
//     public void cleanup() { }
// }

/*
 * LEARNING GOALS:
 * - Use ReadWriteLock for read-heavy concurrent access
 * - Implement cache expiration and LRU eviction
 * - Handle lock upgrading (read to write) safely
 * 
 * When done, compare with: solutions/Exercise02_Solution.java
 */
