# Module 3 Quiz: Thread Communication

Test your understanding of wait/notify and producer-consumer patterns.

---

## Multiple Choice Questions

### Q1: wait() Requirements
What exception is thrown if you call wait() outside a synchronized block?

A) InterruptedException  
B) IllegalStateException  
C) IllegalMonitorStateException  
D) UnsupportedOperationException  

<details>
<summary>Answer</summary>

**C) IllegalMonitorStateException**

You must own the object's monitor (be in a synchronized block on that object) to call wait(), notify(), or notifyAll().
</details>

---

### Q2: Lock Release
What happens to the lock when a thread calls wait()?

A) The lock is kept until notify() is called  
B) The lock is released and reacquired when notified  
C) The lock is destroyed  
D) A new lock is created  

<details>
<summary>Answer</summary>

**B) The lock is released and reacquired when notified**

wait() releases the lock, allowing other threads to enter the synchronized block. When the thread is notified, it must reacquire the lock before continuing.
</details>

---

### Q3: Spurious Wakeups
Why should wait() always be in a while loop?

A) To improve performance  
B) Because wait() can return without notify (spurious wakeup)  
C) To prevent deadlock  
D) It's not necessary, if works fine  

<details>
<summary>Answer</summary>

**B) Because wait() can return without notify (spurious wakeup)**

Spurious wakeups can occur, and even with notify(), the condition might no longer be true (another thread might have acted first). Always re-check the condition in a while loop.
</details>

---

### Q4: notify vs notifyAll
When should you use notifyAll() instead of notify()?

A) Always, notify() is deprecated  
B) When multiple threads might be waiting for different conditions  
C) When you want to wake only one thread  
D) Never, notifyAll() is less efficient  

<details>
<summary>Answer</summary>

**B) When multiple threads might be waiting for different conditions**

notify() wakes one arbitrary thread. If threads wait for different conditions, the wrong thread might wake up. notifyAll() wakes all, letting each re-check its condition.
</details>

---

### Q5: wait() vs sleep()
Which statement about wait() and sleep() is TRUE?

A) Both release the lock  
B) Neither releases the lock  
C) wait() releases the lock, sleep() does not  
D) sleep() releases the lock, wait() does not  

<details>
<summary>Answer</summary>

**C) wait() releases the lock, sleep() does not**

This is a critical difference! wait() is for coordination (releases lock so others can proceed). sleep() just pauses the thread while keeping any locks.
</details>

---

### Q6: Object Methods
Which class defines wait(), notify(), and notifyAll()?

A) Thread  
B) Object  
C) Runnable  
D) Lock  

<details>
<summary>Answer</summary>

**B) Object**

These methods are defined in java.lang.Object, so every object can be used for wait/notify coordination. They work with the object's intrinsic lock (monitor).
</details>

---

### Q7: Producer-Consumer
In the producer-consumer pattern, what should the producer do when the buffer is full?

A) Throw an exception  
B) Drop the item  
C) Wait until space is available  
D) Create a new buffer  

<details>
<summary>Answer</summary>

**C) Wait until space is available**

The classic blocking producer-consumer pattern has the producer wait (using wait()) when the buffer is full. When a consumer removes an item, it notifies the producer.
</details>

---

### Q8: Condition Check Order
What's wrong with this code?

```java
synchronized (lock) {
    lock.wait();
    if (condition) {
        doWork();
    }
}
```

A) Nothing, it's correct  
B) wait() should check condition first  
C) Missing notify() call  
D) Should use Thread.sleep() instead  

<details>
<summary>Answer</summary>

**B) wait() should check condition first**

You should check the condition BEFORE waiting. Otherwise you might wait forever even though the condition is already true. Correct pattern:
```java
while (!condition) {
    wait();
}
doWork();
```
</details>

---

## Code Output Questions

### Q9: What happens?

```java
Object lock = new Object();

Thread t1 = new Thread(() -> {
    synchronized (lock) {
        try {
            lock.wait();
        } catch (InterruptedException e) {}
        System.out.println("T1");
    }
});

Thread t2 = new Thread(() -> {
    synchronized (lock) {
        System.out.println("T2");
        lock.notify();
    }
});

t1.start();
Thread.sleep(100);
t2.start();
```

A) T2, T1  
B) T1, T2  
C) Only T2 (T1 waits forever)  
D) Deadlock  

<details>
<summary>Answer</summary>

**A) T2, T1**

T1 starts and waits. T2 gets the lock (after T1 releases it via wait), prints "T2", then notifies. T1 wakes up, reacquires the lock, and prints "T1".
</details>

---

### Q10: What's the output?

```java
synchronized (lock) {
    while (queue.isEmpty()) {
        lock.wait();
    }
}
// queue.poll() here
```

A) Thread-safe, item will be polled correctly  
B) Race condition - queue might become empty between check and poll  
C) IllegalMonitorStateException  
D) Deadlock  

<details>
<summary>Answer</summary>

**B) Race condition - queue might become empty between check and poll**

The poll() is OUTSIDE the synchronized block! Between exiting synchronized and calling poll(), another thread could empty the queue. The poll should be inside synchronized.
</details>

---

## True or False

### Q11
**True or False:** notify() guarantees that a waiting thread will immediately resume execution.

<details>
<summary>Answer</summary>

**False**

notify() moves a thread from the wait set to the entry set. The thread must still compete to reacquire the lock. It might not run immediately, and another thread might act first.
</details>

---

### Q12
**True or False:** A thread can call wait() on any object, regardless of which lock it holds.

<details>
<summary>Answer</summary>

**False**

You can only call wait() on an object if you hold that object's lock (are in a synchronized block on that specific object). Calling wait() on a different object throws IllegalMonitorStateException.
</details>

---

### Q13
**True or False:** wait(1000) is equivalent to sleep(1000) when no other thread calls notify().

<details>
<summary>Answer</summary>

**False**

wait(1000) releases the lock while waiting, sleep(1000) does not. Also, wait() can return early due to notify() or spurious wakeup, while sleep() sleeps for approximately the full duration.
</details>

---

## Short Answer

### Q14
Explain why this code might cause a "lost notification" bug:

```java
// Thread 1 (waiter)
if (!ready) {
    synchronized (lock) {
        lock.wait();
    }
}

// Thread 2 (notifier)
synchronized (lock) {
    ready = true;
    lock.notify();
}
```

<details>
<summary>Answer</summary>

**Lost Notification Bug:**

The problem is that the check for `ready` is outside the synchronized block in Thread 1.

Timeline that causes the bug:
1. Thread 1 checks `ready` - it's false
2. Thread 2 acquires lock, sets `ready = true`, calls `notify()`
3. Thread 1 acquires lock and calls `wait()`
4. Thread 1 waits forever - the notification was already sent!

**Fix:** Move the condition check inside synchronized:
```java
synchronized (lock) {
    while (!ready) {
        lock.wait();
    }
}
```
</details>

---

### Q15
Why does the producer-consumer pattern need BOTH a condition variable (wait/notify) AND a mutex (synchronized)?

<details>
<summary>Answer</summary>

**Two Different Needs:**

1. **Mutex (synchronized):** Prevents data corruption when multiple threads access the shared buffer simultaneously. Ensures only one thread modifies the buffer at a time.

2. **Condition Variable (wait/notify):** Allows threads to efficiently wait for a specific condition:
   - Producer waits when buffer is full
   - Consumer waits when buffer is empty

**Without mutex:** Race conditions corrupt the buffer.

**Without condition variable:** Threads would have to busy-wait:
```java
while (buffer.isFull()) {
    // Spin, wasting CPU!
}
```

The combination allows threads to sleep efficiently until the condition they need becomes true, while also ensuring thread-safe access to shared data.
</details>

---

## Score Yourself

- **13-15 correct:** Excellent! Ready for Module 4
- **10-12 correct:** Good, review the wait/notify protocol
- **7-9 correct:** Review the module more thoroughly
- **Below 7:** Re-study examples before proceeding

---

**Next:** [Module 4 - Concurrent Utilities](../04-concurrent-utilities/README.md)
