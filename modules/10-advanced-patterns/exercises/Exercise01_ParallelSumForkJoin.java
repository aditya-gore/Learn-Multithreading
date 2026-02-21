/**
 * Exercise 01: Parallel Sum with Fork/Join
 *
 * TASK:
 * Implement a RecursiveTask that computes the sum of an int array in parallel
 * using the Fork/Join framework. Use a threshold (e.g. 1000): below threshold
 * compute sequentially; above threshold split into two subtasks, fork one,
 * compute the other, then join and add results.
 *
 * REQUIREMENTS:
 * 1. Class extends RecursiveTask<Long>
 * 2. Constructor takes (int[] array, int start, int end)
 * 3. If (end - start) <= THRESHOLD, sum in a loop and return
 * 4. Else: create left and right tasks, left.fork(), rightResult = right.compute(), leftResult = left.join(), return sum
 * 5. In main: create array, invoke task via ForkJoinPool.commonPool().invoke(task)
 *
 * When done, compare with: solutions/Exercise01_Solution.java
 */

import java.util.concurrent.*;

public class Exercise01_ParallelSumForkJoin {

    public static void main(String[] args) {
        // TODO: Create int array (e.g. size 100_000), fill with values
        // TODO: Create SumTask, invoke via ForkJoinPool, print result

        System.out.println("Implement Fork/Join parallel sum!");
    }
}

// TODO: class SumTask extends RecursiveTask<Long> { ... }
