# Comprehensive Multithreading Practice Test

This test covers all modules. Try to complete it without looking at solutions!

---

## Section 1: Multiple Choice (20 Questions)

### Q1
What happens when you call `run()` instead of `start()` on a Thread?

A) A new thread is created  
B) Code runs in the calling thread (no new thread)  
C) Exception is thrown  
D) Thread starts but runs slower  

<details><summary>Answer</summary>**B**</details>

---

### Q2
Which thread state is entered when calling `Thread.sleep(1000)`?

A) BLOCKED  
B) WAITING  
C) TIMED_WAITING  
D) RUNNABLE  

<details><summary>Answer</summary>**C**</details>

---

### Q3
What is a race condition?

A) Threads competing for CPU time  
B) Multiple threads accessing shared data with at least one writing  
C) Threads running faster than expected  
D) Threads in different processes communicating  

<details><summary>Answer</summary>**B**</details>

---

### Q4
The `volatile` keyword provides:

A) Mutual exclusion and visibility  
B) Only visibility  
C) Only atomicity  
D) Both atomicity and mutual exclusion  

<details><summary>Answer</summary>**B**</details>

---

### Q5
Why must `wait()` be called inside a synchronized block?

A) Performance optimization  
B) To prevent lost notifications  
C) It's just a convention  
D) JVM requirement without reason  

<details><summary>Answer</summary>**B**</details>

---

### Q6
CountDownLatch vs CyclicBarrier - which is reusable?

A) CountDownLatch  
B) CyclicBarrier  
C) Both  
D) Neither  

<details><summary>Answer</summary>**B**</details>

---

### Q7
What's the purpose of a fair Semaphore?

A) Equal CPU distribution  
B) FIFO ordering of permit acquisition  
C) Faster performance  
D) Prevention of deadlock  

<details><summary>Answer</summary>**B**</details>

---

### Q8
ReentrantLock vs synchronized - which supports tryLock with timeout?

A) synchronized  
B) ReentrantLock  
C) Both  
D) Neither  

<details><summary>Answer</summary>**B**</details>

---

### Q9
What does `compareAndSet(expected, new)` return if current != expected?

A) true  
B) false  
C) Throws exception  
D) null  

<details><summary>Answer</summary>**B**</details>

---

### Q10
ReadWriteLock allows:

A) Multiple writers simultaneously  
B) Multiple readers OR one writer  
C) One reader and one writer  
D) Only one thread at a time  

<details><summary>Answer</summary>**B**</details>

---

### Q11
What happens if you forget to call `unlock()` on a ReentrantLock?

A) Auto-released at method end  
B) Lock remains held, potential deadlock  
C) Exception thrown  
D) Garbage collector releases it  

<details><summary>Answer</summary>**B**</details>

---

### Q12
`Executors.newCachedThreadPool()` can create:

A) Fixed number of threads  
B) Unlimited threads  
C) Single thread  
D) No threads  

<details><summary>Answer</summary>**B**</details>

---

### Q13
`Future.get()` when task hasn't completed:

A) Returns null  
B) Throws exception  
C) Blocks until result available  
D) Returns partial result  

<details><summary>Answer</summary>**C**</details>

---

### Q14
`thenCompose` vs `thenApply` - when to use thenCompose?

A) For simple transformations  
B) When transformation returns CompletableFuture  
C) For error handling  
D) For parallel execution  

<details><summary>Answer</summary>**B**</details>

---

### Q15
ConcurrentHashMap doesn't allow null because:

A) Memory optimization  
B) Can't distinguish "key absent" from "value is null"  
C) Historical reasons  
D) Performance  

<details><summary>Answer</summary>**B**</details>

---

### Q16
CopyOnWriteArrayList is best for:

A) Frequent writes  
B) Read-heavy, write-rare workloads  
C) Large lists  
D) Memory-constrained environments  

<details><summary>Answer</summary>**B**</details>

---

### Q17
Fork/Join framework uses:

A) Thread pool with work queue  
B) Work stealing algorithm  
C) Single thread execution  
D) Distributed computing  

<details><summary>Answer</summary>**B**</details>

---

### Q18
ThreadLocal memory leaks occur because:

A) Too many threads  
B) Thread pools reuse threads without cleaning up  
C) JVM bug  
D) Infinite values  

<details><summary>Answer</summary>**B**</details>

---

### Q19
Four deadlock conditions - which is NOT one?

A) Mutual exclusion  
B) Hold and wait  
C) Lock ordering  
D) Circular wait  

<details><summary>Answer</summary>**C** (Lock ordering is a prevention strategy, not a condition)</details>

---

### Q20
Best thread-safe singleton pattern?

A) Double-checked locking  
B) Eager initialization  
C) Enum singleton or holder pattern  
D) Synchronized getInstance()  

<details><summary>Answer</summary>**C**</details>

---

## Section 2: Code Output (10 Questions)

### Q21
```java
Thread t = new Thread(() -> System.out.print("A"));
t.run();
t.run();
System.out.print("B");
```

Output?

<details><summary>Answer</summary>**AAB** - run() executes in calling thread, not new thread</details>

---

### Q22
```java
AtomicInteger a = new AtomicInteger(10);
int old = a.getAndAdd(5);
System.out.println(old + " " + a.get());
```

<details><summary>Answer</summary>**10 15**</details>

---

### Q23
```java
CountDownLatch latch = new CountDownLatch(2);
latch.countDown();
System.out.println(latch.getCount());
```

<details><summary>Answer</summary>**1**</details>

---

### Q24
```java
private volatile int count = 0;
public void increment() { count++; }
// Called by 2 threads, 1000 times each
```
Final value guaranteed to be 2000?

<details><summary>Answer</summary>**No** - volatile doesn't make count++ atomic</details>

---

### Q25
```java
synchronized void methodA() { methodB(); }
synchronized void methodB() { System.out.println("B"); }
```
Does calling methodA() cause deadlock?

<details><summary>Answer</summary>**No** - synchronized is reentrant, same thread can reacquire lock</details>

---

## Section 3: Find the Bug (5 Questions)

### Q26
```java
public void transfer(Account from, Account to, int amount) {
    synchronized (from) {
        synchronized (to) {
            from.withdraw(amount);
            to.deposit(amount);
        }
    }
}
```

<details><summary>Bug</summary>**Deadlock possible** - if two threads transfer in opposite directions.
Fix: Lock ordering by account ID.</details>

---

### Q27
```java
public synchronized void process() {
    synchronized (new Object()) {
        counter++;
    }
}
```

<details><summary>Bug</summary>**Useless lock** - each call creates new Object, so locks are never shared.
Fix: Use a shared lock object.</details>

---

### Q28
```java
Lock lock = new ReentrantLock();
public void process() {
    lock.lock();
    doWork();
    lock.unlock();
}
```

<details><summary>Bug</summary>**No finally** - if doWork() throws, lock is never released.
Fix: Use try-finally.</details>

---

### Q29
```java
while (!ready) {
    // spin
}
System.out.println("Ready!");
```

<details><summary>Bug</summary>**Visibility issue** - without volatile, thread may cache `ready` forever.
Fix: Make `ready` volatile or use synchronization.</details>

---

### Q30
```java
executor.shutdown();
executor.shutdownNow();
```

<details><summary>Bug</summary>**Immediate force shutdown** - should wait between shutdown and shutdownNow.
Fix: Use awaitTermination() between them.</details>

---

## Scoring

- **25-30 correct:** Interview ready!
- **20-24 correct:** Good foundation, review weak areas
- **15-19 correct:** Needs more practice
- **Below 15:** Re-study the modules

---

## More Practice

- [Code Output Questions](code-output-questions.md)
- [Bug Finding Exercises](find-the-bug.md)
- [Coding Challenges](coding-challenges.md)
