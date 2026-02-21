/**
 * Solution for Exercise 02: Concurrent Cache with ReadWriteLock
 */

import java.util.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Exercise02_Solution {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Concurrent Cache Demo ===\n");

        testBasicOperations();
        testExpiration();
        testConcurrentAccess();
    }

    private static void testBasicOperations() {
        System.out.println("--- Basic Operations ---");
        ConcurrentCache<String, Integer> cache = 
            new ConcurrentCache<>(5, 10, TimeUnit.SECONDS);

        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);

        System.out.println("get(a): " + cache.get("a"));
        System.out.println("get(b): " + cache.get("b"));
        System.out.println("get(d): " + cache.get("d"));

        System.out.println("putIfAbsent(a, 100): " + cache.putIfAbsent("a", 100));  // 1
        System.out.println("putIfAbsent(d, 4): " + cache.putIfAbsent("d", 4));  // null

        System.out.println("computeIfAbsent(e, k -> 5): " + 
            cache.computeIfAbsent("e", k -> 5));  // 5

        System.out.println("Size: " + cache.size());
        System.out.println();
    }

    private static void testExpiration() throws InterruptedException {
        System.out.println("--- Expiration ---");
        ConcurrentCache<String, Integer> cache = 
            new ConcurrentCache<>(10, 1, TimeUnit.SECONDS);

        cache.put("temp", 42);
        System.out.println("Immediately after put: " + cache.get("temp"));

        Thread.sleep(1500);  // Wait for expiration
        System.out.println("After 1.5 seconds: " + cache.get("temp"));  // null
        System.out.println();
    }

    private static void testConcurrentAccess() throws InterruptedException {
        System.out.println("--- Concurrent Access ---");
        ConcurrentCache<Integer, Integer> cache = 
            new ConcurrentCache<>(1000, 1, TimeUnit.MINUTES);

        int numThreads = 4;
        int opsPerThread = 10000;
        Thread[] threads = new Thread[numThreads];

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                Random rand = new Random(threadId);
                for (int j = 0; j < opsPerThread; j++) {
                    int key = rand.nextInt(500);
                    if (rand.nextBoolean()) {
                        cache.get(key);
                    } else {
                        cache.put(key, j);
                    }
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        long endTime = System.currentTimeMillis();
        System.out.println("Completed " + (numThreads * opsPerThread) + 
            " operations in " + (endTime - startTime) + "ms");
        System.out.println("Final cache size: " + cache.size());
        System.out.println();
    }
}

class ConcurrentCache<K, V> {
    private final int maxSize;
    private final long expirationNanos;
    private final Map<K, CacheEntry<V>> cache;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    private static class CacheEntry<V> {
        V value;
        long createdAt;
        volatile long accessedAt;

        CacheEntry(V value) {
            this.value = value;
            this.createdAt = System.nanoTime();
            this.accessedAt = createdAt;
        }

        void touch() {
            this.accessedAt = System.nanoTime();
        }
    }

    public ConcurrentCache(int maxSize, long expiration, TimeUnit unit) {
        this.maxSize = maxSize;
        this.expirationNanos = unit.toNanos(expiration);
        this.cache = new LinkedHashMap<>(16, 0.75f, true);  // Access-order
    }

    public V get(K key) {
        readLock.lock();
        try {
            CacheEntry<V> entry = cache.get(key);
            if (entry == null) {
                return null;
            }
            if (isExpired(entry)) {
                // Need to remove - upgrade to write lock
                readLock.unlock();
                writeLock.lock();
                try {
                    cache.remove(key);
                    return null;
                } finally {
                    writeLock.unlock();
                    readLock.lock();  // Downgrade back
                }
            }
            entry.touch();
            return entry.value;
        } finally {
            readLock.unlock();
        }
    }

    public void put(K key, V value) {
        writeLock.lock();
        try {
            // Evict if at capacity
            if (cache.size() >= maxSize && !cache.containsKey(key)) {
                evictOldest();
            }
            cache.put(key, new CacheEntry<>(value));
        } finally {
            writeLock.unlock();
        }
    }

    public V putIfAbsent(K key, V value) {
        writeLock.lock();
        try {
            CacheEntry<V> existing = cache.get(key);
            if (existing != null && !isExpired(existing)) {
                existing.touch();
                return existing.value;
            }
            if (cache.size() >= maxSize) {
                evictOldest();
            }
            cache.put(key, new CacheEntry<>(value));
            return null;
        } finally {
            writeLock.unlock();
        }
    }

    public V computeIfAbsent(K key, Function<K, V> mappingFunction) {
        // Try read first
        readLock.lock();
        try {
            CacheEntry<V> entry = cache.get(key);
            if (entry != null && !isExpired(entry)) {
                entry.touch();
                return entry.value;
            }
        } finally {
            readLock.unlock();
        }

        // Need to compute - use write lock
        writeLock.lock();
        try {
            // Double-check after acquiring write lock
            CacheEntry<V> entry = cache.get(key);
            if (entry != null && !isExpired(entry)) {
                entry.touch();
                return entry.value;
            }
            
            V newValue = mappingFunction.apply(key);
            if (newValue != null) {
                if (cache.size() >= maxSize) {
                    evictOldest();
                }
                cache.put(key, new CacheEntry<>(newValue));
            }
            return newValue;
        } finally {
            writeLock.unlock();
        }
    }

    public V remove(K key) {
        writeLock.lock();
        try {
            CacheEntry<V> removed = cache.remove(key);
            return removed != null ? removed.value : null;
        } finally {
            writeLock.unlock();
        }
    }

    public int size() {
        readLock.lock();
        try {
            return cache.size();
        } finally {
            readLock.unlock();
        }
    }

    public void cleanup() {
        writeLock.lock();
        try {
            cache.entrySet().removeIf(e -> isExpired(e.getValue()));
        } finally {
            writeLock.unlock();
        }
    }

    private boolean isExpired(CacheEntry<?> entry) {
        return System.nanoTime() - entry.createdAt > expirationNanos;
    }

    private void evictOldest() {
        Iterator<Map.Entry<K, CacheEntry<V>>> it = cache.entrySet().iterator();
        if (it.hasNext()) {
            it.next();
            it.remove();
        }
    }
}

/*
 * KEY INSIGHTS:
 * 
 * 1. LinkedHashMap WITH ACCESS ORDER:
 *    Using `accessOrder=true` makes it maintain LRU order.
 *    The oldest-accessed entry is first in iteration order.
 * 
 * 2. READ-WRITE LOCK PATTERN:
 *    - get() uses read lock (concurrent reads OK)
 *    - put(), remove() use write lock (exclusive)
 * 
 * 3. LOCK UPGRADE/DOWNGRADE:
 *    Cannot upgrade read to write directly. Must release read first.
 *    In get(), we release read, acquire write to remove expired entry.
 * 
 * 4. DOUBLE-CHECKED LOCKING:
 *    In computeIfAbsent(), we check under read lock first, then
 *    re-check under write lock to handle races.
 * 
 * 5. TOUCH ON ACCESS:
 *    Updating accessedAt maintains LRU order for eviction.
 */
