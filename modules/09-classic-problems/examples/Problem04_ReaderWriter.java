/**
 * Classic Problem: Reader-Writer
 *
 * Multiple readers can read simultaneously; writers need exclusive access.
 * Reader-preference: readers never wait if no writer is active.
 */

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Problem04_ReaderWriter {

    private static final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private static final Lock readLock = rwLock.readLock();
    private static final Lock writeLock = rwLock.writeLock();
    private static String data = "initial";

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Reader-Writer ===\n");

        Thread w1 = new Thread(() -> write("W1", "data1"), "Writer-1");
        Thread r1 = new Thread(() -> read("R1"), "Reader-1");
        Thread r2 = new Thread(() -> read("R2"), "Reader-2");
        Thread w2 = new Thread(() -> write("W2", "data2"), "Writer-2");
        Thread r3 = new Thread(() -> read("R3"), "Reader-3");

        w1.start();
        Thread.sleep(50);
        r1.start();
        r2.start();
        Thread.sleep(50);
        w2.start();
        r3.start();

        w1.join();
        r1.join();
        r2.join();
        w2.join();
        r3.join();

        System.out.println("\nDone.");
    }

    private static void read(String name) {
        readLock.lock();
        try {
            System.out.println(name + " read: " + data);
            sleep(100);
        } finally {
            readLock.unlock();
        }
    }

    private static void write(String name, String value) {
        writeLock.lock();
        try {
            System.out.println(name + " writing...");
            sleep(100);
            data = value;
            System.out.println(name + " wrote: " + data);
        } finally {
            writeLock.unlock();
        }
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
