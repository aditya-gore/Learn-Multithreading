/**
 * Exercise 02: Barrier-Based Rendezvous
 *
 * TASK:
 * Three (or N) threads must all reach a "rendezvous" point before any of them proceed.
 * Use CyclicBarrier so that each thread prints "Ready", waits at the barrier,
 * then prints "Go" after all have arrived.
 *
 * REQUIREMENTS:
 * 1. Use CyclicBarrier(3) for three threads
 * 2. Each thread: do some work (e.g. sleep), print "Ready", barrier.await(), print "Go"
 * 3. All "Ready" messages appear before any "Go"
 *
 * When done, compare with: solutions/Exercise02_Solution.java
 */

import java.util.concurrent.CyclicBarrier;

public class Exercise02_BarrierRendezvous {

    public static void main(String[] args) throws Exception {
        // TODO: Create CyclicBarrier(3)
        // TODO: Start 3 threads that print Ready, await(), then print Go

        System.out.println("Implement barrier rendezvous!");
    }
}
