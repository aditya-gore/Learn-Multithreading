/**
 * Classic Problem: Print Numbers 1-100 Using Three Threads
 *
 * Three threads print numbers 1-100 in sequence:
 * Thread1: 1, 4, 7, 10...
 * Thread2: 2, 5, 8, 11...
 * Thread3: 3, 6, 9, 12...
 */

public class Problem02_ThreeThreads {

    private static final Object lock = new Object();
    private static int number = 1;
    private static final int MAX = 100;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Three Threads Print 1-100 ===\n");

        Thread t1 = new Thread(() -> printForRemainder(1), "T1");
        Thread t2 = new Thread(() -> printForRemainder(2), "T2");
        Thread t3 = new Thread(() -> printForRemainder(0), "T3");  // 3, 6, 9 -> remainder 0

        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();

        System.out.println("\nDone!");
    }

    private static void printForRemainder(int remainder) {
        while (number <= MAX) {
            synchronized (lock) {
                while (number <= MAX && number % 3 != remainder) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (number <= MAX) {
                    System.out.println(Thread.currentThread().getName() + ": " + number);
                    number++;
                    lock.notifyAll();
                }
            }
        }
    }
}

/*
 * Key: Each thread prints when (number % 3) matches its remainder (1, 2, or 0).
 * notifyAll() wakes the others so the next one can run.
 */
