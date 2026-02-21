/**
 * Solution for Exercise 01: Bank Account with ReentrantLock
 */

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;

public class Exercise01_Solution {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Bank Account with ReentrantLock ===\n");

        testBasicOperations();
        testConditionalWithdraw();
        testDeadlockFreeTransfer();
    }

    private static void testBasicOperations() {
        System.out.println("--- Basic Operations ---");
        BankAccountWithLock account = new BankAccountWithLock(1, 1000);
        
        account.deposit(500);
        System.out.println("After deposit 500: " + account.getBalance());  // 1500
        
        try {
            account.withdraw(200);
            System.out.println("After withdraw 200: " + account.getBalance());  // 1300
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testConditionalWithdraw() throws InterruptedException {
        System.out.println("--- Conditional Withdraw ---");
        BankAccountWithLock account = new BankAccountWithLock(1, 100);

        // Thread tries to withdraw more than available
        Thread withdrawer = new Thread(() -> {
            try {
                System.out.println("[Withdrawer] Trying to withdraw 500 (only 100 available)");
                account.withdraw(500);  // Will wait for funds
                System.out.println("[Withdrawer] Withdrew 500!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Thread deposits after delay
        Thread depositor = new Thread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("[Depositor] Depositing 400");
                account.deposit(400);  // Now 500 available, withdrawer can proceed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        withdrawer.start();
        depositor.start();
        withdrawer.join();
        depositor.join();
        System.out.println("Final balance: " + account.getBalance());
        System.out.println();
    }

    private static void testDeadlockFreeTransfer() throws InterruptedException {
        System.out.println("--- Deadlock-Free Transfer ---");
        BankAccountWithLock acc1 = new BankAccountWithLock(1, 1000);
        BankAccountWithLock acc2 = new BankAccountWithLock(2, 1000);

        // Two threads transfer in opposite directions - potential deadlock!
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    acc1.transfer(10, acc2);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    acc2.transfer(10, acc1);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        t1.start();
        t2.start();
        t1.join(5000);  // Timeout to detect deadlock
        t2.join(5000);

        if (t1.isAlive() || t2.isAlive()) {
            System.out.println("DEADLOCK DETECTED!");
            t1.interrupt();
            t2.interrupt();
        } else {
            System.out.println("All transfers completed without deadlock!");
            System.out.println("Account 1: " + acc1.getBalance());
            System.out.println("Account 2: " + acc2.getBalance());
            System.out.println("Total (should be 2000): " + 
                (acc1.getBalance() + acc2.getBalance()));
        }
    }
}

class BankAccountWithLock {
    private final int id;
    private double balance;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition sufficientFunds = lock.newCondition();

    public BankAccountWithLock(int id, double initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public int getId() {
        return id;
    }

    public void deposit(double amount) {
        lock.lock();
        try {
            balance += amount;
            sufficientFunds.signalAll();  // Wake up waiting withdrawers
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(double amount) throws InterruptedException {
        lock.lock();
        try {
            while (balance < amount) {
                sufficientFunds.await();  // Wait until enough funds
            }
            balance -= amount;
        } finally {
            lock.unlock();
        }
    }

    public boolean tryWithdraw(double amount, long timeout, TimeUnit unit) 
            throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        lock.lock();
        try {
            while (balance < amount) {
                if (nanos <= 0) {
                    return false;  // Timeout expired
                }
                nanos = sufficientFunds.awaitNanos(nanos);
            }
            balance -= amount;
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Transfer using tryLock() to prevent deadlock.
     * 
     * If we can't get both locks, release and retry.
     * This avoids the circular-wait condition for deadlock.
     */
    public boolean transfer(double amount, BankAccountWithLock toAccount) 
            throws InterruptedException {
        while (true) {
            if (lock.tryLock()) {
                try {
                    if (toAccount.lock.tryLock()) {
                        try {
                            if (balance < amount) {
                                return false;  // Insufficient funds
                            }
                            balance -= amount;
                            toAccount.balance += amount;
                            return true;
                        } finally {
                            toAccount.lock.unlock();
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
            // Couldn't get both locks - sleep briefly and retry
            Thread.sleep(1);
        }
    }

    public double getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }
}

/*
 * KEY INSIGHTS:
 * 
 * 1. CONDITION FOR WAITING:
 *    sufficientFunds.await() is like wait() but works with Lock.
 *    When deposit() adds funds, it signals waiters.
 * 
 * 2. TIMED AWAIT:
 *    awaitNanos() returns remaining time, allowing precise timeout handling.
 * 
 * 3. DEADLOCK-FREE TRANSFER:
 *    Using tryLock() instead of lock() means we never block waiting
 *    for a lock while holding another. If we can't get both, we release
 *    and retry. This breaks the circular-wait condition.
 * 
 * 4. ALWAYS UNLOCK IN FINALLY:
 *    Unlike synchronized, failing to unlock causes permanent lock.
 */
