import java.io.*;
public class Parser {
    final BufferedReader buffR;
    String currLine;

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
        while (next != null && (next.isBlank() || next.contains("//"))) {
            next = this.buffR.readLine();
        }
        this.currLine = next;
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
            return switch (command) {
                case "push" -> CommandType.C_PUSH;
                case "pop" -> CommandType.C_POP;
                case "label" -> CommandType.C_LABEL;
                case "goto" -> CommandType.C_GOTO;
                case "if" -> CommandType.C_IF;
                case "function" -> CommandType.C_FUNCTION;
                case "return" -> CommandType.C_RETURN;
                case "call" -> CommandType.C_CALL;
                default -> null;
            };
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
