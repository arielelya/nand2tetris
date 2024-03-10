import java.util.TreeMap;

import static java.lang.Integer.parseInt;

public class SymbolTable {
    final TreeMap<String, String> symbols;
    public SymbolTable() {
        this.symbols = new TreeMap<>();
        this.insertSymbols();
    }
    private void insertSymbols() {
        this.symbols.put("R0", "0");
        this.symbols.put("R1", "1");
        this.symbols.put("R2", "2");
        this.symbols.put("R3", "3");
        this.symbols.put("R4", "4");
        this.symbols.put("R5", "5");
        this.symbols.put("R6", "6");
        this.symbols.put("R7", "7");
        this.symbols.put("R8", "8");
        this.symbols.put("R9", "9");
        this.symbols.put("R10", "10");
        this.symbols.put("R11", "11");
        this.symbols.put("R12", "12");
        this.symbols.put("R13", "13");
        this.symbols.put("R14", "14");
        this.symbols.put("R15", "15");
        this.symbols.put("SCREEN", "16384");
        this.symbols.put("KBD", "24576");
        this.symbols.put("SP", "0");
        this.symbols.put("LCL", "1");
        this.symbols.put("ARG", "2");
        this.symbols.put("THIS", "3");
        this.symbols.put("THAT", "4");
    }

    public void addEntry(String symbol, int address) {
        this.symbols.put(symbol, String.valueOf(address));
    }
    public boolean contains(String symbol){
        return this.symbols.containsKey(symbol);
    }
    public int getAddress(String symbol){
        return parseInt(this.symbols.get(symbol));
    }
}
