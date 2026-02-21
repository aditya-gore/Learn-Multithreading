/**
 * Fork/Join Framework Example: Parallel Array Sum
 */

import java.util.concurrent.*;

public class Example01_ForkJoin {

    public static void main(String[] args) {
        System.out.println("=== Fork/Join Demo ===\n");

        // Create large array
        int[] array = new int[10_000_000];
        for (int i = 0; i < array.length; i++) {
            array[i] = i + 1;
        }

        // Sequential sum
        long start = System.currentTimeMillis();
        long seqSum = 0;
        for (int n : array) seqSum += n;
        long seqTime = System.currentTimeMillis() - start;

        // Parallel sum with Fork/Join
        ForkJoinPool pool = ForkJoinPool.commonPool();
        start = System.currentTimeMillis();
        long parSum = pool.invoke(new SumTask(array, 0, array.length));
        long parTime = System.currentTimeMillis() - start;

        System.out.println("Sequential sum: " + seqSum + " in " + seqTime + "ms");
        System.out.println("Parallel sum:   " + parSum + " in " + parTime + "ms");
        System.out.println("Speedup: " + String.format("%.2f", (double)seqTime/parTime) + "x");
    }
}

class SumTask extends RecursiveTask<Long> {
    private final int[] arr;
    private final int start, end;
    private static final int THRESHOLD = 10000;

    public SumTask(int[] arr, int start, int end) {
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += arr[i];
            }
            return sum;
        }

        int mid = (start + end) / 2;
        SumTask left = new SumTask(arr, start, mid);
        SumTask right = new SumTask(arr, mid, end);

        left.fork();  // Execute left async
        long rightResult = right.compute();  // Execute right in current thread
        long leftResult = left.join();  // Wait for left

        return leftResult + rightResult;
    }
}
