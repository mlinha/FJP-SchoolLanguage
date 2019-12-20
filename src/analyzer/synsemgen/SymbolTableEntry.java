package analyzer.synsemgen;

import java.util.ArrayList;
import java.util.List;

public class SymbolTableEntry {

    private boolean isConst;
    private String name;
    private String type;
    private String elementType;
    private List<String> parameterTypes;

    public SymbolTableEntry(String name, String type, boolean isConst, String elementType, List<String> parameterTypes) {
        this.isConst = isConst;
        this.name = name;
        this.type = type;
        this.elementType = elementType;
        this.parameterTypes = parameterTypes;
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

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<String> parameterTypes) {
        this.parameterTypes = parameterTypes;
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
}
