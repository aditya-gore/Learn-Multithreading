# Module 3: Thread Communication

## Learning Objectives
By the end of this module, you will understand:
- How threads communicate using wait() and notify()
- Why wait() must be in a synchronized block
- The spurious wakeup problem and how to handle it
- The Producer-Consumer pattern
- Building a custom blocking queue

---

## 1. The Problem: Thread Coordination

Sometimes threads need to coordinate:
- Thread A produces data, Thread B consumes it
- Thread B must wait until data is available
- Thread A must signal when data is ready

**Busy waiting is bad:**
```java
// DON'T DO THIS - wastes CPU!
while (!dataReady) {
    // spin, spin, spin... (CPU at 100%)
}
```

**Solution: wait() and notify()**

---

## 2. wait() and notify() Basics

Every Java object has these methods (from Object class):
- `wait()` - releases lock and waits until notified
- `notify()` - wakes up one waiting thread
- `notifyAll()` - wakes up all waiting threads

```
┌─────────────────────────────────────────────────────────────────┐
│                     OBJECT'S MONITOR                             │
│                                                                  │
│   ┌─────────────┐           ┌─────────────┐                     │
│   │   ENTRY     │           │   WAIT      │                     │
│   │    SET      │           │    SET      │                     │
│   │ ┌─────────┐ │           │ ┌─────────┐ │                     │
│   │ │Thread B │ │           │ │Thread C │ │                     │
│   │ │(BLOCKED)│ │           │ │(WAITING)│ │                     │
│   │ └─────────┘ │           │ └─────────┘ │                     │
│   └──────┬──────┘           └──────┬──────┘                     │
│          │                         │                             │
│          │ acquire lock            │ notify()                    │
│          ▼                         ▼                             │
│   ┌─────────────────────────────────────────────┐               │
│   │              OWNER (has lock)                │               │
│   │                Thread A                      │               │
│   │                                              │               │
│   │  wait() → releases lock, moves to wait set  │               │
│   │  notify() → moves one from wait set to entry│               │
│   └─────────────────────────────────────────────┘               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 3. The wait/notify Protocol

### Rule 1: Must be in synchronized block
```java
// WRONG - throws IllegalMonitorStateException!
object.wait();

// CORRECT
synchronized (object) {
    object.wait();
}
```

### Rule 2: Always wait in a loop (spurious wakeups)
```java
// WRONG - might wake up when condition is still false!
synchronized (lock) {
    if (!condition) {
        lock.wait();
    }
    // condition might still be false (spurious wakeup)
}

// CORRECT
synchronized (lock) {
    while (!condition) {  // RE-CHECK after wakeup!
        lock.wait();
    }
    // condition is guaranteed true here
}
```

### Rule 3: notify() vs notifyAll()

| Method | Behavior | When to use |
|--------|----------|-------------|
| `notify()` | Wakes ONE waiting thread (arbitrary) | When any one waiter can proceed |
| `notifyAll()` | Wakes ALL waiting threads | When waiters have different conditions |

**Rule of thumb:** When in doubt, use `notifyAll()`. It's safer.

---

## 4. Spurious Wakeups

A **spurious wakeup** is when a thread wakes from wait() without being notified.

**Why it happens:**
- OS-level signal handling
- JVM implementation details
- Hardware interrupts

**Solution:** Always use while loop, never if:
```java
synchronized (lock) {
    while (!condition) {  // While, not if!
        lock.wait();
    }
}
```

---

## 5. The Producer-Consumer Pattern

One of the most important concurrency patterns:

```
┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│   Producer   │      │    Buffer    │      │   Consumer   │
│              │─────▶│   (Queue)    │─────▶│              │
│  Produces    │      │  ┌─┬─┬─┬─┐  │      │  Consumes    │
│  items       │      │  │■│■│ │ │  │      │  items       │
└──────────────┘      │  └─┴─┴─┴─┘  │      └──────────────┘
                      └──────────────┘
                      
    Wait if full ◄──────────────────────── Wait if empty
```

### Basic Implementation

```java
class Buffer {
    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity;

    public Buffer(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void produce(int item) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();  // Buffer full, wait
        }
        queue.add(item);
        notifyAll();  // Notify consumers
    }

    public synchronized int consume() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();  // Buffer empty, wait
        }
        int item = queue.poll();
        notifyAll();  // Notify producers
        return item;
    }
}
```

---

## 6. Common Pitfalls

### Pitfall 1: Calling wait() without synchronized
```java
// WRONG - IllegalMonitorStateException!
public void waitForSignal() {
    lock.wait();  // Not in synchronized block
}
```

### Pitfall 2: Using if instead of while
```java
// WRONG - vulnerable to spurious wakeups
synchronized (lock) {
    if (queue.isEmpty()) {
        lock.wait();
    }
    process(queue.poll());  // Might be null!
}
```

### Pitfall 3: Forgetting to notify
```java
// WRONG - consumers wait forever
public synchronized void produce(int item) {
    queue.add(item);
    // Forgot notifyAll()!
}
```

### Pitfall 4: Notifying on wrong object
```java
private final Object lock = new Object();
private final Queue<Integer> queue = new LinkedList<>();

// WRONG - waiting on lock, notifying on this
public void produce(int item) {
    synchronized (lock) {
        queue.add(item);
    }
    this.notifyAll();  // Wrong object!
}
```

---

## 7. wait() vs sleep() vs yield()

| Method | Class | Releases Lock? | Purpose |
|--------|-------|----------------|---------|
| `wait()` | Object | YES | Wait for notification |
| `sleep(ms)` | Thread | NO | Pause for duration |
| `yield()` | Thread | NO | Hint to scheduler |

```
Thread state after:
  wait()  → WAITING (or TIMED_WAITING if timeout)
  sleep() → TIMED_WAITING
  yield() → RUNNABLE (just a scheduling hint)
```

---

## 8. Timed wait()

You can specify a timeout:
```java
synchronized (lock) {
    while (!condition) {
        long startTime = System.currentTimeMillis();
        lock.wait(1000);  // Wait max 1 second
        
        // Check if we timed out
        if (System.currentTimeMillis() - startTime >= 1000) {
            // Handle timeout
            break;
        }
    }
}
```

---

## 9. Interview Key Points

1. **Why must wait() be in synchronized?**
   - To ensure the condition check and wait happen atomically
   - Prevents lost notifications (notify before wait)

2. **Why while loop, not if?**
   - Spurious wakeups
   - Multiple threads might be waiting; condition might change

3. **notify() vs notifyAll()?**
   - notify() is faster but risky if waiters have different conditions
   - notifyAll() is safer, wakes all to re-check conditions

4. **What happens to the lock during wait()?**
   - Released when entering wait()
   - Re-acquired when waking up

5. **Can wait() be interrupted?**
   - Yes, throws InterruptedException

---

## Examples

Study in order:
1. [Example01_WaitNotifyBasics.java](examples/Example01_WaitNotifyBasics.java) - Basic wait/notify
2. [Example02_ProducerConsumer.java](examples/Example02_ProducerConsumer.java) - Classic pattern
3. [Example03_CustomBlockingQueue.java](examples/Example03_CustomBlockingQueue.java) - Full implementation

---

## Exercises

1. [Exercise01_PingPong.java](exercises/Exercise01_PingPong.java) - Alternating thread communication
2. [Exercise02_BoundedBuffer.java](exercises/Exercise02_BoundedBuffer.java) - Implement a bounded buffer

---

## Quiz

Test yourself: [quiz.md](quiz.md)

---

**Next Module:** [Module 4 - Concurrent Utilities](../04-concurrent-utilities/README.md)
