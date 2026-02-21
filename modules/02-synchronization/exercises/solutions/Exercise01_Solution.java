/**
 * Solution for Exercise 01: Thread-Safe Bank Account
 */
public class Exercise01_Solution {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Thread-Safe Bank Account Test ===\n");

        BankAccount account1 = new BankAccount(1, 1000);
        BankAccount account2 = new BankAccount(2, 1000);

        double initialTotal = account1.getBalance() + account2.getBalance();
        System.out.println("Initial total: $" + initialTotal);

        int numTransfers = 100;
        Thread[] threads = new Thread[10];

        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < numTransfers / threads.length; j++) {
                    double amount = Math.random() * 100;
                    // Alternate transfer directions
                    if (threadId % 2 == 0) {
                        account1.transfer(amount, account2);
                    } else {
                        account2.transfer(amount, account1);
                    }
                }
            });
        }

        // Start all threads
        for (Thread t : threads) {
            t.start();
        }

        // Wait for completion
        for (Thread t : threads) {
            t.join();
        }

        double finalTotal = account1.getBalance() + account2.getBalance();
        System.out.println("\nFinal balances:");
        System.out.println("  Account 1: $" + account1.getBalance());
        System.out.println("  Account 2: $" + account2.getBalance());
        System.out.println("  Total: $" + finalTotal);
        
        System.out.println("\nMoney preserved: " + 
            (Math.abs(finalTotal - initialTotal) < 0.01 ? "✓ YES" : "✗ NO"));
    }
}

class BankAccount {
    private final int id;
    private double balance;

    public BankAccount(int id, double initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public int getId() {
        return id;
    }

    public synchronized void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance += amount;
    }

    public synchronized boolean withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }
        if (balance < amount) {
            return false;  // Insufficient funds
        }
        balance -= amount;
        return true;
    }

    /**
     * Transfer money to another account atomically.
     * 
     * CRITICAL: We must lock both accounts, but in a consistent order
     * to prevent deadlock!
     * 
     * Deadlock scenario without consistent ordering:
     *   Thread 1: lock(account1), trying to lock(account2)
     *   Thread 2: lock(account2), trying to lock(account1)
     *   → Both threads wait forever!
     * 
     * Solution: Always lock the account with lower ID first.
     */
    public boolean transfer(double amount, BankAccount toAccount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        // Determine lock order by account ID
        BankAccount first = this.id < toAccount.id ? this : toAccount;
        BankAccount second = this.id < toAccount.id ? toAccount : this;

        synchronized (first) {
            synchronized (second) {
                if (this.balance < amount) {
                    return false;  // Insufficient funds
                }
                this.balance -= amount;
                toAccount.balance += amount;
                return true;
            }
        }
    }

    public synchronized double getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "BankAccount{id=" + id + ", balance=" + balance + "}";
    }
}

/*
 * KEY LEARNINGS:
 * 
 * 1. DEADLOCK PREVENTION:
 *    The transfer() method locks two accounts. Without consistent
 *    ordering, we risk deadlock:
 *    
 *    Thread 1: transfer from A to B → lock A, wait for B
 *    Thread 2: transfer from B to A → lock B, wait for A
 *    
 *    By always locking the lower-ID account first, we ensure
 *    all threads acquire locks in the same order.
 * 
 * 2. ATOMIC OPERATIONS:
 *    The transfer is atomic because both balance changes happen
 *    while holding both locks. No other thread can see an
 *    intermediate state.
 * 
 * 3. DEFENSIVE CHECKS:
 *    We validate inputs and check for sufficient funds before
 *    modifying state.
 * 
 * ALTERNATIVE APPROACHES:
 * 
 * 1. Use a single global lock (simpler but less concurrent)
 * 2. Use java.util.concurrent.locks.Lock with tryLock (more flexible)
 * 3. Use lock-free algorithms with AtomicReference (advanced)
 */
