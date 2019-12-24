package analyzer.synsemgen;

import analyzer.Token;
import com.sun.xml.internal.ws.api.ha.StickyFeature;

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
    private int curLevel = 0;
    private int number = 0;
    private int bodyNumber = 0;

    private int numberOfLocalVars = 0;

    private Iterator<Token> iterator;
    private String symbol;
    private Token token;
    private Stack<SymbolTable> symbolTables;

    private boolean isSyntaxError = false;
    private boolean isSemanticError = false;

    private BufferedWriter writer;

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
        curLevel++;
        bodyNumber = number;
        number += 2;
        funkceProcedury();
        curLevel--;

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
                    numberOfLocalVars++;
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
        int funProcStart = number;
        numberOfLocalVars = 0;
        position = 3;
        if(symbol.equals("end")) {
            return;
        }
        if(symbol.equals("funkce")) {
            verify(symbol, "funkce");
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

            parameters.forEach(entry -> {
                symbolTables.peek().getEntries().add(entry);
            });

            checkIfExistsInScope(symbolTable.getEntries(), name, "func");
            if(!isSyntaxError && !isSemanticError) {
                symbolTable.getEntries().add(new SymbolTableEntry(funProcStart, 0, name, type, false, "func", parameters));
            }

            if(!isSyntaxError && !isSemanticError) {
                for(int i = parameters.size(); i > 0; i--) {
                    try {
                        writer.write(number + "   LOD   0   -" + i);
                        writer.newLine();
                        number++;
                        writer.write(number + "   STO   0   " + parameters.get(Math.abs(i -
                                parameters.size())).getPosition());
                        writer.newLine();
                        position++;
                        number++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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

            parameters.forEach(entry -> {
                symbolTables.peek().getEntries().add(entry);
            });

            checkIfExistsInScope(symbolTable.getEntries(), name, "proc");
            if(!isSyntaxError && !isSemanticError) {
                symbolTable.getEntries().add(new SymbolTableEntry(funProcStart, 0, name, "", false, "proc", parameters));
            }

            if(!isSyntaxError && !isSemanticError) {
                for(int i = parameters.size(); i > 0; i--) {
                    try {
                        writer.write(number + "   LOD   0   -" + i);
                        writer.newLine();
                        number++;
                        writer.write(number + "   STO   0   " + parameters.get(Math.abs(i -
                                parameters.size())).getPosition());
                        writer.newLine();
                        number++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
        if(symbol.equals(")")) {
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
            position++;
            numberOfLocalVars++;
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
        if(!isSyntaxError && !isSemanticError) {
            try {
                writer.write(number + "   RET   0   0");
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        if(!isSyntaxError && !isSemanticError) {
            try {
                writer.write(number + "   RET   0   0");
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                    if(!isSyntaxError && !isSemanticError && entry != null) {
                        try {
                            writer.write(number + "   STO   " + (curLevel - entry.getLevel()) + "   " +
                                    entry.getPosition());
                            writer.newLine();
                            number++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
        int currNumber = number;
        number++;
        verify(symbol, ")");
        verify(symbol, "{");
        viceAkci();
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "}");
        if(!isSyntaxError && !isSemanticError) {
            try {
                writer.write(currNumber + "   JMC   0   " + number);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
        if (symbol.equals("zatimco")) {
            verify(symbol, "zatimco");
            verify(symbol, "(");
            int startNumber = number;
            //slozitaPodminka();
            slozPodm();
            if (symbol.equals("end")) {
                return;
            }
            int currNumber = number;
            number++;
            verify(symbol, ")");
            verify(symbol, "{");
            viceAkci();
            if (symbol.equals("end")) {
                return;
            }
            verify(symbol, "}");
            if (!isSyntaxError && !isSemanticError) {
                try {
                    writer.write(currNumber + "   JMC   0   " + (number + 1));
                    writer.newLine();
                    writer.write(number + "   JMP   0   " + startNumber);
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
            if(!isSyntaxError && !isSemanticError && entry != null) {
                try {
                    writer.write(number + "   LOD   " + (curLevel - entry.getLevel()) + "   " +
                            entry.getPosition());
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

            if(!isSyntaxError && !isSemanticError) {
                try {
                    if(typeL.equals("cislo")) {
                        writer.write(number + "   LIT   0   " + value);
                    }
                    else {
                        writer.write(number + "   LIT   0   " + (value.equals("pravda") ? "1" : "0"));
                    }
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            // TODO: error
            verify(symbol, "value or ident");
        }

        String op = podmOperator(typeL);
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
            if(!isSyntaxError && !isSemanticError && entry != null) {
                try {
                    writer.write(number + "   LOD   " + (curLevel - entry.getLevel()) + "   " +
                            entry.getPosition());
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

            if(!isSyntaxError && !isSemanticError) {
                try {
                    if(typeR.equals("cislo")) {
                        writer.write(number + "   LIT   0   " + value);
                    }
                    else {
                        writer.write(number + "   LIT   0   " + (value.equals("pravda") ? "1" : "0"));
                    }
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

        if(!isSyntaxError && !isSemanticError && op != null) {
            int opNum = 0;
            try {
                switch (op) {
                    case "<":
                        opNum = 10;
                        break;
                    case "<=":
                        opNum = 13;
                        break;
                    case ">":
                        opNum = 12;
                        break;
                    case ">=":
                        opNum = 11;
                        break;
                    case "==":
                        opNum = 8;
                        break;
                    case "!=":
                        opNum = 9;
                        break;
                }
                writer.write(number + "   OPR   0   " + opNum);
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            String op = null;
            if(symbol.equals("IDENTIFIKATOR") || symbol.equals("hodnota")) {
                podminka();
            }
            else {
                verify(symbol, "(");
                slozitaPodminka();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, ")");
                op = symbol;
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
            if(!isSyntaxError && !isSemanticError && op != null) {
                int opNum = 0;
                try {
                    switch (op) {
                        case "&&":
                            opNum = 4;
                            break;
                        case "||":
                            opNum = 2;
                            break;
                    }
                    writer.write(number + "   OPR   0   " + opNum);
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void volaniFunkce(String name) {
        if(symbol.equals("end")) {
            return;
        }
        SymbolTableEntry entry = findFunction(name);
        List<SymbolTableEntry> parameters = null;
        if(entry != null) {
            parameters = entry.getParameters();
        }
        verify(symbol, "(");
        if(!symbol.equals(")")) {
            vstupHodnoty(parameters, 0);
            if(symbol.equals("end")) {
                return;
            }
        }
        verify(symbol, ")");
        if(!isSyntaxError && !isSemanticError && entry != null) {
            try {
                writer.write(number + "   CAL   " + (curLevel - entry.getLevel()) + "   " +
                        entry.getPosition());
                writer.newLine();
                number++;
                writer.write(number + "   INT   0   -" + parameters.size());
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void vstupHodnoty(List<SymbolTableEntry> parameters, int index) {
        if(symbol.equals("end")) {
            return;
        }
        if(symbol.equals("IDENTIFIKATOR")) {
            String name = token.getName();
            verify(symbol, "IDENTIFIKATOR");
            if(!isSyntaxError && !isSemanticError) {
                checkIfIsUsableForOps(name, parameters.get(index).getType());
            }
            SymbolTableEntry entry = findVar(name);
            if(!isSyntaxError && !isSemanticError && entry != null) {
                try {
                    writer.write(number + "   LOD   " + (curLevel - entry.getLevel()) + "   " +
                            entry.getPosition());
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            if(!isSyntaxError && !isSemanticError && !type.equals(parameters.get(index).getType())) {
                isSemanticError = true;
                System.out.println(""); // TODO: error
            }
            if(!isSyntaxError && !isSemanticError) {
                try {
                    if(type.equals("cislo")) {
                        writer.write(number + "   LIT   0   " + value);
                    }
                    else {
                        writer.write(number + "   LIT   0   " + (value.equals("pravda") ? "1" : "0"));
                    }
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(symbol.equals(",")) {
            verify(symbol, ",");
            index++;
            vstupHodnoty(parameters, index);
        }
        if(parameters != null && index != parameters.size() - 1) {
            isSemanticError = true;
            System.out.println(""); // TODO: error
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
            SymbolTableEntry entry = findVar(name);
            if(!isSyntaxError && !isSemanticError && entry != null) {
                try {
                    writer.write(number + "   LOD   " + (curLevel - entry.getLevel()) + "   " +
                            entry.getPosition());
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            if(!isSyntaxError && !isSemanticError) {
                try {
                    if(type.equals("logicky")) {
                        writer.write(number + "   LIT   0   " + (value.equals("pravda") ? "1" : "0"));
                    }
                    else {
                        writer.write(number + "   LIT   0   " + value);
                    }
                    writer.newLine();
                    number++;
                    writer.write(number + "   STO   0   -" + numberOfLocalVars);
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private String podmOperator(String type) {
        if(symbol.equals("end")) {
            return null;
        }
        String op = symbol;
        switch (symbol) {
            case ">":
                verify(symbol, ">");
                if(type.equals("logicky")) {
                    System.out.println(); // TODO: error
                    isSemanticError = true;
                    op = null;
                }
                break;
            case "<":
                verify(symbol, "<");
                if(type.equals("logicky")) {
                    System.out.println(); // TODO: error
                    isSemanticError = true;
                    op = null;
                }
                break;
            case "<=":
                verify(symbol, "<=");
                if(type.equals("logicky")) {
                    System.out.println(); // TODO: error
                    isSemanticError = true;
                    op = null;
                }
                break;
            case ">=":
                verify(symbol, ">=");
                if(type.equals("logicky")) {
                    System.out.println(); // TODO: error
                    isSemanticError = true;
                    op = null;
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

        return op;
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
            else {
                SymbolTableEntry entry = findVar(name);
                if(!isSyntaxError && !isSemanticError && entry != null) {
                    try {
                        writer.write(number + "   LOD   " + (curLevel - entry.getLevel()) + "   " +
                                entry.getPosition());
                        writer.newLine();
                        number++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
                else {
                    SymbolTableEntry entry = findVar(name);
                    if(!isSyntaxError && !isSemanticError && entry != null) {
                        try {
                            writer.write(number + "   LOD   " + (curLevel - entry.getLevel()) + "   " +
                                    entry.getPosition());
                            writer.newLine();
                            number++;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

    private void slozPodm() {
        podmTerm();
        String val = slozPodm2();
        if(!isSyntaxError && !isSemanticError && val != null) {
            try {
                writer.write(number + "   OPR   0   2");
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String slozPodm2() {
        String val = symbol;
        if(symbol.equals("||")) {
            verify(symbol, "||");
        }
        else {
            return null;
        }
        podmTerm();
        String val2 = slozPodm2();
        if(!isSyntaxError && !isSemanticError && val2 != null) {
            try {
                writer.write(number + "   OPR   0   2");
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return val;
    }

    private void podmTerm() {
        podmFaktor();
        String val = podmTerm2();
        if(!isSyntaxError && !isSemanticError && val != null) {
            try {
                writer.write(number + "   OPR   0   4");
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String podmTerm2() {
        String val = symbol;
        if(symbol.equals("&&")) {
            verify(symbol, "&&");
        }
        else {
            return null;
        }
        podmFaktor();
        String val2 = term2();
        if(!isSyntaxError && !isSemanticError && val2 != null) {
            try {
                writer.write(number + "   OPR   0   4");
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return val;
    }

    private void podmFaktor() {
        String typeL = "";
        String typeR = "";
        if(symbol.equals("end")) {
            return;
        }
        if(symbol.equals("(")) {
            verify(symbol, "(");
            slozPodm();
            verify(symbol, ")");
            return;
        }
        else if(symbol.equals("!")) {
            verify(symbol, "!");
            verify(symbol, "(");
            podmFaktor();
            verify(symbol, ")");
            return;
        }
        if(symbol.equals("IDENTIFIKATOR")) {
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            SymbolTableEntry entry = findIdent(name);
            if(entry != null) {
                typeL = entry.getType();
            }
            if(!isSyntaxError && !isSemanticError && entry != null) {
                try {
                    writer.write(number + "   LOD   " + (curLevel - entry.getLevel()) + "   " +
                            entry.getPosition());
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

            if(!isSyntaxError && !isSemanticError) {
                try {
                    if(typeL.equals("cislo")) {
                        writer.write(number + "   LIT   0   " + value);
                    }
                    else {
                        writer.write(number + "   LIT   0   " + (value.equals("pravda") ? "1" : "0"));
                    }
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            // TODO: error
            verify(symbol, "value or ident");
        }

        String op = podmOperator(typeL);
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
            if(!isSyntaxError && !isSemanticError && entry != null) {
                try {
                    writer.write(number + "   LOD   " + (curLevel - entry.getLevel()) + "   " +
                            entry.getPosition());
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

            if(!isSyntaxError && !isSemanticError) {
                try {
                    if(typeR.equals("cislo")) {
                        writer.write(number + "   LIT   0   " + value);
                    }
                    else {
                        writer.write(number + "   LIT   0   " + (value.equals("pravda") ? "1" : "0"));
                    }
                    writer.newLine();
                    number++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

        if(!isSyntaxError && !isSemanticError && op != null) {
            int opNum = 0;
            try {
                switch (op) {
                    case "<":
                        opNum = 10;
                        break;
                    case "<=":
                        opNum = 13;
                        break;
                    case ">":
                        opNum = 12;
                        break;
                    case ">=":
                        opNum = 11;
                        break;
                    case "==":
                        opNum = 8;
                        break;
                    case "!=":
                        opNum = 9;
                        break;
                }
                writer.write(number + "   OPR   0   " + opNum);
                writer.newLine();
                number++;
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                /*
                else {
                    isSemanticError = true;
                    System.out.println("not a func"); // TODO: error
                    return null;
                }

                 */
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
                /*
                else {
                    isSemanticError = true;
                    System.out.println("not a var"); // TODO: error
                    return null;
                }

                 */
            }
        }

        if(symbolTables.size() > 1) {
            symbolTable = symbolTables.firstElement();
            for(SymbolTableEntry entry : symbolTable.getEntries()) {
                if(entry.getName().equals(name)) {
                    if(entry.getElementType().equals("var")) {
                        return entry;
                    }
                    /*
                    else {
                        isSemanticError = true;
                        System.out.println("not a var"); // TODO: error
                        return null;
                    }

                     */
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
}
