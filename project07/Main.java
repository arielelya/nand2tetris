import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static void translate(File file, CodeWriter codeW) throws IOException {
        Parser parser = new Parser(file);
        while (parser.hasMoreLines()) {
            if (parser.commandType().equals(Parser.CommandType.C_POP) || parser.commandType().equals(Parser.CommandType.C_PUSH)) {
                codeW.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
            }
            else {
                codeW.writeArithmetic(parser.currLine);
            }
            parser.advance();
        }
    }

    private static void listVmFiles(File dir, List<File> vmFiles) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        listVmFiles(file, vmFiles);
                    }
                    else if (file.getName().endsWith(".vm")) {
                        vmFiles.add(file);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java org.example.Main <inputFileName.asm>");
            System.exit(1);
        }

        File input = new File(args[0]);

        if (input.isDirectory()) {
            List<File> vmFiles = new ArrayList<>();
            listVmFiles(input, vmFiles);

            if (vmFiles != null) {
                File outputFile = new File(input, input.getName() + ".asm");
                CodeWriter codeW = new CodeWriter(outputFile);
                for (File vm : vmFiles) {
                    codeW.writeToBuff("//" + vm.getName());
                    Main.translate(vm, codeW);
                }
                codeW.close();
            }
        }

        else if (input.isFile() && args[0].endsWith(".vm")) {
            CodeWriter codeW = new CodeWriter(input);
            Main.translate(input, codeW);
            codeW.close();
        } else {
            System.err.println("Invalid input. Please provide a valid .vm file or folder.");
            System.exit(1);
        }
    }
}