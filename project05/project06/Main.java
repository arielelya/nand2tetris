import java.io.IOException;
import java.io.File;

public class Main {


    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java org.example.Main <inputFileName.asm>");
        }
        File input = new File(args[0]);
        if (input.isDirectory()) {
            File[] asmFiles = input.listFiles((dir,name)-> name.endsWith(".asm"));
            if (asmFiles != null) {
                for (File asm : asmFiles) {
                    HackAssembler hackAssembler = new HackAssembler((asm));
                    hackAssembler.parse();
                }
            }
        } else if (input.isFile() && args[0].endsWith(".asm")) {
            HackAssembler hackAssembler = new HackAssembler((input));
            hackAssembler.parse();
        } else {
            System.err.println("Invalid input. Please provide a valid .asm file or folder.");
        }
    }
}