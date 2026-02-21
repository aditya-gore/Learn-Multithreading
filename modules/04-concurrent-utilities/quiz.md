# Module 4 Quiz: Concurrent Utilities

Test your understanding of CountDownLatch, CyclicBarrier, Semaphore, and Phaser.

---

## Multiple Choice Questions

### Q1: CountDownLatch
What happens when countDown() is called on a CountDownLatch with count already at 0?

A) IllegalStateException is thrown  
B) The count goes negative  
C) Nothing happens, count stays at 0  
D) The latch resets to its initial value  

<details>
<summary>Answer</summary>

**C) Nothing happens, count stays at 0**

Once a CountDownLatch reaches 0, additional countDown() calls have no effect. The count cannot go negative or be reset.
</details>

---

### Q2: CyclicBarrier vs CountDownLatch
Which is a key difference between CyclicBarrier and CountDownLatch?

A) CountDownLatch can be reused, CyclicBarrier cannot  
B) CyclicBarrier can be reused, CountDownLatch cannot  
C) CountDownLatch requires all threads to wait, CyclicBarrier doesn't  
D) CyclicBarrier is not thread-safe  

<details>
<summary>Answer</summary>

**B) CyclicBarrier can be reused, CountDownLatch cannot**

CyclicBarrier automatically resets after all parties arrive. CountDownLatch is one-time use and cannot be reset.
</details>

---

### Q3: Semaphore Fairness
What does a "fair" semaphore guarantee?

A) Equal CPU time for all threads  
B) Permits are granted in FIFO order  
C) No thread will ever be denied a permit  
D) Faster performance than non-fair  

<details>
<summary>Answer</summary>

**B) Permits are granted in FIFO order**

A fair semaphore ensures threads acquire permits in the order they requested them, preventing starvation. Non-fair is faster but may starve some threads.
</details>

---

### Q4: Semaphore Permits
What happens if release() is called more times than acquire()?

A) IllegalStateException is thrown  
B) The extra permits are discarded  
C) The number of permits increases beyond the initial count  
D) The semaphore becomes invalid  

<details>
<summary>Answer</summary>

**C) The number of permits increases beyond the initial count**

Semaphores don't track ownership. You can release permits you didn't acquire, increasing the total. This is a common bug!
</details>

---

### Q5: CyclicBarrier Broken
What causes a CyclicBarrier to become "broken"?

A) Calling await() without all parties present  
B) A thread is interrupted while waiting at the barrier  
C) The barrier action throws an exception  
D) Both B and C  

<details>
<summary>Answer</summary>

**D) Both B and C**

If any thread is interrupted while waiting, or if the barrier action throws an exception, the barrier breaks and all waiting threads get BrokenBarrierException.
</details>

---

### Q6: Phaser vs CyclicBarrier
What can Phaser do that CyclicBarrier cannot?

A) Execute a barrier action  
B) Handle dynamic party registration/deregistration  
C) Wait for threads  
D) Be reused for multiple phases  

<details>
<summary>Answer</summary>

**B) Handle dynamic party registration/deregistration**

Phaser allows parties to join (register) and leave (deregister) at any time. CyclicBarrier has a fixed party count set at construction.
</details>

---

### Q7: CountDownLatch await()
What happens when await() is called after the count is already 0?

A) The thread blocks forever  
B) await() returns immediately  
C) IllegalStateException is thrown  
D) The count becomes negative  

<details>
<summary>Answer</summary>

**B) await() returns immediately**

Once the count reaches 0, await() returns immediately. It doesn't block because the condition (count == 0) is already satisfied.
</details>

---

### Q8: Binary Semaphore
What distinguishes a binary semaphore from a mutex (synchronized)?

A) Binary semaphore is faster  
B) Mutex can be released by any thread, semaphore cannot  
C) Semaphore can be released by any thread, mutex only by owner  
D) There is no difference  

<details>
<summary>Answer</summary>

**C) Semaphore can be released by any thread, mutex only by owner**

A synchronized block can only be exited by the thread that entered it. Semaphore permits can be released by any thread, which is both powerful and dangerous.
</details>

---

## Code Output Questions

### Q9: What does this print?

```java
CountDownLatch latch = new CountDownLatch(3);
latch.countDown();
latch.countDown();
System.out.println(latch.getCount());
latch.await(100, TimeUnit.MILLISECONDS);
System.out.println("Done");
```

A) 1, Done  
B) 1, (hangs forever)  
C) 0, Done  
D) Throws exception  

<details>
<summary>Answer</summary>

**A) 1, Done**

After two countDown() calls, count is 1. The await() with timeout returns after 100ms (returns false because count != 0), then "Done" prints.
</details>

---

### Q10: What's the issue?

```java
Semaphore sem = new Semaphore(3);
try {
    sem.acquire();
    doWork();
} catch (Exception e) {
    // handle error
} finally {
    sem.release();
}
```

A) Nothing wrong  
B) release() might be called even if acquire() failed  
C) Should use tryAcquire() instead  
D) Semaphore is not thread-safe  

<details>
<summary>Answer</summary>

**B) release() might be called even if acquire() failed**

If acquire() is interrupted, the exception is caught but release() still runs in finally, increasing permits incorrectly. Better pattern:
```java
sem.acquire();
try {
    doWork();
} finally {
    sem.release();
}
```
</details>

---

## True or False

### Q11
**True or False:** CountDownLatch is typically used when one or more threads need to wait for multiple events to occur.

<details>
<summary>Answer</summary>

**True**

CountDownLatch is designed for scenarios where threads wait for N events/actions to complete before proceeding.
</details>

---

### Q12
**True or False:** CyclicBarrier requires all parties to call await() for any of them to proceed.

<details>
<summary>Answer</summary>

**True**

The barrier only releases when ALL registered parties have called await(). If one party doesn't show up, all others wait indefinitely (unless timeout is used).
</details>

---

### Q13
**True or False:** Semaphore.acquire(n) atomically acquires n permits.

<details>
<summary>Answer</summary>

**True**

acquire(n) is atomic - it blocks until n permits are available and then acquires all n at once.
</details>

---

## Short Answer

### Q14
When would you choose Semaphore over synchronized for mutual exclusion?

<details>
<summary>Answer</summary>

Use Semaphore over synchronized when:

1. **Need timed attempts:** Semaphore has tryAcquire(timeout) for non-blocking or bounded waiting.

2. **Fair access needed:** Semaphore can be made fair (FIFO). synchronized is not fair.

3. **Multiple permits:** Need to allow N concurrent accessors, not just 1.

4. **Interruptible acquisition:** acquire() can be interrupted; synchronized blocks cannot (pre-Lock).

5. **Cross-method release:** Sometimes you need to acquire in one method and release in another. Semaphore allows this (synchronized doesn't).

However, synchronized is simpler for basic single-thread mutual exclusion.
</details>

---

### Q15
Explain a scenario where CountDownLatch is more appropriate than CyclicBarrier.

<details>
<summary>Answer</summary>

**CountDownLatch is better when:**

1. **One-time event:** Waiting for a one-time initialization that won't repeat.

2. **Different waiters and signalers:** Main thread waits while worker threads signal completion. Workers don't need to wait for each other.

3. **Variable number of signals:** Multiple events can call countDown(), not necessarily threads.

**Example: Service startup**
```java
CountDownLatch servicesReady = new CountDownLatch(3);

// Start services (they countDown when ready)
startDatabase(servicesReady);
startCache(servicesReady);
startMessageQueue(servicesReady);

// Main thread waits for ALL services
servicesReady.await();
System.out.println("All services ready!");
```

With CyclicBarrier, all parties would have to wait for each other, but here services don't need to synchronize with each other, only the main thread needs to wait.
</details>

---

## Score Yourself

- **13-15 correct:** Excellent! Ready for Module 5
- **10-12 correct:** Good, review Phaser and edge cases
- **7-9 correct:** Review the synchronizers comparison
- **Below 7:** Re-study the module and examples

---

**Next:** [Module 5 - Locks and Atomics](../05-locks-and-atomics/README.md)
