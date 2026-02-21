/**
 * Example 04: Daemon vs User Threads
 * 
 * This example demonstrates:
 * - User threads (default): JVM waits for them to complete
 * - Daemon threads: JVM exits when only daemon threads remain
 * 
 * Daemon threads are used for background services like:
 * - Garbage Collection
 * - Background monitoring
 * - Periodic cleanup tasks
 */
public class Example04_DaemonThreads {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Daemon Threads Demo ===\n");

        demonstrateDaemonVsUser();
        demonstrateDaemonTermination();
    }

    /**
     * Shows the difference between daemon and user threads
     */
    private static void demonstrateDaemonVsUser() throws InterruptedException {
        System.out.println("--- Daemon vs User Thread Properties ---\n");

        Thread userThread = new Thread(() -> {}, "UserThread");
        Thread daemonThread = new Thread(() -> {}, "DaemonThread");
        daemonThread.setDaemon(true); // Must set BEFORE start()

        System.out.println("User Thread:");
        System.out.println("  Name: " + userThread.getName());
        System.out.println("  Is Daemon: " + userThread.isDaemon());

        System.out.println("\nDaemon Thread:");
        System.out.println("  Name: " + daemonThread.getName());
        System.out.println("  Is Daemon: " + daemonThread.isDaemon());
        System.out.println();
    }

    /**
     * Demonstrates that daemon threads are terminated when JVM exits.
     * 
     * IMPORTANT: Run this method standalone to see daemon termination!
     * When main thread exits and no user threads remain, daemon stops abruptly.
     */
    private static void demonstrateDaemonTermination() throws InterruptedException {
        System.out.println("--- Daemon Termination Behavior ---\n");

        // Daemon thread that runs "forever"
        Thread daemonWorker = new Thread(() -> {
            int count = 0;
            while (true) {
                count++;
                System.out.println("  [Daemon] Working... count=" + count);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("  [Daemon] Interrupted!");
                    return;
                }
            }
        }, "DaemonWorker");

        daemonWorker.setDaemon(true);
        daemonWorker.start();

        // Main thread does some work then exits
        System.out.println("[Main] Started daemon thread");
        System.out.println("[Main] Daemon will run for 2 seconds...\n");

        Thread.sleep(2000);

        System.out.println("\n[Main] Main thread finishing...");
        System.out.println("[Main] If this were a standalone program,");
        System.out.println("[Main] the daemon would be killed when main exits!\n");

        // Note: In this demo, we let the program continue.
        // In a real scenario, if main() returned here and there were no
        // other user threads, the daemon would be terminated immediately.

        // For demo purposes, let's show what happens with a user thread too
        demonstrateUserThreadKeepsJvmAlive();
    }

    /**
     * Shows that a user thread keeps the JVM alive even after main exits
     */
    private static void demonstrateUserThreadKeepsJvmAlive() throws InterruptedException {
        System.out.println("--- User Thread Keeps JVM Alive ---\n");

        Thread userWorker = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("  [UserThread] Working... " + i);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.println("  [UserThread] Finished!\n");
        }, "UserWorker");

        userWorker.start();
        
        System.out.println("[Main] Started user thread");
        System.out.println("[Main] Main will exit but JVM waits for user thread...\n");

        // Even if main exits here, JVM waits for userWorker to complete
        userWorker.join(); // For clean demo output
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Daemon Threads Demo ===
 * 
 * --- Daemon vs User Thread Properties ---
 * 
 * User Thread:
 *   Name: UserThread
 *   Is Daemon: false
 * 
 * Daemon Thread:
 *   Name: DaemonThread
 *   Is Daemon: true
 * 
 * --- Daemon Termination Behavior ---
 * 
 * [Main] Started daemon thread
 * [Main] Daemon will run for 2 seconds...
 * 
 *   [Daemon] Working... count=1
 *   [Daemon] Working... count=2
 *   [Daemon] Working... count=3
 *   [Daemon] Working... count=4
 * 
 * [Main] Main thread finishing...
 * [Main] If this were a standalone program,
 * [Main] the daemon would be killed when main exits!
 * 
 * --- User Thread Keeps JVM Alive ---
 * 
 * [Main] Started user thread
 * [Main] Main will exit but JVM waits for user thread...
 * 
 *   [UserThread] Working... 1
 *   [UserThread] Working... 2
 *   [UserThread] Working... 3
 *   [UserThread] Finished!
 * 
 * KEY INSIGHT: 
 * - Use daemon threads for background tasks that should stop when app exits
 * - Use user threads for important work that must complete
 * - setDaemon() must be called BEFORE start()
 */

/*
 * TRY THIS: Create a new file with just this main method to see daemon termination:
 * 
 * public class DaemonTerminationTest {
 *     public static void main(String[] args) {
 *         Thread daemon = new Thread(() -> {
 *             while (true) {
 *                 System.out.println("Daemon running...");
 *                 try { Thread.sleep(100); } catch (InterruptedException e) { return; }
 *             }
 *         });
 *         daemon.setDaemon(true);
 *         daemon.start();
 *         
 *         System.out.println("Main exiting...");
 *         // When main exits, daemon is killed immediately!
 *     }
 * }
 */
