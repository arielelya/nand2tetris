import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java org.example.Main <inputFileName.asm>");
            System.exit(1);
        }
        File fileInput  = new File(args[0]);
        JackAnalyzer jackAnalyzer = new JackAnalyzer(fileInput);
    }
}