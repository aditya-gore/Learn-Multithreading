# Theory Interview Questions

## Basic Level

### 1. What is the difference between a process and a thread?
**Answer:** A process is an independent program with its own memory space. A thread is a lightweight unit within a process that shares memory with other threads. Threads are cheaper to create and switch between.

### 2. How do you create a thread in Java?
**Answer:** Three ways:
1. Extend Thread class and override run()
2. Implement Runnable interface
3. Use lambda expression (Java 8+)

Runnable is preferred as it allows extending other classes.

### 3. What's the difference between start() and run()?
**Answer:** `start()` creates a new thread and calls run() in that thread. `run()` directly executes in the calling thread without creating a new thread.

### 4. Explain thread states in Java.
**Answer:** NEW (created), RUNNABLE (ready/running), BLOCKED (waiting for lock), WAITING (indefinite wait), TIMED_WAITING (bounded wait), TERMINATED (finished).

### 5. What is a daemon thread?
**Answer:** A background thread that doesn't prevent JVM from exiting. When only daemon threads remain, JVM exits. Set before start() with `setDaemon(true)`.

---

## Intermediate Level

### 6. What is a race condition?
**Answer:** When multiple threads access shared data concurrently and at least one modifies it, leading to unpredictable results based on timing.

### 7. Difference between synchronized and volatile?
**Answer:** 
- `synchronized`: Mutual exclusion + visibility, blocks other threads
- `volatile`: Only visibility, no blocking, no atomicity for compound ops

### 8. Why is double-checked locking broken without volatile?
**Answer:** Without volatile, instruction reordering can expose a partially constructed object. Thread B might see a non-null reference before constructor completes.

### 9. Explain wait(), notify(), notifyAll().
**Answer:** Methods on Object for thread coordination. `wait()` releases lock and waits. `notify()` wakes one waiting thread. `notifyAll()` wakes all. Must be called inside synchronized block.

### 10. Why use while loop with wait()?
**Answer:** To handle spurious wakeups and check if condition is actually true after wakeup. Multiple waiters might wake but only one can proceed.

### 11. CountDownLatch vs CyclicBarrier?
**Answer:**
- CountDownLatch: One-time, any thread can countDown, waiters don't signal
- CyclicBarrier: Reusable, all threads must arrive, auto-resets

### 12. What is a Semaphore?
**Answer:** Controls access to N resources using permits. `acquire()` gets permit (blocks if none), `release()` returns permit. Can be fair (FIFO).

### 13. ReentrantLock vs synchronized?
**Answer:** ReentrantLock offers: tryLock with timeout, fairness option, multiple conditions, interruptible waiting. synchronized is simpler with automatic release.

### 14. What are atomic classes?
**Answer:** Thread-safe classes using CAS (e.g., AtomicInteger). Lock-free, faster than synchronized for simple operations. Provide atomicity for compound operations.

### 15. What is the ABA problem?
**Answer:** CAS succeeds because value is A, but value changed A→B→A in between. Solved with AtomicStampedReference that includes version stamp.

---

## Advanced Level

### 16. Explain happens-before relationship.
**Answer:** Memory visibility guarantee. If action A happens-before B, then A's effects are visible to B. Established by: synchronized, volatile, thread start/join, etc.

### 17. What is lock striping in ConcurrentHashMap?
**Answer:** Instead of one global lock, CHM uses multiple segment locks. Different threads accessing different segments don't block each other, improving concurrency.

### 18. ThreadPoolExecutor parameters?
**Answer:** corePoolSize (min threads), maxPoolSize (max threads), keepAliveTime (idle thread timeout), workQueue (task queue), threadFactory, rejectionHandler.

### 19. Explain Fork/Join framework.
**Answer:** Divide-and-conquer parallelism with work stealing. Tasks recursively split until small enough, then results combine. ForkJoinPool manages worker threads.

### 20. How to prevent deadlock?
**Answer:** 
1. Lock ordering (always acquire in same order)
2. Lock timeout (tryLock with timeout)
3. Single lock (less concurrency)
4. Lock-free algorithms

### 21. What is ThreadLocal memory leak?
**Answer:** Thread pools reuse threads. If ThreadLocal values aren't removed, they persist across tasks, consuming memory. Always call remove() in finally block.

### 22. CompletableFuture thenApply vs thenCompose?
**Answer:** thenApply transforms result (map). thenCompose flattens nested futures (flatMap) - use when transformation returns CompletableFuture.

### 23. When to use CopyOnWriteArrayList?
**Answer:** Read-heavy, write-rare scenarios. Iteration never throws ConcurrentModificationException. Each write copies entire array - expensive for frequent writes.

### 24. Explain optimistic vs pessimistic locking.
**Answer:**
- Pessimistic: Lock before operation, assume conflict (synchronized, Lock)
- Optimistic: Proceed without lock, verify at end (CAS, StampedLock optimistic read)

### 25. How does ReadWriteLock improve concurrency?
**Answer:** Multiple readers can proceed simultaneously since reads don't conflict. Only writers need exclusive access. Better than exclusive lock for read-heavy workloads.
