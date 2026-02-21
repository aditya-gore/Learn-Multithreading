/**
 * Classic Problem: FizzBuzz with Four Threads
 *
 * Four threads print numbers 1 to n:
 * Thread1: "fizz" for multiples of 3
 * Thread2: "buzz" for multiples of 5
 * Thread3: "fizzbuzz" for multiples of 15
 * Thread4: the number otherwise
 *
 * Output must be in order: 1, 2, fizz, 4, buzz, fizz, 7, ...
 */

public class Problem07_FizzBuzz {

    private static final Object lock = new Object();
    private static int n = 1;
    private static final int MAX = 20;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== FizzBuzz (4 threads) ===\n");

        Thread fizz = new Thread(() -> printWhen(3, false, "fizz"), "fizz");
        Thread buzz = new Thread(() -> printWhen(5, false, "buzz"), "buzz");
        Thread fizzbuzz = new Thread(() -> printWhen(15, true, "fizzbuzz"), "fizzbuzz");
        Thread num = new Thread(Problem07_FizzBuzz::printNumber, "number");

        fizz.start();
        buzz.start();
        fizzbuzz.start();
        num.start();

        fizz.join();
        buzz.join();
        fizzbuzz.join();
        num.join();

        System.out.println("\nDone!");
    }

    private static void printWhen(int divisor, boolean exact, String msg) {
        while (n <= MAX) {
            synchronized (lock) {
                while (n <= MAX && !(exact ? n % 15 == 0 : n % divisor == 0 && n % 15 != 0)) {
                    try { lock.wait(); } catch (InterruptedException e) { return; }
                }
                if (n <= MAX) {
                    System.out.println(msg);
                    n++;
                    lock.notifyAll();
                }
            }
        }
    }

    private static void printNumber() {
        while (n <= MAX) {
            synchronized (lock) {
                while (n <= MAX && (n % 3 == 0 || n % 5 == 0)) {
                    try { lock.wait(); } catch (InterruptedException e) { return; }
                }
                if (n <= MAX) {
                    System.out.println(n);
                    n++;
                    lock.notifyAll();
                }
            }
        }
    }
}
