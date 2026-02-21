# Module 5 Quiz: Locks and Atomic Operations

Test your understanding of locks, atomics, and CAS.

---

## Multiple Choice Questions

### Q1: ReentrantLock Advantage
Which feature does ReentrantLock have that synchronized does NOT?

A) Mutual exclusion  
B) Reentrancy  
C) Timed lock acquisition with tryLock(timeout)  
D) Automatic lock release  

<details>
<summary>Answer</summary>

**C) Timed lock acquisition with tryLock(timeout)**

Both provide mutual exclusion and reentrancy. synchronized automatically releases; ReentrantLock does not. The key advantage is tryLock with timeout.
</details>

---

### Q2: Lock Release
What happens if you forget to call unlock() on a ReentrantLock?

A) The lock is automatically released when the method exits  
B) The lock remains held, potentially causing deadlock  
C) An exception is thrown  
D) The JVM releases it during garbage collection  

<details>
<summary>Answer</summary>

**B) The lock remains held, potentially causing deadlock**

Unlike synchronized, ReentrantLock requires explicit unlock(). Forgetting it keeps the lock held permanently, blocking other threads.
</details>

---

### Q3: ReadWriteLock
What does ReadWriteLock allow?

A) Only one reader at a time  
B) Multiple readers OR one writer  
C) Multiple readers AND multiple writers  
D) One reader AND one writer  

<details>
<summary>Answer</summary>

**B) Multiple readers OR one writer**

ReadWriteLock allows concurrent read access but exclusive write access. During a write, no readers or other writers can proceed.
</details>

---

### Q4: AtomicInteger
Which operation is NOT atomic for a regular int but IS atomic for AtomicInteger?

A) Reading the value  
B) Writing a new value  
C) count++  
D) Comparing two values  

<details>
<summary>Answer</summary>

**C) count++**

count++ is read-modify-write (3 operations). AtomicInteger.incrementAndGet() does this atomically using CAS.
</details>

---

### Q5: Compare-And-Set
What does compareAndSet(expected, new) do if the current value does NOT equal expected?

A) Sets the value to new anyway  
B) Throws an exception  
C) Returns false and does nothing  
D) Blocks until the value equals expected  

<details>
<summary>Answer</summary>

**C) Returns false and does nothing**

CAS is conditional: it only updates if current == expected. If not, it returns false without modifying the value.
</details>

---

### Q6: Fair Lock
What does `new ReentrantLock(true)` create?

A) A lock that always fails  
B) A lock that grants access in FIFO order  
C) A lock that only works in single-threaded mode  
D) A lock with no reentrancy  

<details>
<summary>Answer</summary>

**B) A lock that grants access in FIFO order**

`true` enables fairness. Threads acquire the lock in the order they requested it, preventing starvation.
</details>

---

### Q7: ABA Problem
What is the ABA problem?

A) Lock contention causing slowdown  
B) A thread reading stale data  
C) CAS succeeds because value changed back to original (A→B→A)  
D) Deadlock between threads A and B  

<details>
<summary>Answer</summary>

**C) CAS succeeds because value changed back to original (A→B→A)**

CAS sees the value is A (same as expected) and succeeds, but the value actually changed in between, which might matter for some algorithms.
</details>

---

### Q8: Condition vs wait()
What is an advantage of Condition over wait()/notify()?

A) Condition is faster  
B) Multiple conditions can be associated with one lock  
C) Condition doesn't require holding a lock  
D) Condition automatically retries  

<details>
<summary>Answer</summary>

**B) Multiple conditions can be associated with one lock**

With Condition, you can have separate wait queues (e.g., notFull and notEmpty for a buffer), allowing more targeted signaling.
</details>

---

## Code Output Questions

### Q9: What's the issue?

```java
Lock lock = new ReentrantLock();

public void process() {
    lock.lock();
    if (someCondition) {
        return;  // Early return
    }
    lock.unlock();
}
```

A) Nothing wrong  
B) Lock is never released on early return  
C) Lock is released twice  
D) Compilation error  

<details>
<summary>Answer</summary>

**B) Lock is never released on early return**

The early return skips unlock(). Always use try-finally:
```java
lock.lock();
try {
    if (someCondition) return;
    // ...
} finally {
    lock.unlock();
}
```
</details>

---

### Q10: What's the output?

```java
AtomicInteger counter = new AtomicInteger(10);
int old = counter.getAndAdd(5);
System.out.println(old + " " + counter.get());
```

A) 10 15  
B) 15 15  
C) 10 10  
D) 15 10  

<details>
<summary>Answer</summary>

**A) 10 15**

getAndAdd returns the old value (10), then adds 5. After the operation, counter is 15.
</details>

---

## True or False

### Q11
**True or False:** AtomicInteger is implemented using locks internally.

<details>
<summary>Answer</summary>

**False**

AtomicInteger uses hardware CAS (Compare-And-Swap) instructions, which are lock-free. This makes it faster than lock-based alternatives under contention.
</details>

---

### Q12
**True or False:** You can upgrade a read lock to a write lock in ReentrantReadWriteLock.

<details>
<summary>Answer</summary>

**False**

Lock upgrading is not supported. If multiple readers tried to upgrade simultaneously, they would deadlock. You must release the read lock first, then acquire the write lock.
</details>

---

### Q13
**True or False:** tryLock() with timeout can be used to prevent deadlock.

<details>
<summary>Answer</summary>

**True**

By using tryLock() with timeout, a thread can detect it's unable to acquire a lock and back off, breaking the "hold and wait" condition for deadlock.
</details>

---

## Short Answer

### Q14
When would you choose AtomicInteger over synchronized for a counter?

<details>
<summary>Answer</summary>

**Choose AtomicInteger when:**

1. **Simple operations:** Just increment, decrement, add, or compare-and-set.

2. **High contention:** Atomic operations scale better than locks because they don't block threads.

3. **Lock-free requirement:** Need guaranteed progress without blocking.

**Choose synchronized when:**

1. **Complex operations:** Need to perform multiple operations atomically (e.g., check-then-act on multiple variables).

2. **Need wait/notify:** Condition-based coordination.

3. **Already using synchronized:** Consistency with existing code.

General rule: For single-variable atomic operations, AtomicInteger is simpler and faster.
</details>

---

### Q15
Explain why this double-checked locking is broken without volatile:

```java
private static Singleton instance;

public static Singleton getInstance() {
    if (instance == null) {
        synchronized (Singleton.class) {
            if (instance == null) {
                instance = new Singleton();
            }
        }
    }
    return instance;
}
```

<details>
<summary>Answer</summary>

**The Problem: Instruction Reordering**

`instance = new Singleton()` involves three steps:
1. Allocate memory
2. Initialize the object (run constructor)
3. Assign reference to `instance`

Without `volatile`, the JVM may reorder to: 1, 3, 2.

**Race condition:**
- Thread A executes 1, 3 (instance is now non-null but uninitialized)
- Thread B checks `instance == null`, sees false
- Thread B returns the partially constructed object

**Solution:** Declare `instance` as `volatile`. This prevents reordering and ensures the constructor completes before the reference is visible to other threads.
</details>

---

## Score Yourself

- **13-15 correct:** Excellent! Ready for Module 6
- **10-12 correct:** Good, review CAS and lock patterns
- **7-9 correct:** Review atomic operations and lock pitfalls
- **Below 7:** Re-study the module and examples

---

**Next:** [Module 6 - Thread Pools](../06-thread-pools/README.md)
