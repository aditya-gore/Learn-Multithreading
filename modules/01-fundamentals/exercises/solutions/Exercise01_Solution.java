/**
 * Solution for Exercise 01: Thread Race
 */

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Exercise01_Solution {

    // For bonus: track the winner
    private static final AtomicReference<String> winner = new AtomicReference<>(null);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Race starting!\n");

        // Create 3 runner threads
        Thread[] runners = new Thread[3];
        
        for (int i = 0; i < 3; i++) {
            final int runnerId = i + 1;
            runners[i] = new Thread(() -> {
                Random random = new Random();
                String name = Thread.currentThread().getName();
                
                for (int count = 1; count <= 5; count++) {
                    System.out.println(name + ": " + count);
                    
                    try {
                        // Random delay between 0-100ms
                        Thread.sleep(random.nextInt(100));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                
                // BONUS: Try to claim victory (only first thread succeeds)
                if (winner.compareAndSet(null, name)) {
                    System.out.println("\n*** " + name + " WINS! ***\n");
                }
            }, "Runner-" + runnerId);
        }

        // Start all threads
        for (Thread runner : runners) {
            runner.start();
        }

        // Wait for all threads to complete
        for (Thread runner : runners) {
            runner.join();
        }

        System.out.println("\nRace finished!");
    }
}

/*
 * KEY LEARNINGS:
 * 
 * 1. Thread Interleaving: Each run produces different output because
 *    the OS scheduler decides when each thread runs.
 * 
 * 2. join() for coordination: Without join(), main would print 
 *    "Race finished!" before runners complete.
 * 
 * 3. Random delays: Help visualize interleaving and simulate
 *    real-world variable execution times.
 * 
 * 4. BONUS - AtomicReference: Used for thread-safe winner tracking.
 *    compareAndSet() ensures only one thread can set the winner.
 *    (We'll cover atomic operations in detail in Module 5)
 * 
 * TRY THIS:
 * - Remove the sleep() call - notice output is less interleaved
 * - Increase count to 100 - more visible interleaving
 * - Remove join() calls - see "Race finished!" appear early
 */
