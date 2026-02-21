# Module 2 Quiz: Synchronization

Test your understanding of synchronization concepts.

---

## Multiple Choice Questions

### Q1: Race Condition
Which operation is NOT atomic in Java?

A) Reading an int variable  
B) Writing a reference variable  
C) count++  
D) Writing a boolean variable  

<details>
<summary>Answer</summary>

**C) count++**

`count++` is actually three operations: read, increment, write. All other options are single operations that are atomic for most primitive types.
</details>

---

### Q2: synchronized Keyword
What does synchronized on an instance method lock on?

A) The Class object  
B) The method itself  
C) The `this` reference  
D) A global JVM lock  

<details>
<summary>Answer</summary>

**C) The `this` reference**

Instance synchronized methods lock on `this`. Static synchronized methods lock on the Class object.
</details>

---

### Q3: volatile vs synchronized
Which statement is TRUE about volatile?

A) volatile provides mutual exclusion  
B) volatile makes compound operations atomic  
C) volatile ensures visibility of changes across threads  
D) volatile is slower than synchronized  

<details>
<summary>Answer</summary>

**C) volatile ensures visibility of changes across threads**

volatile only guarantees visibility, not atomicity or mutual exclusion. It's generally faster than synchronized.
</details>

---

### Q4: Lock Object
What's wrong with this code?

```java
public void process() {
    synchronized (new Object()) {
        count++;
    }
}
```

A) Can't synchronize on Object  
B) Each call creates a new lock, providing no protection  
C) Object is null  
D) Missing synchronized on count declaration  

<details>
<summary>Answer</summary>

**B) Each call creates a new lock, providing no protection**

Each call creates a new Object, so different threads use different locks. They never block each other!
</details>

---

### Q5: Reentrant Locks
What happens when a thread holding a lock tries to acquire it again?

A) Deadlock occurs  
B) IllegalMonitorStateException is thrown  
C) The thread acquires it again (reentrant)  
D) The thread waits until it releases the first lock  

<details>
<summary>Answer</summary>

**C) The thread acquires it again (reentrant)**

Java's intrinsic locks are reentrant. A thread can acquire the same lock multiple times without deadlock.
</details>

---

### Q6: Double-Checked Locking
Why is volatile required in double-checked locking?

A) To make the instance creation faster  
B) To prevent instruction reordering that could expose partially constructed objects  
C) To ensure the instance is garbage collected properly  
D) volatile is not actually required  

<details>
<summary>Answer</summary>

**B) To prevent instruction reordering that could expose partially constructed objects**

Without volatile, a thread might see a non-null reference to an object whose constructor hasn't completed.
</details>

---

### Q7: Visibility
Without synchronization or volatile, what can happen?

A) Threads always see the latest values  
B) Threads may cache old values and never see updates  
C) The JVM throws an exception  
D) The program crashes  

<details>
<summary>Answer</summary>

**B) Threads may cache old values and never see updates**

The JVM and CPU may cache variables in registers or thread-local caches. Without synchronization, changes might not be visible to other threads.
</details>

---

### Q8: Synchronizing getters
Is it necessary to synchronize a getter method?

A) Never, getters only read data  
B) Only if the field is an object reference  
C) Yes, if any other method modifies the field while synchronized  
D) Only for primitive types  

<details>
<summary>Answer</summary>

**C) Yes, if any other method modifies the field while synchronized**

For proper visibility and happens-before guarantees, getters should also be synchronized if setters are synchronized.
</details>

---

## Code Output Questions

### Q9: What is the potential issue?

```java
private Integer count = 0;

public void increment() {
    synchronized (count) {
        count++;
    }
}
```

A) No issue, this is correct  
B) Synchronizing on Integer which gets replaced on each increment  
C) Integer is immutable so can't be incremented  
D) synchronized can't be used with Integer  

<details>
<summary>Answer</summary>

**B) Synchronizing on Integer which gets replaced on each increment**

`count++` replaces the Integer object (autoboxing creates new object). Each increment changes what we're synchronizing on! This provides no real protection.
</details>

---

### Q10: What does this print?

```java
private static int x = 0;
private static int y = 0;

// Thread 1
x = 1;
System.out.print(y);

// Thread 2  
y = 1;
System.out.print(x);
```

A) Always "11"  
B) Could be "00", "01", "10", or "11"  
C) Always "00"  
D) Compilation error  

<details>
<summary>Answer</summary>

**B) Could be "00", "01", "10", or "11"**

Without synchronization or volatile, each thread may not see the other's writes, and operations may be reordered. All four outcomes are possible!
</details>

---

### Q11: Is this thread-safe?

```java
private volatile int count = 0;

public void increment() {
    count++;
}
```

A) Yes, volatile makes it thread-safe  
B) No, count++ is not atomic even with volatile  
C) Yes, but only for single-threaded access  
D) No, volatile can't be used with int  

<details>
<summary>Answer</summary>

**B) No, count++ is not atomic even with volatile**

volatile ensures visibility but not atomicity. count++ is still read-modify-write, which can race. Use synchronized or AtomicInteger.
</details>

---

## True or False

### Q12
**True or False:** A thread must own a lock before calling wait() on that object.

<details>
<summary>Answer</summary>

**True**

You must call wait() from within a synchronized block on the same object, or you get IllegalMonitorStateException.
</details>

---

### Q13
**True or False:** Making a field final eliminates the need for synchronization when reading it.

<details>
<summary>Answer</summary>

**True**

Final fields are safely published after construction completes. They can be read without synchronization. However, if the final field refers to a mutable object, access to that object's contents still needs synchronization.
</details>

---

### Q14
**True or False:** Static synchronized methods and instance synchronized methods use the same lock.

<details>
<summary>Answer</summary>

**False**

Static methods lock on the Class object (e.g., `MyClass.class`). Instance methods lock on `this`. They're different locks and don't block each other.
</details>

---

## Short Answer

### Q15
Explain how to prevent deadlock when two methods need to lock two different objects.

<details>
<summary>Answer</summary>

**Lock Ordering:** Always acquire locks in a consistent, predetermined order across all threads.

Example: When locking accounts A and B, always lock the one with the lower ID first:

```java
BankAccount first = a.id < b.id ? a : b;
BankAccount second = a.id < b.id ? b : a;
synchronized (first) {
    synchronized (second) {
        // transfer
    }
}
```

This prevents circular wait conditions.

Other strategies:
- Use a single coarse-grained lock
- Use tryLock with timeout (with Lock interface)
- Restructure to avoid needing multiple locks
</details>

---

### Q16
What's the difference between visibility and atomicity?

<details>
<summary>Answer</summary>

**Visibility:** Changes made by one thread are seen by other threads. Without visibility guarantees, threads may see stale cached values.

**Atomicity:** An operation completes entirely or not at all, with no intermediate state visible to other threads. Compound operations like `count++` are not atomic.

**Key distinction:**
- volatile provides visibility but NOT atomicity
- synchronized provides BOTH visibility AND atomicity

Example:
```java
// volatile ensures visibility but count++ still races
volatile int count;
count++;  // Not atomic!

// synchronized ensures both
synchronized void increment() {
    count++;  // Atomic within the synchronized block
}
```
</details>

---

## Score Yourself

- **14-16 correct:** Excellent! Ready for Module 3
- **11-13 correct:** Good, review volatile vs synchronized
- **8-10 correct:** Review the module carefully
- **Below 8:** Re-study examples and re-read the concepts

---

**Next:** [Module 3 - Thread Communication](../03-thread-communication/README.md)
