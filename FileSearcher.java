import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileSearcher {
    private String filename;
    private String searchString;

    public FileSearcher(String filename, String searchString) {
        this.filename = filename;
        this.searchString = searchString;
    }

    public List<String> search() {
        List<String> results = new ArrayList<String>();
        int lineNumber = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.contains(searchString)) {
                    results.add("{" + filename + ", " + lineNumber + "}: ");
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return results;
    }
}
