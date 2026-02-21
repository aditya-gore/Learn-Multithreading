/**
 * Solution for Exercise 02: Build a Mini Thread Pool
 */

import java.util.concurrent.*;
import java.util.*;

public class Exercise02_Solution {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Mini Thread Pool Demo ===\n");

        MiniThreadPool pool = new MiniThreadPool(2, 10);

        for (int i = 0; i < 5; i++) {
            final int id = i;
            pool.submit(() -> {
                System.out.println("Task " + id + " by " + Thread.currentThread().getName());
                try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
        }

        pool.shutdown();
        System.out.println("\nAll tasks completed.");
    }
}

class MiniThreadPool {
    private final BlockingQueue<Runnable> workQueue;
    private final List<Thread> workers;
    private volatile boolean shutdown = false;

    public MiniThreadPool(int numThreads, int queueCapacity) {
        this.workQueue = new ArrayBlockingQueue<>(queueCapacity);
        this.workers = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            Thread worker = new Thread(() -> {
                while (!shutdown || !workQueue.isEmpty()) {
                    try {
                        Runnable task = workQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (task != null) {
                            task.run();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, "Worker-" + i);
            worker.start();
            workers.add(worker);
        }
    }

    public void submit(Runnable task) throws InterruptedException {
        if (shutdown) {
            throw new RejectedExecutionException("Pool is shutdown");
        }
        workQueue.put(task);
    }

    public void shutdown() throws InterruptedException {
        shutdown = true;
        for (Thread worker : workers) {
            worker.join();
        }
    }

    public List<Runnable> shutdownNow() {
        shutdown = true;
        for (Thread worker : workers) {
            worker.interrupt();
        }
        List<Runnable> remaining = new ArrayList<>();
        workQueue.drainTo(remaining);
        return remaining;
    }

}

/*
 * KEY INSIGHTS:
 *
 * 1. WORKER LOOP: Each worker polls the queue (with timeout to check shutdown).
 * 2. SHUTDOWN: Set flag so no new tasks are accepted; workers drain the queue.
 * 3. SHUTDOWN NOW: Interrupt workers and drainTo() to get unexecuted tasks.
 * 4. BlockingQueue.put() blocks when full; poll(timeout) allows checking shutdown.
 */
