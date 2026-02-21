/**
 * Exercise 01: Thread-Safe Bank Account
 * 
 * TASK:
 * Implement a thread-safe bank account that supports:
 * - deposit(amount)
 * - withdraw(amount) - should fail if insufficient funds
 * - transfer(amount, toAccount) - atomic transfer between accounts
 * - getBalance()
 * 
 * REQUIREMENTS:
 * 1. All operations must be thread-safe
 * 2. withdraw() should return false if balance < amount (don't go negative)
 * 3. transfer() must be atomic - both accounts update together or neither does
 * 4. transfer() must not deadlock even if two accounts transfer to each other
 * 
 * TEST SCENARIO:
 * - Create 2 accounts with $1000 each ($2000 total)
 * - Run 100 transfers of random amounts between them
 * - Final total should still be $2000
 * 
 * HINTS:
 * 1. Use synchronized for thread safety
 * 2. For transfer(), be careful about lock ordering to avoid deadlock
 * 3. Consider using account IDs to establish consistent lock order
 * 
 * BONUS: Add a transaction history that records all operations.
 */
public class Exercise01_BankAccount {

    public static void main(String[] args) throws InterruptedException {
        // TODO: Implement BankAccount class

        // Test your implementation:
        // BankAccount account1 = new BankAccount(1, 1000);
        // BankAccount account2 = new BankAccount(2, 1000);
        
        // Create threads that do random transfers
        // Verify total money is preserved
        
        System.out.println("Implement the BankAccount class and uncomment the test code!");
    }
}

// TODO: Implement this class
// class BankAccount {
//     private final int id;
//     private double balance;
//     
//     public BankAccount(int id, double initialBalance) { }
//     
//     public synchronized void deposit(double amount) { }
//     
//     public synchronized boolean withdraw(double amount) { }
//     
//     // TRICKY: How to lock both accounts without deadlock?
//     public boolean transfer(double amount, BankAccount toAccount) { }
//     
//     public synchronized double getBalance() { }
// }

/*
 * LEARNING GOALS:
 * - Practice using synchronized for mutual exclusion
 * - Understand the deadlock problem with multiple locks
 * - Learn lock ordering to prevent deadlock
 * 
 * When done, compare with: solutions/Exercise01_Solution.java
 */
