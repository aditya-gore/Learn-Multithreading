/**
 * Solution for Exercise 02: Bounded Task Queue with BlockingQueue and Worker Threads
 */

import java.util.concurrent.*;
import java.util.*;

public class Exercise02_Solution {

    private static final Runnable POISON = () -> {};

    public static void main(String[] args) throws InterruptedException {
        TaskProcessor processor = new TaskProcessor(2, 5);
        for (int i = 0; i < 6; i++) {
            final int id = i;
            processor.submit(() -> System.out.println("Task " + id + " by " + Thread.currentThread().getName()));
        }
        processor.shutdown();
        System.out.println("Done.");
    }
}

class TaskProcessor {
    private final BlockingQueue<Runnable> queue;
    private final List<Thread> workers;
    private volatile boolean shutdown = false;

    public TaskProcessor(int numWorkers, int queueCapacity) {
        this.queue = new ArrayBlockingQueue<>(queueCapacity);
        this.workers = new ArrayList<>();

        for (int i = 0; i < numWorkers; i++) {
            Thread w = new Thread(() -> {
                try {
                    while (!shutdown || !queue.isEmpty()) {
                        Runnable task = queue.poll(100, TimeUnit.MILLISECONDS);
                        if (task == POISON) break;
                        if (task != null) task.run();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Worker-" + i);
            w.start();
            workers.add(w);
        }
    }

    public void submit(Runnable task) throws InterruptedException {
        if (shutdown) throw new RejectedExecutionException("shutdown");
        queue.put(task);
    }

    public void shutdown() throws InterruptedException {
        shutdown = true;
        for (int i = 0; i < workers.size(); i++) {
            queue.put(POISON);
        }
        for (Thread w : workers) w.join();
    }
}
