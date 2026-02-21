# Module 1: Thread Fundamentals

## Learning Objectives
By the end of this module, you will understand:
- The difference between processes and threads
- How to create threads in Java (3 different ways)
- Thread lifecycle and states
- Essential thread methods
- Daemon vs User threads

---

## 1. Process vs Thread

### Process
A process is an independent program in execution with its own memory space.

### Thread
A thread is a lightweight unit of execution within a process. Multiple threads share the same memory space.

```
┌─────────────────────────────────────────────────────────────────┐
│                           PROCESS                               │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    SHARED MEMORY                        │    │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐    │    │
│  │  │  Heap   │  │ Static  │  │  Code   │  │  Data   │    │    │
│  │  │ Objects │  │Variables│  │ Segment │  │ Segment │    │    │
│  │  └─────────┘  └─────────┘  └─────────┘  └─────────┘    │    │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │   Thread 1   │  │   Thread 2   │  │   Thread 3   │          │
│  │ ┌──────────┐ │  │ ┌──────────┐ │  │ ┌──────────┐ │          │
│  │ │  Stack   │ │  │ │  Stack   │ │  │ │  Stack   │ │          │
│  │ │ (Local)  │ │  │ │ (Local)  │ │  │ │ (Local)  │ │          │
│  │ └──────────┘ │  │ └──────────┘ │  │ └──────────┘ │          │
│  │ Program Ctr  │  │ Program Ctr  │  │ Program Ctr  │          │
│  │ Registers    │  │ Registers    │  │ Registers    │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└─────────────────────────────────────────────────────────────────┘
```

### Key Differences

| Aspect | Process | Thread |
|--------|---------|--------|
| Memory | Separate memory space | Shared memory space |
| Creation | Expensive (heavy) | Cheap (lightweight) |
| Communication | IPC required | Direct via shared memory |
| Crash Impact | Isolated | Can crash entire process |
| Context Switch | Slow | Fast |

---

## 2. Creating Threads in Java

There are three main ways to create threads:

### Way 1: Extending Thread Class
```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running: " + getName());
    }
}

// Usage
MyThread t = new MyThread();
t.start();  // NOT t.run()!
```

### Way 2: Implementing Runnable Interface
```java
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable running");
    }
}

// Usage
Thread t = new Thread(new MyRunnable());
t.start();
```

### Way 3: Using Lambda (Java 8+)
```java
Thread t = new Thread(() -> {
    System.out.println("Lambda thread running");
});
t.start();
```

### Which to Use?

| Approach | Pros | Cons |
|----------|------|------|
| Extend Thread | Simple, direct access to Thread methods | Can't extend other classes |
| Implement Runnable | Can extend other classes, separates task from thread | Slightly more verbose |
| Lambda | Concise, modern | Only for simple tasks |

**Best Practice:** Prefer `Runnable` (or `Callable` for return values) - it separates the task from the threading mechanism.

---

## 3. Thread Lifecycle

A thread goes through various states during its lifetime:

```
                              ┌─────────────┐
                              │     NEW     │
                              │  (Created)  │
                              └──────┬──────┘
                                     │ start()
                                     ▼
                              ┌─────────────┐
         ┌────────────────────│  RUNNABLE   │◄───────────────────┐
         │                    │  (Ready/    │                    │
         │                    │   Running)  │                    │
         │                    └──────┬──────┘                    │
         │                           │                           │
         │           ┌───────────────┼───────────────┐           │
         │           │               │               │           │
         │           ▼               ▼               ▼           │
         │    ┌─────────────┐ ┌─────────────┐ ┌─────────────┐   │
         │    │   BLOCKED   │ │   WAITING   │ │TIMED_WAITING│   │
         │    │  (Monitor   │ │  (wait(),   │ │ (sleep(n),  │   │
         │    │   lock)     │ │   join())   │ │  wait(n))   │   │
         │    └──────┬──────┘ └──────┬──────┘ └──────┬──────┘   │
         │           │               │               │           │
         │           │   notify()/   │    timeout/   │           │
         │           │   lock free   │    notify()   │           │
         │           └───────────────┴───────────────┘           │
         │                           │                           │
         │                           └───────────────────────────┘
         │
         │ run() completes
         ▼
  ┌─────────────┐
  │ TERMINATED  │
  │   (Dead)    │
  └─────────────┘
```

### Thread States Explained

| State | Description | How to Enter |
|-------|-------------|--------------|
| NEW | Thread created but not started | `new Thread()` |
| RUNNABLE | Ready to run or currently running | `start()` |
| BLOCKED | Waiting to acquire a monitor lock | Entering `synchronized` block |
| WAITING | Waiting indefinitely for another thread | `wait()`, `join()` |
| TIMED_WAITING | Waiting for specified time | `sleep(n)`, `wait(n)`, `join(n)` |
| TERMINATED | Thread has completed execution | `run()` completes |

---

## 4. Essential Thread Methods

### start() vs run()

```java
Thread t = new Thread(() -> System.out.println("Hello"));

t.run();    // WRONG! Runs in current thread (no new thread created)
t.start();  // CORRECT! Creates new thread and calls run()
```

**Critical:** Always use `start()`. Calling `run()` directly does NOT create a new thread!

### sleep(milliseconds)

Pauses the current thread for specified time:
```java
try {
    Thread.sleep(1000);  // Sleep for 1 second
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();  // Restore interrupt status
}
```

### join()

Waits for a thread to complete:
```java
Thread t = new Thread(() -> {
    // do work
});
t.start();
t.join();  // Current thread waits until t finishes
System.out.println("Thread t has finished");
```

### join(timeout)

Waits with a timeout:
```java
t.join(5000);  // Wait max 5 seconds for t to finish
```

### yield()

Hints to scheduler that thread is willing to yield:
```java
Thread.yield();  // May or may not be honored
```

### interrupt()

Requests a thread to stop:
```java
t.interrupt();  // Sets interrupt flag

// In the thread:
while (!Thread.currentThread().isInterrupted()) {
    // do work
}
```

### Thread Information Methods

```java
Thread t = Thread.currentThread();
t.getName();        // Thread name
t.getId();          // Thread ID
t.getPriority();    // Priority (1-10)
t.getState();       // Current state
t.isAlive();        // Is thread still running?
t.isDaemon();       // Is daemon thread?
```

---

## 5. Daemon vs User Threads

### User Threads (Default)
- JVM waits for all user threads to complete before exiting
- Created by default when you create a thread

### Daemon Threads
- Background service threads
- JVM exits when only daemon threads remain
- Used for garbage collection, background tasks

```java
Thread daemon = new Thread(() -> {
    while (true) {
        // background work
    }
});
daemon.setDaemon(true);  // MUST set before start()
daemon.start();
```

```
┌────────────────────────────────────────────────────────────┐
│                         JVM                                 │
│                                                             │
│   User Threads          Daemon Threads                      │
│   ┌─────────┐           ┌─────────┐                        │
│   │ Main    │           │   GC    │                        │
│   │ Thread  │           │ Thread  │                        │
│   └─────────┘           └─────────┘                        │
│   ┌─────────┐           ┌─────────┐                        │
│   │ Worker  │           │ Timer   │                        │
│   │ Thread  │           │ Thread  │                        │
│   └─────────┘           └─────────┘                        │
│                                                             │
│   JVM exits when ──────► All user threads complete          │
│   (daemon threads are abruptly stopped)                     │
└────────────────────────────────────────────────────────────┘
```

---

## 6. Thread Priority

Thread priorities range from 1 (MIN) to 10 (MAX), with 5 as default (NORM):

```java
thread.setPriority(Thread.MAX_PRIORITY);  // 10
thread.setPriority(Thread.MIN_PRIORITY);  // 1
thread.setPriority(Thread.NORM_PRIORITY); // 5 (default)
```

**Warning:** Priority is only a hint to the scheduler. Different OS handle priorities differently. Don't rely on it for correctness!

---

## 7. Common Interview Questions

1. **What's the difference between `start()` and `run()`?**
   - `start()` creates a new thread and calls `run()` in that thread
   - `run()` executes in the current thread (no new thread)

2. **Can we start a thread twice?**
   - No! Throws `IllegalThreadStateException`

3. **What happens if we call `run()` instead of `start()`?**
   - Code executes sequentially in current thread, no multithreading

4. **Difference between `sleep()` and `wait()`?**
   - `sleep()` - Thread class method, doesn't release lock
   - `wait()` - Object class method, releases lock (covered in Module 3)

5. **What is a daemon thread?**
   - Background thread that doesn't prevent JVM exit

---

## Examples

Study the examples in order:
1. [Example01_ThreadCreation.java](examples/Example01_ThreadCreation.java) - Three ways to create threads
2. [Example02_ThreadLifecycle.java](examples/Example02_ThreadLifecycle.java) - Observing thread states
3. [Example03_JoinAndSleep.java](examples/Example03_JoinAndSleep.java) - Using join() and sleep()
4. [Example04_DaemonThreads.java](examples/Example04_DaemonThreads.java) - Daemon vs user threads
5. [Example05_ThreadInterrupt.java](examples/Example05_ThreadInterrupt.java) - Interrupting threads

---

## Exercises

After studying the examples, try:
1. [Exercise01_ThreadRace.java](exercises/Exercise01_ThreadRace.java) - Create racing threads
2. [Exercise02_ThreadCoordination.java](exercises/Exercise02_ThreadCoordination.java) - Coordinate thread completion

---

## Quiz

Test your understanding: [quiz.md](quiz.md)

---

**Next Module:** [Module 2 - Synchronization](../02-synchronization/README.md)
