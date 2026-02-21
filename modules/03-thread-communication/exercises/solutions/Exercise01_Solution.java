/**
 * Solution for Exercise 01: Ping Pong
 */
public class Exercise01_Solution {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Ping Pong Solution ===\n");

        PingPongCoordinator coordinator = new PingPongCoordinator();
        int rounds = 10;

        Thread pingThread = new Thread(() -> {
            try {
                for (int i = 0; i < rounds; i++) {
                    coordinator.ping();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "PingThread");

        Thread pongThread = new Thread(() -> {
            try {
                for (int i = 0; i < rounds; i++) {
                    coordinator.pong();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "PongThread");

        pingThread.start();
        pongThread.start();

        pingThread.join();
        pongThread.join();

        System.out.println("\n=== Done! ===");
    }
}

class PingPongCoordinator {
    private boolean pingTurn = true;

    public synchronized void ping() throws InterruptedException {
        // Wait while it's NOT ping's turn
        while (!pingTurn) {
            wait();
        }

        System.out.println("Ping");

        // Switch turn to pong
        pingTurn = false;
        notify();  // Wake up pong thread
    }

    public synchronized void pong() throws InterruptedException {
        // Wait while it's NOT pong's turn (i.e., while it's ping's turn)
        while (pingTurn) {
            wait();
        }

        System.out.println("Pong");

        // Switch turn to ping
        pingTurn = true;
        notify();  // Wake up ping thread
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Ping Pong Solution ===
 * 
 * Ping
 * Pong
 * Ping
 * Pong
 * Ping
 * Pong
 * ... (exactly alternating, 10 of each)
 * 
 * === Done! ===
 * 
 * 
 * KEY INSIGHTS:
 * 
 * 1. TURN-BASED COORDINATION:
 *    The boolean 'pingTurn' acts as a flag indicating whose turn it is.
 *    Each thread waits until it's their turn.
 * 
 * 2. SINGLE notify():
 *    Since only one other thread is waiting, notify() is sufficient.
 *    notifyAll() would also work but is unnecessary.
 * 
 * 3. WHILE LOOP:
 *    Always use while (!condition) not if (!condition) to handle
 *    spurious wakeups.
 * 
 * 
 * BONUS: Three-way Ping-Pong-Pang
 */
class ThreeWayCoordinator {
    private int turn = 0;  // 0=Ping, 1=Pong, 2=Pang

    public synchronized void ping() throws InterruptedException {
        while (turn != 0) wait();
        System.out.println("Ping");
        turn = 1;
        notifyAll();  // Need notifyAll() with more than 2 threads
    }

    public synchronized void pong() throws InterruptedException {
        while (turn != 1) wait();
        System.out.println("Pong");
        turn = 2;
        notifyAll();
    }

    public synchronized void pang() throws InterruptedException {
        while (turn != 2) wait();
        System.out.println("Pang");
        turn = 0;
        notifyAll();
    }
}
