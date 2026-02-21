# Module 6 Quiz: Thread Pools and Executors

Test your understanding of the Executor framework.

---

## Multiple Choice Questions

### Q1: Thread Pool Benefit
What is the main benefit of using a thread pool?

A) Threads run faster  
B) Avoids overhead of creating new threads for each task  
C) Guarantees task order  
D) Eliminates synchronization needs  

<details>
<summary>Answer</summary>

**B) Avoids overhead of creating new threads for each task**

Thread creation is expensive. Thread pools reuse threads, dramatically reducing overhead for many short tasks.
</details>

---

### Q2: Fixed vs Cached Pool
When would you prefer newCachedThreadPool() over newFixedThreadPool()?

A) When you need guaranteed thread count  
B) For many short-lived asynchronous tasks  
C) For CPU-intensive computations  
D) When memory is limited  

<details>
<summary>Answer</summary>

**B) For many short-lived asynchronous tasks**

CachedThreadPool creates threads on demand and removes idle ones. Good for bursts of short tasks. Fixed pool is better when you want controlled concurrency.
</details>

---

### Q3: Callable vs Runnable
What can Callable do that Runnable cannot?

A) Run in a thread pool  
B) Return a value and throw checked exceptions  
C) Be cancelled  
D) Run concurrently  

<details>
<summary>Answer</summary>

**B) Return a value and throw checked exceptions**

Callable's call() returns a value and can throw checked exceptions. Runnable's run() returns void and cannot throw checked exceptions.
</details>

---

### Q4: shutdown() Behavior
What does ExecutorService.shutdown() do?

A) Immediately stops all running tasks  
B) Prevents new tasks and waits for running tasks to complete  
C) Prevents new tasks but doesn't wait  
D) Kills the JVM  

<details>
<summary>Answer</summary>

**C) Prevents new tasks but doesn't wait**

shutdown() initiates orderly shutdown - no new tasks accepted, but existing tasks continue. Use awaitTermination() to wait, or shutdownNow() for immediate stop.
</details>

---

### Q5: Future.get()
What happens when you call get() on a Future before the task completes?

A) Returns null  
B) Throws exception  
C) Blocks until the result is available  
D) Returns a partial result  

<details>
<summary>Answer</summary>

**C) Blocks until the result is available**

get() is a blocking call that waits for the task to complete. Use get(timeout, unit) to avoid indefinite blocking.
</details>

---

### Q6: invokeAny vs invokeAll
What does invokeAny() return?

A) All results as a List  
B) The first completed result  
C) The fastest result only if all complete  
D) void  

<details>
<summary>Answer</summary>

**B) The first completed result**

invokeAny() returns as soon as ONE task completes successfully, and cancels remaining tasks. invokeAll() waits for ALL tasks.
</details>

---

### Q7: ThreadPoolExecutor Queue Full
What happens by default when ThreadPoolExecutor's queue is full and max threads are busy?

A) Task is discarded silently  
B) RejectedExecutionException is thrown  
C) Caller's thread runs the task  
D) Task waits indefinitely  

<details>
<summary>Answer</summary>

**B) RejectedExecutionException is thrown**

The default AbortPolicy throws RejectedExecutionException. Other policies: CallerRunsPolicy, DiscardPolicy, DiscardOldestPolicy.
</details>

---

### Q8: scheduleAtFixedRate
If a task scheduled at fixed rate takes longer than the period, what happens?

A) Next execution is skipped  
B) Task is cancelled  
C) Next execution starts immediately after current finishes  
D) Exception is thrown  

<details>
<summary>Answer</summary>

**C) Next execution starts immediately after current finishes**

If execution time exceeds the period, the next execution starts immediately (no overlap, but no delay either). Tasks may "bunch up".
</details>

---

## True or False

### Q9
**True or False:** Executors.newCachedThreadPool() can create an unlimited number of threads.

<details>
<summary>Answer</summary>

**True**

CachedThreadPool has maximumPoolSize of Integer.MAX_VALUE. Under heavy load, it can create too many threads and exhaust system resources.
</details>

---

### Q10
**True or False:** After calling shutdown(), already-submitted tasks are discarded.

<details>
<summary>Answer</summary>

**False**

shutdown() is graceful - existing tasks in the queue continue to execute. Only NEW submissions are rejected. Use shutdownNow() to attempt cancellation of running tasks.
</details>

---

## Short Answer

### Q11
Why might you choose CallerRunsPolicy as a rejection handler?

<details>
<summary>Answer</summary>

**CallerRunsPolicy Benefits:**

1. **Back pressure:** When the pool is overwhelmed, the submitting thread slows down because it runs the task itself.

2. **No task loss:** Unlike DiscardPolicy, no tasks are silently dropped.

3. **No exceptions:** Unlike AbortPolicy, doesn't throw exceptions.

4. **Self-regulating:** The system naturally throttles when overloaded.

**Use case:** When it's acceptable for the submitting thread to be blocked temporarily, and you don't want to lose tasks.
</details>

---

### Q12
What's wrong with this shutdown code?

```java
executor.shutdown();
executor.shutdownNow();
```

<details>
<summary>Answer</summary>

**Problem:** Calling shutdownNow() immediately after shutdown() defeats the purpose of graceful shutdown.

**Correct pattern:**
```java
executor.shutdown();  // Stop accepting new tasks
try {
    // Wait for existing tasks to complete
    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        executor.shutdownNow();  // Force shutdown if timeout
    }
} catch (InterruptedException e) {
    executor.shutdownNow();
    Thread.currentThread().interrupt();
}
```

Give tasks time to complete before forcing shutdown.
</details>

---

## Score Yourself

- **10-12 correct:** Excellent! Ready for Module 7
- **7-9 correct:** Good, review shutdown and rejection policies
- **Below 7:** Re-study the executor framework

---

**Next:** [Module 7 - CompletableFuture](../07-completable-future/README.md)
