# Coding Challenges

Implement these on your own before checking solutions!

---

## Challenge 1: Thread-Safe Counter

Implement a thread-safe counter with the following methods:
- `increment()` - increases by 1
- `decrement()` - decreases by 1
- `getValue()` - returns current value
- `compareAndSet(expected, newValue)` - atomic conditional update

Test with multiple threads incrementing concurrently.

---

## Challenge 2: Bounded Blocking Queue

Implement a blocking queue from scratch:
- `put(item)` - blocks if full
- `take()` - blocks if empty
- Configurable capacity

Do NOT use java.util.concurrent classes.

---

## Challenge 3: Read-Write Lock

Implement a simple read-write lock:
- Multiple readers can hold the lock simultaneously
- Only one writer can hold the lock
- Writer has exclusive access

---

## Challenge 4: Print Numbers with N Threads

Print numbers 1 to 100 using N threads where:
- Thread 0 prints 1, N+1, 2N+1...
- Thread 1 prints 2, N+2, 2N+2...
- Thread i prints i+1, N+i+1, 2N+i+1...

Output must be in order: 1, 2, 3, 4...100

---

## Challenge 5: Rate Limiter

Implement a rate limiter that allows at most N requests per second:
- `acquire()` - blocks until request is allowed
- `tryAcquire()` - returns false if rate limit exceeded

---

## Challenge 6: Concurrent LRU Cache

Implement a thread-safe LRU cache:
- `get(key)` - returns value, updates access time
- `put(key, value)` - adds/updates entry, evicts LRU if full
- Configurable capacity

---

## Challenge 7: Producer-Consumer with Multiple Producers/Consumers

Create a system with:
- Multiple producer threads
- Multiple consumer threads
- Bounded buffer
- Graceful shutdown

Track total items produced vs consumed.

---

## Challenge 8: Deadlock Detection

Write code that:
1. Creates a deadlock scenario
2. Detects the deadlock (using ThreadMXBean or dumps)
3. Shows how to prevent it

---

## Challenge 9: Parallel File Processor

Process multiple files concurrently:
- Use thread pool
- Aggregate results
- Handle errors gracefully
- Limit concurrent file operations

---

## Challenge 10: Dining Philosophers

Implement dining philosophers without deadlock using:
1. Lock ordering
2. Resource hierarchy
3. Or another strategy

Ensure all philosophers eventually eat!

---

## Submission Tips

For each challenge:
1. Write code from scratch
2. Test with multiple threads
3. Verify thread safety
4. Handle edge cases
5. Then compare with module examples
