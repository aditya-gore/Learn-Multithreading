/**
 * Example 04: Double-Checked Locking Pattern
 * 
 * This example demonstrates:
 * 1. Why lazy initialization needs synchronization
 * 2. The double-checked locking pattern for singletons
 * 3. Why volatile is required for correctness
 * 4. Alternative approaches
 */
public class Example04_DoubleCheckedLocking {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Double-Checked Locking Demo ===\n");

        demonstrateSingletonUsage();
        demonstrateConcurrentAccess();
        showAlternatives();
    }

    private static void demonstrateSingletonUsage() {
        System.out.println("--- Singleton Usage ---");
        
        ExpensiveResource r1 = ExpensiveResource.getInstance();
        ExpensiveResource r2 = ExpensiveResource.getInstance();
        
        System.out.println("r1 == r2: " + (r1 == r2));  // true
        System.out.println("Same instance returned ✓\n");
    }

    private static void demonstrateConcurrentAccess() throws InterruptedException {
        System.out.println("--- Concurrent Singleton Access ---");
        
        // Reset for demo (not normally possible with singletons)
        ExpensiveResource.resetForDemo();
        
        int numThreads = 10;
        Thread[] threads = new Thread[numThreads];
        ExpensiveResource[] results = new ExpensiveResource[numThreads];

        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = ExpensiveResource.getInstance();
            });
        }

        // Start all threads simultaneously
        for (Thread t : threads) {
            t.start();
        }

        // Wait for all to complete
        for (Thread t : threads) {
            t.join();
        }

        // Verify all got the same instance
        boolean allSame = true;
        for (int i = 1; i < numThreads; i++) {
            if (results[i] != results[0]) {
                allSame = false;
                break;
            }
        }

        System.out.println("All threads got same instance: " + allSame);
        System.out.println("Total instances created: " + ExpensiveResource.getInstanceCount());
        System.out.println();
    }

    private static void showAlternatives() {
        System.out.println("--- Alternative Singleton Patterns ---\n");
        
        System.out.println("1. Eager Initialization:");
        System.out.println("   private static final Singleton INSTANCE = new Singleton();");
        System.out.println("   + Thread-safe by JVM class loading");
        System.out.println("   - Created even if never used\n");
        
        System.out.println("2. Initialization-on-demand holder:");
        System.out.println("   private static class Holder {");
        System.out.println("       static final Singleton INSTANCE = new Singleton();");
        System.out.println("   }");
        System.out.println("   + Lazy AND thread-safe");
        System.out.println("   + No synchronization overhead\n");
        
        System.out.println("3. Enum Singleton (RECOMMENDED):");
        System.out.println("   enum Singleton { INSTANCE; }");
        System.out.println("   + Thread-safe by JVM");
        System.out.println("   + Serialization-safe");
        System.out.println("   + Reflection-safe\n");
    }
}

/**
 * Thread-safe lazy singleton using Double-Checked Locking.
 * 
 * KEY POINT: The 'volatile' keyword is REQUIRED!
 * 
 * Without volatile, another thread might see a partially constructed object:
 * 1. Memory allocated for instance
 * 2. Reference assigned to instance (non-null now!)
 * 3. Constructor runs <-- another thread could see instance before this!
 */
class ExpensiveResource {
    // MUST be volatile to prevent instruction reordering!
    private static volatile ExpensiveResource instance;
    
    // For demo purposes only
    private static int instanceCount = 0;

    private ExpensiveResource() {
        instanceCount++;
        System.out.println("ExpensiveResource created (simulating slow init)");
        try {
            Thread.sleep(100);  // Simulate expensive initialization
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static ExpensiveResource getInstance() {
        // First check (no lock) - fast path for already-initialized case
        if (instance == null) {
            // Only synchronize if we think we need to create
            synchronized (ExpensiveResource.class) {
                // Second check (with lock) - ensures only one thread creates
                if (instance == null) {
                    instance = new ExpensiveResource();
                }
            }
        }
        return instance;
    }

    // Demo helpers
    public static void resetForDemo() {
        instance = null;
        instanceCount = 0;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }
}

/**
 * Alternative: Initialization-on-demand holder idiom
 * This is often preferred over double-checked locking.
 */
class HolderSingleton {
    private HolderSingleton() {}

    // Inner class is not loaded until getInstance() is called
    private static class Holder {
        static final HolderSingleton INSTANCE = new HolderSingleton();
    }

    public static HolderSingleton getInstance() {
        return Holder.INSTANCE;  // Triggers class loading of Holder
    }
}

/**
 * Alternative: Enum Singleton (Best for most cases)
 */
enum EnumSingleton {
    INSTANCE;

    public void doSomething() {
        System.out.println("Doing something...");
    }
}

/*
 * EXPECTED OUTPUT:
 * 
 * === Double-Checked Locking Demo ===
 * 
 * --- Singleton Usage ---
 * ExpensiveResource created (simulating slow init)
 * r1 == r2: true
 * Same instance returned ✓
 * 
 * --- Concurrent Singleton Access ---
 * ExpensiveResource created (simulating slow init)
 * All threads got same instance: true
 * Total instances created: 1
 * 
 * --- Alternative Singleton Patterns ---
 * ...
 * 
 * 
 * WHY DOUBLE-CHECKED LOCKING?
 * 
 * 1. First check without lock: Avoid synchronization overhead after initialization
 * 2. Synchronized block: Ensure only one thread creates the instance
 * 3. Second check: Another thread might have created it while we waited for lock
 * 
 * 
 * WHY volatile IS REQUIRED:
 * 
 * Without volatile, the following can happen due to instruction reordering:
 * 
 * Thread 1:                          Thread 2:
 * ─────────                          ─────────
 * Allocate memory                    
 * Assign reference to instance       
 *   (instance is now non-null!)      if (instance == null) // FALSE!
 *                                    return instance; // PARTIALLY CONSTRUCTED!
 * Run constructor                    
 * 
 * Thread 2 returns an object whose constructor hasn't finished!
 * 
 * volatile prevents this reordering and ensures the constructor
 * completes before the reference is visible to other threads.
 */
