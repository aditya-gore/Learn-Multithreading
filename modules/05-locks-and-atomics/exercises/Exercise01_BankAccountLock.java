/**
 * Exercise 01: Bank Account with ReentrantLock
 * 
 * TASK:
 * Refactor the bank account from Module 2 to use ReentrantLock instead of synchronized.
 * 
 * REQUIREMENTS:
 * 1. Use ReentrantLock for thread safety
 * 2. Implement tryTransfer() that times out if lock unavailable
 * 3. Use Condition objects for overdraft protection (wait until funds available)
 * 4. Prevent deadlock in transfer using tryLock()
 * 
 * METHODS TO IMPLEMENT:
 * - deposit(amount)
 * - withdraw(amount) - blocks until funds available (using Condition)
 * - tryWithdraw(amount, timeout) - returns false if timeout or insufficient funds
 * - transfer(amount, toAccount) - deadlock-safe using tryLock
 * - getBalance()
 * 
 * BONUS: Add a transaction history with timestamps.
 */

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;

public class Exercise01_BankAccountLock {

    public static void main(String[] args) throws InterruptedException {
        // TODO: Implement BankAccountWithLock class
        
        // Test:
        // BankAccountWithLock acc1 = new BankAccountWithLock(1, 1000);
        // BankAccountWithLock acc2 = new BankAccountWithLock(2, 1000);
        
        // Test concurrent transfers without deadlock
        // Test withdraw that waits for deposit
        // Test tryWithdraw with timeout
        
        System.out.println("Implement BankAccountWithLock and uncomment the tests!");
    }
}

// TODO: Implement this class
// class BankAccountWithLock {
//     private final int id;
//     private double balance;
//     private final ReentrantLock lock = new ReentrantLock();
//     private final Condition sufficientFunds = lock.newCondition();
//     
//     public BankAccountWithLock(int id, double initialBalance) { }
//     
//     public void deposit(double amount) { }
//     
//     // Blocks until sufficient funds available
//     public void withdraw(double amount) throws InterruptedException { }
//     
//     // Returns false if timeout or insufficient funds after timeout
//     public boolean tryWithdraw(double amount, long timeout, TimeUnit unit) 
//         throws InterruptedException { }
//     
//     // Deadlock-safe transfer using tryLock
//     public boolean transfer(double amount, BankAccountWithLock toAccount) 
//         throws InterruptedException { }
//     
//     public double getBalance() { }
// }

/*
 * LEARNING GOALS:
 * - Practice ReentrantLock API
 * - Use Condition objects for complex waiting
 * - Use tryLock() for deadlock prevention
 * 
 * When done, compare with: solutions/Exercise01_Solution.java
 */
