/**
 * ThreadLocal Example
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class Example02_ThreadLocal {

    // Each thread gets its own SimpleDateFormat
    private static final ThreadLocal<SimpleDateFormat> dateFormat = 
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    // Per-thread request context
    private static final ThreadLocal<String> requestId = new ThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ThreadLocal Demo ===\n");

        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                // Set request context
                requestId.set("REQ-" + id);
                
                // Use thread-local date formatter
                String timestamp = dateFormat.get().format(new Date());
                System.out.println(requestId.get() + " at " + timestamp);

                // Clean up
                requestId.remove();
                dateFormat.remove();
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
    }
}

/*
 * Key Points:
 * 1. Each thread has its own copy of ThreadLocal values
 * 2. Useful for non-thread-safe objects like SimpleDateFormat
 * 3. MUST call remove() in thread pools to prevent memory leaks
 * 4. InheritableThreadLocal passes values to child threads
 */
