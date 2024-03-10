import java.io.*;
public class CodeWriter {
    final BufferedReader buffR;
    final File fileOut;
    final BufferedWriter buffW;
    final Parser parser;
    private static String fileName;
    static int funcInx = 0;

    public CodeWriter(File fileInput) throws IOException {
        if (!fileInput.exists()) {
            fileInput.createNewFile();
            fileInput = new File(fileInput.getParent() + "/" + fileInput.getName().replace("vm", "asm"));
        }
        this.fileOut = new File(fileInput.getParent(), fileInput.getName().replace("vm", "asm"));
        this.buffR = new BufferedReader(new FileReader(fileInput));
        this.buffW = new BufferedWriter(new FileWriter(fileOut.getAbsoluteFile()));
        this.parser = new Parser(fileInput);
        this.fileName = fileInput.getName().split("\\.")[0];
        initSP();
    }

    public void setFileName (String fileName) {
        this.fileName = fileName;
    }
    private void initSP () {
        // SP = 256
        writeToBuff("// initializing SP");
        writeToBuff("@256");
        writeToBuff("D=A");
        writeToBuff("@SP");
        writeToBuff("M=D");
        // call Sys.init
        if (Main.sys) {
            writeCall("Sys.init", 0);
        }
    }
    public void writeArithmetic(String command) {
        writeToBuff("@SP");
        writeToBuff("M=M-1");
        writeToBuff("A=M");
        writeToBuff("D=M");

        switch (command) {
            case "add", "sub", "and", "or" -> {
                writeToBuff("@SP");
                writeToBuff("M=M-1");
                writeToBuff("A=M");
                switch (command) {
                    case "add" -> writeToBuff("M=M+D");
                    case "sub" -> writeToBuff("M=M-D");
                    case "and" -> writeToBuff("M=M&D");
                    default -> writeToBuff("M=M|D");
                }
            }
            case "eq", "lt", "gt" -> {
                writeToBuff("@SP");
                writeToBuff("M=M-1");
                writeToBuff("A=M");
                writeToBuff("D=M-D");
                writeToBuff("M=-1");

                if (command.equals("eq")) {
                    writeToBuff("@EQ");
                    writeToBuff("D;JEQ");
                    writeToBuff("@SP");
                    writeToBuff("A=M");
                    writeToBuff("M=0");
                    writeToBuff("(EQ)");
                } else if (command.equals("gt")) {
                    writeToBuff("@GT");
                    writeToBuff("D;JLT");
                    writeToBuff("@SP");
                    writeToBuff("A=M");
                    writeToBuff("M=0");
                    writeToBuff("(GT)");
                } else {
                    writeToBuff("@LT");
                    writeToBuff("D;JGT");
                    writeToBuff("@SP");
                    writeToBuff("A=M");
                    writeToBuff("M=0");
                    writeToBuff("(LT)");
                }
            }
            case "not" -> writeToBuff("M=!D");
            case "neg" -> writeToBuff("M=-D");
        }
        writeToBuff("@SP");
        writeToBuff("M=M+1");
    }
    public void writePushPop(Parser.CommandType command, String segment, int index) {
        if (command == Parser.CommandType.C_PUSH) {
            if (segment.equals("local") || segment.equals("argument") || segment.equals("this") || segment.equals("that") || segment.equals("temp")) {
                if (segment.equals("temp")) {
                    writeToBuff("@5");
                    writeToBuff("D=A");
                } else {
                    writeToBuff(getLabel(segment));
                    writeToBuff("D=M");
                }
                writeToBuff("@" + index);
                writeToBuff("D=D+A");
                writeToBuff("@addr");
                writeToBuff("M=D");
                writeToBuff("A=M");
                writeToBuff("D=M");
                writeToBuff("@SP");
                writeToBuff("A=M");
                writeToBuff("M=D");
            }

            else if (segment.equals("constant")) {
                writeToBuff("@" + index);
                writeToBuff("D=A");
                writeToBuff("@SP");
                writeToBuff("A=M");
                writeToBuff("M=D");
            }
            else if (segment.equals("static")) {
                writeToBuff("@" + fileName + "." + index);
                writeToBuff("D=M");
                writeToBuff("@SP");
                writeToBuff("A=M");
                writeToBuff("M=D");
            }
            else if (segment.equals("pointer")) {
                if (index == 0) {
                    writeToBuff("@THIS");
                    writeToBuff("D=M");
                    writeToBuff("@SP");
                    writeToBuff("A=M");
                    writeToBuff("M=D");
                } else {
                    writeToBuff("@THAT");
                    writeToBuff("D=M");
                    writeToBuff("@SP");
                    writeToBuff("A=M");
                    writeToBuff("M=D");
                }
            }
        writeToBuff("@SP");
        writeToBuff("M=M+1");
        }

        else if (command == Parser.CommandType.C_POP) {
            if (segment.equals("local") || segment.equals("argument") || segment.equals("this") || segment.equals("that") || segment.equals("temp")) {
                if (segment.equals("temp")) {
                    writeToBuff("@5");
                    writeToBuff("D=A");
                }
                else {
                    writeToBuff(getLabel(segment));
                    writeToBuff("D=M");
                }
                writeToBuff("@" + index);
                writeToBuff("D=D+A");
                writeToBuff("@addr");
                writeToBuff("M=D");
                writeToBuff("@SP");
                writeToBuff("M=M-1");
                writeToBuff("A=M");
                writeToBuff("D=M");
                writeToBuff("@addr");
                writeToBuff("A=M");
                writeToBuff("M=D");
            }
            else if (segment.equals("static")) {
                writeToBuff("@SP");
                writeToBuff("M=M-1");
                writeToBuff("A=M");
                writeToBuff("D=M");
                writeToBuff("@" + fileName + "." + index);
                writeToBuff("M=D");
            }
            else if (segment.equals("pointer")) {
                writeToBuff("@SP");
                writeToBuff("M=M-1");
                writeToBuff("A=M");
                writeToBuff("D=M");
                if (index == 0) {
                    writeToBuff("@THIS");
                } else {
                    writeToBuff("@THAT");
                }
                writeToBuff("M=D");
            }
        }
    }
    public void writeLabel (String label) {
        writeToBuff("(" + label + ")");
    }
    public void writeGoTo (String label) {
        writeToBuff("@" + label);
        writeToBuff("0;JMP");
    }
    public void writeIf (String label) {
        writeToBuff("@SP");
        writeToBuff("AM=M-1");
        writeToBuff("D=M");
        writeToBuff("@" + label);
        writeToBuff("D;JNE");
    }
    public void writeCall(String functionName, int nArgs) {
       String retLabel = (fileName + "." + functionName + "$ret." + funcInx);
        writeToBuff( "@" + retLabel);
        writeToBuff("D=A");
        push();

        writeToBuff("@LCL");
        writeToBuff("D=M");
        push();

        writeToBuff("@ARG");
        writeToBuff("D=M");
        push();

        writeToBuff("@THIS");
        writeToBuff("D=M");
        push();

        writeToBuff("@THAT");
        writeToBuff("D=M");
        push();

        // ARG = SP-5-nArgs
        writeToBuff("@SP");
        writeToBuff("D=M");
        writeToBuff("@5");
        writeToBuff("D=D-A");
        writeToBuff("@" + nArgs);
        writeToBuff("D=D-A");
        writeToBuff("@ARG");
        writeToBuff("M=D");

        // LCL=SP
        writeToBuff("@SP");
        writeToBuff("D=M");
        writeToBuff("@LCL");
        writeToBuff("M=D");

        writeGoTo(functionName);
        writeLabel(retLabel);
        funcInx++;
    }
    public void push() {
        writeToBuff("@SP");
        writeToBuff("A=M");
        writeToBuff("M=D");
        writeToBuff("@SP");
        writeToBuff("M=M+1");
    }
    public void writeFunction (String functionName, int nVars) {
        writeLabel(functionName);
        for (int i = 0; i < nVars; i++) {
            writeToBuff("@LCL");
            writeToBuff("D=M");
            writeToBuff("@" + i);
            writeToBuff("A=D+A");
            writeToBuff("M=0");
            writeToBuff("@SP");
            writeToBuff("M=M+1");
        }
    }
    public void writeReturn () {
        //  endFrame = LCL
        writeToBuff("@LCL");
        writeToBuff("D=M");
        writeToBuff( "@endFrame");
        writeToBuff("M=D");

        //retAddr = *(endFrame - 5)
        writeToBuff("@5");
        writeToBuff("A=D-A");
        writeToBuff("D=M");
        writeToBuff("@retAddr");
        writeToBuff("M=D");

        //ARG = pop()
        writeToBuff("@SP");
        writeToBuff("AM=M-1");
        writeToBuff("D=M");
        writeToBuff("@ARG");
        writeToBuff("A=M");
        writeToBuff("M=D");

        //SP = ARG + 1
        writeToBuff("@ARG");
        writeToBuff("D=M+1");
        writeToBuff("@SP");
        writeToBuff("M=D");

        // THAT = (endFrame-1)
        writeToBuff("@endFrame");
        writeToBuff("A=M-1");
        writeToBuff("D=M");
        writeToBuff("@THAT");
        writeToBuff("M=D");

        // THIS = (endFrame-2)
        writeToBuff("@endFrame");
        writeToBuff("D=M");
        writeToBuff("@2");
        writeToBuff("A=D-A");
        writeToBuff("D=M");
        writeToBuff("@THIS");
        writeToBuff("M=D");

        // ARG = (endFrame-3)
        writeToBuff("@endFrame");
        writeToBuff("D=M");
        writeToBuff("@3");
        writeToBuff("A=D-A");
        writeToBuff("D=M");
        writeToBuff("@ARG");
        writeToBuff("M=D");

        // LCL = (endFrame-4)
        writeToBuff("@endFrame");
        writeToBuff("D=M");
        writeToBuff("@4");
        writeToBuff("A=D-A");
        writeToBuff("D=M");
        writeToBuff("@LCL");
        writeToBuff("M=D");

        writeToBuff("@retAddr");
        writeToBuff("A=M");
        writeToBuff("0;JMP");
    }
    private String getLabel (String segment) {
        if (segment.equals("local")) {
            return "@LCL";
        }
        else if (segment.equals("argument")) {
            return "@ARG";
        }
        else if (segment.equals("this")) {
            return "@THIS";
        }
        else if (segment.equals("that")) {
            return "@THAT";
        }
        else if (segment.equals("temp")) {
            return "@R5";
        }
        else {
            return null;
        }
    }
    public void writeToBuff(String command) {
        try {
            buffW.write(command);
            buffW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close() {
        try {
            buffW.close();
        } catch (IOException e)     {
            e.printStackTrace();
        }
    }
}