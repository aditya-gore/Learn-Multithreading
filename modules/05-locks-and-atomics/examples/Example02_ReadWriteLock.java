/**
 * Example 02: ReadWriteLock
 * 
 * Demonstrates using ReadWriteLock for a thread-safe cache where:
 * - Multiple threads can read simultaneously
 * - Writes are exclusive (block all readers and other writers)
 */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

public class Example02_ReadWriteLock {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ReadWriteLock Demo ===\n");

        ThreadSafeCache<String, Integer> cache = new ThreadSafeCache<>();

        // Writer threads
        Thread[] writers = new Thread[2];
        for (int i = 0; i < writers.length; i++) {
            final int writerId = i;
            writers[i] = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    String key = "key" + (writerId * 10 + j);
                    int value = writerId * 100 + j;
                    cache.put(key, value);
                    System.out.println("[Writer-" + writerId + "] Put " + key + "=" + value);
                    sleep(100);
                }
            }, "Writer-" + writerId);
        }

        // Reader threads
        Thread[] readers = new Thread[5];
        for (int i = 0; i < readers.length; i++) {
            final int readerId = i;
            readers[i] = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    String key = "key" + (j % 10);
                    Integer value = cache.get(key);
                    System.out.println("[Reader-" + readerId + "] Get " + key + "=" + value);
                    sleep(50);
                }
            }, "Reader-" + readerId);
        }

        // Start all threads
        for (Thread w : writers) w.start();
        sleep(50);  // Let writers start first
        for (Thread r : readers) r.start();

        // Wait for completion
        for (Thread w : writers) w.join();
        for (Thread r : readers) r.join();

        System.out.println("\n--- Final Cache State ---");
        System.out.println("Cache size: " + cache.size());
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

/**
 * Thread-safe cache using ReadWriteLock.
 * 
 * Benefits:
 * - Multiple concurrent reads (no blocking between readers)
 * - Writes are exclusive (block readers and other writers)
 * - Better performance than synchronized for read-heavy workloads
 */
class ThreadSafeCache<K, V> {
    private final Map<K, V> cache = new HashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    public V get(K key) {
        readLock.lock();
        try {
            // Simulate slow read
            try { Thread.sleep(10); } catch (InterruptedException e) {}
            return cache.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public void put(K key, V value) {
        writeLock.lock();
        try {
            // Simulate slow write
            try { Thread.sleep(50); } catch (InterruptedException e) {}
            cache.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    public V remove(K key) {
        writeLock.lock();
        try {
            return cache.remove(key);
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

    public boolean containsKey(K key) {
        readLock.lock();
        try {
            return cache.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }
}

/*
 * EXPECTED OUTPUT (interleaved):
 * 
 * === ReadWriteLock Demo ===
 * 
 * [Writer-0] Put key0=0
 * [Writer-1] Put key10=100
 * [Reader-0] Get key0=0
 * [Reader-1] Get key0=0
 * [Reader-2] Get key0=0    <- Multiple readers at same time!
 * [Reader-3] Get key0=0
 * [Reader-4] Get key0=0
 * [Writer-0] Put key1=1
 * [Reader-0] Get key1=1
 * ...
 * 
 * --- Final Cache State ---
 * Cache size: 10
 * 
 * 
 * KEY INSIGHTS:
 * 
 * 1. CONCURRENT READS:
 *    Notice multiple readers access the cache simultaneously.
 *    This is the main benefit over synchronized or ReentrantLock.
 * 
 * 2. EXCLUSIVE WRITES:
 *    When a writer holds the write lock, all readers and writers block.
 * 
 * 3. PERFORMANCE:
 *    For read-heavy workloads (many readers, few writers),
 *    ReadWriteLock significantly outperforms exclusive locks.
 * 
 * 4. LOCK DOWNGRADING:
 *    You can hold write lock, acquire read lock, then release write lock.
 *    This allows atomic read-after-write without releasing to readers.
 * 
 * 5. NO LOCK UPGRADING:
 *    Cannot upgrade read lock to write lock - would deadlock if
 *    multiple readers tried to upgrade simultaneously.
 * 
 * WHEN TO USE:
 * - Cache systems
 * - Configuration stores
 * - Any data with read >> write ratio
 */
