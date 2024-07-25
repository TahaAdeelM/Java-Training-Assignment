import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class QueryRunner implements Callable<List<String>> {
    private List<String> files;
    private String searchString;
    private List<String> results = new ArrayList<String>();
    private Integer numOfThreads;
    private ExecutorService executor;

    // Wrapper class to search a file and add the results to the results list in a synchronized manner
    private class FileSearcherCallable implements Callable<Void> {
        private FileSearcher fileSearcher;

        FileSearcherCallable(String file, String searchString) {
            this.fileSearcher = new FileSearcher(file, searchString);
        }

        public Void call() {
            List<String> fileResults = fileSearcher.search();
            synchronized (results) {
                results.addAll(fileResults);
            }
            return null;
        }
    }

    public QueryRunner(List<String> files, String searchString, Integer numOfThreads) {
        this.files = files;
        this.searchString = searchString;
        this.numOfThreads = numOfThreads;
    }

    // Search each file in a new thread
    public List<String> call() {
        executor = Executors.newFixedThreadPool(numOfThreads);
        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
        for (String file : files) {
            tasks.add(new FileSearcherCallable(file, searchString));
        }

        try {
            // Execute all tasks and wait for them to finish
            List<Future<Void>> futures = executor.invokeAll(tasks);
            for (Future<Void> future : futures) {
                future.get();
            }
        } catch (Exception e) {
            System.out.println("Error executing query: " + e.getMessage());
        } finally {
            executor.shutdown();
        }

        return this.results;
    }
}
