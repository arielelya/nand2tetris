import java.io.*;

public class CompilationEngine {
    final BufferedReader buffR;
    final BufferedWriter buffW;
    private final JackTokenizer jack;
    private int indentation = 0;
    final String OP = "+-*/&|<>=";


    public CompilationEngine(File fileInput) throws IOException {
        if (!fileInput.exists()) {
            fileInput.createNewFile();
            fileInput = new File(fileInput.getParent() + "/" + fileInput.getName().replace("jack", "xml"));
        }
        File outputFile = new File(fileInput.getParent(), fileInput.getName().replace(".jack", ".xml"));
        this.buffR = new BufferedReader(new FileReader(fileInput));
        this.buffW = new BufferedWriter(new FileWriter(outputFile.getAbsoluteFile()));
        this.jack = new JackTokenizer(fileInput);
    }

    public void compileClass() throws IOException {
        if (jack.hasMoreTokens()) {
            jack.advance(); // get the first token
            writeToBuff("<class>");
            indentation++;
            writeLine("keyword", jack.currToken); //class
            writeLine("identifier", jack.identifier()); // name of class
            writeLine("symbol", String.valueOf(jack.symbol())); // {
            while (jack.keyWord().equals(JackTokenizer.KEYWORD.FIELD) || jack.keyWord().equals(JackTokenizer.KEYWORD.STATIC)) {
                compileClassVarDec();
            }
            while (jack.tokenType() != JackTokenizer.TokenType.SYMBOL && (jack.keyWord().equals(JackTokenizer.KEYWORD.CONSTRUCTOR) || jack.keyWord().equals(JackTokenizer.KEYWORD.FUNCTION) || jack.keyWord().equals(JackTokenizer.KEYWORD.METHOD))) {
                compileSubRoutine();
            }
            writeLine("symbol", String.valueOf(jack.symbol())); // }
            indentation--;
            writeToBuff("</class>");
            close();
        }
    }

    private void compileClassVarDec() throws IOException {
        writeTitle("<classVarDec>");
        indentation++;
        writeLine("keyword", jack.currToken);
        compileTypeAndVar();
        indentation--;
        writeIndentation();
        writeTitle("</classVarDec>");
    }

    private void compileTypeAndVar() throws IOException {
        if (jack.tokenType().equals(JackTokenizer.TokenType.IDENTIFIER)) {
            writeLine("identifier", jack.identifier());
        } else if (jack.tokenType().equals(JackTokenizer.TokenType.KEYWORD)) {
            writeLine("keyword", jack.currToken);
        }
        writeLine("identifier", jack.identifier());
        while (jack.symbol() == ',') {
            writeLine("symbol", String.valueOf(jack.symbol()));
            writeLine("identifier", jack.identifier());
        }
        writeLine("symbol", String.valueOf(jack.symbol()));
    }

    private void compileSubRoutine() throws IOException {
        writeTitle("<subroutineDec>");
        indentation++;
        writeLine("keyword", jack.currToken);
        if (jack.tokenType().equals(JackTokenizer.TokenType.KEYWORD)) {
            writeLine("keyword", jack.currToken);
        } else if (jack.tokenType().equals(JackTokenizer.TokenType.IDENTIFIER)) {
            writeLine("identifier", jack.identifier());
        }
        writeLine("identifier", jack.identifier());
        writeLine("symbol", String.valueOf(jack.symbol()));
        compileParameterList();
        writeLine("symbol", String.valueOf(jack.symbol()));
        compileSubroutineBody();
        indentation--;
        writeIndentation();
        writeTitle("</subroutineDec>");
    }

    private void compileSubroutineBody() throws IOException {
        writeTitle("<subroutineBody>");
        indentation++;
        writeLine("symbol", String.valueOf(jack.symbol()));
        while (jack.tokenType().equals(JackTokenizer.TokenType.KEYWORD) && jack.keyWord().equals(JackTokenizer.KEYWORD.VAR)) {
            compileVarDec();
        }
        compileStatements();
        writeLine("symbol", String.valueOf(jack.symbol()));
        indentation--;
        writeTitle("</subroutineBody>");
    }

    private void compileParameterList() throws IOException {
        writeTitle("<parameterList>");
        indentation++;
        while (!(jack.tokenType().equals(JackTokenizer.TokenType.SYMBOL))) {
            if (jack.tokenType().equals(JackTokenizer.TokenType.KEYWORD)) {
                writeLine("keyword", jack.currToken);
            } else if (jack.tokenType().equals(JackTokenizer.TokenType.IDENTIFIER)) {
                writeLine("identifier", jack.identifier());
            }
            writeLine("identifier", jack.identifier());
            if (jack.tokenType().equals(JackTokenizer.TokenType.SYMBOL) && jack.symbol() == ',') {
                writeLine("symbol", String.valueOf(jack.symbol()));
            }
        }
        indentation--;
        writeTitle("</parameterList>");
    }

    private void compileVarDec() throws IOException {
        writeTitle("<varDec>");
        indentation++;
        writeLine("keyword", jack.currToken);
        compileTypeAndVar();
        indentation--;
        writeTitle("</varDec>");
    }

    private void compileStatements() throws IOException {
        writeTitle("<statements>");
        indentation++;
        while (jack.tokenType().equals(JackTokenizer.TokenType.KEYWORD)) {
            if (jack.keyWord().equals(JackTokenizer.KEYWORD.LET)) {
                compileLet();
            } else if (jack.keyWord().equals(JackTokenizer.KEYWORD.IF)) {
                compileIf();
            } else if (jack.keyWord().equals(JackTokenizer.KEYWORD.WHILE)) {
                compileWhile();
            } else if (jack.keyWord().equals(JackTokenizer.KEYWORD.DO)) {
                compileDo();
            } else if (jack.keyWord().equals(JackTokenizer.KEYWORD.RETURN)) {
                compileReturn();
            }
        }
        indentation--;
        writeTitle("</statements>");
    }

    private void compileLet() throws IOException {
        writeTitle("<letStatement>");
        indentation++;
        writeLine("keyword", jack.currToken);
        writeLine("identifier", jack.identifier());
        if (jack.currToken.equals("[")) {
            writeLine("symbol", String.valueOf(jack.symbol()));
            compileExpression();
            writeLine("symbol", String.valueOf(jack.symbol()));
        }
        writeLine("symbol", String.valueOf(jack.symbol()));
        compileExpression();
        writeLine("symbol", String.valueOf(jack.symbol()));
        indentation--;
        writeTitle("</letStatement>");
    }

    private void compileExpression() throws IOException {
        writeTitle("<expression>");
        indentation++;
        compileTerm();
        while (jack.tokenType().equals(JackTokenizer.TokenType.SYMBOL) && OP.contains(jack.currToken)) {
            writeLine("symbol", String.valueOf(jack.symbol()));
            compileTerm();
        }
        indentation--;
        writeTitle("</expression>");
    }

    private void compileTerm() throws IOException {
        writeTitle("<term>");
        indentation++;
        if (jack.tokenType().equals(JackTokenizer.TokenType.INT_CONST)) {
            writeLine("integerConstant", String.valueOf(jack.intVal()));
        } else if (jack.tokenType().equals(JackTokenizer.TokenType.STRING_CONST)) {
            writeLine("stringConstant", jack.stringVal());
        } else if (jack.tokenType().equals(JackTokenizer.TokenType.KEYWORD)) {
            writeLine("keyword", jack.currToken);
        } else if (jack.tokenType().equals(JackTokenizer.TokenType.IDENTIFIER)) {
            writeLine("identifier", jack.identifier());
            if (jack.currToken.equals("[")) {
                writeLine("symbol", String.valueOf(jack.symbol()));
                compileExpression();
                writeLine("symbol", String.valueOf(jack.symbol()));
            }
            else if (jack.currToken.equals(".")) {
                writeLine("symbol", String.valueOf(jack.symbol()));
                writeLine("identifier", jack.identifier());
                writeLine("symbol", String.valueOf(jack.symbol()));
                compileExpressionList();
                writeLine("symbol", String.valueOf(jack.symbol()));
            }
            else if (jack.currToken.equals("(")) {
                writeLine("symbol", String.valueOf(jack.symbol()));
                compileExpressionList();
                writeLine("symbol", String.valueOf(jack.symbol()));
            }

        } else if (jack.currToken.equals("(")) {
            writeLine("symbol", String.valueOf(jack.symbol()));
            compileExpression();
            writeLine("symbol", String.valueOf(jack.symbol()));
        } else if (jack.currToken.equals("~") || (jack.currToken.equals("-"))) {
            writeLine("symbol", String.valueOf(jack.symbol()));
            compileTerm();
        }
        indentation--;
        writeTitle("</term>");
    }

    private void compileExpressionList() throws IOException {
        writeTitle("<expressionList>");
        indentation++;
        if (!jack.tokenType().equals(JackTokenizer.TokenType.SYMBOL)) {
            compileExpression();
            while (jack.symbol() == ',') {
                writeLine("symbol", String.valueOf(jack.symbol()));
                compileExpression();
            }
        }
        if (jack.symbol() == '(') {
            compileExpression();
            while (jack.symbol() == ',') {
                writeLine("symbol", String.valueOf(jack.symbol()));
                compileExpression();
            }
        }
        indentation--;
        writeTitle("</expressionList>");
    }

    public void compileIf() throws IOException {
        writeTitle("<ifStatement>");
        indentation++;
        writeLine("keyword", jack.currToken);
        writeLine("symbol", String.valueOf(jack.symbol()));
        compileExpression();
        writeLine("symbol", String.valueOf(jack.symbol()));
        writeLine("symbol", String.valueOf(jack.symbol()));
        compileStatements();
        writeLine("symbol", String.valueOf(jack.symbol()));
        if (jack.tokenType().equals(JackTokenizer.TokenType.KEYWORD) && jack.keyWord().equals(JackTokenizer.KEYWORD.ELSE)) {
            writeLine("keyword", jack.currToken);
            writeLine("symbol", String.valueOf(jack.symbol()));
            compileStatements();
            writeLine("symbol", String.valueOf(jack.symbol()));
        }
        indentation--;
        writeTitle("</ifStatement>");
    }

    private void compileWhile() throws IOException {
        writeTitle("<whileStatement>");
        indentation++;
        writeLine("keyword", jack.currToken);
        writeLine("symbol", String.valueOf(jack.symbol()));
        compileExpression();
        writeLine("symbol", String.valueOf(jack.symbol()));
        writeLine("symbol", String.valueOf(jack.symbol()));
        compileStatements();
        writeLine("symbol", String.valueOf(jack.symbol()));
        indentation--;
        writeTitle("</whileStatement>");
    }

    private void compileDo() throws IOException {
        writeTitle("<doStatement>");
        indentation++;
        writeLine("keyword", jack.currToken);
        writeLine("identifier", jack.identifier());
        if (jack.currToken.equals(".")) {
            writeLine("symbol", String.valueOf(jack.symbol()));
            writeLine("identifier", jack.identifier());
        }
        writeLine("symbol", String.valueOf(jack.symbol()));
        compileExpressionList();
        writeLine("symbol", String.valueOf(jack.symbol()));
        writeLine("symbol", String.valueOf(jack.symbol()));
        indentation--;
        writeTitle("</doStatement>");
    }

    private void compileReturn() throws IOException {
        writeTitle("<returnStatement>");
        indentation++;
        writeLine("keyword", jack.currToken);
        if (!(jack.currToken.equals(";"))) {
            compileExpression();
        }
        writeLine("symbol", String.valueOf(jack.symbol()));
        indentation--;
        writeTitle("</returnStatement>");
    }

    private void writeLine(String type, String str) throws IOException {
        if (type.equals("symbol")) {
            if (str.equals("<")) {
                str = "&lt;";
            }
            else if (str.equals(">")) {
                str = "&gt;";
            }
            else if (str.equals("&")) {
                str = "&amp;";
            }
        }
        writeIndentation();
        writeToBuff("<" + type + "> " + str + " </" + type + ">");
        jack.advance();
    }

    private void writeIndentation() throws IOException {
        for (int i = 0; i < indentation; i++) {
            buffW.write("  ");
        }
    }
    private void writeTitle (String title) throws IOException {
        writeIndentation();
        writeToBuff(title);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
