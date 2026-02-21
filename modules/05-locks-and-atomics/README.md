# Module 5: Locks and Atomic Operations

## Learning Objectives
By the end of this module, you will understand:
- ReentrantLock and its advantages over synchronized
- ReadWriteLock for read-heavy workloads
- Condition objects for advanced coordination
- Atomic classes and lock-free programming
- Compare-And-Swap (CAS) operations
- The ABA problem

---

## 1. ReentrantLock

`ReentrantLock` is an explicit lock with more features than `synchronized`.

### Basic Usage

```java
Lock lock = new ReentrantLock();

lock.lock();
try {
    // Critical section
} finally {
    lock.unlock();  // ALWAYS in finally!
}
```

### ReentrantLock vs synchronized

| Feature | synchronized | ReentrantLock |
|---------|-------------|---------------|
| Syntax | Implicit (keyword) | Explicit (object) |
| Lock release | Automatic | Manual (finally) |
| Fairness | No | Optional (fair mode) |
| Interruptible | No | Yes (lockInterruptibly) |
| Try with timeout | No | Yes (tryLock) |
| Multiple conditions | No | Yes (newCondition) |
| Lock query | No | Yes (isLocked, isHeldByCurrentThread) |

---

## 2. Key ReentrantLock Features

### Fairness

```java
// Non-fair (default): Better throughput, may starve
Lock unfair = new ReentrantLock();

// Fair: FIFO order, no starvation, slower
Lock fair = new ReentrantLock(true);
```

### tryLock() - Non-Blocking

```java
if (lock.tryLock()) {
    try {
        // Got lock
    } finally {
        lock.unlock();
    }
} else {
    // Lock not available, do something else
}
```

### tryLock(timeout) - Timed

```java
if (lock.tryLock(1, TimeUnit.SECONDS)) {
    try {
        // Got lock within timeout
    } finally {
        lock.unlock();
    }
} else {
    // Timeout expired
}
```

### lockInterruptibly() - Interruptible

```java
try {
    lock.lockInterruptibly();  // Can be interrupted while waiting
    try {
        // Critical section
    } finally {
        lock.unlock();
    }
} catch (InterruptedException e) {
    // Handle interruption
}
```

---

## 3. Condition Objects

Conditions replace wait/notify with more flexibility:

```java
Lock lock = new ReentrantLock();
Condition notFull = lock.newCondition();
Condition notEmpty = lock.newCondition();

// Producer
lock.lock();
try {
    while (isFull()) {
        notFull.await();  // Like wait()
    }
    add(item);
    notEmpty.signal();  // Like notify()
} finally {
    lock.unlock();
}

// Consumer
lock.lock();
try {
    while (isEmpty()) {
        notEmpty.await();
    }
    item = remove();
    notFull.signal();
} finally {
    lock.unlock();
}
```

### Condition vs wait/notify

| wait/notify | Condition |
|-------------|-----------|
| wait() | await() |
| wait(timeout) | await(timeout, unit) |
| notify() | signal() |
| notifyAll() | signalAll() |
| One wait set per object | Multiple conditions per lock |

---

## 4. ReadWriteLock

Allows multiple readers OR one writer:

```
┌─────────────────────────────────────────────────────────────┐
│                    ReadWriteLock                             │
│                                                              │
│   ┌─────────────┐              ┌─────────────┐              │
│   │  Read Lock  │              │ Write Lock  │              │
│   │   (shared)  │              │ (exclusive) │              │
│   └──────┬──────┘              └──────┬──────┘              │
│          │                            │                      │
│   Multiple readers                One writer only            │
│   can hold                        (blocks all)               │
│   simultaneously                                             │
│                                                              │
│   R1 ───┐                        W1 ──────────              │
│   R2 ───┼─── OK                  R1 ───X (blocked)          │
│   R3 ───┘                        R2 ───X (blocked)          │
└─────────────────────────────────────────────────────────────┘
```

### Example

```java
ReadWriteLock rwLock = new ReentrantReadWriteLock();
Lock readLock = rwLock.readLock();
Lock writeLock = rwLock.writeLock();

// Read operation (concurrent reads OK)
readLock.lock();
try {
    return data.get(key);
} finally {
    readLock.unlock();
}

// Write operation (exclusive)
writeLock.lock();
try {
    data.put(key, value);
} finally {
    writeLock.unlock();
}
```

---

## 5. StampedLock (Java 8+)

Optimistic reading for even better read performance:

```java
StampedLock lock = new StampedLock();

// Optimistic read (no blocking!)
long stamp = lock.tryOptimisticRead();
int currentX = x;
int currentY = y;
if (!lock.validate(stamp)) {
    // Data might have changed, fall back to read lock
    stamp = lock.readLock();
    try {
        currentX = x;
        currentY = y;
    } finally {
        lock.unlockRead(stamp);
    }
}

// Write
long stamp = lock.writeLock();
try {
    x = newX;
    y = newY;
} finally {
    lock.unlockWrite(stamp);
}
```

---

## 6. Atomic Classes

Lock-free thread-safe operations using hardware CAS:

### Common Atomic Classes

| Class | Description |
|-------|-------------|
| AtomicInteger | Thread-safe int |
| AtomicLong | Thread-safe long |
| AtomicBoolean | Thread-safe boolean |
| AtomicReference\<T\> | Thread-safe reference |
| AtomicIntegerArray | Thread-safe int array |
| AtomicStampedReference | Reference with version stamp |

### AtomicInteger Operations

```java
AtomicInteger counter = new AtomicInteger(0);

counter.get();              // Read
counter.set(10);            // Write
counter.incrementAndGet();  // ++counter (returns new)
counter.getAndIncrement();  // counter++ (returns old)
counter.addAndGet(5);       // counter += 5
counter.compareAndSet(10, 20);  // If 10, set to 20
counter.updateAndGet(x -> x * 2);  // Apply function
```

---

## 7. Compare-And-Swap (CAS)

CAS is the foundation of lock-free algorithms:

```
┌─────────────────────────────────────────────────────────────┐
│                Compare-And-Swap (CAS)                        │
│                                                              │
│   CAS(memory_location, expected_value, new_value)           │
│                                                              │
│   1. Read current value from memory                          │
│   2. If current == expected:                                 │
│      - Write new_value                                       │
│      - Return true                                           │
│   3. Else:                                                   │
│      - Do nothing                                            │
│      - Return false                                          │
│                                                              │
│   ALL THREE STEPS ARE ATOMIC (hardware instruction)         │
└─────────────────────────────────────────────────────────────┘
```

### CAS-Based Increment

```java
// How AtomicInteger.incrementAndGet() works:
public int incrementAndGet() {
    int current;
    int next;
    do {
        current = get();      // Read current value
        next = current + 1;   // Compute new value
    } while (!compareAndSet(current, next));  // Try to update
    return next;
}
```

### Why CAS is Fast

- No locks = no context switches
- No blocking = no thread parking
- Hardware-level atomic operation
- Scales better under contention

---

## 8. The ABA Problem

A subtle issue with CAS:

```
Thread 1:                     Thread 2:
─────────                     ─────────
Read A
                              Read A
                              CAS(A → B) success
                              CAS(B → A) success
CAS(A → C) success!           
But A is not the same A!
```

### Solution: AtomicStampedReference

```java
AtomicStampedReference<String> ref = 
    new AtomicStampedReference<>("A", 0);

int[] stampHolder = new int[1];
String current = ref.get(stampHolder);
int currentStamp = stampHolder[0];

// CAS with stamp check
ref.compareAndSet(current, "B", currentStamp, currentStamp + 1);
```

---

## 9. When to Use What

```
┌─────────────────────────────────────────────────────────────┐
│                   DECISION GUIDE                             │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  Simple mutual exclusion?                                    │
│  └─► synchronized (simplest)                                 │
│                                                              │
│  Need tryLock, timeout, or fairness?                         │
│  └─► ReentrantLock                                           │
│                                                              │
│  Read-heavy with rare writes?                                │
│  └─► ReentrantReadWriteLock                                  │
│                                                              │
│  Extremely read-heavy, writes very rare?                     │
│  └─► StampedLock (optimistic reads)                          │
│                                                              │
│  Single variable updates (counter, flag)?                    │
│  └─► Atomic classes (best performance)                       │
│                                                              │
│  Complex lock-free data structure?                           │
│  └─► CAS + careful design (expert level)                     │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

---

## 10. Interview Key Points

1. **ReentrantLock vs synchronized?**
   - ReentrantLock: tryLock, timeout, fairness, multiple conditions
   - synchronized: simpler syntax, automatic release

2. **What is fairness in locks?**
   - FIFO ordering prevents starvation but reduces throughput

3. **Why are Atomic classes faster than locks?**
   - Use hardware CAS, no context switches or blocking

4. **What is the ABA problem?**
   - CAS may succeed even though value changed and changed back
   - Solution: use stamps/versions

5. **When to use ReadWriteLock?**
   - When reads greatly outnumber writes
   - Multiple readers can proceed concurrently

---

## Examples

1. [Example01_ReentrantLock.java](examples/Example01_ReentrantLock.java) - Basic ReentrantLock usage
2. [Example02_ReadWriteLock.java](examples/Example02_ReadWriteLock.java) - Concurrent cache
3. [Example03_AtomicClasses.java](examples/Example03_AtomicClasses.java) - Lock-free counter
4. [Example04_LockFreeStack.java](examples/Example04_LockFreeStack.java) - CAS-based data structure

---

## Exercises

1. [Exercise01_BankAccountLock.java](exercises/Exercise01_BankAccountLock.java) - Bank with ReentrantLock
2. [Exercise02_ConcurrentCache.java](exercises/Exercise02_ConcurrentCache.java) - Build a thread-safe cache

---

## Quiz

Test yourself: [quiz.md](quiz.md)

---

**Next Module:** [Module 6 - Thread Pools](../06-thread-pools/README.md)
