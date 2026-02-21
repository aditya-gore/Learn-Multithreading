# Module 7 Quiz: CompletableFuture

## Questions

### Q1: thenApply vs thenCompose
When should you use `thenCompose` instead of `thenApply`?

A) When transforming to a different type  
B) When the transformation returns a CompletableFuture  
C) When you need error handling  
D) When running in a different thread  

<details>
<summary>Answer</summary>

**B) When the transformation returns a CompletableFuture**

thenCompose flattens nested futures. If your function returns CompletableFuture, use thenCompose to avoid CompletableFuture<CompletableFuture<T>>.
</details>

---

### Q2: exceptionally
What does `exceptionally()` return if no exception occurred?

A) null  
B) The original result unchanged  
C) A completed future with null  
D) Throws an exception  

<details>
<summary>Answer</summary>

**B) The original result unchanged**

exceptionally() only applies its function when there's an exception. On success, it passes through the original result.
</details>

---

### Q3: allOf Return Type
What is the return type of `CompletableFuture.allOf()`?

A) CompletableFuture<List<Object>>  
B) CompletableFuture<Object[]>  
C) CompletableFuture<Void>  
D) List<CompletableFuture<?>>  

<details>
<summary>Answer</summary>

**C) CompletableFuture<Void>**

allOf() returns CompletableFuture<Void>. To get individual results, you must access the original futures after allOf completes.
</details>

---

### Q4: handle vs exceptionally
What's the difference between `handle()` and `exceptionally()`?

A) No difference  
B) handle() gets both result and exception, exceptionally() only gets exception  
C) exceptionally() is async, handle() is sync  
D) handle() can only be used once  

<details>
<summary>Answer</summary>

**B) handle() gets both result and exception, exceptionally() only gets exception**

handle(result, ex) receives both (one will be null). exceptionally(ex) only receives exception on failure.
</details>

---

### Q5: Async Execution
Which statement about `thenApplyAsync()` is TRUE?

A) It always runs on a new thread  
B) It always uses ForkJoinPool.commonPool() unless specified  
C) It's faster than thenApply()  
D) It blocks the calling thread  

<details>
<summary>Answer</summary>

**B) It always uses ForkJoinPool.commonPool() unless specified**

thenApplyAsync() executes in the common pool by default. You can specify a custom executor as a second parameter.
</details>

---

### Q6: join() vs get()
What's the difference between `join()` and `get()`?

A) join() is async, get() is sync  
B) join() throws unchecked exception, get() throws checked exception  
C) get() is deprecated  
D) No practical difference  

<details>
<summary>Answer</summary>

**B) join() throws unchecked exception, get() throws checked exception**

join() throws CompletionException (unchecked). get() throws ExecutionException and InterruptedException (checked), requiring try-catch.
</details>

---

## Score Yourself

- **5-6 correct:** Excellent! Ready for Module 8
- **3-4 correct:** Good, review error handling patterns
- **Below 3:** Re-study the chaining operations

---

**Next:** [Module 8 - Concurrent Collections](../08-concurrent-collections/README.md)
