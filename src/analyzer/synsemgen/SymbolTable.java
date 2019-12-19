package analyzer.synsemgen;

import java.util.ArrayList;
import java.util.List;

public class SymbolTable {

    public List<SymbolTableEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<SymbolTableEntry> entries) {
        this.entries = entries;
    }

    private List<SymbolTableEntry> entries;

    public SymbolTable() {
        entries = new ArrayList<>();
    }
}
