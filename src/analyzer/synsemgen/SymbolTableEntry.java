package analyzer.synsemgen;

import java.util.ArrayList;
import java.util.List;

/**
 * Záznam do tabulky symbolů
 */
public class SymbolTableEntry {

    /**
     * Pozice v paměti
     */
    private int position;

    /**
     * Úroveň
     */
    private int level;

    /**
     * Informace, zda se jedná o konstantu
     */
    private boolean isConst;

    /**
     * Identifikátor
     */
    private String name;

    /**
     * Datový typ
     */
    private String type;

    /**
     * Typ záznamu - proměnná, funkce,...
     */
    private String elementType;

    /**
     * Seznam parametrů
     */
    private List<SymbolTableEntry> parameters;

    /**
     * Vytvoří tabulku symbolů
     * @param postion pozice v paměti
     * @param level úroveň
     * @param name identifikátor
     * @param type datový typ
     * @param isConst informace, zda je konstanta
     * @param elementType typ záznamu
     * @param parameters seznam parametrů
     */
    public SymbolTableEntry(int postion, int level, String name, String type, boolean isConst, String elementType,
                            List<SymbolTableEntry> parameters) {
        this.position = postion;
        this.level = level;
        this.isConst = isConst;
        this.name = name;
        this.type = type;
        this.elementType = elementType;
        this.parameters = parameters;
    }

    /**
     * Získá jméno
     * @return jméno
     */
    public String getName() {
        return name;
    }

    /**
     * Získá datový typ
     * @return datový typ
     */
    public String getType() {
        return type;
    }

    /**
     * Získá parametry
     * @return parametry
     */
    public List<SymbolTableEntry> getParameters() {
        return parameters;
    }

    /**
     * Získá typ záznamu
     * @return typ záznamu
     */
    public String getElementType() {
        return elementType;
    }

    /**
     * Získá zda je konstanta
     * @return zda je konstanta
     */
    public boolean isConst() {
        return isConst;
    }

    /**
     * Získá pozici v paměti
     * @return pozice v paměti
     */
    public int getPosition() {
        return position;
    }

    /**
     * Získá úroveň
     * @return úroveň
     */
    public int getLevel() {
        return level;
    }
}
