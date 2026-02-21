# Module 9: Classic Concurrency Problems

## Learning Objectives
These classic problems appear frequently in interviews. Master them to demonstrate your understanding of concurrency patterns.

---

## Problems Covered

1. **Producer-Consumer** - Multiple variants
2. **Dining Philosophers** - Deadlock avoidance
3. **Reader-Writer Problem** - Concurrent access patterns
4. **Print Odd-Even** - Thread coordination
5. **Print Numbers Sequentially** - Multi-thread ordering
6. **H2O Molecule Problem** - Barrier synchronization
7. **Bounded Blocking Queue** - From scratch

---

## Problem 1: Print Odd-Even Using Two Threads

**Challenge:** Print numbers 1-20 with two threads alternating: Thread1 prints odd, Thread2 prints even.

```
Expected: 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20
          ^   ^   ^   ^    ^     ^     ^     ^     ^     ^  (Thread 1)
            ^   ^   ^   ^     ^     ^     ^     ^     ^     (Thread 2)
```

See: [examples/Problem01_OddEven.java](examples/Problem01_OddEven.java)

---

## Problem 2: Print Numbers 1-100 Using Three Threads

**Challenge:** Three threads print numbers 1-100 in sequence:
- Thread1: 1, 4, 7, 10...
- Thread2: 2, 5, 8, 11...
- Thread3: 3, 6, 9, 12...

See: [examples/Problem02_ThreeThreads.java](examples/Problem02_ThreeThreads.java)

---

## Problem 3: Dining Philosophers

**The Problem:**
5 philosophers sit at a round table. Each needs two forks to eat.
If each picks up their left fork first, deadlock occurs!

```
      P1
    F    F
  P5      P2
    F    F
  P4  F  P3
```

**Solutions:**
1. Lock ordering (always pick up lower-numbered fork first)
2. Resource hierarchy
3. Arbitrator (waiter controls fork access)

See: [examples/Problem03_DiningPhilosophers.java](examples/Problem03_DiningPhilosophers.java)

---

## Problem 4: Reader-Writer Problem

**Challenge:** Multiple readers can read simultaneously, but writers need exclusive access.

**Variants:**
- Reader-preference: Readers never wait if no writer active
- Writer-preference: New readers wait if writer is waiting
- Fair: FIFO ordering

See: [examples/Problem04_ReaderWriter.java](examples/Problem04_ReaderWriter.java)

---

## Problem 5: H2O Molecule Problem (LeetCode 1117)

**Challenge:** Given threads calling H() and O(), form water molecules H2O.
- Each molecule needs exactly 2 hydrogen and 1 oxygen
- H and O must be called in batches that form complete molecules

See: [examples/Problem05_H2O.java](examples/Problem05_H2O.java)

---

## Problem 6: Custom Blocking Queue

**Challenge:** Implement a thread-safe bounded queue from scratch with:
- put() - blocks when full
- take() - blocks when empty
- size(), isEmpty(), isFull()

See: [examples/Problem06_BlockingQueue.java](examples/Problem06_BlockingQueue.java)

---

## Problem 7: Print FizzBuzz with Four Threads

**Challenge:** Four threads print numbers 1-n:
- Thread1: prints "fizz" for multiples of 3
- Thread2: prints "buzz" for multiples of 5
- Thread3: prints "fizzbuzz" for multiples of 15
- Thread4: prints the number otherwise

See: [examples/Problem07_FizzBuzz.java](examples/Problem07_FizzBuzz.java)

---

## Examples Summary

All problem implementations: [Problem01_OddEven.java](examples/Problem01_OddEven.java), [Problem02_ThreeThreads.java](examples/Problem02_ThreeThreads.java), [Problem03_DiningPhilosophers.java](examples/Problem03_DiningPhilosophers.java), [Problem04_ReaderWriter.java](examples/Problem04_ReaderWriter.java), [Problem05_H2O.java](examples/Problem05_H2O.java), [Problem06_BlockingQueue.java](examples/Problem06_BlockingQueue.java), [Problem07_FizzBuzz.java](examples/Problem07_FizzBuzz.java).

---

## Exercises

1. [Exercise01_NThreadSequentialPrinter.java](exercises/Exercise01_NThreadSequentialPrinter.java) - N threads print 1 to M in order
2. [Exercise02_BarrierRendezvous.java](exercises/Exercise02_BarrierRendezvous.java) - CyclicBarrier rendezvous (3 threads meet then proceed)

Solutions are in [exercises/solutions/](exercises/solutions/).

---

## Interview Tips

1. **Start Simple:** Begin with the basic solution, then optimize
2. **Identify the Pattern:** Most problems use wait/notify, semaphores, or locks
3. **Think About Edge Cases:** Empty/full conditions, interruption handling
4. **Discuss Deadlock:** How your solution prevents deadlock
5. **Know Multiple Solutions:** Each problem usually has several valid approaches

---

## Next Module

[Module 10 - Advanced Patterns](../10-advanced-patterns/README.md)
