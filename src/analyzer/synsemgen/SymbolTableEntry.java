package analyzer.synsemgen;

import java.util.ArrayList;
import java.util.List;

public class SymbolTableEntry {

    private int position;
    private int level;
    private boolean isConst;
    private String name;
    private String type;
    private String elementType;
    private List<SymbolTableEntry> parameters;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<SymbolTableEntry> getParameters() {
        return parameters;
    }

    public void setParameters(List<SymbolTableEntry> parameters) {
        this.parameters = parameters;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<String> getParameterTypes() {
        List<String> parameterTypes = new ArrayList<>();
        parameters.forEach(entry -> parameterTypes.add(entry.type));

        return parameterTypes;
    }
}
