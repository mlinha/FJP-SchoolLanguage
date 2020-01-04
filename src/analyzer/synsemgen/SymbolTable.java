package analyzer.synsemgen;

import java.util.ArrayList;
import java.util.List;

/**
 * Tabulka symbolů
 */
public class SymbolTable {

    /**
     * Seznam záznamů
     */
    private List<SymbolTableEntry> entries;

    /**
     * Vytvoří tabulku
     */
    public SymbolTable() {
        entries = new ArrayList<>();
    }

    /**
     * Získá seznam záznamů
     * @return seznam záznamů
     */
    public List<SymbolTableEntry> getEntries() {
        return entries;
    }
}
