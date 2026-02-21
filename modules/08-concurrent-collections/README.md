# Module 8: Concurrent Collections

## Learning Objectives
By the end of this module, you will understand:
- ConcurrentHashMap and its atomic operations
- BlockingQueue implementations
- CopyOnWriteArrayList for read-heavy workloads
- When to use which concurrent collection

---

## 1. Overview

```
┌─────────────────────────────────────────────────────────────────┐
│              CONCURRENT COLLECTIONS                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Maps                          Lists & Sets                     │
│   ────                          ────────────                     │
│   ConcurrentHashMap             CopyOnWriteArrayList             │
│   ConcurrentSkipListMap         CopyOnWriteArraySet              │
│                                 ConcurrentSkipListSet            │
│                                                                  │
│   Queues                                                         │
│   ──────                                                         │
│   ArrayBlockingQueue      (bounded, FIFO)                       │
│   LinkedBlockingQueue     (optionally bounded)                  │
│   PriorityBlockingQueue   (unbounded, priority order)           │
│   DelayQueue              (elements available after delay)      │
│   SynchronousQueue        (direct handoff, no storage)          │
│   LinkedTransferQueue     (producer can wait for consumer)      │
│                                                                  │
│   Deques                                                         │
│   ──────                                                         │
│   ConcurrentLinkedDeque   (unbounded, non-blocking)             │
│   LinkedBlockingDeque     (optionally bounded, blocking)        │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. ConcurrentHashMap

Thread-safe HashMap with better concurrency than synchronized HashMap.

### Basic Operations
```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("a", 1);
map.get("a");
map.remove("a");
```

### Atomic Operations (Key Feature!)
```java
// putIfAbsent - atomic check and put
map.putIfAbsent("key", 1);  // Only puts if key absent

// computeIfAbsent - atomic compute and put
map.computeIfAbsent("key", k -> expensiveComputation(k));

// computeIfPresent - atomic update if present
map.computeIfPresent("key", (k, v) -> v + 1);

// compute - always computes
map.compute("key", (k, v) -> (v == null) ? 1 : v + 1);

// merge - combine old and new values
map.merge("key", 1, (oldVal, newVal) -> oldVal + newVal);
```

### DO NOT DO THIS!
```java
// WRONG: Not atomic!
if (!map.containsKey("key")) {
    map.put("key", value);  // Race condition!
}

// CORRECT: Atomic operation
map.putIfAbsent("key", value);
```

---

## 3. BlockingQueue

Queues that block on `take()` when empty and `put()` when full.

### Interface Methods
```java
// Blocking operations
void put(E e)       // Blocks until space available
E take()            // Blocks until element available

// Timed operations
boolean offer(E e, timeout, unit)  // Returns false on timeout
E poll(timeout, unit)              // Returns null on timeout

// Non-blocking operations
boolean offer(E e)  // Returns false if full
E poll()            // Returns null if empty
```

### ArrayBlockingQueue (Bounded)
```java
BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);
queue.put("item");  // Blocks if queue has 100 items
String item = queue.take();  // Blocks if queue is empty
```

### LinkedBlockingQueue (Optionally Bounded)
```java
BlockingQueue<String> unbounded = new LinkedBlockingQueue<>();
BlockingQueue<String> bounded = new LinkedBlockingQueue<>(100);
```

### PriorityBlockingQueue
```java
// Elements retrieved in priority order (natural or Comparator)
BlockingQueue<Task> queue = new PriorityBlockingQueue<>();
```

---

## 4. CopyOnWriteArrayList

Thread-safe ArrayList for read-heavy, write-light workloads.

```java
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
list.add("item");  // Creates new internal array copy!

// Iteration is always safe (uses snapshot)
for (String s : list) {
    // No ConcurrentModificationException even if list is modified
}
```

### When to Use
- Many readers, few writers
- Iteration must be fast
- Examples: Listener lists, configuration, routing tables

### When NOT to Use
- Frequent writes (each write copies entire array)
- Large lists

---

## 5. Choosing the Right Collection

```
┌─────────────────────────────────────────────────────────────────┐
│                    DECISION GUIDE                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Need a Map?                                                     │
│  └─► ConcurrentHashMap (usually)                                │
│      ConcurrentSkipListMap (if need sorted keys)                │
│                                                                  │
│  Need a Queue?                                                   │
│  └─► ArrayBlockingQueue (bounded, fair optional)                │
│      LinkedBlockingQueue (unbounded or bounded)                 │
│      PriorityBlockingQueue (priority ordering)                  │
│      SynchronousQueue (direct handoff)                          │
│                                                                  │
│  Need a List?                                                    │
│  └─► CopyOnWriteArrayList (read >> write)                       │
│      Collections.synchronizedList (writes common)               │
│                                                                  │
│  Need a Set?                                                     │
│  └─► ConcurrentHashMap.newKeySet()                              │
│      CopyOnWriteArraySet (read >> write)                        │
│      ConcurrentSkipListSet (sorted)                             │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 6. Common Patterns

### Thread-Safe Counter
```java
ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

// Increment counter atomically
counters.computeIfAbsent("requests", k -> new AtomicLong()).incrementAndGet();
```

### Thread-Safe Cache
```java
ConcurrentHashMap<String, Data> cache = new ConcurrentHashMap<>();

Data data = cache.computeIfAbsent(key, k -> loadFromDatabase(k));
```

### Producer-Consumer with BlockingQueue
```java
BlockingQueue<Task> queue = new LinkedBlockingQueue<>(100);

// Producer
queue.put(task);

// Consumer
Task task = queue.take();
```

---

## 7. Interview Key Points

1. **ConcurrentHashMap vs Hashtable?**
   - CHM: Segment locking, better concurrency, null not allowed
   - Hashtable: Single lock, slower, legacy

2. **Why no null in ConcurrentHashMap?**
   - Can't distinguish "key not found" from "value is null"
   - get() returning null is ambiguous

3. **computeIfAbsent vs putIfAbsent?**
   - computeIfAbsent: Only computes value if key absent
   - putIfAbsent: Value already computed, may be wasted

4. **When to use CopyOnWriteArrayList?**
   - Read-heavy, write-rare workloads
   - Iteration must not throw ConcurrentModificationException

---

## Examples

1. [Example01_ConcurrentCollections.java](examples/Example01_ConcurrentCollections.java) - ConcurrentHashMap, BlockingQueue, CopyOnWriteArrayList
2. [Example02_BlockingQueue.java](examples/Example02_BlockingQueue.java) - Producer-consumer with ArrayBlockingQueue
3. [Example03_ConcurrentHashMapAtomicOps.java](examples/Example03_ConcurrentHashMapAtomicOps.java) - compute, merge, word-count style

---

## Exercises

1. [Exercise01_WordCount.java](exercises/Exercise01_WordCount.java) - Thread-safe word count using ConcurrentHashMap
2. [Exercise02_BoundedTaskQueue.java](exercises/Exercise02_BoundedTaskQueue.java) - Bounded task queue with BlockingQueue and workers

Solutions are in [exercises/solutions/](exercises/solutions/).

---

## Quiz

Test yourself: [quiz.md](quiz.md)

---

**Next Module:** [Module 9 - Classic Problems](../09-classic-problems/README.md)
