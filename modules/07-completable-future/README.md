# Module 7: CompletableFuture

## Learning Objectives
By the end of this module, you will understand:
- Async programming with CompletableFuture
- Chaining operations: thenApply, thenCompose, thenCombine
- Error handling: exceptionally, handle, whenComplete
- Combining multiple futures: allOf, anyOf
- Best practices and common patterns

---

## 1. Why CompletableFuture?

Traditional Future limitations:
- Can't manually complete
- No callback support
- Can't chain operations
- Poor error handling

CompletableFuture solves all of these!

```
┌─────────────────────────────────────────────────────────────────┐
│                   CompletableFuture                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Future                    CompletableFuture                    │
│   ──────                    ─────────────────                    │
│   get() blocks              get() + callbacks                    │
│   No chaining               Fluent API chaining                  │
│   No manual completion      complete(), completeExceptionally()  │
│   Basic exceptions          Rich error handling                  │
│                                                                  │
│   CompletableFuture<String> future = CompletableFuture          │
│       .supplyAsync(() -> fetchData())                           │
│       .thenApply(data -> process(data))                         │
│       .thenApply(result -> format(result))                      │
│       .exceptionally(ex -> "default");                          │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Creating CompletableFutures

### From async computation
```java
// Run task async, return result
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return fetchData();
});

// Run task async, no result
CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
    doSomething();
});
```

### From known value
```java
CompletableFuture<String> completed = CompletableFuture.completedFuture("result");
```

### Manual completion
```java
CompletableFuture<String> future = new CompletableFuture<>();
// Later...
future.complete("result");
// Or
future.completeExceptionally(new RuntimeException("error"));
```

---

## 3. Transformation Operations

### thenApply - Transform result
```java
CompletableFuture<Integer> future = CompletableFuture
    .supplyAsync(() -> "hello")
    .thenApply(s -> s.length());  // String -> Integer
```

### thenAccept - Consume result
```java
CompletableFuture<Void> future = CompletableFuture
    .supplyAsync(() -> "hello")
    .thenAccept(s -> System.out.println(s));  // Consumes, returns void
```

### thenRun - Run after completion
```java
CompletableFuture<Void> future = CompletableFuture
    .supplyAsync(() -> "hello")
    .thenRun(() -> System.out.println("Done!"));  // Ignores result
```

---

## 4. Composition Operations

### thenCompose - Flatten nested futures (flatMap)
```java
// Without thenCompose: CompletableFuture<CompletableFuture<String>>
// With thenCompose: CompletableFuture<String>

CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> getUserId())
    .thenCompose(userId -> fetchUserDetails(userId));  // Returns CF
```

### thenCombine - Combine two futures
```java
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");

CompletableFuture<String> combined = future1.thenCombine(future2, 
    (s1, s2) -> s1 + " " + s2);  // "Hello World"
```

---

## 5. Error Handling

### exceptionally - Handle exceptions
```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> {
        if (true) throw new RuntimeException("error");
        return "success";
    })
    .exceptionally(ex -> "default value");
```

### handle - Handle result OR exception
```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> fetchData())
    .handle((result, ex) -> {
        if (ex != null) {
            return "error: " + ex.getMessage();
        }
        return result;
    });
```

### whenComplete - Side effect on completion
```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> fetchData())
    .whenComplete((result, ex) -> {
        if (ex != null) {
            log.error("Failed", ex);
        } else {
            log.info("Success: " + result);
        }
    });  // Returns original result, not transformed
```

---

## 6. Combining Multiple Futures

### allOf - Wait for all
```java
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "A");
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "B");
CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "C");

CompletableFuture<Void> allDone = CompletableFuture.allOf(f1, f2, f3);
allDone.join();  // Wait for all

// Get results
String combined = f1.join() + f2.join() + f3.join();
```

### anyOf - First to complete
```java
CompletableFuture<Object> first = CompletableFuture.anyOf(f1, f2, f3);
Object result = first.join();  // Result of whichever finished first
```

---

## 7. Async vs Non-Async Methods

```
┌──────────────────┬───────────────────┬───────────────────────┐
│  Method          │ Execution Thread  │ When to Use           │
├──────────────────┼───────────────────┼───────────────────────┤
│ thenApply()      │ Same thread or    │ Quick transformations │
│                  │ completing thread │                       │
├──────────────────┼───────────────────┼───────────────────────┤
│ thenApplyAsync() │ ForkJoinPool or   │ Slow operations or    │
│                  │ specified executor│ blocking calls        │
└──────────────────┴───────────────────┴───────────────────────┘
```

```java
// Async version with custom executor
ExecutorService executor = Executors.newFixedThreadPool(4);

CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> fetchData(), executor)
    .thenApplyAsync(data -> process(data), executor);
```

---

## 8. Common Patterns

### Pattern 1: Sequential async calls
```java
CompletableFuture<OrderDetails> order = CompletableFuture
    .supplyAsync(() -> getUser(userId))
    .thenCompose(user -> getOrders(user))
    .thenCompose(orders -> getOrderDetails(orders.get(0)));
```

### Pattern 2: Parallel async calls
```java
CompletableFuture<User> userFuture = fetchUserAsync(userId);
CompletableFuture<List<Order>> ordersFuture = fetchOrdersAsync(userId);
CompletableFuture<List<Recommendation>> recsFuture = fetchRecommendationsAsync(userId);

CompletableFuture<UserDashboard> dashboard = CompletableFuture
    .allOf(userFuture, ordersFuture, recsFuture)
    .thenApply(v -> new UserDashboard(
        userFuture.join(),
        ordersFuture.join(),
        recsFuture.join()
    ));
```

### Pattern 3: Timeout
```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> slowOperation())
    .orTimeout(5, TimeUnit.SECONDS)  // Java 9+
    .exceptionally(ex -> "timeout fallback");
```

### Pattern 4: Default on timeout
```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> slowOperation())
    .completeOnTimeout("default", 5, TimeUnit.SECONDS);  // Java 9+
```

---

## 9. Interview Key Points

1. **thenApply vs thenCompose?**
   - thenApply: Transforms result (map)
   - thenCompose: Flattens nested futures (flatMap)

2. **exceptionally vs handle?**
   - exceptionally: Only handles exceptions
   - handle: Handles both success and failure

3. **Async vs non-async methods?**
   - Non-async: May run in completing thread
   - Async: Always runs in separate thread (pool)

4. **allOf vs anyOf?**
   - allOf: Waits for ALL futures, returns Void
   - anyOf: Returns on FIRST completion

5. **How to get exception from failed future?**
   - Use handle() or whenComplete() to access exception
   - join() throws CompletionException wrapping original

---

## Examples

See the [examples/](examples/) folder for code demonstrations.

---

## Quiz

Test yourself: [quiz.md](quiz.md)

---

**Next Module:** [Module 8 - Concurrent Collections](../08-concurrent-collections/README.md)
