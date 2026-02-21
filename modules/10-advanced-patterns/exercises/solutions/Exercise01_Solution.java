/**
 * Solution for Exercise 01: Parallel Sum with Fork/Join
 */

import java.util.concurrent.*;

public class Exercise01_Solution {

    public static void main(String[] args) {
        int[] array = new int[100_000];
        for (int i = 0; i < array.length; i++) array[i] = i + 1;

        ForkJoinPool pool = ForkJoinPool.commonPool();
        long sum = pool.invoke(new SumTask(array, 0, array.length));
        System.out.println("Sum: " + sum);
    }
}

class SumTask extends RecursiveTask<Long> {
    private final int[] arr;
    private final int start, end;
    private static final int THRESHOLD = 1000;

    SumTask(int[] arr, int start, int end) {
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) sum += arr[i];
            return sum;
        }
        int mid = (start + end) / 2;
        SumTask left = new SumTask(arr, start, mid);
        SumTask right = new SumTask(arr, mid, end);
        left.fork();
        long rightResult = right.compute();
        long leftResult = left.join();
        return leftResult + rightResult;
    }
}
