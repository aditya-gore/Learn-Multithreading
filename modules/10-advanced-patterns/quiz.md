# Module 10 Quiz: Advanced Patterns

Test your understanding of Fork/Join, ThreadLocal, deadlock, and singleton patterns.

---

## Multiple Choice Questions

### Q1: Fork/Join
What is the main advantage of Fork/Join over a fixed thread pool for divide-and-conquer tasks?

A) Lower memory usage  
B) Work stealing balances load across threads  
C) Fewer threads created  
D) No synchronization needed  

<details>
<summary>Answer</summary>

**B) Work stealing balances load across threads**

Fork/Join pools use work stealing: idle threads can steal tasks from busy threads' queues, improving load balance for recursive tasks.
</details>

---

### Q2: RecursiveTask vs RecursiveAction
What does RecursiveTask return that RecursiveAction does not?

A) A Future  
B) A result value  
C) A subtask  
D) Nothing; RecursiveAction returns a value  

<details>
<summary>Answer</summary>

**B) A result value**

RecursiveTask\<V\> returns a value from compute(); RecursiveAction returns void.
</details>

---

### Q3: ThreadLocal
Why should you call ThreadLocal.remove() when using thread pools?

A) To free memory for the pool  
B) Threads are reused; without remove(), stale values can leak to the next task  
C) To allow garbage collection of the thread  
D) remove() is not necessary with thread pools  

<details>
<summary>Answer</summary>

**B) Threads are reused; without remove(), stale values can leak to the next task**

In a thread pool, the same thread runs many tasks. If you don't remove() after each task, the next task might see the previous task's value.
</details>

---

### Q4: Deadlock
Which condition is NOT required for deadlock to occur?

A) Mutual exclusion  
B) Hold and wait  
C) No preemption  
D) Single lock  

<details>
<summary>Answer</summary>

**D) Single lock**

Deadlock requires at least two locks (or resources). The four conditions are: mutual exclusion, hold and wait, no preemption, and circular wait.
</details>

---

### Q5: Singleton
Which singleton implementation is thread-safe without explicit synchronization?

A) Double-checked locking with volatile  
B) Enum singleton  
C) Lazy holder (initialization-on-demand holder)  
D) Both B and C  

<details>
<summary>Answer</summary>

**D) Both B and C**

Enum singletons are thread-safe by JVM guarantee. The lazy holder idiom uses class initialization, which is thread-safe. Double-checked locking requires volatile for correctness.
</details>

---

### Q6: fork() and join()
In Fork/Join, why is it often better to call compute() on one subtask and join() on the forked one (instead of fork both and join both)?

A) Fewer threads  
B) Better cache locality; one subtree runs in the current thread  
C) join() is faster than compute()  
D) It avoids deadlock  

<details>
<summary>Answer</summary>

**B) Better cache locality; one subtree runs in the current thread**

Calling right.compute() in the current thread keeps that subtree's work in the same thread, improving cache usage and reducing context switches.
</details>

---

## Short Answer

### Q7: How would you detect deadlock in a running Java application?

<details>
<summary>Answer</summary>

- **jstack \<pid\>** and look for "deadlock" in the output, or look for threads holding locks and waiting for locks held by others.
- **ThreadMXBean.findDeadlockedThreads()** from JMX.
- Some IDEs and monitoring tools can report deadlocks from thread dumps.
</details>

---

### Q8: When is CopyOnWriteArrayList a bad choice?

<details>
<summary>Answer</summary>

When writes are frequent: every write (add, set, remove) copies the entire array, so write-heavy workloads get O(n) per modification and high memory churn. Use concurrent structures or locking for write-heavy data.
</details>

---

**Next:** [Practice Tests](../../practice-tests/) | [Interview Questions](../../interview-questions/)
