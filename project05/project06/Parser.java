import java.io.*;
public class Parser {
    final BufferedReader buffR;
    private String currLine;

    // Opens the file/stream
    public Parser(File fileInput) throws IOException {
        if (!fileInput.exists()) {
            System.out.println("File does not exist: " + fileInput.getAbsolutePath());
            throw new FileNotFoundException(fileInput.getAbsolutePath());
        }
        this.buffR = new BufferedReader(new FileReader(fileInput));
        this.advance();
    }
    public enum instructType {
        A_INSTRUCTION, C_INSTRUCTION,L_INSTRUCTION
    }
    // Checks if there is more work to do
    public boolean hasMoreLines(){
        return (this.currLine != null);
    }
    public void advance() throws IOException {
        String next = this.buffR.readLine();
        // Proceed lines until there is no blank line or comment line
        while (next != null && (next.isBlank() || next.contains("//")))
        {
            next = this.buffR.readLine();
        }
        this.currLine = next;
    }
    public instructType instructionType() {
        String str = this.currLine.trim();
        if (str.startsWith("(") && str.endsWith(")")) {
            return instructType.L_INSTRUCTION;
        }
        if (str.startsWith("@")) {
            return instructType.A_INSTRUCTION;
        }
        else {
            return instructType.C_INSTRUCTION;
        }
    }
    public String symbol() {
        String str = this.currLine.trim();
        int len = str.length();
        if (this.instructionType().equals(instructType.A_INSTRUCTION)) {
            return (str.substring(1));
        }
        if (this.instructionType().equals(instructType.L_INSTRUCTION)) {
            return (str.substring(1,len-1));
        }
        return null;
    }

    public String dest(){
        String str = this.currLine.trim();
        int idx = str.indexOf("=");
        if (idx != -1) {
            return (str.substring(0,idx));
        }
        return null;
    }
    public String comp(){
        String str = this.currLine.trim();
        int idxEqual = str.indexOf("=");
        int idx = str.indexOf(";");
        if (idxEqual != -1) {
            if (idx != -1) {
                return (str.substring(idxEqual + 1, idx));
            } else {
                return (str.substring(idxEqual + 1));
            }
        }
        else {
            if (idx != -1) {
                return (str.substring(0, idx));
            } else {
                return (str.substring(0));
            }
        }
    }
    public String jump() {
        String str = this.currLine.trim();
        int idx = str.indexOf(";");
        if (idx != -1) {
            return (str.substring(idx + 1));
        }
        return null;
    }
}
