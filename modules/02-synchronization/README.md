# Module 2: Synchronization

## Learning Objectives
By the end of this module, you will understand:
- What race conditions are and why they're dangerous
- How to use the `synchronized` keyword
- The difference between intrinsic locks and monitor locks
- How `volatile` ensures visibility
- The happens-before relationship

---

## 1. The Problem: Race Conditions

A **race condition** occurs when multiple threads access shared data concurrently, and at least one thread modifies it.

### Classic Example: Counter Race Condition

```java
class Counter {
    private int count = 0;
    
    public void increment() {
        count++;  // NOT atomic! This is actually 3 operations:
                  // 1. READ count
                  // 2. ADD 1
                  // 3. WRITE result back
    }
    
    public int getCount() {
        return count;
    }
}
```

```
Thread 1                    Thread 2                    count
────────                    ────────                    ─────
READ count (0)                                            0
                            READ count (0)                0
ADD 1 (result: 1)                                         0
                            ADD 1 (result: 1)             0
WRITE (1)                                                 1
                            WRITE (1)                     1

Expected: 2, Actual: 1  ← RACE CONDITION!
```

### Why Race Conditions Are Dangerous

1. **Non-deterministic**: Different results on different runs
2. **Hard to reproduce**: May work 999 times, fail once
3. **Hard to debug**: Traditional debugging changes timing
4. **Data corruption**: Can lead to invalid states

---

## 2. The Solution: synchronized Keyword

The `synchronized` keyword ensures that only one thread can execute a block of code at a time.

### Synchronized Methods

```java
class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;  // Now thread-safe!
    }
    
    public synchronized int getCount() {
        return count;
    }
}
```

### Synchronized Blocks

```java
class Counter {
    private int count = 0;
    private final Object lock = new Object();
    
    public void increment() {
        synchronized (lock) {  // Acquire lock
            count++;
        }  // Release lock
    }
}
```

### How It Works: Monitor Locks

```
┌─────────────────────────────────────────────────────────────┐
│                      OBJECT MONITOR                          │
│                                                              │
│   ┌──────────────┐                                          │
│   │    LOCK      │◄── Only ONE thread can hold this         │
│   │   (mutex)    │                                          │
│   └──────────────┘                                          │
│          │                                                   │
│          ▼                                                   │
│   ┌──────────────┐     ┌──────────────┐                     │
│   │   Thread 1   │     │ Thread 2     │                     │
│   │  (has lock)  │     │ (BLOCKED)    │                     │
│   │  executing   │     │  waiting     │                     │
│   └──────────────┘     └──────────────┘                     │
│                                                              │
│   When Thread 1 exits synchronized block:                    │
│   1. Releases lock                                           │
│   2. Thread 2 acquires lock                                  │
│   3. Thread 2 enters block                                   │
└─────────────────────────────────────────────────────────────┘
```

### Lock Objects

| Syntax | Lock Object |
|--------|-------------|
| `synchronized void method()` | `this` (instance) |
| `static synchronized void method()` | `ClassName.class` |
| `synchronized(obj) { }` | `obj` |

---

## 3. Reentrant Synchronization

A thread can acquire the same lock multiple times:

```java
class Reentrant {
    public synchronized void outer() {
        System.out.println("outer");
        inner();  // Same thread can enter another synchronized method
    }
    
    public synchronized void inner() {
        System.out.println("inner");
    }
}
```

This works because Java locks are **reentrant** - they track the owning thread and a count.

---

## 4. The volatile Keyword

`volatile` ensures **visibility** of changes across threads.

### The Visibility Problem

Without volatile, threads may cache variables locally:

```
┌────────────────────────────────────────────────────────────┐
│                     MAIN MEMORY                             │
│                    ┌─────────┐                              │
│                    │ flag=T  │                              │
│                    └─────────┘                              │
│                         │                                   │
│         ┌───────────────┼───────────────┐                  │
│         ▼               ▼               ▼                  │
│   ┌───────────┐   ┌───────────┐   ┌───────────┐           │
│   │ Thread 1  │   │ Thread 2  │   │ Thread 3  │           │
│   │ CPU Cache │   │ CPU Cache │   │ CPU Cache │           │
│   │ flag=T    │   │ flag=F    │   │ flag=T    │           │
│   └───────────┘   └───────────┘   └───────────┘           │
│                                                             │
│   Thread 2 may have stale value! (visibility issue)        │
└────────────────────────────────────────────────────────────┘
```

### volatile Ensures Visibility

```java
class Task {
    private volatile boolean running = true;  // volatile!
    
    public void run() {
        while (running) {  // Always reads from main memory
            doWork();
        }
    }
    
    public void stop() {
        running = false;  // Immediately visible to all threads
    }
}
```

### volatile vs synchronized

| Feature | volatile | synchronized |
|---------|----------|--------------|
| Visibility | Yes | Yes |
| Atomicity | No (single read/write only) | Yes |
| Mutual exclusion | No | Yes |
| Performance | Faster | Slower |
| Use case | Flags, single variables | Compound operations |

### When to Use volatile

✅ Good for:
- Boolean flags
- Single variable that's written by one thread, read by others
- Singleton double-checked locking (with care)

❌ Not sufficient for:
- `count++` (read-modify-write)
- Check-then-act operations
- Multiple variables that must be consistent

---

## 5. Happens-Before Relationship

The **happens-before** relationship defines the order guarantees in concurrent Java.

### Key Rules

1. **Program Order**: Within a thread, each action happens-before the next action.

2. **Monitor Lock**: An unlock happens-before every subsequent lock of the same monitor.

3. **Volatile Variable**: A write to volatile happens-before every subsequent read.

4. **Thread Start**: `thread.start()` happens-before any action in that thread.

5. **Thread Termination**: Any action in a thread happens-before `join()` returns.

```
Happens-Before Guarantees:
                                                              
Thread 1                Thread 2                              
────────                ────────                              
write x = 1                                                   
    │                                                         
    │ happens-before (volatile write/synchronized exit)       
    │                                                         
    └─────────────────► read x = 1                            
                        (guaranteed to see 1)                 
```

### Why It Matters

Without happens-before, compilers and CPUs may reorder operations:

```java
// Original code:
a = 1;
flag = true;

// May be reordered to:
flag = true;
a = 1;  // Another thread might see flag=true but a=0!
```

Making `flag` volatile prevents this reordering.

---

## 6. Double-Checked Locking Pattern

A classic pattern for lazy initialization of singletons:

```java
class Singleton {
    private static volatile Singleton instance;  // Must be volatile!
    
    public static Singleton getInstance() {
        if (instance == null) {              // First check (no lock)
            synchronized (Singleton.class) {
                if (instance == null) {      // Second check (with lock)
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

### Why volatile is Required

Without volatile, another thread might see a partially constructed object due to instruction reordering.

---

## 7. Common Synchronization Mistakes

### Mistake 1: Synchronizing on Wrong Object

```java
// WRONG: Each call creates new lock object!
public void bad() {
    synchronized (new Object()) {  // Useless!
        count++;
    }
}
```

### Mistake 2: Synchronizing on Non-Final Reference

```java
private Object lock = new Object();

// DANGEROUS: lock reference could change
public void risky() {
    synchronized (lock) {
        lock = new Object();  // Now another thread can enter!
    }
}

// CORRECT: Make lock final
private final Object lock = new Object();
```

### Mistake 3: Inconsistent Locking

```java
// WRONG: Not all methods synchronized
public synchronized void increment() { count++; }
public int getCount() { return count; }  // May see stale value!

// CORRECT: All access to shared state must be synchronized
public synchronized void increment() { count++; }
public synchronized int getCount() { return count; }
```

---

## 8. Interview Key Points

1. **What is a race condition?**
   - Multiple threads accessing shared data, at least one writing, without proper synchronization.

2. **synchronized vs volatile?**
   - synchronized: mutual exclusion + visibility
   - volatile: visibility only, no mutual exclusion

3. **What makes an operation atomic?**
   - Reads and writes to reference variables
   - Reads and writes to primitives (except long/double)
   - Reads and writes to volatile long/double
   - NOT: compound operations like `count++`

4. **Can you synchronize on a primitive?**
   - No, only on objects. Primitives aren't objects.

5. **What is reentrant synchronization?**
   - A thread that holds a lock can acquire it again without deadlock.

---

## Examples

Study in order:
1. [Example01_RaceCondition.java](examples/Example01_RaceCondition.java) - See a race condition in action
2. [Example02_SynchronizedFix.java](examples/Example02_SynchronizedFix.java) - Fix with synchronized
3. [Example03_VolatileFlag.java](examples/Example03_VolatileFlag.java) - Using volatile for flags
4. [Example04_DoubleCheckedLocking.java](examples/Example04_DoubleCheckedLocking.java) - Singleton pattern

---

## Exercises

1. [Exercise01_BankAccount.java](exercises/Exercise01_BankAccount.java) - Thread-safe bank account
2. [Exercise02_ThreadSafeCounter.java](exercises/Exercise02_ThreadSafeCounter.java) - Multiple counter operations

---

## Quiz

Test yourself: [quiz.md](quiz.md)

---

**Next Module:** [Module 3 - Thread Communication](../03-thread-communication/README.md)
