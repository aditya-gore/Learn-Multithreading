# Coding Interview Questions

## Frequently Asked

### 1. Implement a Thread-Safe Singleton
```java
// Best: Enum singleton
public enum Singleton {
    INSTANCE;
    public void doSomething() { }
}

// OR: Holder pattern
public class Singleton {
    private Singleton() {}
    private static class Holder {
        static final Singleton INSTANCE = new Singleton();
    }
    public static Singleton getInstance() {
        return Holder.INSTANCE;
    }
}
```

---

### 2. Print Numbers 1-100 Using Two Threads (Odd/Even)
```java
class OddEvenPrinter {
    private int number = 1;
    private final int max = 100;
    
    synchronized void printOdd() throws InterruptedException {
        while (number <= max) {
            while (number % 2 == 0) wait();
            if (number <= max) System.out.println(number++);
            notify();
        }
    }
    
    synchronized void printEven() throws InterruptedException {
        while (number <= max) {
            while (number % 2 == 1) wait();
            if (number <= max) System.out.println(number++);
            notify();
        }
    }
}
```

---

### 3. Implement a Bounded Blocking Queue
```java
class BlockingQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;
    
    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }
    
    public synchronized void put(T item) throws InterruptedException {
        while (queue.size() == capacity) wait();
        queue.add(item);
        notifyAll();
    }
    
    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) wait();
        T item = queue.poll();
        notifyAll();
        return item;
    }
}
```

---

### 4. Design a Rate Limiter
```java
class RateLimiter {
    private final Semaphore semaphore;
    private final ScheduledExecutorService scheduler;
    
    public RateLimiter(int permitsPerSecond) {
        this.semaphore = new Semaphore(permitsPerSecond);
        this.scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            int toRelease = permitsPerSecond - semaphore.availablePermits();
            if (toRelease > 0) semaphore.release(toRelease);
        }, 1, 1, TimeUnit.SECONDS);
    }
    
    public void acquire() throws InterruptedException {
        semaphore.acquire();
    }
}
```

---

### 5. Producer-Consumer with Multiple Threads
```java
class ProducerConsumer {
    private final BlockingQueue<Integer> queue;
    
    public ProducerConsumer(int capacity) {
        this.queue = new ArrayBlockingQueue<>(capacity);
    }
    
    public void produce(int item) throws InterruptedException {
        queue.put(item);
    }
    
    public int consume() throws InterruptedException {
        return queue.take();
    }
}
```

---

### 6. Implement a Connection Pool
```java
class ConnectionPool {
    private final Semaphore semaphore;
    private final BlockingQueue<Connection> pool;
    
    public ConnectionPool(int size) {
        this.semaphore = new Semaphore(size);
        this.pool = new LinkedBlockingQueue<>();
        for (int i = 0; i < size; i++) {
            pool.add(createConnection());
        }
    }
    
    public Connection acquire() throws InterruptedException {
        semaphore.acquire();
        return pool.take();
    }
    
    public void release(Connection conn) {
        pool.offer(conn);
        semaphore.release();
    }
}
```

---

### 7. Implement a Read-Write Lock
```java
class SimpleReadWriteLock {
    private int readers = 0;
    private boolean writing = false;
    
    public synchronized void lockRead() throws InterruptedException {
        while (writing) wait();
        readers++;
    }
    
    public synchronized void unlockRead() {
        readers--;
        notifyAll();
    }
    
    public synchronized void lockWrite() throws InterruptedException {
        while (writing || readers > 0) wait();
        writing = true;
    }
    
    public synchronized void unlockWrite() {
        writing = false;
        notifyAll();
    }
}
```

---

### 8. Dining Philosophers (Deadlock-Free)
```java
class DiningPhilosophers {
    private final Lock[] forks;
    
    public void eat(int philosopher) {
        int left = philosopher;
        int right = (philosopher + 1) % 5;
        
        // Lock ordering to prevent deadlock
        Lock first = forks[Math.min(left, right)];
        Lock second = forks[Math.max(left, right)];
        
        first.lock();
        try {
            second.lock();
            try {
                // Eat
            } finally {
                second.unlock();
            }
        } finally {
            first.unlock();
        }
    }
}
```

---

### 9. Design a Task Scheduler
```java
class TaskScheduler {
    private final ScheduledExecutorService executor;
    
    public TaskScheduler(int threads) {
        this.executor = Executors.newScheduledThreadPool(threads);
    }
    
    public void scheduleOnce(Runnable task, long delay, TimeUnit unit) {
        executor.schedule(task, delay, unit);
    }
    
    public void scheduleRepeating(Runnable task, long period, TimeUnit unit) {
        executor.scheduleAtFixedRate(task, 0, period, unit);
    }
}
```

---

### 10. Implement H2O Molecule (LeetCode 1117)
```java
class H2O {
    private Semaphore hydrogen = new Semaphore(2);
    private Semaphore oxygen = new Semaphore(0);
    
    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        hydrogen.acquire();
        releaseHydrogen.run();
        oxygen.release();
    }
    
    public void oxygen(Runnable releaseOxygen) throws InterruptedException {
        oxygen.acquire(2);
        releaseOxygen.run();
        hydrogen.release(2);
    }
}
```

---

## Tips for Coding Interviews

1. **Start with requirements** - clarify inputs, outputs, constraints
2. **Discuss approaches** - show multiple solutions before coding
3. **Think about thread safety** - identify shared state
4. **Handle edge cases** - empty queue, timeout, interruption
5. **Clean shutdown** - don't forget graceful termination
6. **Test mentally** - walk through with example scenarios
