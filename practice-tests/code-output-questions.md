# Code Output Questions

Predict the output of each code snippet.

---

### Q1
```java
Thread t = new Thread(() -> {
    System.out.print("A");
});
t.start();
t.join();
System.out.print("B");
```

<details><summary>Answer</summary>**AB** - join() ensures A prints before B</details>

---

### Q2
```java
Thread t = new Thread(() -> {
    System.out.print("A");
});
t.start();
System.out.print("B");
```

<details><summary>Answer</summary>**AB or BA** - race condition, either order possible</details>

---

### Q3
```java
volatile boolean flag = false;
// Thread 1:
while (!flag) { }
System.out.println("Done");

// Thread 2 (after 1 second):
flag = true;
```

<details><summary>Answer</summary>**Done** (eventually) - volatile ensures visibility</details>

---

### Q4
```java
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();
counter.getAndIncrement();
System.out.println(counter.get());
```

<details><summary>Answer</summary>**2** - both operations increment</details>

---

### Q5
```java
CompletableFuture.supplyAsync(() -> "Hello")
    .thenApply(s -> s + " World")
    .thenAccept(System.out::println);
Thread.sleep(1000);
```

<details><summary>Answer</summary>**Hello World**</details>

---

### Q6
```java
ExecutorService e = Executors.newFixedThreadPool(1);
e.submit(() -> { throw new RuntimeException(); });
System.out.println("After submit");
```

<details><summary>Answer</summary>**After submit** - exception is captured in Future, not thrown</details>

---

### Q7
```java
Semaphore sem = new Semaphore(2);
sem.acquire();
sem.acquire();
System.out.println(sem.availablePermits());
```

<details><summary>Answer</summary>**0**</details>

---

### Q8
```java
ReentrantLock lock = new ReentrantLock();
System.out.println(lock.tryLock());
System.out.println(lock.tryLock());
```

<details><summary>Answer</summary>**true true** - reentrant, same thread can acquire multiple times</details>

---

### Q9
```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("a", 1);
map.computeIfAbsent("a", k -> 100);
System.out.println(map.get("a"));
```

<details><summary>Answer</summary>**1** - key exists, computation not performed</details>

---

### Q10
```java
ThreadLocal<Integer> tl = ThreadLocal.withInitial(() -> 0);
tl.set(5);
new Thread(() -> System.out.println(tl.get())).start();
```

<details><summary>Answer</summary>**0** - new thread gets its own initial value</details>
