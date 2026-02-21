/**
 * Example 01: Three Ways to Create Threads in Java
 * 
 * This example demonstrates:
 * 1. Extending the Thread class
 * 2. Implementing the Runnable interface
 * 3. Using Lambda expressions (Java 8+)
 * 
 * Run this example and observe that threads may execute in any order!
 */
public class Example01_ThreadCreation {

    public static void main(String[] args) {
        System.out.println("=== Thread Creation Demo ===\n");
        System.out.println("Main thread: " + Thread.currentThread().getName());
        System.out.println();

        // Way 1: Extending Thread class
        System.out.println("--- Way 1: Extending Thread class ---");
        MyThread thread1 = new MyThread("Worker-1");
        thread1.start();

        // Way 2: Implementing Runnable interface
        System.out.println("--- Way 2: Implementing Runnable ---");
        Thread thread2 = new Thread(new MyRunnable(), "Worker-2");
        thread2.start();

        // Way 3: Using Lambda expression
        System.out.println("--- Way 3: Using Lambda ---");
        Thread thread3 = new Thread(() -> {
            String name = Thread.currentThread().getName();
            System.out.println("[" + name + "] Running via Lambda");
            for (int i = 1; i <= 3; i++) {
                System.out.println("[" + name + "] Count: " + i);
                sleep(100);
            }
            System.out.println("[" + name + "] Finished");
        }, "Worker-3");
        thread3.start();

        // Way 3b: Anonymous Runnable (pre-Java 8 style)
        Thread thread4 = new Thread(new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                System.out.println("[" + name + "] Running via Anonymous Runnable");
                System.out.println("[" + name + "] Finished");
            }
        }, "Worker-4");
        thread4.start();

        System.out.println("\nMain thread continues...");
        System.out.println("Notice: Output may be interleaved!\n");
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

/**
 * Way 1: Create a thread by extending Thread class
 * 
 * Pros: Simple, direct access to Thread methods
 * Cons: Can't extend any other class (Java single inheritance)
 */
class MyThread extends Thread {
    
    public MyThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.println("[" + name + "] Running via Thread extension");
        for (int i = 1; i <= 3; i++) {
            System.out.println("[" + name + "] Count: " + i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println("[" + name + "] Finished");
    }
}

/**
 * Way 2: Create a thread by implementing Runnable interface
 * 
 * Pros: 
 *   - Can extend other classes
 *   - Separates task definition from thread management
 *   - Same Runnable can be used by multiple threads
 * 
 * Cons: Slightly more verbose
 * 
 * This is the RECOMMENDED approach for most cases!
 */
class MyRunnable implements Runnable {

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.println("[" + name + "] Running via Runnable");
        for (int i = 1; i <= 3; i++) {
            System.out.println("[" + name + "] Count: " + i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println("[" + name + "] Finished");
    }
}

/*
 * EXPECTED OUTPUT (order may vary due to thread scheduling):
 * 
 * === Thread Creation Demo ===
 * 
 * Main thread: main
 * 
 * --- Way 1: Extending Thread class ---
 * --- Way 2: Implementing Runnable ---
 * --- Way 3: Using Lambda ---
 * 
 * Main thread continues...
 * Notice: Output may be interleaved!
 * 
 * [Worker-1] Running via Thread extension
 * [Worker-2] Running via Runnable
 * [Worker-3] Running via Lambda
 * [Worker-4] Running via Anonymous Runnable
 * [Worker-4] Finished
 * [Worker-1] Count: 1
 * [Worker-2] Count: 1
 * [Worker-3] Count: 1
 * ... (interleaved output)
 * 
 * KEY TAKEAWAY: The order of execution is non-deterministic!
 */
