/**
 * Solution for Exercise 02: Barrier-Based Rendezvous
 */

import java.util.concurrent.CyclicBarrier;

public class Exercise02_Solution {

    public static void main(String[] args) throws Exception {
        CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("--- All ready ---"));

        for (int i = 0; i < 3; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    Thread.sleep(100L * (id + 1));
                    System.out.println("Thread " + id + " Ready");
                    barrier.await();
                    System.out.println("Thread " + id + " Go");
                } catch (Exception e) {
                    if (e instanceof InterruptedException) Thread.currentThread().interrupt();
                }
            }).start();
        }

        Thread.sleep(1000);
        System.out.println("Done.");
    }
}
