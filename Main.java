import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.util.concurrent.Future;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static String dirName;
    private static Map<Integer, Future<List<String>>> queryResults = new HashMap<Integer, Future<List<String>>>();
    private static Integer currentQueryId = 1;
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static Integer numThreadsPerQuery = 10;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get the directory name from the user
        System.out.print("Enter the directory containing the files to search: ");
        dirName = scanner.nextLine();

        // Add the files in the directory to the list
        List<String> files = new ArrayList<String>();
        File directory = new File(dirName);
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                files.add(dirName + "/" + file.getName());
            } else {
                System.out.println("Skipping directory: " + file.getName());
            }
        }

        // Query loop
        while (true) {
            System.out.print("\nEnter the type of query (1: New Query) (2: Check Result) (3: Exit): ");
            int queryType = scanner.nextInt();
            scanner.nextLine();

            // 1. New query
            if (queryType == 1) {
                System.out.print("Enter the search string: ");
                String searchString = scanner.nextLine();

                // Run the query
                QueryRunner queryRunner = new QueryRunner(files, searchString, numThreadsPerQuery);
                Future<List<String>> result = executor.submit(queryRunner);
                queryResults.put(currentQueryId, result);

                System.out.println("Query ID: " + currentQueryId);
                currentQueryId++;
            }
            // 2. Check result
            else if (queryType == 2) {
                System.out.print("Enter the query ID: ");
                int queryId = scanner.nextInt();
                scanner.nextLine();
                
                // Return the result
                if (queryResults.containsKey(queryId)) {
                    Future<List<String>> result = queryResults.get(queryId);
                    if (!result.isDone()) {
                        System.out.println("Query not done yet.");
                    } else {
                        try {
                            List<String> results = result.get();
                            for (String r : results) {
                                System.out.println(r);
                            }
                        } catch (Exception e) {
                            System.out.println("Error getting query result: " + e.getMessage());
                        }
                    }
                } else {
                    System.out.println("Query ID not found.");
                }
            }
            // 3. Exit
            else if (queryType == 3) {
                break;
            } 
            else {
                System.out.println("Invalid query type.");
            }
        }

        // Close the resources
        executor.shutdown();
        scanner.close();
    }
}
