/**
 * Example 02: CyclicBarrier
 * 
 * Demonstrates using CyclicBarrier for:
 * 1. Synchronizing parallel computation phases
 * 2. Running a barrier action when all threads arrive
 * 3. Reusing the barrier across multiple rounds
 */

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Example02_CyclicBarrier {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== CyclicBarrier Demo ===\n");

        parallelComputationExample();
        gameSimulationExample();
    }

    /**
     * Use case: Parallel computation with multiple phases.
     * All workers must complete phase N before any starts phase N+1.
     */
    private static void parallelComputationExample() throws InterruptedException {
        System.out.println("--- Parallel Computation Example ---\n");

        int workerCount = 3;
        int[][] matrix = new int[workerCount][5];  // Shared data

        // Barrier with action that runs when all threads arrive
        CyclicBarrier barrier = new CyclicBarrier(workerCount, () -> {
            System.out.println(">>> All workers completed phase! <<<\n");
        });

        Thread[] workers = new Thread[workerCount];
        for (int i = 0; i < workerCount; i++) {
            workers[i] = new Thread(new MatrixWorker(i, matrix, barrier));
            workers[i].start();
        }

        for (Thread worker : workers) {
            worker.join();
        }

        // Print final matrix
        System.out.println("Final matrix:");
        for (int i = 0; i < matrix.length; i++) {
            System.out.print("  Row " + i + ": ");
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Use case: Game simulation where all players must complete
     * each turn before the next turn begins.
     */
    private static void gameSimulationExample() throws InterruptedException {
        System.out.println("--- Game Simulation Example ---\n");

        int playerCount = 3;
        int rounds = 3;

        CyclicBarrier barrier = new CyclicBarrier(playerCount, () -> {
            System.out.println("=== Round complete! ===\n");
        });

        Thread[] players = new Thread[playerCount];
        for (int i = 0; i < playerCount; i++) {
            final int playerId = i + 1;
            players[i] = new Thread(() -> {
                try {
                    for (int round = 1; round <= rounds; round++) {
                        // Simulate player action (variable time)
                        int actionTime = (int) (Math.random() * 500) + 100;
                        Thread.sleep(actionTime);
                        System.out.println("[Player-" + playerId + "] Completed round " + 
                            round + " (took " + actionTime + "ms)");
                        
                        barrier.await();  // Wait for all players
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Player-" + playerId);
            players[i].start();
        }

        for (Thread player : players) {
            player.join();
        }

        System.out.println("Game over!\n");
    }
}

class MatrixWorker implements Runnable {
    private final int rowIndex;
    private final int[][] matrix;
    private final CyclicBarrier barrier;

    public MatrixWorker(int rowIndex, int[][] matrix, CyclicBarrier barrier) {
        this.rowIndex = rowIndex;
        this.matrix = matrix;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            // Phase 1: Initialize row
            System.out.println("[Worker-" + rowIndex + "] Phase 1: Initializing row");
            for (int j = 0; j < matrix[rowIndex].length; j++) {
                matrix[rowIndex][j] = rowIndex + 1;
            }
            barrier.await();  // Wait for all to finish phase 1

            // Phase 2: Double values
            System.out.println("[Worker-" + rowIndex + "] Phase 2: Doubling values");
            for (int j = 0; j < matrix[rowIndex].length; j++) {
                matrix[rowIndex][j] *= 2;
            }
            barrier.await();  // Wait for all to finish phase 2

            // Phase 3: Add row index
            System.out.println("[Worker-" + rowIndex + "] Phase 3: Adding row index");
            for (int j = 0; j < matrix[rowIndex].length; j++) {
                matrix[rowIndex][j] += rowIndex;
            }
            barrier.await();  // Wait for all to finish phase 3

            System.out.println("[Worker-" + rowIndex + "] Done!");

        } catch (InterruptedException | BrokenBarrierException e) {
            Thread.currentThread().interrupt();
        }
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === CyclicBarrier Demo ===
 * 
 * --- Parallel Computation Example ---
 * 
 * [Worker-0] Phase 1: Initializing row
 * [Worker-1] Phase 1: Initializing row
 * [Worker-2] Phase 1: Initializing row
 * >>> All workers completed phase! <<<
 * 
 * [Worker-0] Phase 2: Doubling values
 * [Worker-2] Phase 2: Doubling values
 * [Worker-1] Phase 2: Doubling values
 * >>> All workers completed phase! <<<
 * 
 * [Worker-1] Phase 3: Adding row index
 * [Worker-0] Phase 3: Adding row index
 * [Worker-2] Phase 3: Adding row index
 * >>> All workers completed phase! <<<
 * 
 * [Worker-0] Done!
 * [Worker-1] Done!
 * [Worker-2] Done!
 * Final matrix:
 *   Row 0: 2 2 2 2 2
 *   Row 1: 5 5 5 5 5
 *   Row 2: 8 8 8 8 8
 * 
 * --- Game Simulation Example ---
 * 
 * [Player-1] Completed round 1 (took 234ms)
 * [Player-3] Completed round 1 (took 456ms)
 * [Player-2] Completed round 1 (took 567ms)
 * === Round complete! ===
 * 
 * [Player-2] Completed round 2 (took 123ms)
 * ...
 * 
 * Game over!
 * 
 * 
 * KEY INSIGHTS:
 * 
 * 1. CYCLIC = REUSABLE: After all threads pass, barrier automatically resets.
 *    This is the key difference from CountDownLatch.
 * 
 * 2. BARRIER ACTION: The optional Runnable runs ONCE when last thread arrives,
 *    before any thread is released. Useful for combining results.
 * 
 * 3. BROKEN BARRIER: If a thread is interrupted while waiting, the barrier
 *    becomes "broken" and all waiting threads get BrokenBarrierException.
 * 
 * 4. PARTY COUNT: All parties must call await() for release. If any party
 *    doesn't show up, others wait forever (use timeout!).
 */
