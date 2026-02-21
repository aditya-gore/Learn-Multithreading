/**
 * Exercise 01: N-Thread Sequential Printer
 *
 * TASK:
 * Generalize the "three threads print 1-100" problem to N threads.
 * N threads print numbers 1 to M in order: Thread 0 prints 1, N+1, 2N+1...;
 * Thread 1 prints 2, N+2, ...; etc.
 *
 * REQUIREMENTS:
 * 1. Constructor or params: SequentialPrinter(int numThreads, int maxNumber)
 * 2. Each thread prints only when (number % numThreads) equals its index
 * 3. Output is strictly 1, 2, 3, ..., maxNumber
 *
 * HINTS:
 * - Shared lock, shared counter, each thread waits while (counter % N != myIndex)
 * - After printing, increment counter and notifyAll()
 *
 * When done, compare with: solutions/Exercise01_Solution.java
 */

public class Exercise01_NThreadSequentialPrinter {

    public static void main(String[] args) throws InterruptedException {
        // TODO: Create SequentialPrinter(3, 15) and run
        // Should print 1, 2, 3, ..., 15 with 3 threads

        System.out.println("Implement N-thread sequential printer!");
    }
}
