/**
 * Concurrent Collections Examples
 */

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Example01_ConcurrentCollections {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Concurrent Collections Demo ===\n");

        // ConcurrentHashMap atomic operations
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        
        // Atomic increment pattern
        map.compute("counter", (k, v) -> (v == null) ? 1 : v + 1);
        map.compute("counter", (k, v) -> (v == null) ? 1 : v + 1);
        System.out.println("Counter: " + map.get("counter"));  // 2

        // computeIfAbsent - lazy initialization
        map.computeIfAbsent("expensive", k -> {
            System.out.println("Computing value for: " + k);
            return 100;
        });
        
        // Won't compute again
        map.computeIfAbsent("expensive", k -> {
            System.out.println("This won't print");
            return 200;
        });
        System.out.println("Expensive: " + map.get("expensive"));  // 100
        System.out.println();

        // BlockingQueue producer-consumer
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(5);
        
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    queue.put("Item-" + i);
                    System.out.println("Produced: Item-" + i);
                }
            } catch (InterruptedException e) {}
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    String item = queue.take();
                    System.out.println("Consumed: " + item);
                    Thread.sleep(100);  // Slow consumer
                }
            } catch (InterruptedException e) {}
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println();

        // CopyOnWriteArrayList - safe iteration
        CopyOnWriteArrayList<String> cowList = new CopyOnWriteArrayList<>();
        cowList.add("A");
        cowList.add("B");

        // Iteration uses snapshot - modifications don't affect ongoing iteration
        for (String s : cowList) {
            System.out.println("Iterating: " + s);
            cowList.add("C");  // Won't be seen in this iteration
        }
        System.out.println("Final list: " + cowList);  // [A, B, C, C]
    }
}
