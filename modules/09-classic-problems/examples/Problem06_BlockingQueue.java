/**
 * Classic Problem: Implement Blocking Queue from Scratch
 */

import java.util.LinkedList;
import java.util.Queue;

public class Problem06_BlockingQueue {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Custom Blocking Queue ===\n");

        MyBlockingQueue<Integer> queue = new MyBlockingQueue<>(3);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    queue.put(i);
                    System.out.println("Produced: " + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(200);  // Slow consumer
                    Integer item = queue.take();
                    System.out.println("Consumed: " + item);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }
}

class MyBlockingQueue<E> {
    private final Queue<E> queue = new LinkedList<>();
    private final int capacity;

    public MyBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(E item) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();  // Wait until not full
        }
        queue.add(item);
        notifyAll();  // Notify waiting consumers
    }

    public synchronized E take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();  // Wait until not empty
        }
        E item = queue.poll();
        notifyAll();  // Notify waiting producers
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }
}

/*
 * Key Points:
 * 1. While loops handle spurious wakeups
 * 2. notifyAll() wakes both producers and consumers
 * 3. Synchronized ensures atomic operations
 * 4. This is how java.util.concurrent.ArrayBlockingQueue works internally
 */
