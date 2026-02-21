/**
 * Classic Problem: Print Odd-Even Using Two Threads
 * 
 * Thread 1 prints odd numbers: 1, 3, 5, 7...
 * Thread 2 prints even numbers: 2, 4, 6, 8...
 * Output should be: 1 2 3 4 5 6 7 8...
 */
public class Problem01_OddEven {

    private static final Object lock = new Object();
    private static int number = 1;
    private static final int MAX = 20;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Odd-Even Problem ===\n");

        Thread oddThread = new Thread(() -> {
            while (number <= MAX) {
                synchronized (lock) {
                    while (number <= MAX && number % 2 == 0) {
                        try { lock.wait(); } catch (InterruptedException e) { return; }
                    }
                    if (number <= MAX) {
                        System.out.println("Odd Thread: " + number);
                        number++;
                        lock.notify();
                    }
                }
            }
        }, "OddThread");

        Thread evenThread = new Thread(() -> {
            while (number <= MAX) {
                synchronized (lock) {
                    while (number <= MAX && number % 2 == 1) {
                        try { lock.wait(); } catch (InterruptedException e) { return; }
                    }
                    if (number <= MAX) {
                        System.out.println("Even Thread: " + number);
                        number++;
                        lock.notify();
                    }
                }
            }
        }, "EvenThread");

        oddThread.start();
        evenThread.start();
        oddThread.join();
        evenThread.join();

        System.out.println("\nDone!");
    }
}

/*
 * Key Points:
 * 1. Shared counter accessed under lock
 * 2. Each thread waits while it's not their turn
 * 3. After printing, increment and notify other thread
 * 4. While loop handles spurious wakeups
 */
