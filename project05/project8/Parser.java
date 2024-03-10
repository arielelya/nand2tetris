import java.io.*;
public class Parser {
    final BufferedReader buffR;
    public String currLine;

    // Opens the file/stream
    public Parser(File fileInput) throws IOException {
        if (!fileInput.exists()) {
            System.out.println("File does not exist: " + fileInput.getAbsolutePath());
            throw new FileNotFoundException(fileInput.getAbsolutePath());
        }
        this.buffR = new BufferedReader(new FileReader(fileInput));
        this.advance();
    }


    // Checks if there is more work to do
    public boolean hasMoreLines() {
        return (this.currLine != null);
    }
    public void advance() throws IOException {
        String next = this.buffR.readLine();
        // Proceed lines until there is no blank line or comment line
        while (next != null) {
            if (next.isBlank() || next.trim().startsWith("//")) {
                next = this.buffR.readLine();
                continue;
            }
            int commentIdx = next.indexOf("//");
            if (commentIdx != -1) {
                next = next.substring(0, commentIdx).trim();
            }
            this.currLine = next.trim();
            return;
        }
        this.currLine = null;
        //System.out.println(this.currLine);
    }
    public enum CommandType {
        C_ARITHMETIC, C_PUSH, C_POP, C_LABEL, C_GOTO, C_IF, C_FUNCTION, C_RETURN, C_CALL
    }
    public CommandType commandType() {
        String command = currLine.trim().split(" ")[0];
        String[] arithmetic = {"add","sub","neg", "eq", "gt", "lt", "and", "or", "not"};
        if (contains(arithmetic, command))
        {
            return CommandType.C_ARITHMETIC;
        }
        else {
            if (command.equals("push")) {
                return CommandType.C_PUSH;
            }
            else  if (command.equals("pop")) {
                return CommandType.C_POP;
            }
            else  if (command.equals("label")) {
                return CommandType.C_LABEL;
            }
            else  if (command.equals("goto")) {
                return CommandType.C_GOTO;
            }
            else  if (command.equals("if-goto")) {
                return CommandType.C_IF;
            }
            else  if (command.equals("function")) {
                return CommandType.C_FUNCTION;
            }
            else  if (command.equals("return")) {
                return CommandType.C_RETURN;
            }
            else  if (command.equals("call")) {
                return CommandType.C_CALL;
            }
            else {
                return null;
            }
        }
    }

    public static boolean contains(String[] array, String value) {
        for (String element : array) {
            if (element.equals(value)) {
                return true;
            }
        }
        return false;
    }

    // Should not be called if the current command is RETURN
    public String arg1 () throws IOException {
        CommandType type = commandType();
        if (type.equals(CommandType.C_RETURN)) {
            throw new InvalidObjectException("Invalid command type");
        }
        if (type.equals(CommandType.C_ARITHMETIC))
        {
            return currLine.trim().split(" ")[0];
        }
        return currLine.trim().split(" ")[1];
    }

    // Should be called only if the command is PUSH, POP, FUNCTION, CALL
    public int arg2 () throws IOException {
        CommandType type = commandType();
        if (type.equals(CommandType.C_PUSH) || type.equals(CommandType.C_POP) || type.equals(CommandType.C_FUNCTION) ||
                type.equals(CommandType.C_CALL)) {
            return Integer.parseInt(currLine.trim().split(" ")[2]);
        }
        else {
            throw new InvalidObjectException("Invalid command type");
        }
    }
}
