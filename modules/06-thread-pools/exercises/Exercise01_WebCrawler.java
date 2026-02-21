/**
 * Exercise 01: Simple Web Crawler with Thread Pool
 * 
 * TASK:
 * Implement a concurrent web crawler that:
 * - Uses a thread pool to process URLs concurrently
 * - Maintains a set of visited URLs to avoid duplicates
 * - Limits crawl depth and total pages
 * - Returns list of all discovered URLs
 * 
 * REQUIREMENTS:
 * 1. Use ExecutorService for concurrent URL processing
 * 2. Thread-safe visited set (ConcurrentHashMap or synchronized)
 * 3. Respect max depth and max pages limits
 * 4. Graceful shutdown when complete
 * 
 * Note: For this exercise, simulate URL fetching - don't make real HTTP calls.
 */

import java.util.concurrent.*;
import java.util.*;

public class Exercise01_WebCrawler {

    public static void main(String[] args) {
        // TODO: Implement WebCrawler class
        
        // WebCrawler crawler = new WebCrawler(4, 2, 50);  // 4 threads, depth 2, max 50 pages
        // Set<String> pages = crawler.crawl("https://example.com");
        // System.out.println("Found " + pages.size() + " pages");

        System.out.println("Implement WebCrawler and uncomment the test!");
    }
}

// TODO: Implement this class
// class WebCrawler {
//     private final ExecutorService executor;
//     private final int maxDepth;
//     private final int maxPages;
//     private final Set<String> visited = ConcurrentHashMap.newKeySet();
//     
//     public WebCrawler(int threadCount, int maxDepth, int maxPages) { }
//     
//     public Set<String> crawl(String startUrl) { }
//     
//     // Simulate fetching links from a page
//     private List<String> fetchLinks(String url) {
//         // Return simulated links
//     }
// }

/*
 * LEARNING GOALS:
 * - Use thread pool for I/O-bound concurrent tasks
 * - Handle concurrent data structures
 * - Implement graceful shutdown
 * 
 * When done, compare with: solutions/Exercise01_Solution.java
 */
