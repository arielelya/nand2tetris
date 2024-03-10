import java.io.*;
import java.util.ArrayList;

public class JackTokenizer {

    final BufferedReader buffR;
    public String currLine;
    public String currToken;
    private static String[] keywords;
    final String symbol;
    private static int place = 0;
    final static ArrayList <String> tokenList= new ArrayList<>() ;
    private int size;

    public JackTokenizer(File fileInput) throws IOException {
        this.buffR = new BufferedReader(new FileReader(fileInput));
        initKeywords();
        this.symbol = "{}()[].,;+-*/&|<>=~";
        parser();
    }
    public void initKeywords () {
         keywords = new String[] {
                "class", "constructor", "function", "method", "field", "static", "var", "int", "char",
                 "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return"};
    }

    // goes over the file and removes black lines, comments and call initTokenList to add
    // the current line's tokens to the list
    public void parser() throws IOException {
        String next = this.buffR.readLine();
        while (next!= null) {
            // if it's a blank line or a comment
            if (next.isBlank() || next.trim().startsWith("//") || next.trim().startsWith("/") || next.trim().startsWith("*")) {
                next = this.buffR.readLine();
                continue;
            }
            // remove comments in the middle of the line
            int commentIdx = next.indexOf("//");
            if (commentIdx != -1) {
                next = next.substring(0, commentIdx).trim();
            }
            this.currLine = next.trim();
            next = buffR.readLine();
            // add this line's tokens to the token list
            addToTokenList();
        }
        size = tokenList.size();
    }

    // go over the current line and add its tokens to the token list
    public void addToTokenList() {
        int len = currLine.length();
        String token = "";
        for (int i = 0; i < len ; i++) {
            if (currLine.substring(i,i+1).isBlank()) {
                // if we have a saved word in token
                if (!token.equals("")) {
                    // if it's a beginning of a string
                    if (token.startsWith("\"")) {
                        // the end of a string
                        if (token.endsWith("\"")) {
                            tokenList.add(token);
                            token = "";
                        }
                        else { token = token.concat(currLine.substring(i, i + 1)); }
                    }
                    else {
                        tokenList.add(token);
                        token = "";
                    }
                }
            }
            // if the current char is a symbol
            else if (symbol.contains(currLine.substring(i,i+1))) {
                // if we have a saved word in token
                if (!token.equals("")) {
                    tokenList.add(token);
                    token = "";
                }
                tokenList.add(currLine.substring(i,i+1));
            }
            else {
                token = token.concat(currLine.substring(i, i + 1));
            }
        }
    }

    public void advance() {
        if (hasMoreTokens()) {
            currToken = tokenList.get(place);
            place ++ ;
        }
    }

    public boolean isKeyword(String value) {
        for (String element : keywords) {
            if (element.equals(value)) {
                return true;
            }
        }
        return false;
    }
    public boolean hasMoreTokens() {

        return (place < size);
    }

    public enum TokenType {
        KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST
    }
    public TokenType tokenType () {
        // if the token is keyword
        if (isKeyword(currToken)) {
            return TokenType.KEYWORD;
        }
        //
        else if (symbol.contains(currToken)) {
            return TokenType.SYMBOL;
        }
        try {
            Integer.parseInt(currToken);
            return TokenType.INT_CONST;
        }
        catch (NumberFormatException e) {
            if (currToken.startsWith("\"")) {
                return TokenType.STRING_CONST;
            } else {
                return TokenType.IDENTIFIER;
            }
        }
    }
    public enum KEYWORD {
        CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID,  VAR, STATIC, FIELD, LET, DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS
    }
    public KEYWORD keyWord () {
        if ("class".equals(currToken)) {
            return KEYWORD.CLASS;
        } else if ("method".equals(currToken)) {
            return KEYWORD.METHOD;
        } else if ("function".equals(currToken)) {
            return KEYWORD.FUNCTION;
        } else if ("constructor".equals(currToken)) {
            return KEYWORD.CONSTRUCTOR;
        } else if ("int".equals(currToken)) {
            return KEYWORD.INT;
        } else if ("boolean".equals(currToken)) {
            return KEYWORD.BOOLEAN;
        } else if ("char".equals(currToken)) {
            return KEYWORD.CHAR;
        } else if ("void".equals(currToken)) {
            return KEYWORD.VOID;
        } else if ("var".equals(currToken)) {
            return KEYWORD.VAR;
        } else if ("static".equals(currToken)) {
            return KEYWORD.STATIC;
        } else if ("field".equals(currToken)) {
            return KEYWORD.FIELD;
        } else if ("let".equals(currToken)) {
            return KEYWORD.LET;
        } else if ("do".equals(currToken)) {
            return KEYWORD.DO;
        } else if ("if".equals(currToken)) {
            return KEYWORD.IF;
        } else if ("else".equals(currToken)) {
            return KEYWORD.ELSE;
        } else if ("while".equals(currToken)) {
            return KEYWORD.WHILE;
        } else if ("return".equals(currToken)) {
            return KEYWORD.RETURN;
        } else if ("true".equals(currToken)) {
            return KEYWORD.TRUE;
        } else if ("false".equals(currToken)) {
            return KEYWORD.FALSE;
        } else if ("null".equals(currToken)) {
            return KEYWORD.NULL;
        } else if ("this".equals(currToken)) {
            return KEYWORD.THIS;
        } else {
            return null;
        }
    }
    public char symbol() {
        return currToken.charAt(0);
    }
    public String identifier () {
        return currToken;
    }
    public int intVal () {
        return Integer.parseInt(currToken);
    }
    public String stringVal () {
        return currToken.substring(1, currToken.length()-1);
    }

}

