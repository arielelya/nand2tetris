import java.io.*;
public class CodeWriter {
    final BufferedReader buffR;
    final File fileOut;
    final BufferedWriter buffW;
    final Parser parser;
    private int currLine;
    final String fileName;

    public CodeWriter(File fileInput) throws IOException {
        if (!fileInput.exists()) {
            fileInput.createNewFile();
            fileInput = new File(fileInput.getParent() + "/" + fileInput.getName().replace("vm", "asm"));
        }
        this.fileOut = new File(fileInput.getParent(), fileInput.getName().replace("vm", "asm"));
        this.buffR = new BufferedReader(new FileReader(fileInput));
        this.buffW = new BufferedWriter(new FileWriter(fileOut.getAbsoluteFile()));
        this.parser = new Parser(fileInput);
        this.currLine = 0;
        this.fileName = fileInput.getName().split("\\.")[0];
        initSP();
    }

    private void initSP () {
        writeToBuff("@256");
        writeToBuff("D=A");
        writeToBuff("@SP");
        writeToBuff("M=D");
    }

    public void writeArithmetic(String command) {
        writeToBuff("//" + command);
        switch (command) {
            case "and", "or", "add", "sub" -> {
                writeToBuff("@SP");
                writeToBuff("AM=M-1");
                writeToBuff("D=M");
                writeToBuff("A=A-1");
                switch (command) {
                    case "and" -> writeToBuff("M=D&M");
                    case "or" -> writeToBuff("M=D|M");
                    case "add" -> writeToBuff("M=D+M");
                    default -> writeToBuff("M=M-D");
                }
            }
            case "eq", "lt", "gt" -> {
                String trueLabel = "TRUE" + currLine;
                String endLabel = "END" + currLine;
                writeToBuff("@SP");
                writeToBuff("AM=M-1");
                writeToBuff("D=M");
                writeToBuff("A=A-1");
                writeToBuff("D=D-M");
                writeToBuff("@" + trueLabel); // skip the next 7 lines if condition is not met
                if (command.equals("eq")) {
                    writeToBuff("D;JEQ");
                } else if (command.equals("lt")) {
                    writeToBuff("D;JGT");
                } else {
                    writeToBuff("D;JLT");
                }
                writeToBuff("@SP");
                writeToBuff("A=M-1");
                writeToBuff("M=0"); // false case
                writeToBuff("@" + endLabel);
                writeToBuff("0;JMP");
                writeToBuff("(" + trueLabel + ")");
                writeToBuff("@SP");
                writeToBuff("A=M-1");
                writeToBuff("M=-1");
                writeToBuff("(" + endLabel + ")");
            }
            case "not" -> {
                writeToBuff("@SP");
                writeToBuff("A=M-1");
                writeToBuff("M=!M");
            }
            case "neg" -> {
                writeToBuff("D=0");
                writeToBuff("@SP");
                writeToBuff("A=M-1");
                writeToBuff("M=D-M");
            }
        }
        currLine = currLine + 7;

    }
    public void writePushPop(Parser.CommandType command, String segment, int index) {
        if (command == Parser.CommandType.C_PUSH) {
            if (segment.equals("pointer")) {
                if (index == 0) {
                    writeToBuff("@THIS");
                } else if (index == 1) {
                    writeToBuff("@THAT");
                }
                writeToBuff("D=M");
            }
            else if (segment.equals("static")) {
                writeToBuff("@" + this.fileName + "." + index);
                writeToBuff("D=M");
            } else if (!segment.equals("constant") && index == 0) {
                writeToBuff(getLabel(segment));
                writeToBuff("A=M");
                writeToBuff("D=M");
            } else if (segment.equals("constant") || index > 0) {
                writeToBuff("@" + index);
                writeToBuff("D=A");
            }
            if (!segment.equals("static") && !segment.equals("pointer") && !segment.equals("constant") && index > 0) {
                writeToBuff(getLabel(segment));
                if (segment.equals("temp")) {
                    writeToBuff("A=D+A");
                } else {
                    writeToBuff("A=D+M");
                }
                writeToBuff("D=M");
            }
            writeToBuff("@SP");
            writeToBuff("A=M");
            writeToBuff("M=D");
            writeToBuff("@SP");
            writeToBuff("M=M+1");

        } else if (command == Parser.CommandType.C_POP) {
            if (segment.equals("pointer") || segment.equals("static") || index == 0) {
                writeToBuff("@SP");
                writeToBuff("AM=M-1");
                writeToBuff("D=M");

                if (segment.equals("pointer") && index == 0) {
                    writeToBuff("@THIS");
                }
                else if (segment.equals("pointer") && index == 1) {
                    writeToBuff("@THAT");
                }
                else if (segment.equals("static")) {
                    writeToBuff("@" + fileName + "." + index);
                }
                else {
                    writeToBuff(getLabel(segment));
                }
            }
            else if (index > 0) {
                writeToBuff("@" + index);
                writeToBuff("D=A");
                writeToBuff(getLabel(segment));

                if (segment.equals("temp")) {
                    writeToBuff("D=D+A");
                } else {
                    writeToBuff("D=D+M");
                }
                writeToBuff("@R13");
                writeToBuff("M=D");
                writeToBuff("@SP");
                writeToBuff("AM=M-1");
                writeToBuff("D=M");
                writeToBuff("@R13");
            }
            if (!segment.equals("static") && !segment.equals("pointer") && !segment.equals("constant")) {
                writeToBuff("A=M");
            }
            writeToBuff("M=D");
        }
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
            currLine++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close() {
        writeToBuff("(END)");
        writeToBuff("@END");
        writeToBuff("0;JMP");
        try {
            buffW.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}