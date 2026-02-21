/**
 * Classic Problem: Dining Philosophers
 * 
 * 5 philosophers sit at a round table. Each needs two forks to eat.
 * Solution: Lock ordering - always pick up lower-numbered fork first.
 */

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Problem03_DiningPhilosophers {

    private static final int NUM_PHILOSOPHERS = 5;
    private static final Lock[] forks = new Lock[NUM_PHILOSOPHERS];

    static {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new ReentrantLock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Dining Philosophers ===\n");

        Thread[] philosophers = new Thread[NUM_PHILOSOPHERS];
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            final int id = i;
            philosophers[i] = new Thread(() -> {
                try {
                    for (int meal = 0; meal < 3; meal++) {
                        think(id);
                        eat(id);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Philosopher-" + i);
        }

        for (Thread p : philosophers) p.start();
        for (Thread p : philosophers) p.join();

        System.out.println("\nAll philosophers finished eating!");
    }

    private static void think(int id) throws InterruptedException {
        System.out.println("Philosopher " + id + " is thinking...");
        Thread.sleep((long) (Math.random() * 500));
    }

    private static void eat(int id) throws InterruptedException {
        int leftFork = id;
        int rightFork = (id + 1) % NUM_PHILOSOPHERS;

        // Lock ordering: always pick up lower-numbered fork first
        Lock firstFork = forks[Math.min(leftFork, rightFork)];
        Lock secondFork = forks[Math.max(leftFork, rightFork)];

        firstFork.lock();
        try {
            secondFork.lock();
            try {
                System.out.println("Philosopher " + id + " is eating...");
                Thread.sleep((long) (Math.random() * 500));
            } finally {
                secondFork.unlock();
            }
        } finally {
            firstFork.unlock();
        }
    }
}

/*
 * Why Lock Ordering Works:
 * - Without ordering: Each philosopher picks left fork, waits for right = DEADLOCK
 * - With ordering: Fork 0 is always picked before Fork 4
 * - Philosopher 4 must pick Fork 0 first (not Fork 4), breaking circular wait
 */
