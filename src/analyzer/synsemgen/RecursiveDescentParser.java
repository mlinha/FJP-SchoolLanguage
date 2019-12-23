package analyzer.synsemgen;

import analyzer.Token;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecursiveDescentParser {

    private int position = 3;
    private int number = 0;

    private Iterator<Token> iterator;
    private String symbol;
    private Token token;
    private Stack<SymbolTable> symbolTables;

    private boolean isSyntaxError = false;
    private boolean isSemanticError = false;

    BufferedWriter writer;

    {
        try {
            writer = new BufferedWriter(new FileWriter("out.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

        try {
            writer.write(number + "   JMP   0   1");
            writer.newLine();
            number++;
            writer.write(number + "   INT   0   3");
            writer.newLine();
            number++;
        } catch (IOException e) {
            e.printStackTrace();
        }

        getNextSymbol();
        globPromenne();
        funkceProcedury();

        symbolTables.pop();

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isSyntaxError;
    }

    private void globPromenne() {
        SymbolTable symbolTable = symbolTables.peek();
        if(symbol.equals("end")) {
            return;
        }
        if(!symbol.equals("procedura") && !symbol.equals("funkce")) {
            if(!isSyntaxError && !isSemanticError) {
                try {
                    writer.write(number + "   INT   0   1");
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

            if(type != null && type.equals("cislo")) {
                matVyraz();
            }
            else if(type != null && type.equals("logicky")) {
                logVyraz();
            }
            else {
                // TODO: error
                System.out.println("no type");
                matVyraz();
            }
            verify(symbol, ";");
            checkIfExistsInScope(symbolTable.getEntries(), name, "var");
            if(!isSyntaxError && !isSemanticError) {
                symbolTable.getEntries().add(new SymbolTableEntry(position, 0, name, type, isConst, "var", null));
                try {
                    writer.write(number + "   STO   0   " + position);
                    position++;
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            if(!isSyntaxError && !isSemanticError) {
                try {
                    writer.write(number + "   INT   0   1");
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
            if(type != null && type.equals("cislo")) {
                matVyraz();
            }
            else if(type != null && type.equals("logicky")) {
                logVyraz();
            }
            else {
                // TODO: error
                System.out.println("no type");
                matVyraz();
            }
            verify(symbol, ";");
            checkIfExistsInScope(symbolTable.getEntries(), name, "var");
            if(!isSyntaxError && !isSemanticError) {
                symbolTable.getEntries().add(new SymbolTableEntry(position, 1, name, type, isConst, "var", null));
                try {
                    writer.write(number + "   STO   0   " + position);
                    position++;
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            position = 3;
            if(!isSyntaxError && !isSemanticError) {
                try {
                    writer.write(number + "   INT   0   3");
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
                symbolTable.getEntries().add(new SymbolTableEntry(position, 0, name, type, false, "func", parameterTypes));
            }

            vnitrekFunkce(type);
            symbolTables.pop();
            if(symbol.equals("end")) {
                return;
            }
            verify(symbol, "}");
            funkceProcedury();
        }
        else if(symbol.equals("procedura")) {
            verify(symbol, "procedura");
            if(!isSyntaxError && !isSemanticError) {
                try {
                    writer.write(number + "   INT   0   3");
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

            checkIfExistsInScope(symbolTable.getEntries(), name, "proc");
            if(!isSyntaxError && !isSemanticError) {
                symbolTable.getEntries().add(new SymbolTableEntry(position, 0, name, "", false, "proc", parameterTypes));
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
        if(!isSyntaxError && !isSemanticError) {
            try {
                writer.write(number + "   INT   0   1");
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String name = token.getValue();
        verify(symbol, "IDENTIFIKATOR");
        if(!isSyntaxError && !isSemanticError) {
            parameters.add(new SymbolTableEntry(position, 1, name, type, false, "var", null));
        }
        if(symbol.equals(",")) {
            verify(symbol, ",");
            parametry(parameters);
        }
    }

    private void vnitrekFunkce(String typeOut) {
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
        vracHodnoty(typeOut);
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
                String name = token.getValue();
                verify(symbol, "IDENTIFIKATOR");
                if(symbol.equals("(")) {
                    volaniFunkce(name);
                }
                else {
                    SymbolTableEntry entry = findVar(name);
                    String type = "";
                    if(entry != null) {
                        type = entry.getType();
                    }
                    if(entry != null && entry.isConst()) {
                        isSemanticError = true;
                        System.out.println(); // TODO: error
                    }
                    verify(symbol, "=");
                    if(type.equals("logicky")) {
                        logVyraz();
                    }
                    else {
                        matVyraz();
                    }
                }
                verify(symbol, ";");
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
                matVyraz();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, ";");
                podminka();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, ";");
                matVyraz();
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
        String typeL = "";
        String typeR = "";
        if(symbol.equals("end")) {
            return;
        }
        if(symbol.equals("IDENTIFIKATOR")) {
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            SymbolTableEntry entry = findIdent(name);
            if(entry != null) {
                typeL = entry.getType();
            }
            if(symbol.equals("(")) {
                if(entry != null && !isSyntaxError && !isSemanticError && entry.getElementType().equals("proc")) {
                    System.out.println("not a function"); // TODO: error
                }
                volaniFunkce(name);
            }
        }
        else if(symbol.equals("hodnota")) {
            String value = token.getValue();
            verify(symbol, "hodnota");
            if(value.matches("\\d+")) {
                typeL = "cislo";
            }
            else {
                typeL = "logicky";
            }
        }
        else {
            // TODO: error
            verify(symbol, "value or ident");
        }
        podmOperator(typeL);
        if(symbol.equals("end")) {
            return;
        }
        if(symbol.equals("IDENTIFIKATOR")) {
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            SymbolTableEntry entry = findIdent(name);
            if(entry != null) {
                typeR = entry.getType();
            }
            if(symbol.equals("(")) {
                if(entry != null && !isSyntaxError && !isSemanticError && entry.getElementType().equals("proc")) {
                    System.out.println("not a function"); // TODO: error
                }
                volaniFunkce(name);
            }
        }
        else if(symbol.equals("hodnota")) {
            String value = token.getValue();
            verify(symbol, "hodnota");
            if(value.matches("\\d+")) {
                typeR = "cislo";
            }
            else {
                typeR = "logicky";
            }
        }
        else {
            // TODO: error
            verify(symbol, "value or ident");
        }

        if(!typeL.equals(typeR)) {
            isSemanticError = true;
            System.out.println(); // TODO: error
        }
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

    private void volaniFunkce(String name) {
        if(symbol.equals("end")) {
            return;
        }
        SymbolTableEntry entry = findFunction(name);
        List<String> parameterTypes = null;
        if(entry != null) {
            parameterTypes = entry.getParameterTypes();
        }
        verify(symbol, "(");
        if(!symbol.equals(")")) {
            vstupHodnoty(parameterTypes, 0);
            if(symbol.equals("end")) {
                return;
            }
        }
        verify(symbol, ")");
        verify(symbol, ";");
    }

    private void vstupHodnoty(List<String> parameterTypes, int index) {
        if(symbol.equals("end")) {
            return;
        }
        if(symbol.equals("IDENTIFIKATOR")) {
            String name = token.getName();
            verify(symbol, "IDENTIFIKATOR");
            if(!isSyntaxError && !isSemanticError) {
                checkIfIsUsableForOps(name, "logicky");
            }
            if(symbol.equals("(")) {
                volaniFunkce(name); // TODO: not sure
            }
        }
        else {
            String value = token.getValue();
            String type;
            verify(symbol, "hodnota");
            if(value.matches("\\d+")) {
                type = "cislo";
            }
            else {
                type = "logicky";
            }
            if(!isSyntaxError && !isSemanticError && !type.equals(parameterTypes.get(index))) {
                isSemanticError = true;
                System.out.println(""); // TODO: error
            }
        }
        if(symbol.equals(",")) {
            verify(symbol, ",");
            index++;
            vstupHodnoty(parameterTypes, index);
        }
    }

    private void vracHodnoty(String typeOut) {
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "vrat");
        if(symbol.equals("IDENTIFIKATOR")) {
            String name = token.getName();
            verify(symbol, "IDENTIFIKATOR");
            if(!isSyntaxError && !isSemanticError) {
                checkIfIsUsableForOps(name, typeOut);
            }
            if(symbol.equals("(")) {
                volaniFunkce(name);
            }
        }
        else if(symbol.equals("hodnota")) {
            String value = token.getValue();
            String type;
            verify(symbol, "hodnota");
            if(value.matches("\\d+")) {
                type = "cislo";
            }
            else {
                type = "logicky";
            }
            if(!isSyntaxError && !isSemanticError && !type.equals(typeOut)) {
                isSemanticError = true;
                System.out.println(""); // TODO: error
            }
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
        matVyraz();
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
        matVyraz();
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

    private void podmOperator(String type) {
        if(symbol.equals("end")) {
            return;
        }
        switch (symbol) {
            case ">":
                verify(symbol, ">");
                if(type.equals("logicky")) {
                    System.out.println(); // TODO: error
                    isSemanticError = true;
                }
                break;
            case "<":
                verify(symbol, "<");
                if(type.equals("logicky")) {
                    System.out.println(); // TODO: error
                    isSemanticError = true;
                }
                break;
            case "<=":
                verify(symbol, "<=");
                if(type.equals("logicky")) {
                    System.out.println(); // TODO: error
                    isSemanticError = true;
                }
                break;
            case ">=":
                verify(symbol, ">=");
                if(type.equals("logicky")) {
                    System.out.println(); // TODO: error
                    isSemanticError = true;
                }
                break;
            case "==":
                verify(symbol, "==");
                break;
            case "!=":
                verify(symbol, "!=");
                break;
            default:
                verify(symbol, "conditional operator");
                break;
        }
    }

    private void negace() {
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "!");
    }

    private void logVyraz() {
        if(symbol.equals("hodnota")) {
            String value = token.getValue();
            verify(symbol, "hodnota");
            if(!value.equals("pravda") && !value.equals("nepravda")) {
                isSemanticError = true;
                System.out.println(); // TODO: error
            }
            if(!isSyntaxError && !isSemanticError) {
                try {
                    writer.write(number + "   LIT   0   " + (value.equals("pravda") ? "1" : "0"));
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(symbol.equals("IDENTIFIKATOR")) {
            String name = token.getName();
            verify(symbol, "IDENTIFIKATOR");
            if(!isSyntaxError && !isSemanticError) {
                checkIfIsUsableForOps(name, "logicky");
            }
            if(symbol.equals("(")) {
                volaniFunkce(name);
            }
            try {
                writer.write(number + "   LOD   0   " + "not ready");
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            if(!symbol.equals("end")) {
                System.out.println("Error: expected \"value\" or \"function call\", was \"" + symbol + "\"");
                getNextSymbol();
            }
            isSyntaxError = true;
        }
    }

    private void matVyraz() {
        term();
        String val = matVyraz2();
        if(!isSyntaxError && !isSemanticError && val != null) {
            try {
                writer.write(number + "   OPR   0   " + (val.equals("+") ? "2" : "3"));
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String matVyraz2() {
        String val = symbol;
        if(symbol.equals("+")) {
            verify(symbol, "+");
        }
        else if(symbol.equals("-")) {
            verify(symbol, "-");
        }
        else {
            return null;
        }
        term();
        String val2 = matVyraz2();
        if(!isSyntaxError && !isSemanticError && val2 != null) {
            try {
                writer.write(number + "   OPR   0   " + (val2.equals("+") ? "2" : "3"));
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return val;
    }

    private void term() {
        faktor();
        String val = term2();
        if(!isSyntaxError && !isSemanticError && val != null) {
            try {
                writer.write(number + "   OPR   0   " + (val.equals("*") ? "4" : "5"));
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String term2() {
        String val = symbol;
        if(symbol.equals("*")) {
            verify(symbol, "*");
        }
        else if(symbol.equals("/")) {
            verify(symbol, "/");
        }
        else {
            return null;
        }
        faktor();
        String val2 = term2();
        if(!isSyntaxError && !isSemanticError && val2 != null) {
            try {
                writer.write(number + "   OPR   0   " + (val2.equals("*") ? "4" : "5"));
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return val;
    }

    private void faktor() {
        switch (symbol) {
            case "(":
                verify(symbol, "(");
                matVyraz();
                verify(symbol, ")");
                break;
            case "IDENTIFIKATOR":
                String name = token.getValue();
                verify(symbol, "IDENTIFIKATOR");
                if(!isSyntaxError && !isSemanticError) {
                    checkIfIsUsableForOps(name, "cislo");
                }
                if(symbol.equals("(")) {
                    volaniFunkce(name);
                }
                if(!isSyntaxError && !isSemanticError) {
                    try {
                        writer.write(number + "   LOD   0   " + "not ready");
                        writer.newLine();
                        number++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "hodnota":
                String value = token.getValue();
                verify(symbol, "hodnota");
                if(!isSyntaxError && !isSemanticError && !value.matches("\\d+")) {
                    isSemanticError = true;
                    System.out.println(""); // TODO: error
                }
                if(!isSyntaxError && !isSemanticError) {
                    try {
                        writer.write(number + "   LIT   0   " + Integer.parseInt(value));
                        writer.newLine();
                        number++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                if (!symbol.equals("end")) {
                    System.out.println("Error: expected \"value\" or \"function call\", was \"" + symbol + "\"");
                    getNextSymbol();
                }
                isSyntaxError = true;
                break;
        }
    }

    //############################SEMANTIC##############################

    private void checkIfExistsInScope(List<SymbolTableEntry> entries, String name, String elementType) {
        entries.forEach(entry -> {
            if(entry.getElementType().equals(elementType) && entry.getName().equals(name)) {
                isSemanticError = true;
                System.out.println("Element \"" + elementType + "\" name: \"" + name + "\" already defined in the scope!");
            }
        });
    }

    private void checkIfIsUsableForOps(String name, String type) {
        AtomicBoolean isFound = new AtomicBoolean(false);
        SymbolTable symbolTable = symbolTables.peek();

        checkAllEntries(name, type, isFound, symbolTable);

        if(!isFound.get() && symbolTables.size() > 1) {
            symbolTable = symbolTables.firstElement();
            checkAllEntries(name, type, isFound, symbolTable);
        }

        if(!isFound.get()) {
            isSemanticError = true;
            System.out.println("not found"); // TODO: error
        }
    }

    private void checkAllEntries(String name, String type, AtomicBoolean isFound, SymbolTable symbolTable) {
        for(SymbolTableEntry entry : symbolTable.getEntries()) {
            if(entry.getName().equals(name)) {
                if(!entry.getType().equals(type)) {
                    isSemanticError = true;
                    System.out.println("wrong type"); // TODO: error
                }
                isFound.set(true);
                break;
            }
        }
    }

    private SymbolTableEntry findFunction(String name) {
        SymbolTable symbolTable = symbolTables.firstElement();
        for(SymbolTableEntry entry : symbolTable.getEntries()) {
            if(entry.getName().equals(name)) {
                if(entry.getElementType().equals("func") || entry.getElementType().equals("proc")) {
                    return entry;
                }
                else {
                    isSemanticError = true;
                    System.out.println("not a func"); // TODO: error
                    return null;
                }
            }
        }

        isSemanticError = true;
        System.out.println("not found"); // TODO: error
        return null;
    }

    private SymbolTableEntry findVar(String name) {
        SymbolTable symbolTable = symbolTables.peek();
        for(SymbolTableEntry entry : symbolTable.getEntries()) {
            if(entry.getName().equals(name)) {
                if(entry.getElementType().equals("var")) {
                    return entry;
                }
                else {
                    isSemanticError = true;
                    System.out.println("not a var"); // TODO: error
                    return null;
                }
            }
        }

        if(symbolTables.size() > 1) {
            symbolTable = symbolTables.firstElement();
            for(SymbolTableEntry entry : symbolTable.getEntries()) {
                if(entry.getName().equals(name)) {
                    if(entry.getElementType().equals("var")) {
                        return entry;
                    }
                    else {
                        isSemanticError = true;
                        System.out.println("not a var"); // TODO: error
                        return null;
                    }
                }
            }
        }

        isSemanticError = true;
        System.out.println("not found"); // TODO: error
        return null;
    }

    private SymbolTableEntry findIdent(String name) {
        SymbolTable symbolTable = symbolTables.peek();
        for(SymbolTableEntry entry : symbolTable.getEntries()) {
            if(entry.getName().equals(name)) {
               return entry;
            }
        }

        if(symbolTables.size() > 1) {
            symbolTable = symbolTables.firstElement();
            for(SymbolTableEntry entry : symbolTable.getEntries()) {
                if(entry.getName().equals(name)) {
                    return entry;
                }
            }
        }

        isSemanticError = true;
        System.out.println("not found"); // TODO: error
        return null;
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

    //############################GENERATOR##############################


}
