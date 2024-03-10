import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class HackAssembler {
    final File fileOut;
    final File fileIn;
    final SymbolTable symbolTable;
    final Code codes;
    private BufferedWriter buffW;
    private int lineCounter;
    public int symbolCounter;
    public HackAssembler(File fileInput){
        this.fileOut = new File(fileInput.getParent(), fileInput.getName().replace("asm","hack"));
        this.fileIn = fileInput;
        this.symbolTable = new SymbolTable();
        this.codes = new Code();
        this.buffW = null;
        this.lineCounter = 0;
        this.symbolCounter = 16;
    }
    public void parse() throws IOException {
        Parser parser = new Parser(this.fileIn);
        try {
            buffW = new BufferedWriter(new FileWriter(fileOut.getAbsoluteFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // First pass - only adding LABEL declarations
        while (parser.hasMoreLines()) {
            if (parser.instructionType().equals(Parser.instructType.L_INSTRUCTION)) {
                symbolTable.addEntry(parser.symbol(), this.lineCounter);
                this.lineCounter--;
            }
            this.lineCounter++;
            parser.advance();
        }
        // Second pass
        parser = new Parser(this.fileIn);

        Parser.instructType instruct;
        while (parser.hasMoreLines()) {
            instruct = parser.instructionType();
            // if it's an A instruction (@xxx)
            if (instruct.equals(Parser.instructType.A_INSTRUCTION)) {
                // parser.symbol() = xxx
                String xxx = parser.symbol();
                dealAInstruct(xxx);
            }
            else if (instruct.equals(Parser.instructType.C_INSTRUCTION)) {
                String destBinary = "000";
                String compBinary = "0101010";
                String jumpBinary = "000";
                if (parser.dest() != null) {
                    destBinary = codes.dest(parser.dest());
                }
                if (parser.comp() != null) {
                    compBinary = codes.comp(parser.comp());
                }
                if (parser.jump() != null) {
                    jumpBinary = codes.jump(parser.jump());
                }
                try {
                    buffW.write("111" + compBinary + destBinary + jumpBinary);
                    buffW.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            parser.advance();
        }
        try {
            buffW.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void dealAInstruct (String xxx) {
        // if xxx is not a number - it's a variable
        if (!isNumber(xxx)) {
            // if the symbol doesn't exist yet
            if (!symbolTable.contains(xxx)) {
                symbolTable.addEntry(xxx, this.symbolCounter);
                symbolCounter ++;
            }
            // add its address value to the buffer
            int address = symbolTable.getAddress(xxx);
            String binary = len16(numToBinary(address));
            try {
                buffW.write(binary);
                buffW.newLine();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        // if xxx is a number - add its value to the buffer
        else {
            int num = Integer.parseInt(xxx);
            String binary = len16(numToBinary(num));
            try {
                buffW.write(binary );
                buffW.newLine();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Gets a binary representation and making sure its length is 16 by adding leading zeroes
    private String len16 (String binary) {
        return String.format("%16s", binary).replace(' ', '0');
    }

    // Gets a number and return its presentation in binary
    private String numToBinary (int number) {
        return Integer.toBinaryString(number);
    }
    // Checks if a string is a number
    private boolean isNumber(String str) {
        try { Integer.parseInt(str); return true; }
        catch (NumberFormatException e) { return false; }
    }
}