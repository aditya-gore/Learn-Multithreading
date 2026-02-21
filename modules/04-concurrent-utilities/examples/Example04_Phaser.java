/**
 * Example 04: Phaser
 * 
 * Demonstrates Phaser for:
 * 1. Dynamic party registration/deregistration
 * 2. Multi-phase execution
 * 3. Flexible barrier patterns
 */

import java.util.concurrent.Phaser;

public class Example04_Phaser {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Phaser Demo ===\n");

        basicPhaserExample();
        dynamicRegistrationExample();
    }

    /**
     * Basic phaser usage: like CyclicBarrier but more flexible.
     */
    private static void basicPhaserExample() throws InterruptedException {
        System.out.println("--- Basic Phaser Example ---\n");

        // Register main thread as party
        Phaser phaser = new Phaser(1);

        int workerCount = 3;
        int phases = 3;

        for (int i = 0; i < workerCount; i++) {
            final int workerId = i + 1;
            phaser.register();  // Register new party
            
            new Thread(() -> {
                for (int phase = 1; phase <= phases; phase++) {
                    System.out.println("[Worker-" + workerId + "] Working on phase " + phase);
                    try {
                        Thread.sleep((long) (Math.random() * 500));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    
                    System.out.println("[Worker-" + workerId + "] Completed phase " + phase);
                    phaser.arriveAndAwaitAdvance();  // Wait for all
                }
                phaser.arriveAndDeregister();  // Done, leave phaser
            }, "Worker-" + workerId).start();
        }

        // Main thread coordinates phases
        for (int phase = 1; phase <= phases; phase++) {
            phaser.arriveAndAwaitAdvance();  // Wait with workers
            System.out.println(">>> Phase " + phase + " complete! <<<\n");
        }

        phaser.arriveAndDeregister();  // Main thread leaves
        System.out.println("All phases complete!\n");
    }

    /**
     * Dynamic registration: parties can join/leave at any time.
     */
    private static void dynamicRegistrationExample() throws InterruptedException {
        System.out.println("--- Dynamic Registration Example ---\n");

        Phaser phaser = new Phaser(1);  // Only main initially

        System.out.println("Phase 0: Starting with 1 party (main)");
        System.out.println("Registered parties: " + phaser.getRegisteredParties());

        // Phase 1: Add 2 workers
        System.out.println("\nPhase 1: Adding 2 workers");
        Thread w1 = createWorker(phaser, 1, 2);
        Thread w2 = createWorker(phaser, 2, 2);
        w1.start();
        w2.start();
        
        phaser.arriveAndAwaitAdvance();  // Phase 0→1
        System.out.println("After phase 1 - Registered parties: " + 
            phaser.getRegisteredParties());

        // Phase 2: Add 1 more worker, w1 will leave after this
        System.out.println("\nPhase 2: Adding worker 3, worker 1 will leave");
        Thread w3 = createWorker(phaser, 3, 1);
        w3.start();
        
        phaser.arriveAndAwaitAdvance();  // Phase 1→2
        System.out.println("After phase 2 - Registered parties: " + 
            phaser.getRegisteredParties());

        // Phase 3: Only main, w2, w3 remaining
        System.out.println("\nPhase 3: Worker 1 has left");
        phaser.arriveAndAwaitAdvance();  // Phase 2→3
        System.out.println("After phase 3 - Registered parties: " + 
            phaser.getRegisteredParties());

        // Clean up
        phaser.arriveAndDeregister();
        w1.join();
        w2.join();
        w3.join();
        
        System.out.println("\nPhaser terminated: " + phaser.isTerminated());
        System.out.println();
    }

    private static Thread createWorker(Phaser phaser, int id, int phases) {
        phaser.register();  // Register before starting
        return new Thread(() -> {
            for (int i = 0; i < phases; i++) {
                System.out.println("  [Worker-" + id + "] Doing work in phase " + 
                    phaser.getPhase());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                phaser.arriveAndAwaitAdvance();
            }
            System.out.println("  [Worker-" + id + "] Deregistering");
            phaser.arriveAndDeregister();
        }, "Worker-" + id);
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Phaser Demo ===
 * 
 * --- Basic Phaser Example ---
 * 
 * [Worker-1] Working on phase 1
 * [Worker-2] Working on phase 1
 * [Worker-3] Working on phase 1
 * [Worker-2] Completed phase 1
 * [Worker-1] Completed phase 1
 * [Worker-3] Completed phase 1
 * >>> Phase 1 complete! <<<
 * 
 * [Worker-1] Working on phase 2
 * [Worker-2] Working on phase 2
 * [Worker-3] Working on phase 2
 * ...
 * 
 * All phases complete!
 * 
 * --- Dynamic Registration Example ---
 * 
 * Phase 0: Starting with 1 party (main)
 * Registered parties: 1
 * 
 * Phase 1: Adding 2 workers
 *   [Worker-1] Doing work in phase 0
 *   [Worker-2] Doing work in phase 0
 * After phase 1 - Registered parties: 3
 * 
 * Phase 2: Adding worker 3, worker 1 will leave
 *   [Worker-1] Doing work in phase 1
 *   [Worker-2] Doing work in phase 1
 *   [Worker-3] Doing work in phase 1
 *   [Worker-1] Deregistering
 * After phase 2 - Registered parties: 3
 * 
 * ...
 * 
 * 
 * KEY INSIGHTS:
 * 
 * 1. DYNAMIC PARTIES: Unlike CyclicBarrier, parties can join (register)
 *    and leave (deregister) at any time.
 * 
 * 2. PHASE TRACKING: getPhase() returns current phase number.
 *    Increments each time all parties arrive.
 * 
 * 3. FLEXIBLE ARRIVAL:
 *    - arrive(): Arrive without waiting
 *    - arriveAndAwaitAdvance(): Arrive and wait
 *    - arriveAndDeregister(): Arrive, then leave
 * 
 * 4. TERMINATION: Phaser terminates when registered parties reaches 0.
 *    Can also override onAdvance() to control termination.
 * 
 * 5. HIERARCHICAL: Phasers can be organized in a tree for better
 *    scalability with many parties.
 * 
 * PHASER VS CYCLICBARRIER:
 * - Phaser: Dynamic parties, phase numbers, more control
 * - CyclicBarrier: Fixed parties, simpler API, barrier action
 */
