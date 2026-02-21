# System Design Questions (Concurrency Focus)

## 1. Design a Web Crawler

**Requirements:**
- Crawl web pages concurrently
- Avoid visiting same URL twice
- Respect rate limits
- Handle failures gracefully

**Key Points:**
- Thread pool for concurrent fetching
- ConcurrentHashMap for visited URLs
- BlockingQueue for URL frontier
- Semaphore for rate limiting

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   URL       │───▶│  Thread     │───▶│  Visited    │
│  Frontier   │    │   Pool      │    │    Set      │
│ (Queue)     │◀───│ (Fetchers)  │    │ (CHM)       │
└─────────────┘    └─────────────┘    └─────────────┘
```

---

## 2. Design a Task Scheduler

**Requirements:**
- Execute tasks at scheduled times
- Support one-time and recurring tasks
- Handle priority
- Allow cancellation

**Key Points:**
- PriorityBlockingQueue (ordered by execution time)
- ScheduledExecutorService
- Delayed interface for timing
- ConcurrentHashMap for task lookup

---

## 3. Design a Connection Pool

**Requirements:**
- Limit max concurrent connections
- Reuse idle connections
- Timeout on borrow
- Health check idle connections

**Key Points:**
- Semaphore to limit count
- BlockingQueue for idle connections
- Scheduled cleanup thread
- Lock-free borrow/return where possible

---

## 4. Design a Pub-Sub System

**Requirements:**
- Multiple publishers and subscribers
- Topics/channels
- Message ordering per topic
- Handle slow subscribers

**Key Points:**
- ConcurrentHashMap<Topic, List<Subscriber>>
- Per-topic message queues
- ExecutorService for async delivery
- Bounded queues to handle backpressure

---

## 5. Design a Rate Limiter

**Requirements:**
- N requests per time window
- Support multiple clients
- Fair distribution
- Distributed (bonus)

**Algorithms:**
1. **Token Bucket:** Tokens added at rate R, bucket holds max B
2. **Sliding Window:** Count requests in past window
3. **Leaky Bucket:** Fixed rate output

**Key Points:**
- AtomicInteger/AtomicLong for counters
- ScheduledExecutorService for token replenishment
- ConcurrentHashMap for per-client limits

---

## 6. Design a Cache

**Requirements:**
- Thread-safe get/put
- LRU eviction
- TTL support
- High read throughput

**Key Points:**
- ConcurrentHashMap for storage
- LinkedHashMap (access order) for LRU
- ReadWriteLock for concurrent reads
- ScheduledExecutorService for cleanup

---

## 7. Design a Logger

**Requirements:**
- Thread-safe logging from multiple threads
- Async writing (don't block caller)
- Log rotation
- Guaranteed delivery order

**Key Points:**
- BlockingQueue for log entries
- Single consumer thread writes to file
- Per-source sequence numbers for ordering
- Graceful shutdown (flush queue)

---

## 8. Design an Elevator System

**Requirements:**
- Multiple elevators
- Efficient dispatch
- Handle concurrent requests

**Key Points:**
- State machine per elevator
- BlockingQueue for requests
- Scheduler thread for dispatch decisions
- Lock per elevator for state changes

---

## Interview Discussion Points

For each design question, be prepared to discuss:

1. **Thread Safety:** Which data is shared? What synchronization?
2. **Deadlock:** Can your design deadlock? How to prevent?
3. **Starvation:** Can any request wait forever?
4. **Performance:** Bottlenecks? How to scale?
5. **Failure Handling:** What if a component fails?
6. **Testing:** How to test concurrent correctness?

---

## Common Patterns to Know

1. **Producer-Consumer:** BlockingQueue between stages
2. **Read-Write:** ReadWriteLock for read-heavy data
3. **Work Stealing:** Fork/Join for divide-and-conquer
4. **Object Pool:** Semaphore + Queue for resource limiting
5. **Double-Checked Locking:** Lazy init with volatile
6. **Immutability:** Eliminate synchronization needs
