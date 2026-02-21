/**
 * Solution for Exercise 01: Web Crawler
 */

import java.util.concurrent.*;
import java.util.*;

public class Exercise01_Solution {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Web Crawler Demo ===\n");

        WebCrawler crawler = new WebCrawler(4, 2, 20);
        Set<String> pages = crawler.crawl("https://example.com");

        System.out.println("\n--- Crawl Results ---");
        System.out.println("Total pages found: " + pages.size());
        pages.forEach(url -> System.out.println("  " + url));
    }
}

class WebCrawler {
    private final ExecutorService executor;
    private final int maxDepth;
    private final int maxPages;
    private final Set<String> visited = ConcurrentHashMap.newKeySet();
    private final Random random = new Random();

    public WebCrawler(int threadCount, int maxDepth, int maxPages) {
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.maxDepth = maxDepth;
        this.maxPages = maxPages;
    }

    public Set<String> crawl(String startUrl) {
        try {
            crawlRecursive(startUrl, 0);
            
            // Wait for all tasks to complete
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        }
        return visited;
    }

    private void crawlRecursive(String url, int depth) {
        // Check limits
        if (depth > maxDepth || visited.size() >= maxPages) {
            return;
        }

        // Try to add URL (returns false if already present)
        if (!visited.add(url)) {
            return;  // Already visited
        }

        // Submit task to process this URL
        executor.execute(() -> {
            System.out.println("[" + Thread.currentThread().getName() + 
                "] Crawling: " + url + " (depth " + depth + ")");
            
            // Simulate network delay
            sleep(100);
            
            // Get links from this page
            List<String> links = fetchLinks(url);
            
            // Recursively crawl each link
            for (String link : links) {
                if (visited.size() < maxPages) {
                    crawlRecursive(link, depth + 1);
                }
            }
        });
    }

    private List<String> fetchLinks(String url) {
        // Simulate fetching links from a page
        List<String> links = new ArrayList<>();
        int numLinks = random.nextInt(5) + 1;
        
        for (int i = 0; i < numLinks; i++) {
            String newUrl = url + "/page" + random.nextInt(100);
            links.add(newUrl);
        }
        return links;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

/*
 * KEY INSIGHTS:
 * 
 * 1. CONCURRENT SET:
 *    ConcurrentHashMap.newKeySet() provides a thread-safe Set.
 *    visited.add() returns false if already present (atomic check-and-add).
 * 
 * 2. BOUNDED CRAWLING:
 *    maxDepth prevents infinite recursion.
 *    maxPages prevents crawling the entire internet.
 * 
 * 3. THREAD POOL SIZE:
 *    For I/O-bound tasks like web crawling, you can use more threads
 *    than CPU cores because threads spend time waiting for I/O.
 * 
 * 4. GRACEFUL SHUTDOWN:
 *    shutdown() + awaitTermination() ensures all tasks complete
 *    before returning results.
 */
