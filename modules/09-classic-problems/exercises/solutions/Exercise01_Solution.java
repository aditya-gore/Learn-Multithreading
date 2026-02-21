/**
 * Solution for Exercise 01: N-Thread Sequential Printer
 */

public class Exercise01_Solution {

    public static void main(String[] args) throws InterruptedException {
        runSequentialPrinter(3, 15);
    }

    private static void runSequentialPrinter(int numThreads, int maxNumber) throws InterruptedException {
        Object lock = new Object();
        int[] counter = { 1 };

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                while (true) {
                    synchronized (lock) {
                        while (counter[0] <= maxNumber && (counter[0] - 1) % numThreads != index) {
                            try { lock.wait(); } catch (InterruptedException e) { return; }
                        }
                        if (counter[0] > maxNumber) break;
                        System.out.println(Thread.currentThread().getName() + ": " + counter[0]);
                        counter[0]++;
                        lock.notifyAll();
                    }
                }
            }, "T" + i);
            threads[i].start();
        }
        for (Thread t : threads) t.join();
        System.out.println("Done.");
    }
}
