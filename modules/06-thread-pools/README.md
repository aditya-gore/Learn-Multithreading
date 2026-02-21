# Module 6: Thread Pools and Executors

## Learning Objectives
By the end of this module, you will understand:
- Why thread pools are essential
- The Executor framework architecture
- ThreadPoolExecutor configuration
- Callable, Future, and return values
- Scheduled execution
- Proper shutdown patterns

---

## 1. Why Thread Pools?

Creating threads is expensive:
- Memory allocation for stack
- OS thread creation overhead
- Context switching costs

Thread pools solve this by:
- Reusing existing threads
- Controlling maximum concurrency
- Managing task queuing

```
WITHOUT THREAD POOL              WITH THREAD POOL
──────────────────              ─────────────────
Task 1 → Create Thread          Task 1 ─┐
Task 2 → Create Thread          Task 2 ─┼─→ [Thread Pool] → Execute
Task 3 → Create Thread          Task 3 ─┤   (reuse threads)
    ⋮                           Task 4 ─┘
Task N → Create Thread          
                                
Cost: N thread creations        Cost: Pool size thread creations
Memory: N × stack size          Memory: Pool size × stack size
```

---

## 2. Executor Framework Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    EXECUTOR FRAMEWORK                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ┌──────────────┐                                              │
│   │   Executor   │  execute(Runnable)                           │
│   └──────┬───────┘                                              │
│          │                                                       │
│          ▼                                                       │
│   ┌──────────────────┐                                          │
│   │ ExecutorService  │  submit(), shutdown(), invokeAll()       │
│   └──────┬───────────┘                                          │
│          │                                                       │
│    ┌─────┴─────┐                                                │
│    ▼           ▼                                                │
│ ┌──────────────────────┐   ┌─────────────────────────┐         │
│ │ ThreadPoolExecutor   │   │ScheduledExecutorService│         │
│ │ (configurable pool)  │   │  (delayed/periodic)     │         │
│ └──────────────────────┘   └─────────────────────────┘         │
│                                                                  │
│   Executors Factory:                                            │
│   - newFixedThreadPool(n)                                       │
│   - newCachedThreadPool()                                       │
│   - newSingleThreadExecutor()                                   │
│   - newScheduledThreadPool(n)                                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. Executors Factory Methods

### newFixedThreadPool(n)
Fixed number of threads, unbounded queue.

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
// Always 4 threads, tasks queue if all busy
```

**Use when:** Known number of concurrent tasks needed.

### newCachedThreadPool()
Unlimited threads, creates on demand, removes idle threads.

```java
ExecutorService executor = Executors.newCachedThreadPool();
// Threads created as needed, cached for 60 seconds
```

**Use when:** Many short-lived tasks.
**Warning:** Can create too many threads under load!

### newSingleThreadExecutor()
Single thread, tasks execute sequentially.

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
// Guarantees sequential execution
```

**Use when:** Tasks must run sequentially.

### newScheduledThreadPool(n)
For delayed and periodic execution.

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
scheduler.schedule(task, 5, TimeUnit.SECONDS);        // Run after 5s
scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);  // Every 1s
```

---

## 4. ThreadPoolExecutor Deep Dive

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    corePoolSize,      // Minimum threads to keep alive
    maximumPoolSize,   // Maximum threads allowed
    keepAliveTime,     // Idle time before thread removed
    TimeUnit.SECONDS,
    workQueue,         // Queue for waiting tasks
    threadFactory,     // Creates new threads
    rejectionHandler   // What to do when queue is full
);
```

### Parameters Explained

```
┌─────────────────────────────────────────────────────────────────┐
│              ThreadPoolExecutor Behavior                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Task arrives:                                                  │
│                                                                  │
│   1. threads < corePoolSize?                                     │
│      YES → Create new thread                                     │
│                                                                  │
│   2. Queue has space?                                            │
│      YES → Add to queue                                          │
│                                                                  │
│   3. threads < maximumPoolSize?                                  │
│      YES → Create new thread                                     │
│                                                                  │
│   4. None of above?                                              │
│      → Execute rejection policy                                  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Work Queue Types

| Queue | Behavior |
|-------|----------|
| `LinkedBlockingQueue` | Unbounded (fixed pool default) |
| `ArrayBlockingQueue(n)` | Bounded, blocks when full |
| `SynchronousQueue` | No capacity, direct handoff |

### Rejection Policies

| Policy | Behavior |
|--------|----------|
| `AbortPolicy` | Throws RejectedExecutionException (default) |
| `CallerRunsPolicy` | Caller's thread runs the task |
| `DiscardPolicy` | Silently discards the task |
| `DiscardOldestPolicy` | Discards oldest queued task |

---

## 5. Callable and Future

### Runnable vs Callable

```java
// Runnable: no return value, no checked exceptions
Runnable runnable = () -> {
    System.out.println("Running");
};

// Callable: returns value, can throw exceptions
Callable<Integer> callable = () -> {
    return 42;
};
```

### Using Future

```java
ExecutorService executor = Executors.newFixedThreadPool(2);

Future<Integer> future = executor.submit(() -> {
    Thread.sleep(1000);
    return 42;
});

// Do other work...

// Get result (blocks if not ready)
Integer result = future.get();

// Get with timeout
Integer result = future.get(5, TimeUnit.SECONDS);

// Check status
future.isDone();      // Completed?
future.isCancelled(); // Cancelled?
future.cancel(true);  // Cancel (may interrupt)
```

---

## 6. Invoking Multiple Tasks

### invokeAll - Wait for All

```java
List<Callable<Integer>> tasks = Arrays.asList(
    () -> { return 1; },
    () -> { return 2; },
    () -> { return 3; }
);

List<Future<Integer>> futures = executor.invokeAll(tasks);
// Returns when ALL tasks complete

for (Future<Integer> f : futures) {
    System.out.println(f.get());  // 1, 2, 3
}
```

### invokeAny - First Result

```java
Integer result = executor.invokeAny(tasks);
// Returns when FIRST task completes, cancels others
```

---

## 7. Proper Shutdown

```java
ExecutorService executor = Executors.newFixedThreadPool(4);

// Submit tasks...

// Graceful shutdown
executor.shutdown();  // No new tasks, finish existing

// Wait for completion
if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
    // Force shutdown if not finished
    executor.shutdownNow();  // Interrupt running tasks
}
```

```
┌─────────────────────────────────────────────────────────────────┐
│                    SHUTDOWN STATES                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   RUNNING ──► shutdown() ──► SHUTDOWN ──► TERMINATED            │
│      │                          │                                │
│      │                          │ (queue empty, all tasks done)  │
│      │                          │                                │
│      └─────► shutdownNow() ──►──┴──► STOP ──► TERMINATED        │
│                                                                  │
│   shutdown():     Gentle - finish queued tasks                   │
│   shutdownNow():  Forceful - interrupt running, return queued   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 8. Pitfalls to Avoid

### Pitfall 1: Unbounded Queue with CachedThreadPool
```java
// DANGER! Creates unlimited threads
Executors.newCachedThreadPool();

// SAFER: Use bounded queue
new ThreadPoolExecutor(10, 100, 60L, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(1000));
```

### Pitfall 2: Forgetting to Shutdown
```java
// WRONG: Executor never shuts down, app won't exit
ExecutorService executor = Executors.newFixedThreadPool(4);
executor.submit(task);
// Missing: executor.shutdown()
```

### Pitfall 3: Ignoring Exceptions in Tasks
```java
// WRONG: Exception silently swallowed
executor.execute(() -> {
    throw new RuntimeException("Oops!");
});

// BETTER: Use submit() and check future
Future<?> future = executor.submit(() -> {
    throw new RuntimeException("Oops!");
});
future.get();  // Throws ExecutionException
```

---

## 9. Interview Key Points

1. **Why use thread pools?**
   - Reuse threads (avoid creation overhead)
   - Control concurrency level
   - Manage task queuing

2. **Fixed vs Cached pool?**
   - Fixed: Known load, controlled threads
   - Cached: Variable short-lived tasks, can grow unbounded

3. **What is Future?**
   - Handle to async result
   - Can check status, cancel, get result

4. **shutdown() vs shutdownNow()?**
   - shutdown(): Graceful, finish queued tasks
   - shutdownNow(): Aggressive, interrupt running tasks

5. **How to handle task exceptions?**
   - Use submit() and call get() on Future
   - Or provide custom ThreadFactory with uncaughtExceptionHandler

---

## Examples

1. [Example01_ExecutorBasics.java](examples/Example01_ExecutorBasics.java) - Basic executor usage
2. [Example02_ThreadPoolExecutor.java](examples/Example02_ThreadPoolExecutor.java) - Custom pool configuration
3. [Example03_CallableAndFuture.java](examples/Example03_CallableAndFuture.java) - Return values and futures
4. [Example04_ScheduledExecutor.java](examples/Example04_ScheduledExecutor.java) - Scheduled tasks

---

## Exercises

1. [Exercise01_WebCrawler.java](exercises/Exercise01_WebCrawler.java) - Simple concurrent web crawler
2. [Exercise02_CustomThreadPool.java](exercises/Exercise02_CustomThreadPool.java) - Build a mini thread pool

---

## Quiz

Test yourself: [quiz.md](quiz.md)

---

**Next Module:** [Module 7 - CompletableFuture](../07-completable-future/README.md)
