import java.io.File;
import java.io.IOException;

public class JackAnalyzer {
    public JackAnalyzer(File input) throws IOException {
        if (input.isDirectory()) {
            File[] jackFiles = input.listFiles((dir,name)-> name.endsWith(".jack"));
            if (jackFiles != null) {
                for (File jack : jackFiles) {
                    CompilationEngine compile = new CompilationEngine(jack);
                    compile.compileClass();
                }
            }
        } else if (input.isFile() && input.getName().endsWith(".jack")) {
            CompilationEngine compile = new CompilationEngine(input);
            compile.compileClass();

        } else {
            System.err.println("Invalid input. Please provide a valid .asm file or folder.");
        }
    }
}
