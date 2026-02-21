# Module 10: Advanced Patterns

## Learning Objectives
By the end of this module, you will understand:
- Fork/Join framework for divide-and-conquer
- ThreadLocal for per-thread data
- Deadlock detection and prevention
- Thread-safe singleton patterns
- Immutability as a concurrency strategy

---

## 1. Fork/Join Framework

Designed for divide-and-conquer parallelism:

```
┌─────────────────────────────────────────────────────────────────┐
│                    FORK/JOIN PATTERN                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│                    ┌─────────────┐                              │
│                    │  Big Task   │                              │
│                    └──────┬──────┘                              │
│                           │ FORK                                 │
│               ┌───────────┴───────────┐                         │
│               │                       │                         │
│         ┌─────┴─────┐           ┌─────┴─────┐                   │
│         │ Subtask 1 │           │ Subtask 2 │                   │
│         └─────┬─────┘           └─────┬─────┘                   │
│               │ FORK                  │ FORK                    │
│         ┌─────┴─────┐           ┌─────┴─────┐                   │
│         │ ┌───┐┌───┐│           │ ┌───┐┌───┐│                   │
│         │ │1.1││1.2││           │ │2.1││2.2││                   │
│         │ └─┬─┘└─┬─┘│           │ └─┬─┘└─┬─┘│                   │
│         │   │    │  │           │   │    │  │                   │
│         │   └──┬─┘  │           │   └──┬─┘  │                   │
│         │      │    │           │      │    │                   │
│         └──────┼────┘           └──────┼────┘                   │
│                │ JOIN                  │ JOIN                   │
│                └───────────┬───────────┘                        │
│                            │ JOIN                               │
│                     ┌──────┴──────┐                             │
│                     │   Result    │                             │
│                     └─────────────┘                             │
└─────────────────────────────────────────────────────────────────┘
```

### Example: Parallel Sum
```java
class SumTask extends RecursiveTask<Long> {
    private final int[] arr;
    private final int start, end;
    private static final int THRESHOLD = 1000;

    public SumTask(int[] arr, int start, int end) {
        this.arr = arr; this.start = start; this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            // Small enough - compute directly
            long sum = 0;
            for (int i = start; i < end; i++) sum += arr[i];
            return sum;
        }
        // Fork into subtasks
        int mid = (start + end) / 2;
        SumTask left = new SumTask(arr, start, mid);
        SumTask right = new SumTask(arr, mid, end);
        left.fork();  // Async
        long rightResult = right.compute();  // Compute in current thread
        long leftResult = left.join();  // Wait for forked task
        return leftResult + rightResult;
    }
}

// Usage
ForkJoinPool pool = ForkJoinPool.commonPool();
long sum = pool.invoke(new SumTask(array, 0, array.length));
```

---

## 2. ThreadLocal

Per-thread storage - each thread has its own copy:

```java
ThreadLocal<SimpleDateFormat> dateFormat = ThreadLocal.withInitial(
    () -> new SimpleDateFormat("yyyy-MM-dd")
);

// Each thread gets its own SimpleDateFormat instance
String formatted = dateFormat.get().format(new Date());

// Clean up when done (important for thread pools!)
dateFormat.remove();
```

### Use Cases
- Non-thread-safe objects (SimpleDateFormat, Random)
- Per-request context (user ID, transaction ID)
- Avoiding parameter passing through call stack

### InheritableThreadLocal
Child threads inherit parent's values:
```java
InheritableThreadLocal<String> context = new InheritableThreadLocal<>();
context.set("parent-value");

new Thread(() -> {
    System.out.println(context.get());  // "parent-value"
}).start();
```

---

## 3. Deadlock

Four conditions (all must hold):
1. **Mutual Exclusion** - Resources can't be shared
2. **Hold and Wait** - Holding one, waiting for another
3. **No Preemption** - Resources can't be forcibly taken
4. **Circular Wait** - A→B→C→A

### Prevention Strategies

```
┌─────────────────────────────────────────────────────────────────┐
│              DEADLOCK PREVENTION                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   1. Lock Ordering                                               │
│      Always acquire locks in same global order                   │
│      If lock A < lock B, always acquire A before B               │
│                                                                  │
│   2. Lock Timeout (tryLock)                                      │
│      if (!lock.tryLock(1, SECONDS)) {                           │
│          // Back off and retry                                   │
│      }                                                           │
│                                                                  │
│   3. Single Lock                                                 │
│      Use one lock for all resources (less concurrency)          │
│                                                                  │
│   4. Lock-Free Algorithms                                        │
│      Use atomic operations instead of locks                      │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 4. Thread-Safe Singleton Patterns

### 1. Eager Initialization
```java
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    public static Singleton getInstance() { return INSTANCE; }
}
```

### 2. Initialization-on-Demand Holder (Best)
```java
public class Singleton {
    private static class Holder {
        static final Singleton INSTANCE = new Singleton();
    }
    public static Singleton getInstance() {
        return Holder.INSTANCE;
    }
}
```

### 3. Enum Singleton (Simplest)
```java
public enum Singleton {
    INSTANCE;
    public void doSomething() { }
}
```

---

## 5. Immutability

Immutable objects are inherently thread-safe:

```java
public final class ImmutablePerson {
    private final String name;
    private final List<String> hobbies;

    public ImmutablePerson(String name, List<String> hobbies) {
        this.name = name;
        this.hobbies = List.copyOf(hobbies);  // Defensive copy
    }

    public String getName() { return name; }
    public List<String> getHobbies() { return hobbies; }  // Unmodifiable
}
```

### Benefits
- No synchronization needed
- Can be shared freely
- No race conditions

---

## Interview Key Points

1. **Fork/Join vs ExecutorService?**
   - Fork/Join: Divide-and-conquer, work stealing
   - ExecutorService: Task queue, fixed work distribution

2. **ThreadLocal memory leaks?**
   - Thread pools reuse threads
   - Must call remove() to avoid leaks

3. **How to detect deadlock?**
   - Thread dumps (jstack)
   - JMX MBeans
   - Timeout on lock acquisition

4. **Best singleton pattern?**
   - Enum singleton or initialization-on-demand holder

---

## Examples

See the [examples/](examples/) folder for code demonstrations.

---

**Congratulations!** You've completed all modules. Now test yourself with:
- [Practice Tests](../../practice-tests/)
- [Interview Questions](../../interview-questions/)
