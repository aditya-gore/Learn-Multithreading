# Module 1 Quiz: Thread Fundamentals

Test your understanding of thread basics. Try to answer without looking at the solutions!

---

## Multiple Choice Questions

### Q1: Thread Creation
What happens when you call `run()` directly on a Thread object instead of `start()`?

A) A new thread is created and run() executes in it  
B) The code runs in the current thread, no new thread is created  
C) An IllegalThreadStateException is thrown  
D) The thread is added to a queue to be executed later  

<details>
<summary>Answer</summary>

**B) The code runs in the current thread, no new thread is created**

Calling `run()` directly is just a regular method call. Only `start()` creates a new thread.
</details>

---

### Q2: Thread States
A thread calls `Thread.sleep(5000)`. What state is it in?

A) BLOCKED  
B) WAITING  
C) TIMED_WAITING  
D) RUNNABLE  

<details>
<summary>Answer</summary>

**C) TIMED_WAITING**

`sleep()` with a duration puts the thread in TIMED_WAITING. WAITING is for indefinite waits like `join()` without timeout. BLOCKED is for waiting on a monitor lock.
</details>

---

### Q3: join() Method
What does `thread.join()` do?

A) Merges two threads into one  
B) Makes the current thread wait until `thread` completes  
C) Starts the thread  
D) Interrupts the thread  

<details>
<summary>Answer</summary>

**B) Makes the current thread wait until `thread` completes**

`join()` blocks the calling thread until the target thread terminates.
</details>

---

### Q4: Daemon Threads
When does the JVM exit?

A) When the main thread completes  
B) When all threads complete  
C) When all user (non-daemon) threads complete  
D) When any thread calls System.exit()  

<details>
<summary>Answer</summary>

**C) When all user (non-daemon) threads complete**

The JVM doesn't wait for daemon threads. It exits when only daemon threads remain.
</details>

---

### Q5: Thread Priority
Which statement about thread priority is TRUE?

A) Higher priority threads always run first  
B) Priority is only a hint to the scheduler  
C) Priority ranges from 0 to 100  
D) You cannot change a thread's priority  

<details>
<summary>Answer</summary>

**B) Priority is only a hint to the scheduler**

Thread priority is advisory only. The OS scheduler may or may not honor it. Priority ranges from 1-10.
</details>

---

### Q6: Starting Threads
What happens if you call `start()` on a thread that has already been started?

A) The thread restarts from the beginning  
B) Nothing happens  
C) IllegalThreadStateException is thrown  
D) The thread is cloned  

<details>
<summary>Answer</summary>

**C) IllegalThreadStateException is thrown**

A thread can only be started once. Attempting to start it again throws an exception.
</details>

---

### Q7: setDaemon() Timing
When must `setDaemon(true)` be called?

A) Anytime before or after start()  
B) Only after the thread has started  
C) Only before the thread has started  
D) Only in the thread's constructor  

<details>
<summary>Answer</summary>

**C) Only before the thread has started**

Calling `setDaemon()` after `start()` throws `IllegalThreadStateException`.
</details>

---

### Q8: InterruptedException
What does catching InterruptedException do to the interrupt flag?

A) Sets it to true  
B) Clears it (sets to false)  
C) Leaves it unchanged  
D) Throws another exception  

<details>
<summary>Answer</summary>

**B) Clears it (sets to false)**

When InterruptedException is thrown, the interrupt flag is cleared. Best practice is to restore it with `Thread.currentThread().interrupt()`.
</details>

---

## Code Output Questions

### Q9: What does this print?

```java
Thread t = new Thread(() -> {
    System.out.print("A");
});
t.run();
t.run();
System.out.print("B");
```

A) AAB  
B) ABA  
C) BAA  
D) AB or BA (non-deterministic)  

<details>
<summary>Answer</summary>

**A) AAB**

Since `run()` is called directly (not `start()`), everything runs sequentially in the main thread. First "A" prints, then "A" again, then "B".
</details>

---

### Q10: What does this print?

```java
Thread t = new Thread(() -> {
    System.out.print("A");
});
t.start();
t.join();
System.out.print("B");
```

A) AB  
B) BA  
C) AB or BA (non-deterministic)  
D) Throws exception  

<details>
<summary>Answer</summary>

**A) AB**

The `join()` ensures the main thread waits for thread `t` to complete before printing "B". So "A" always prints before "B".
</details>

---

### Q11: What does this print?

```java
Thread t = new Thread(() -> {
    System.out.print("A");
});
t.start();
System.out.print("B");
```

A) AB  
B) BA  
C) AB or BA (non-deterministic)  
D) Throws exception  

<details>
<summary>Answer</summary>

**C) AB or BA (non-deterministic)**

Without `join()`, there's a race between the main thread printing "B" and thread `t` printing "A". Either could happen first.
</details>

---

## True or False

### Q12
**True or False:** A daemon thread can create user threads.

<details>
<summary>Answer</summary>

**True**

Daemon threads can create both daemon and user threads. New threads inherit the daemon status of their parent by default, but this can be changed with `setDaemon()` before `start()`.
</details>

---

### Q13
**True or False:** Thread.yield() guarantees that another thread will run.

<details>
<summary>Answer</summary>

**False**

`yield()` is just a hint to the scheduler. It may be ignored entirely, and the same thread might continue running.
</details>

---

### Q14
**True or False:** Each thread has its own stack but shares the heap with other threads.

<details>
<summary>Answer</summary>

**True**

Each thread has its own stack for local variables and method calls. All threads in a process share the heap memory where objects are allocated.
</details>

---

## Short Answer

### Q15
Why is it recommended to implement `Runnable` instead of extending `Thread`?

<details>
<summary>Answer</summary>

1. **Single inheritance:** Java only allows extending one class. If you extend Thread, you can't extend anything else.

2. **Separation of concerns:** Runnable separates the task (what to do) from the threading mechanism (how to run it).

3. **Reusability:** The same Runnable can be executed by multiple threads or submitted to thread pools.

4. **Better design:** Your task class doesn't need to "be a" Thread; it just needs to be runnable.
</details>

---

### Q16
Explain the difference between BLOCKED and WAITING thread states.

<details>
<summary>Answer</summary>

**BLOCKED:**
- Thread is waiting to acquire a monitor lock (synchronized block/method)
- Another thread holds the lock the blocked thread needs
- Automatically unblocked when lock becomes available

**WAITING:**
- Thread is waiting for a specific action from another thread
- Entered via `Object.wait()`, `Thread.join()`, `LockSupport.park()`
- Must be explicitly notified/signaled to wake up

Key difference: BLOCKED is about lock acquisition; WAITING is about explicit inter-thread coordination.
</details>

---

## Score Yourself

- **14-16 correct:** Excellent! Ready for Module 2
- **11-13 correct:** Good understanding, review missed topics
- **8-10 correct:** Review the module before proceeding
- **Below 8:** Re-study the examples and re-read the README

---

**Next:** [Module 2 - Synchronization](../02-synchronization/README.md)
