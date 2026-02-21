/**
 * Exercise 01: Thread Race
 * 
 * TASK:
 * Create 3 threads that each count from 1 to 5, printing their progress.
 * Each thread should have a unique name (Runner-1, Runner-2, Runner-3).
 * Add a small random delay between counts to see interleaving.
 * 
 * EXPECTED OUTPUT (order will vary):
 * Race starting!
 * Runner-1: 1
 * Runner-2: 1
 * Runner-3: 1
 * Runner-1: 2
 * Runner-3: 2
 * ... (interleaved)
 * Runner-2: 5
 * Runner-1: 5
 * Runner-3: 5
 * Race finished!
 * 
 * HINTS:
 * 1. Use Thread.sleep() with random duration for delay
 * 2. Use Thread.currentThread().getName() to get thread name
 * 3. Use join() to wait for all threads before printing "Race finished!"
 * 
 * BONUS: Track which thread finishes first and print the winner!
 */

import java.util.Random;

public class Exercise01_ThreadRace {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Race starting!\n");

        // TODO: Create 3 runner threads
        // Each thread should:
        // 1. Count from 1 to 5
        // 2. Print "[ThreadName]: [count]" for each number
        // 3. Sleep for a random time (0-100ms) between counts

        // YOUR CODE HERE

        System.out.println("\nRace finished!");
    }
}

/*
 * LEARNING GOALS:
 * - Practice creating multiple threads
 * - Observe non-deterministic thread interleaving
 * - Use join() to coordinate thread completion
 * 
 * When you're done, compare with: solutions/Exercise01_Solution.java
 */
