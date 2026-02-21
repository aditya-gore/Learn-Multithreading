# Module 8 Quiz: Concurrent Collections

## Questions

### Q1: ConcurrentHashMap Null
Why doesn't ConcurrentHashMap allow null keys or values?

A) To save memory  
B) Can't distinguish "key not found" from "value is null"  
C) Performance optimization  
D) Historical reasons  

<details>
<summary>Answer</summary>

**B) Can't distinguish "key not found" from "value is null"**

In concurrent context, if get() returns null, you can't tell if the key doesn't exist or if someone just mapped it to null.
</details>

---

### Q2: computeIfAbsent
What's the benefit of `computeIfAbsent()` over `putIfAbsent()`?

A) It's faster  
B) The value is computed lazily only if key is absent  
C) It allows null values  
D) It's thread-safe  

<details>
<summary>Answer</summary>

**B) The value is computed lazily only if key is absent**

With putIfAbsent, you compute the value first even if not needed. computeIfAbsent only calls the function if the key is absent.
</details>

---

### Q3: BlockingQueue
What does `take()` do when the queue is empty?

A) Returns null  
B) Throws exception  
C) Blocks until an element is available  
D) Returns immediately with default value  

<details>
<summary>Answer</summary>

**C) Blocks until an element is available**

take() blocks the calling thread until an element becomes available. Use poll() or poll(timeout, unit) for non-blocking alternatives.
</details>

---

### Q4: CopyOnWriteArrayList
When is CopyOnWriteArrayList a good choice?

A) Frequent writes  
B) Large lists  
C) Read-heavy with rare writes  
D) Memory-constrained environments  

<details>
<summary>Answer</summary>

**C) Read-heavy with rare writes**

COWAL copies the entire array on each write, which is expensive. It's designed for scenarios where reads vastly outnumber writes.
</details>

---

## Score Yourself

- **4 correct:** Ready for Module 9
- **2-3 correct:** Review the atomic operations
- **Below 2:** Re-study the module

---

**Next:** [Module 9 - Classic Problems](../09-classic-problems/README.md)
