/**
 * Exercise 01: Ping Pong
 * 
 * TASK:
 * Create two threads that alternate printing "Ping" and "Pong":
 * - Thread 1 prints "Ping"
 * - Thread 2 prints "Pong"
 * - They must alternate exactly: Ping, Pong, Ping, Pong...
 * - Print 10 rounds total
 * 
 * EXPECTED OUTPUT:
 * Ping
 * Pong
 * Ping
 * Pong
 * ... (10 times each)
 * 
 * HINTS:
 * 1. Use a shared boolean to track whose turn it is
 * 2. Each thread waits while it's NOT their turn
 * 3. After printing, flip the boolean and notify
 * 
 * BONUS: Extend to three threads: Ping, Pong, Pang
 */
public class Exercise01_PingPong {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ping Pong ===\n");

        // TODO: Create shared state to coordinate turns
        // TODO: Create Ping thread that prints "Ping" when it's its turn
        // TODO: Create Pong thread that prints "Pong" when it's its turn
        // TODO: Start both threads and wait for completion

        System.out.println("Implement the ping pong coordination!");
    }
}

// TODO: You might want to create a coordinator class:
// class PingPongCoordinator {
//     private boolean pingTurn = true;
//     
//     public synchronized void ping() throws InterruptedException {
//         // Wait while it's NOT ping's turn
//         // Print "Ping"
//         // Switch turn
//         // Notify
//     }
//     
//     public synchronized void pong() throws InterruptedException {
//         // Wait while it's NOT pong's turn
//         // Print "Pong"
//         // Switch turn
//         // Notify
//     }
// }

/*
 * LEARNING GOALS:
 * - Practice using wait/notify for turn-based coordination
 * - Understand how to enforce ordering between threads
 * 
 * When done, compare with: solutions/Exercise01_Solution.java
 */
