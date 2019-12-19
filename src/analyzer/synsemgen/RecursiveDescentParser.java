package analyzer.synsemgen;

import analyzer.Token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class RecursiveDescentParser {

    private Iterator<Token> iterator;
    private String symbol;
    private Token token;
    private Stack<SymbolTable> symbolTables;

    private boolean isSyntaxError = false;
    private boolean isSemanticError = false;

    protected RecursiveDescentParser(List<Token> tokens) {
        iterator = tokens.iterator();
        symbolTables = new Stack<>();
    }

    private void getNextSymbol() {
        if(iterator.hasNext()) {
            token = iterator.next();
            symbol = token.getName();
        }
        else {
            symbol = "end";
        }

    }

    private void verify(String symbol, String with) {
        if(symbol.equals(with)) {
            getNextSymbol();
        }
        else {
            if(!symbol.equals("end")) {
                System.out.println("Error: expected \"" + with + "\", was \"" + symbol + "\"");
            }
            getNextSymbol();
            isSyntaxError = true;
        }
    }

    protected boolean program() {
        symbolTables.push(new SymbolTable());

        getNextSymbol();
        globPromenne();
        funkceProcedury();

        symbolTables.pop();

        return isSyntaxError;
    }

    private void globPromenne() {
        SymbolTable symbolTable = symbolTables.peek();
        if(symbol.equals("end")) {
            return;
        }
        if(!symbol.equals("procedura") && !symbol.equals("funkce")) {
            boolean isConst = modifikator();
            if(symbol.equals("end")) {
                return;
            }
            String type = typ();
            if(symbol.equals("end")) {
                return;
            }
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "=");
            /*
            if(symbol.equals("hodnota")) {
                verify(symbol, "hodnota");
            }
            else if(symbol.equals("IDENTIFIKATOR")) {
                verify(symbol, "IDENTIFIKATOR");
            }
            */
            vyraz();
            /*
            else {
                if(!symbol.equals("end")) {
                    System.out.println("Error: expected \"value\" or \"function call\", was \"" + symbol + "\"");
                    getNextSymbol();
                }
                isSyntaxError = true;
            }

             */
            verify(symbol, ";");
            checkIfExistsInScope(symbolTable.getEntries(), name, "var");
            if(!isSyntaxError && !isSemanticError) {
                symbolTable.getEntries().add(new SymbolTableEntry(name, type, isConst, "var", null));
            }
            globPromenne();
        }
    }

    private void lokPromenne() {
        SymbolTable symbolTable = symbolTables.peek();
        if(symbol.equals("end")) {
            return;
        }
        if(!symbol.equals("pokud") && !symbol.equals("pro") && !symbol.equals("zatimco") &&
                !symbol.equals("IDENTIFIKATOR") && !symbol.equals("vrat")) {
            boolean isConst = modifikator();
            if(symbol.equals("end")) {
                return;
            }
            String type = typ();
            if(symbol.equals("end")) {
                return;
            }
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "=");
            if(symbol.equals("hodnota")) {
                verify(symbol, "hodnota");
            }
            else if(symbol.equals("volani funkce")) {
                volaniFunkce();
            }
            else {
                if(!symbol.equals("end")) {
                    System.out.println("Error: expected \"value\" or \"function call\", was \"" + symbol + "\"");
                    getNextSymbol();
                }
                isSyntaxError = true;
            }
            verify(symbol, ";");
            checkIfExistsInScope(symbolTable.getEntries(), name, "var");
            if(!isSyntaxError && !isSemanticError) {
                symbolTable.getEntries().add(new SymbolTableEntry(name, type, isConst, "var", null));
            }
            lokPromenne();
        }
    }

    private boolean modifikator() {
        if(symbol.equals("end")) {
            return false;
        }
        if(symbol.equals("konst")) {
            verify(symbol, "konst");

            return true;
        }

        return false;
    }

    private void funkceProcedury() {
        SymbolTable symbolTable = symbolTables.peek();
        if(symbol.equals("end")) {
            return;
        }
        if(symbol.equals("funkce")) {
            verify(symbol, "funkce");
            String type = typ();
            if(symbol.equals("end")) {
                return;
            }
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "(");
            List<SymbolTableEntry> parameters = new ArrayList<>();
            parametry(parameters);
            if(symbol.equals("end")) {
                return;
            }
            verify(symbol, ")");
            verify(symbol, "{");

            symbolTables.push(new SymbolTable());

            List<String> parameterTypes = new ArrayList<>();
            parameters.forEach(entry -> {
                symbolTables.peek().getEntries().add(entry);
                parameterTypes.add(entry.getType());
            });

            checkIfExistsInScope(symbolTable.getEntries(), name, "func");
            if(!isSyntaxError && !isSemanticError) {
                symbolTable.getEntries().add(new SymbolTableEntry(name, type, false, "func", parameterTypes));
            }

            vnitrekFunkce();
            symbolTables.pop();
            if(symbol.equals("end")) {
                return;
            }
            verify(symbol, "}");
            funkceProcedury();
        }
        else if(symbol.equals("procedura")) {
            verify(symbol, "procedura");
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "(");
            List<SymbolTableEntry> parameters = new ArrayList<>();
            parametry(parameters);
            if(symbol.equals("end")) {
                return;
            }
            verify(symbol, ")");
            verify(symbol, "{");

            symbolTables.push(new SymbolTable());

            List<String> parameterTypes = new ArrayList<>();
            parameters.forEach(entry -> {
                symbolTables.peek().getEntries().add(entry);
                parameterTypes.add(entry.getType());
            });

            checkIfExistsInScope(symbolTable.getEntries(), name, "proc");
            if(!isSyntaxError && !isSemanticError) {
                symbolTable.getEntries().add(new SymbolTableEntry(name, "", false, "proc", parameterTypes));
            }

            vnitrekProcedury();
            symbolTables.pop();
            verify(symbol, "}");
            funkceProcedury();
        }
    }

    private void parametry(List<SymbolTableEntry> parameters) {
        if(symbol.equals("end")) {
            return;
        }
        String type = typ();
        if(symbol.equals("end")) {
            return;
        }
        String name = token.getValue();
        verify(symbol, "IDENTIFIKATOR");
        if(!isSyntaxError && !isSemanticError) {
            parameters.add(new SymbolTableEntry(name, type, false, "var", null));
        }
        if(symbol.equals(",")) {
            verify(symbol, ",");
            parametry(parameters);
        }
    }

    private void vnitrekFunkce() {
        if(symbol.equals("end")) {
            return;
        }
        lokPromenne();
        if(symbol.equals("end")) {
            return;
        }
        viceAkci();
        if(symbol.equals("end")) {
            return;
        }
        vracHodnoty();
    }

    private void vnitrekProcedury() {
        if(symbol.equals("end")) {
            return;
        }
        lokPromenne();
        if(symbol.equals("end")) {
            return;
        }
        viceAkci();
    }

    private void viceAkci() {
        if(symbol.equals("end")) {
            return;
        }
        switch (symbol) {
            case "pokud":
                rozhodnuti();
                if(symbol.equals("end")) {
                    return;
                }
                viceAkci();
                break;
            case "IDENTIFIKATOR":
                verify(symbol, "IDENTIFIKATOR");
                viceAkci();
                // TODO vyraz, volani
                break;
            case "zatimco":
            case "pro":
                cyklus();
                if(symbol.equals("end")) {
                    return;
                }
                viceAkci();
                break;
        }
    }

    private void rozhodnuti() {
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "pokud");
        verify(symbol, "(");
        slozitaPodminka();
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, ")");
        verify(symbol, "{");
        viceAkci();
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "}");
        if(symbol.equals("pokudne")) {
            verify(symbol, "pokudne");
            verify(symbol, "{");
            viceAkci();
            if(symbol.equals("end")) {
                return;
            }
            verify(symbol, "}");
        }
    }

    private void cyklus() {
        if(symbol.equals("end")) {
            return;
        }
        switch (symbol) {
            case "zatimco":
                verify(symbol, "zatimco");
                verify(symbol, "(");
                slozitaPodminka();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, ")");
                verify(symbol, "{");
                viceAkci();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, "}");
                break;
            case "pro":
                verify(symbol, "pro");
                verify(symbol, "(");
                vyraz();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, ";");
                podminka();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, ";");
                vyraz();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, ")");
                verify(symbol, "{");
                viceAkci();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, "}");
                break;
        }
    }

    private void podminka() {
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "IDENTIFIKATOR");
        podmOperator();
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "IDENTIFIKATOR");
    }

    private void slozitaPodminka() {
        if(symbol.equals("end")) {
            return;
        }
        if(symbol.equals("!")) {
            negace();
            if(symbol.equals("end")) {
                return;
            }
            verify(symbol, "(");
            slozitaPodminka();
            if(symbol.equals("end")) {
                return;
            }
            verify(symbol, ")");
            if(symbol.equals("||")) {
                verify(symbol, "||");
            }
            else if(symbol.equals("&&")) {
                verify(symbol, "&&");
            }
            else {
                return;
            }
            if(symbol.equals("!")) {
                negace();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, "(");
                slozitaPodminka();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, ")");
            }
            else {
                verify(symbol, "(");
                slozitaPodminka();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, ")");
            }
        }
        else {
            if(symbol.equals("IDENTIFIKATOR")) {
                podminka();
            }
            else {
                verify(symbol, "(");
                slozitaPodminka();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, ")");
                if(symbol.equals("||")) {
                    verify(symbol, "||");
                }
                else if(symbol.equals("&&")) {
                    verify(symbol, "&&");
                }
                else {
                    return;
                }
                if(symbol.equals("!")) {
                    negace();
                    if(symbol.equals("end")) {
                        return;
                    }
                    verify(symbol, "(");
                    slozitaPodminka();
                    if(symbol.equals("end")) {
                        return;
                    }
                    verify(symbol, ")");
                }
                else {
                    verify(symbol, "(");
                    slozitaPodminka();
                    if(symbol.equals("end")) {
                        return;
                    }
                    verify(symbol, ")");
                }
            }
        }
    }

    private void volaniFunkce() {
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "IDENTIFIKATOR");
        verify(symbol, "(");
        if(!symbol.equals(")")) {
            vstupHodnoty();
            if(symbol.equals("end")) {
                return;
            }
        }
        verify(symbol, ")");
        verify(symbol, ";");
    }

    private void vstupHodnoty() {
        if(symbol.equals("end")) {
            return;
        }
        if(symbol.equals("IDENTIFIKATOR")) {
            verify(symbol, "IDENTIFIKATOR");
        }
        else {
            verify(symbol, "hodnota");
        }
        if(symbol.equals(",")) {
            verify(symbol, ",");
            vstupHodnoty();
        }
    }

    private void vracHodnoty() {
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "vrat");
        if(symbol.equals("IDENTIFIKATOR")) {
            verify(symbol, "IDENTIFIKATOR");
        }
        else if(symbol.equals("hodnota")) {
            verify(symbol, "hodnota");
        }
        verify(symbol, ";");
    }

    private void zastaveni() {
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "zastav");
        verify(symbol, ";");
    }

    private void prepinani() {
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "prepinac");
        verify(symbol, "(");
        vyraz();
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, ")");
        verify(symbol, "{");
        vicePripadu();
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "}");
    }

    private void vicePripadu() {
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "pripad");
        vyraz();
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, ":");
        viceAkci();
        if(symbol.equals("end")) {
            return;
        }
        zastaveni();
        if(symbol.equals("end")) {
            return;
        }
        if(symbol.equals("pripad")) {
            vicePripadu();
        }
    }

    private String typ() {
        if(symbol.equals("end")) {
            return null;
        }
        switch (symbol) {
            case "cislo":
                verify(symbol, "cislo");
                return "cislo";
            case "logicky":
                verify(symbol, "logicky");
                return "logicky";
        }
        return null;
    }

    private void operator() {
        if(symbol.equals("end")) {
            return;
        }

    }

    private void podmOperator() {
        if(symbol.equals("end")) {
            return;
        }
        switch (symbol) {
            case ">":
                verify(symbol, ">");
                break;
            case "<":
                verify(symbol, "<");
                break;
            case "<=":
                verify(symbol, "<=");
                break;
            case ">=":
                verify(symbol, ">=");
                break;
            case "==":
                verify(symbol, "==");
                break;
            case "!=":
                verify(symbol, "!=");
                break;
        }
    }

    private void negace() {
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "!");
    }

    private void vyraz() {
        term();
        vyraz2();
    }

    private void  vyraz2() {
        if(symbol.equals("+")) {
            verify(symbol, "+");
        }
        else if(symbol.equals("-")) {
            verify(symbol, "-");
        }
        else {
            return;
        }
        term();
        vyraz2();
    }

    private void term() {
        faktor();
        term2();
    }

    private void term2() {
        if(symbol.equals("*")) {
            verify(symbol, "*");
        }
        else if(symbol.equals("/")) {
            verify(symbol, "/");
        }
        else {
            return;
        }
        faktor();
        term2();
    }

    private void faktor() {
        if(symbol.equals("(")) {
            verify(symbol, "(");
            vyraz();
            verify(symbol, ")");
        }
        else if(symbol.equals("IDENTIFIKATOR")) {
            String name = token.getName();
            verify(symbol, "IDENTIFIKATOR");
            if(symbol.equals("(")) {
                volaniFunkce();
            }
        }
        else if(symbol.equals("hodnota")) {
            verify(symbol, "hodnota");
        }
        else {
            if(!symbol.equals("end")) {
                System.out.println("Error: expected \"value\" or \"function call\", was \"" + symbol + "\"");
                getNextSymbol();
            }
            isSyntaxError = true;
        }
    }

    //#############SEMANTIC###############

    private void checkIfExistsInScope(List<SymbolTableEntry> entries, String name, String elementType) {
        entries.forEach(entry -> {
            if(entry.getElementType().equals(elementType) && entry.getName().equals(name)) {
                isSemanticError = true;
                System.out.println("Element \"" + elementType + "\" name: \"" + name + "\" already defined in the scope!");
            }
        });
    }

    private void checkIfIsUsable(String name) {

    }

    private boolean checkTypes(String firstType, String secondType) {
        if(!isSyntaxError && !isSemanticError) {
            if(firstType.equals(secondType)) {
                return true;
            }
            else {
                isSemanticError = true;

                return false;
            }
        }

        return false;
    }

    private void addEntry(SymbolTable symbolTable) {

    }

    private boolean isFunc() {
        return true;
    }

    private boolean isProc() {
        return true;
    }

    private boolean isVar() {
        return true;
    }
}
