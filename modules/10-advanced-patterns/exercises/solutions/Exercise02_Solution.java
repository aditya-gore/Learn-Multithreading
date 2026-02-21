/**
 * Solution for Exercise 02: ThreadLocal Usage and Cleanup
 */

public class Exercise02_Solution {

    private static final ThreadLocal<String> requestId = new ThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                try {
                    requestId.set("request-" + id);
                    System.out.println(Thread.currentThread().getName() + " has ID: " + requestId.get());
                } finally {
                    requestId.remove();
                }
            }, "Worker-" + id);
            threads[i].start();
        }
        for (Thread t : threads) t.join();
        System.out.println("Done.");
    }
}
