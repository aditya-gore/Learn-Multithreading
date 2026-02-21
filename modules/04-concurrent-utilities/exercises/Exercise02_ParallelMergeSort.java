/**
 * Exercise 02: Parallel Processing with CyclicBarrier
 * 
 * TASK:
 * Implement a parallel data processor where:
 * - Multiple worker threads process chunks of data
 * - After all workers complete a phase, a coordinator merges results
 * - Process continues for multiple phases
 * 
 * PHASES:
 * 1. Load: Each worker loads its data chunk
 * 2. Transform: Each worker transforms its data
 * 3. Validate: Each worker validates results
 * 4. Complete: Coordinator collects all results
 * 
 * REQUIREMENTS:
 * 1. Use CyclicBarrier for phase synchronization
 * 2. Barrier action should print phase completion and merge partial results
 * 3. Handle BrokenBarrierException gracefully
 * 
 * BONUS: Add timeout handling so slow workers don't block forever.
 */

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class Exercise02_ParallelMergeSort {

    public static void main(String[] args) throws InterruptedException {
        // TODO: Implement parallel data processing

        // DataProcessor processor = new DataProcessor(4); // 4 workers
        // processor.process(testData);

        System.out.println("Implement the DataProcessor and uncomment the test!");
    }
}

// TODO: Implement this class
// class DataProcessor {
//     private final int workerCount;
//     private final CyclicBarrier barrier;
//     private final int[][] partialResults;
//     
//     public DataProcessor(int workerCount) {
//         this.workerCount = workerCount;
//         this.partialResults = new int[workerCount][];
//         
//         // Create barrier with action to merge results
//         this.barrier = new CyclicBarrier(workerCount, () -> {
//             // Called when all workers reach barrier
//             // Merge partial results here
//         });
//     }
//     
//     public int[] process(int[] data) {
//         // Split data among workers
//         // Start worker threads
//         // Wait for completion
//         // Return merged result
//     }
// }

// class DataWorker implements Runnable {
//     // Implement worker that processes data in phases
// }

/*
 * LEARNING GOALS:
 * - Use CyclicBarrier for multi-phase processing
 * - Implement barrier actions for coordination
 * - Handle parallel data processing patterns
 * 
 * When done, compare with: solutions/Exercise02_Solution.java
 */
