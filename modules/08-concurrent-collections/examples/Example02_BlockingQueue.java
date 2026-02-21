/**
 * Example 02: BlockingQueue Producer-Consumer
 *
 * Demonstrates ArrayBlockingQueue with multiple producers and consumers.
 */

import java.util.concurrent.*;

public class Example02_BlockingQueue {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== BlockingQueue Producer-Consumer ===\n");

        BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);

        Thread p1 = new Thread(() -> produce(queue, "P1", 5), "Producer-1");
        Thread p2 = new Thread(() -> produce(queue, "P2", 5), "Producer-2");
        Thread c1 = new Thread(() -> consume(queue, 5), "Consumer-1");
        Thread c2 = new Thread(() -> consume(queue, 5), "Consumer-2");

        p1.start();
        p2.start();
        c1.start();
        c2.start();

        p1.join();
        p2.join();
        c1.join();
        c2.join();

        System.out.println("\nDone.");
    }

    private static void produce(BlockingQueue<String> queue, String prefix, int count) {
        try {
            for (int i = 0; i < count; i++) {
                String item = prefix + "-" + i;
                queue.put(item);
                System.out.println(Thread.currentThread().getName() + " put " + item);
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void consume(BlockingQueue<String> queue, int count) {
        try {
            for (int i = 0; i < count; i++) {
                String item = queue.take();
                System.out.println(Thread.currentThread().getName() + " took " + item);
                Thread.sleep(80);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
