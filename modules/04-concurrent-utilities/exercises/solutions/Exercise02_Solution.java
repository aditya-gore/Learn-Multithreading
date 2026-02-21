/**
 * Solution for Exercise 02: Parallel Processing with CyclicBarrier
 */

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.Arrays;

public class Exercise02_Solution {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Parallel Data Processor ===\n");

        // Test data
        int[] data = {5, 2, 8, 1, 9, 3, 7, 4, 6, 10, 12, 11};
        System.out.println("Original data: " + Arrays.toString(data));

        DataProcessor processor = new DataProcessor(4);
        int[] result = processor.process(data);

        System.out.println("\nFinal result: " + Arrays.toString(result));
    }
}

class DataProcessor {
    private final int workerCount;
    private final CyclicBarrier barrier;
    private int[][] workerData;
    private int[] mergedResult;
    private volatile int currentPhase = 0;

    public DataProcessor(int workerCount) {
        this.workerCount = workerCount;

        // Barrier action runs when all workers reach the barrier
        this.barrier = new CyclicBarrier(workerCount, () -> {
            currentPhase++;
            System.out.println("\n>>> Phase " + currentPhase + " complete <<<");
            
            if (currentPhase == 3) {  // After validation phase
                mergeResults();
            }
        });
    }

    public int[] process(int[] data) {
        // Split data among workers
        workerData = splitData(data);
        Thread[] workers = new Thread[workerCount];

        for (int i = 0; i < workerCount; i++) {
            workers[i] = new Thread(new DataWorker(i, barrier, workerData));
            workers[i].start();
        }

        // Wait for all workers to complete
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return mergedResult;
    }

    private int[][] splitData(int[] data) {
        int[][] chunks = new int[workerCount][];
        int chunkSize = (data.length + workerCount - 1) / workerCount;

        for (int i = 0; i < workerCount; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, data.length);
            if (start < data.length) {
                chunks[i] = Arrays.copyOfRange(data, start, end);
            } else {
                chunks[i] = new int[0];
            }
        }
        return chunks;
    }

    private void mergeResults() {
        System.out.println("Merging results from all workers...");
        
        // Calculate total size
        int totalSize = 0;
        for (int[] chunk : workerData) {
            totalSize += chunk.length;
        }

        // Merge sorted chunks (simplified - just concatenate for demo)
        mergedResult = new int[totalSize];
        int pos = 0;
        for (int[] chunk : workerData) {
            System.arraycopy(chunk, 0, mergedResult, pos, chunk.length);
            pos += chunk.length;
        }
        
        // Final sort of merged result
        Arrays.sort(mergedResult);
    }
}

class DataWorker implements Runnable {
    private final int workerId;
    private final CyclicBarrier barrier;
    private final int[][] allData;

    public DataWorker(int workerId, CyclicBarrier barrier, int[][] allData) {
        this.workerId = workerId;
        this.barrier = barrier;
        this.allData = allData;
    }

    @Override
    public void run() {
        try {
            // Phase 1: Load
            System.out.println("[Worker-" + workerId + "] Phase 1: Loading data: " + 
                Arrays.toString(allData[workerId]));
            Thread.sleep(100 + (long)(Math.random() * 200));
            barrier.await();

            // Phase 2: Transform (sort each chunk)
            System.out.println("[Worker-" + workerId + "] Phase 2: Sorting data");
            Arrays.sort(allData[workerId]);
            Thread.sleep(100 + (long)(Math.random() * 200));
            barrier.await();

            // Phase 3: Validate
            System.out.println("[Worker-" + workerId + "] Phase 3: Validating - sorted: " + 
                Arrays.toString(allData[workerId]));
            boolean valid = validateSorted(allData[workerId]);
            System.out.println("[Worker-" + workerId + "] Validation: " + 
                (valid ? "PASSED" : "FAILED"));
            barrier.await();

            System.out.println("[Worker-" + workerId + "] Complete!");

        } catch (InterruptedException e) {
            System.out.println("[Worker-" + workerId + "] Interrupted!");
            Thread.currentThread().interrupt();
        } catch (BrokenBarrierException e) {
            System.out.println("[Worker-" + workerId + "] Barrier broken!");
        }
    }

    private boolean validateSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i-1]) return false;
        }
        return true;
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Parallel Data Processor ===
 * 
 * Original data: [5, 2, 8, 1, 9, 3, 7, 4, 6, 10, 12, 11]
 * [Worker-0] Phase 1: Loading data: [5, 2, 8]
 * [Worker-1] Phase 1: Loading data: [1, 9, 3]
 * [Worker-2] Phase 1: Loading data: [7, 4, 6]
 * [Worker-3] Phase 1: Loading data: [10, 12, 11]
 * 
 * >>> Phase 1 complete <<<
 * [Worker-0] Phase 2: Sorting data
 * [Worker-2] Phase 2: Sorting data
 * [Worker-1] Phase 2: Sorting data
 * [Worker-3] Phase 2: Sorting data
 * 
 * >>> Phase 2 complete <<<
 * [Worker-0] Phase 3: Validating - sorted: [2, 5, 8]
 * [Worker-1] Phase 3: Validating - sorted: [1, 3, 9]
 * [Worker-2] Phase 3: Validating - sorted: [4, 6, 7]
 * [Worker-3] Phase 3: Validating - sorted: [10, 11, 12]
 * [Worker-0] Validation: PASSED
 * [Worker-1] Validation: PASSED
 * [Worker-2] Validation: PASSED
 * [Worker-3] Validation: PASSED
 * 
 * >>> Phase 3 complete <<<
 * Merging results from all workers...
 * [Worker-0] Complete!
 * [Worker-1] Complete!
 * [Worker-2] Complete!
 * [Worker-3] Complete!
 * 
 * Final result: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
 * 
 * 
 * KEY INSIGHTS:
 * 
 * 1. BARRIER ACTION: The merge operation runs in the barrier action,
 *    which executes after all workers arrive but before they continue.
 * 
 * 2. PHASE SYNCHRONIZATION: Workers can't start phase N+1 until all
 *    workers complete phase N. This ensures data consistency.
 * 
 * 3. ERROR HANDLING: BrokenBarrierException occurs if any thread is
 *    interrupted while waiting. All threads should handle this.
 * 
 * 4. REUSABILITY: CyclicBarrier resets automatically after each use,
 *    making it perfect for multi-phase algorithms.
 */
