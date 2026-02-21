/**
 * Classic Problem: H2O Molecule (LeetCode 1117)
 *
 * Threads call H() or O(). Form water: each molecule needs 2 H and 1 O.
 * Release hydrogen/oxygen in batches that form complete H2O molecules.
 */

import java.util.concurrent.Semaphore;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class Problem05_H2O {

    private final Semaphore hydrogen = new Semaphore(2);
    private final Semaphore oxygen = new Semaphore(1);
    private final CyclicBarrier barrier = new CyclicBarrier(3, () -> {
        System.out.println(" H2O molecule formed!");
        hydrogen.release(2);
        oxygen.release(1);
    });

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        hydrogen.acquire();
        try {
            releaseHydrogen.run();
            barrier.await();
        } catch (BrokenBarrierException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {
        oxygen.acquire();
        try {
            releaseOxygen.run();
            barrier.await();
        } catch (BrokenBarrierException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== H2O Molecule ===\n");

        Problem05_H2O h2o = new Problem05_H2O();

        String input = "OOHHHH";  // 2 molecules
        for (char c : input.toCharArray()) {
            if (c == 'H') {
                new Thread(() -> {
                    try {
                        h2o.hydrogen(() -> System.out.print("H"));
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }).start();
            } else {
                new Thread(() -> {
                    try {
                        h2o.oxygen(() -> System.out.print("O"));
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }).start();
            }
        }

        Thread.sleep(1000);
        System.out.println("\nDone.");
    }
}
