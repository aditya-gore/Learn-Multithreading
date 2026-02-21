# Module 4: Concurrent Utilities (java.util.concurrent)

## Learning Objectives
By the end of this module, you will understand:
- CountDownLatch for waiting on multiple events
- CyclicBarrier for synchronizing thread groups
- Semaphore for controlling resource access
- Phaser for flexible phased coordination
- When to use each synchronizer

---

## 1. Overview of Synchronizers

Java provides high-level synchronization utilities that are easier and safer than raw wait/notify:

```
┌─────────────────────────────────────────────────────────────────┐
│                    SYNCHRONIZERS OVERVIEW                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  CountDownLatch     CyclicBarrier       Semaphore               │
│  ───────────────    ───────────────     ───────────             │
│  Wait for N         All wait for        Control access          │
│  events/threads     each other          to N resources          │
│                                                                  │
│       │                  │                    │                  │
│       ▼                  ▼                    ▼                  │
│  ┌─────────┐        ┌─────────┐         ┌─────────┐            │
│  │  One    │        │Threads  │         │ Permits │            │
│  │  time   │        │  sync   │         │  pool   │            │
│  │  use    │        │  at     │         │ (N max) │            │
│  └─────────┘        │ barrier │         └─────────┘            │
│                     │(reusable)│                                │
│                     └─────────┘                                 │
│                                                                  │
│  Phaser            Exchanger                                     │
│  ─────────         ──────────                                    │
│  Flexible          Two threads                                   │
│  phases            swap data                                     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. CountDownLatch

**Purpose:** Wait for N events before proceeding.

**Use Cases:**
- Wait for N services to start
- Main thread waits for worker threads
- One-time gate (cannot be reset)

```
┌───────────────────────────────────────────────────────────────┐
│                    CountDownLatch(3)                           │
│                                                                │
│   Initial:  [ 3 ]  ──► countDown() ──► [ 2 ]                  │
│             count                       count                  │
│                       ──► countDown() ──► [ 1 ]               │
│                                                                │
│                       ──► countDown() ──► [ 0 ]               │
│                                           │                    │
│                                           ▼                    │
│   Waiting thread: ════════════════════► RELEASED!             │
│   (blocked on await())                                         │
└───────────────────────────────────────────────────────────────┘
```

### Example

```java
CountDownLatch latch = new CountDownLatch(3);

// Worker threads
for (int i = 0; i < 3; i++) {
    new Thread(() -> {
        doWork();
        latch.countDown();  // Signal completion
    }).start();
}

// Main thread waits
latch.await();  // Blocks until count reaches 0
System.out.println("All workers done!");
```

### Key Points

- Count can only go down (not reset)
- `await()` blocks until count reaches 0
- `await(timeout, unit)` returns false on timeout
- Thread-safe, any thread can call `countDown()`

---

## 3. CyclicBarrier

**Purpose:** N threads wait for each other at a barrier point.

**Use Cases:**
- Parallel algorithms with phases
- Multi-player game sync
- Reusable (unlike CountDownLatch)

```
┌───────────────────────────────────────────────────────────────┐
│                    CyclicBarrier(3)                            │
│                                                                │
│   Thread A ─────────────┐                                      │
│                         │                                      │
│   Thread B ─────────────┼────► BARRIER ────► All proceed       │
│                         │      (wait)                          │
│   Thread C ─────────────┘                                      │
│                                                                │
│   When 3rd thread arrives, barrier "breaks" and all continue  │
│   Barrier resets automatically for next round                  │
└───────────────────────────────────────────────────────────────┘
```

### Example

```java
CyclicBarrier barrier = new CyclicBarrier(3, () -> {
    System.out.println("All threads reached barrier!");
});

for (int i = 0; i < 3; i++) {
    new Thread(() -> {
        while (running) {
            doPhase1();
            barrier.await();  // Wait for all threads
            doPhase2();
            barrier.await();  // Barrier resets and reused
        }
    }).start();
}
```

### Key Points

- Reusable after all threads pass
- Optional barrier action runs when last thread arrives
- `BrokenBarrierException` if any thread is interrupted while waiting

---

## 4. CountDownLatch vs CyclicBarrier

| Feature | CountDownLatch | CyclicBarrier |
|---------|---------------|---------------|
| Reusable | No (one-time) | Yes (automatic reset) |
| Who waits | One or more threads wait | All participating threads wait |
| Who signals | Any thread can countDown() | All threads must arrive |
| Use case | Wait for events | Synchronize thread groups |
| Reset | Cannot reset | Auto-resets after release |

---

## 5. Semaphore

**Purpose:** Control access to a limited number of resources.

**Use Cases:**
- Connection pool limits
- Rate limiting
- Resource pooling

```
┌───────────────────────────────────────────────────────────────┐
│                    Semaphore(3)                                │
│                                                                │
│   Permits: [■][■][■]  (3 available)                           │
│                                                                │
│   Thread A: acquire() → [■][■][ ]  (A got permit)             │
│   Thread B: acquire() → [■][ ][ ]  (B got permit)             │
│   Thread C: acquire() → [ ][ ][ ]  (C got permit)             │
│   Thread D: acquire() → BLOCKED    (no permits)               │
│                                                                │
│   Thread A: release() → [■][ ][ ]  (D can now proceed)        │
│   Thread D: ─────────► [■][ ][ ]  (D got permit)              │
└───────────────────────────────────────────────────────────────┘
```

### Example

```java
Semaphore semaphore = new Semaphore(3);  // Max 3 concurrent

void accessResource() {
    semaphore.acquire();  // Block if no permits
    try {
        useResource();
    } finally {
        semaphore.release();  // Always release!
    }
}
```

### Fairness

```java
// Non-fair (default): Threads may be granted permits out of order
Semaphore unfair = new Semaphore(3);

// Fair: FIFO ordering, threads acquire in request order
Semaphore fair = new Semaphore(3, true);
```

### Binary Semaphore (Mutex)

```java
Semaphore mutex = new Semaphore(1);  // Only 1 permit
mutex.acquire();  // Like synchronized
try {
    criticalSection();
} finally {
    mutex.release();
}
```

---

## 6. Phaser

**Purpose:** Flexible barrier with dynamic party registration.

**Advantages over CyclicBarrier:**
- Parties can register/deregister dynamically
- Supports multiple phases with different party counts
- Can be terminated

```java
Phaser phaser = new Phaser(1);  // Register self

for (int i = 0; i < 3; i++) {
    phaser.register();  // Add party
    new Thread(() -> {
        phaser.arriveAndAwaitAdvance();  // Phase 0
        doWork();
        phaser.arriveAndDeregister();    // Leave
    }).start();
}

phaser.arriveAndDeregister();  // Main deregisters
```

---

## 7. Exchanger

**Purpose:** Two threads exchange data at a synchronization point.

```java
Exchanger<String> exchanger = new Exchanger<>();

// Thread 1
String data1 = "Hello from T1";
String received1 = exchanger.exchange(data1);
// received1 = "Hello from T2"

// Thread 2
String data2 = "Hello from T2";
String received2 = exchanger.exchange(data2);
// received2 = "Hello from T1"
```

---

## 8. Comparison Chart

```
┌─────────────────┬──────────────┬───────────────┬─────────────┐
│  Synchronizer   │  Threads     │   Reusable    │  Use When   │
├─────────────────┼──────────────┼───────────────┼─────────────┤
│ CountDownLatch  │ Many → One   │     No        │ Wait for    │
│                 │              │               │ N events    │
├─────────────────┼──────────────┼───────────────┼─────────────┤
│ CyclicBarrier   │ N ←→ N       │     Yes       │ All sync    │
│                 │              │               │ at point    │
├─────────────────┼──────────────┼───────────────┼─────────────┤
│ Semaphore       │ N compete    │     Yes       │ Limit       │
│                 │ for M        │               │ concurrency │
├─────────────────┼──────────────┼───────────────┼─────────────┤
│ Phaser          │ N (dynamic)  │     Yes       │ Flexible    │
│                 │              │               │ phases      │
├─────────────────┼──────────────┼───────────────┼─────────────┤
│ Exchanger       │ 2            │     Yes       │ Swap data   │
│                 │              │               │ between 2   │
└─────────────────┴──────────────┴───────────────┴─────────────┘
```

---

## 9. Interview Key Points

1. **CountDownLatch vs CyclicBarrier?**
   - Latch: one-time, any thread can count down
   - Barrier: reusable, all threads must arrive

2. **When to use Semaphore?**
   - Limiting concurrent access to a resource
   - Connection pools, rate limiters

3. **Can CountDownLatch be reset?**
   - No. Use CyclicBarrier or Phaser if you need reset.

4. **What's a fair semaphore?**
   - Guarantees FIFO ordering of permit acquisition.
   - Prevents starvation but has overhead.

5. **Semaphore vs synchronized?**
   - Semaphore allows N concurrent threads
   - synchronized allows only 1

---

## Examples

1. [Example01_CountDownLatch.java](examples/Example01_CountDownLatch.java) - Service startup coordination
2. [Example02_CyclicBarrier.java](examples/Example02_CyclicBarrier.java) - Parallel matrix computation
3. [Example03_Semaphore.java](examples/Example03_Semaphore.java) - Connection pool limiter
4. [Example04_Phaser.java](examples/Example04_Phaser.java) - Dynamic phased execution

---

## Exercises

1. [Exercise01_RateLimiter.java](exercises/Exercise01_RateLimiter.java) - Build a rate limiter with Semaphore
2. [Exercise02_ParallelMergeSort.java](exercises/Exercise02_ParallelMergeSort.java) - Use CyclicBarrier for parallel sorting

---

## Quiz

Test yourself: [quiz.md](quiz.md)

---

**Next Module:** [Module 5 - Locks and Atomics](../05-locks-and-atomics/README.md)
