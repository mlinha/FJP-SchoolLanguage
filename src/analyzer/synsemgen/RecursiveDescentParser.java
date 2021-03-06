package analyzer.synsemgen;

import analyzer.Token;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementace rekurzivního sestupu pro analýzu a generování kódu
 */
public class RecursiveDescentParser {

    /**
     * Pozice v paměti
     */
    private int position = 3;

    /**
     * Výstupní mapa
     */
    private Map<Integer, String> output;

    /**
     * Aktuální úroven
     */
    private int curLevel = 0;

    /**
     * Číslo řádku
     */
    private int number = 0;

    /**
     * Číslo začátku těla
     */
    private int bodyNumber = 0;

    /**
     * Počet parametrů
     */
    private int numberOfParameters = 0;

    /**
     * Počet vnořených cyklů
     */
    private int numberOfNestedLoops = 0;

    /**
     * Pozice příkazů zastav
     */
    private Stack<List<Integer>> breakPositions = new Stack<>();

    /**
     * Iterátor tokenů
     */
    private Iterator<Token> iterator;

    /**
     * Symbol
     */
    private String symbol;

    /**
     * Token
     */
    private Token token;

    /**
     * Zásobník tabulek symbolů
     */
    private Stack<SymbolTable> symbolTables;

    /**
     * Informace, zda nedošlo k syntaktické chybě
     */
    private boolean isSyntaxError = false;

    /**
     * Informace, zda nedošlo k sémantické chybě
     */
    private boolean isSemanticError = false;

    /**
     * Vytvoří a inicializuje parser
     *
     * @param tokens seznam tokenů
     */
    protected RecursiveDescentParser(List<Token> tokens) {
        iterator = tokens.iterator();
        symbolTables = new Stack<>();
        output = new TreeMap<>();
    }

    /**
     * Načte další symbol
     */
    private void getNextSymbol() {
        if (iterator.hasNext()) {
            token = iterator.next();
            symbol = token.getName();
        } else {
            symbol = "end";
        }

    }

    /**
     * Ověří symbol
     *
     * @param symbol symbol
     * @param with   očekávaná hodnota
     */
    private void verify(String symbol, String with) {
        if (symbol.equals(with)) {
            getNextSymbol();
        } else {
            if (!symbol.equals("end")) {
                System.out.println("Error: expected \"" + with + "\", was \"" + symbol + "\"");
            }
            getNextSymbol();
            isSyntaxError = true;
        }
    }

    //############################GRAMMAR-RULES##############################
    //#####################METODY-REKURZIVNIHO-SESTUPU#######################
    //############################JEDEN-PRUCHOD##############################

    /**
     * Vstupní bod
     *
     * @return informace, zda nedošlo k chybě
     */
    protected boolean program() {
        symbolTables.push(new SymbolTable());

        output.put(number, number + "   JMP   0   1");
        number++;
        output.put(number, number + "   INT   0   3");
        number++;

        getNextSymbol();
        globPromenne();
        curLevel++;
        bodyNumber = number;
        number += 2;
        funkceProcedury();
        curLevel--;

        start();

        symbolTables.pop();

        return isSyntaxError || isSemanticError;
    }

    private void globPromenne() {
        SymbolTable symbolTable = symbolTables.peek();
        if (symbol.equals("end")) {
            return;
        }
        if (!symbol.equals("procedura") && !symbol.equals("funkce")) {
            if (!isSyntaxError && !isSemanticError) {
                output.put(number, number + "   INT   0   1");
                number++;
            }
            boolean isConst = modifikator();
            if (symbol.equals("end")) {
                return;
            }
            String type = typ();
            if (symbol.equals("end")) {
                return;
            }
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "=");

            if (type != null && type.equals("cislo")) {
                matVyraz();
            } else if (type != null && type.equals("logicky")) {
                logVyraz();
            } else {
                System.out.println("Syntax error: No type defined");
                matVyraz();
            }
            verify(symbol, ";");
            checkIfExistsInScope(symbolTable.getEntries(), name, "var");
            if (!isSyntaxError && !isSemanticError) {
                symbolTable.getEntries().add(new SymbolTableEntry(position, 0, name, type, isConst, "var", null));
                output.put(number, number + "   STO   0   " + position);
                position++;

                number++;
            }
            globPromenne();
        }
    }

    private void lokPromenne() {
        SymbolTable symbolTable = symbolTables.peek();
        if (symbol.equals("end")) {
            return;
        }
        if (!symbol.equals("pokud") && !symbol.equals("pro") && !symbol.equals("zatimco") &&
                !symbol.equals("IDENTIFIKATOR") && !symbol.equals("vrat") && !symbol.equals("zastav")) {
            if (!isSyntaxError && !isSemanticError) {
                output.put(number, number + "   INT   0   1");
                number++;
            }
            boolean isConst = modifikator();
            if (symbol.equals("end")) {
                return;
            }
            String type = typ();
            if (symbol.equals("end")) {
                return;
            }
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "=");
            if (type != null && type.equals("cislo")) {
                matVyraz();
            } else if (type != null && type.equals("logicky")) {
                logVyraz();
            } else {
                System.out.println("Syntax error: No type defined");
                matVyraz();
            }
            verify(symbol, ";");
            checkIfExistsInScope(symbolTable.getEntries(), name, "var");
            if (!isSyntaxError && !isSemanticError) {
                symbolTable.getEntries().add(new SymbolTableEntry(position, 1, name, type, isConst,
                        "var", null));

                output.put(number, number + "   STO   0   " + position);
                position++;
                number++;

            }
            lokPromenne();
        }
    }

    private boolean modifikator() {
        if (symbol.equals("end")) {
            return false;
        }
        if (symbol.equals("konst")) {
            verify(symbol, "konst");

            return true;
        }

        return false;
    }

    private void funkceProcedury() {
        SymbolTable symbolTable = symbolTables.peek();
        int funProcStart = number;
        numberOfParameters = 0;
        position = 3;
        if (symbol.equals("end")) {
            return;
        }
        if (symbol.equals("funkce")) {
            verify(symbol, "funkce");
            if (!isSyntaxError && !isSemanticError) {

                output.put(number, number + "   INT   0   3");
                number++;

            }
            String type = typ();
            if (symbol.equals("end")) {
                return;
            }
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "(");
            List<SymbolTableEntry> parameters = new ArrayList<>();
            parametry(parameters);
            numberOfParameters = parameters.size();
            if (symbol.equals("end")) {
                return;
            }
            verify(symbol, ")");
            verify(symbol, "{");

            symbolTables.push(new SymbolTable());

            parameters.forEach(entry -> symbolTables.peek().getEntries().add(entry));

            checkIfExistsInScope(symbolTable.getEntries(), name, "func");
            if (!isSyntaxError && !isSemanticError) {
                symbolTable.getEntries().add(new SymbolTableEntry(funProcStart, 0, name, type, false, "func", parameters));
            }

            if (!isSyntaxError && !isSemanticError) {
                for (int i = parameters.size(); i > 0; i--) {

                    output.put(number, number + "   LOD   0   -" + i);

                    number++;
                    output.put(number, number + "   STO   0   " + parameters.get(Math.abs(i -
                            parameters.size())).getPosition());

                    number++;

                }
            }

            vnitrekFunkce(type);
            symbolTables.pop();
            if (symbol.equals("end")) {
                return;
            }
            verify(symbol, "}");
            funkceProcedury();
        } else if (symbol.equals("procedura")) {
            verify(symbol, "procedura");
            if (!isSyntaxError && !isSemanticError) {

                output.put(number, number + "   INT   0   3");

                number++;

            }
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "(");
            List<SymbolTableEntry> parameters = new ArrayList<>();
            parametry(parameters);
            if (symbol.equals("end")) {
                return;
            }
            verify(symbol, ")");
            verify(symbol, "{");

            symbolTables.push(new SymbolTable());

            parameters.forEach(entry -> symbolTables.peek().getEntries().add(entry));

            checkIfExistsInScope(symbolTable.getEntries(), name, "proc");
            if (!isSyntaxError && !isSemanticError) {
                symbolTable.getEntries().add(new SymbolTableEntry(funProcStart, 0, name, "", false, "proc", parameters));
            }

            if (!isSyntaxError && !isSemanticError) {
                for (int i = parameters.size(); i > 0; i--) {
                    output.put(number, number + "   LOD   0   -" + i);

                    number++;
                    output.put(number, number + "   STO   0   " + parameters.get(Math.abs(i -
                            parameters.size())).getPosition());

                    number++;
                }
            }

            vnitrekProcedury();
            symbolTables.pop();
            verify(symbol, "}");
            funkceProcedury();
        }
    }

    private void parametry(List<SymbolTableEntry> parameters) {
        if (symbol.equals("end")) {
            return;
        }
        String type = typ();
        if (symbol.equals("end")) {
            return;
        }
        if (symbol.equals(")")) {
            return;
        }
        if (!isSyntaxError && !isSemanticError) {
            output.put(number, number + "   INT   0   1");
            number++;
        }
        String name = token.getValue();
        verify(symbol, "IDENTIFIKATOR");
        if (!isSyntaxError && !isSemanticError) {
            parameters.add(new SymbolTableEntry(position, 1, name, type, false, "var", null));
            position++;
        }
        if (symbol.equals(",")) {
            verify(symbol, ",");
            parametry(parameters);
        }
    }

    private void vnitrekFunkce(String typeOut) {
        if (symbol.equals("end")) {
            return;
        }
        lokPromenne();
        if (symbol.equals("end")) {
            return;
        }
        viceAkci();
        if (symbol.equals("end")) {
            return;
        }
        vracHodnoty(typeOut);
        if (!isSyntaxError && !isSemanticError) {
            output.put(number, number + "   RET   0   0");
            number++;
        }
    }

    private void vnitrekProcedury() {
        if (symbol.equals("end")) {
            return;
        }
        lokPromenne();
        if (symbol.equals("end")) {
            return;
        }
        viceAkci();
        if (!isSyntaxError && !isSemanticError) {
            output.put(number, number + "   RET   0   0");
            number++;
        }
    }

    private void viceAkci() {
        if (symbol.equals("end")) {
            return;
        }
        switch (symbol) {
            case "pokud":
                rozhodnuti();
                if (symbol.equals("end")) {
                    return;
                }
                viceAkci();
                break;
            case "IDENTIFIKATOR":
                String name = token.getValue();
                verify(symbol, "IDENTIFIKATOR");
                if (symbol.equals("(")) {
                    volaniFunkce(name);
                } else {
                    SymbolTableEntry entry = findVar(name);
                    String type = "";
                    if (entry != null) {
                        type = entry.getType();
                    }
                    if (entry != null && entry.isConst()) {
                        isSemanticError = true;
                        System.out.println("Semantic error: variable is a constant!");
                    }
                    verify(symbol, "=");
                    if (type.equals("logicky")) {
                        logVyraz();
                    } else {
                        matVyraz();
                    }
                    if (!isSyntaxError && !isSemanticError && entry != null) {
                        output.put(number, number + "   STO   " + (curLevel - entry.getLevel()) + "   " +
                                entry.getPosition());
                        number++;
                    }
                }
                verify(symbol, ";");
                viceAkci();
                break;
            case "zatimco":
            case "pro":
                cyklus();
                if (symbol.equals("end")) {
                    return;
                }
                viceAkci();
                break;
            case "zastav":
                zastaveni();
                if (symbol.equals("end")) {
                    return;
                }
                viceAkci();
                break;
        }
    }

    private void rozhodnuti() {
        if (symbol.equals("end")) {
            return;
        }
        verify(symbol, "pokud");
        verify(symbol, "(");
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
        int endIFNumber = number;
        number++;
        verify(symbol, "}");
        if (!isSyntaxError && !isSemanticError) {
            output.put(currNumber, currNumber + "   JMC   0   " + number);
        }

        if (symbol.equals("pokudne")) {
            verify(symbol, "pokudne");
            verify(symbol, "{");
            viceAkci();
            if (symbol.equals("end")) {
                return;
            }
            verify(symbol, "}");
        }

        if (!isSyntaxError && !isSemanticError) {
            output.put(endIFNumber, endIFNumber + "   JMP   0   " + number);
        }
    }

    private void cyklus() {
        if (symbol.equals("end")) {
            return;
        }
        if (symbol.equals("zatimco")) {
            numberOfNestedLoops++;
            breakPositions.push(new ArrayList<>());
            verify(symbol, "zatimco");
            verify(symbol, "(");
            int startNumber = number;
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
            numberOfNestedLoops--;
            if (!isSyntaxError && !isSemanticError) {
                List<Integer> breakPos = breakPositions.pop();
                if (breakPos.size() > 0) {
                    for (Integer i : breakPos) {
                        output.put(i, i + "   JMP   0   " + (number + 1));
                    }

                }
                output.put(currNumber, currNumber + "   JMC   0   " + (number + 1));
                output.put(number, number + "   JMP   0   " + startNumber);
                number++;
            }
        }
    }

    private void volaniFunkce(String name) {
        if (symbol.equals("end")) {
            return;
        }
        SymbolTableEntry entry = findFunction(name);
        List<SymbolTableEntry> parameters = null;
        if (entry != null) {
            parameters = entry.getParameters();
        }
        if (!isSyntaxError && !isSemanticError && entry != null && entry.getElementType().equals("func")) {
            output.put(number, number + "   INT   0   1");
            number++;
        }
        verify(symbol, "(");
        int index = 0;
        index = vstupHodnoty(parameters, index);
        if (parameters != null && index < parameters.size()) {
            isSemanticError = true;
            System.out.println("Semantic error: not enough parameters. Expected: \"" + parameters.size() + "\"");
        }
        if (symbol.equals("end")) {
            return;
        }
        verify(symbol, ")");
        if (!isSyntaxError && !isSemanticError && entry != null) {
            output.put(number, number + "   CAL   " + (curLevel - entry.getLevel()) + "   " +
                    entry.getPosition());
            number++;
            if (parameters != null) {
                output.put(number, number + "   INT   0   -" + parameters.size());
            }
            number++;
        }
    }

    private int vstupHodnoty(List<SymbolTableEntry> parameters, int index) {
        if (symbol.equals("end")) {
            return index;
        }
        if (parameters != null && index > parameters.size()) {
            isSemanticError = true;
            System.out.println("Semantic error: too many parameters. Expected: \"" + parameters.size() + "\"");
            return index;
        }
        if (symbol.equals("IDENTIFIKATOR")) {
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            if (!isSyntaxError && !isSemanticError && parameters != null) {
                checkIfIsUsableForOps(name, parameters.get(index).getType());
            }
            SymbolTableEntry entry = findVar(name);
            if (!isSyntaxError && !isSemanticError && entry != null) {
                output.put(number, number + "   LOD   " + (curLevel - entry.getLevel()) + "   " +
                        entry.getPosition());
                number++;
            }
            index++;
        } else if (symbol.equals("hodnota")) {
            String value = token.getValue();
            String type;
            verify(symbol, "hodnota");
            if (value.matches("\\d+") || value.matches("0")) {
                type = "cislo";
            } else {
                type = "logicky";
            }
            if (parameters != null && !isSyntaxError && !isSemanticError &&
                    !type.equals(parameters.get(index).getType())) {
                isSemanticError = true;
                System.out.println("Semantic error: wrong type of parameter! Expected: \"" +
                        type.equals(parameters.get(index).getType()) + "\"");
            }
            if (!isSyntaxError && !isSemanticError) {
                if (type.equals("cislo")) {
                    output.put(number, number + "   LIT   0   " + value);
                } else {
                    output.put(number, number + "   LIT   0   " + (value.equals("pravda") ? "1" : "0"));
                }
                number++;
            }
            index++;
        } else {
            if (!symbol.equals(")")) {
                verify(symbol, "value or IDENTIFIER");
            }
        }

        if (symbol.equals(",")) {
            verify(symbol, ",");
            index = vstupHodnoty(parameters, index);
        }

        return index;
    }

    private void vracHodnoty(String typeOut) {
        if (symbol.equals("end")) {
            return;
        }
        verify(symbol, "vrat");
        if (symbol.equals("IDENTIFIKATOR")) {
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            if (!isSyntaxError && !isSemanticError) {
                checkIfIsUsableForOps(name, typeOut);
            }
            SymbolTableEntry entry = findVar(name);
            if (!isSyntaxError && !isSemanticError && entry != null) {
                output.put(number, number + "   LOD   " + (curLevel - entry.getLevel()) + "   " +
                        entry.getPosition());
                number++;
                output.put(number, number + "   STO   0   -" + (numberOfParameters + 1));
                number++;
            }
        } else if (symbol.equals("hodnota")) {
            String value = token.getValue();
            String type;
            verify(symbol, "hodnota");
            if (value.matches("\\d+")) {
                type = "cislo";
            } else {
                type = "logicky";
            }
            if (!isSyntaxError && !isSemanticError && !type.equals(typeOut)) {
                isSemanticError = true;
                System.out.println("Semantic error: wrong type of return value. Expected: \"" + typeOut + "\"");
            }
            if (!isSyntaxError && !isSemanticError) {
                if (type.equals("logicky")) {
                    output.put(number, number + "   LIT   0   " + (value.equals("pravda") ? "1" : "0"));
                } else {
                    output.put(number, number + "   LIT   0   " + value);
                }
                number++;
                output.put(number, number + "   STO   0   -" + (numberOfParameters + 1));
                number++;
            }
        } else {
            verify(symbol, "value or variable IDENTIFIER");
        }
        verify(symbol, ";");
    }

    private void zastaveni() {
        if (numberOfNestedLoops == 0) {
            isSemanticError = true;
            System.out.println("Semantic error: \"zastav\" not in a loop");
            return;
        }
        if (symbol.equals("end")) {
            return;
        }
        verify(symbol, "zastav");
        verify(symbol, ";");
        breakPositions.peek().add(number);
        number++;
    }

    private String typ() {
        if (symbol.equals("end")) {
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
        if (symbol.equals("end")) {
            return null;
        }
        String op = symbol;
        switch (symbol) {
            case ">":
                verify(symbol, ">");
                if (type.equals("logicky")) {
                    System.out.println("Semantic error: cannot use \">\" for logical operations!");
                    isSemanticError = true;
                    op = null;
                }
                break;
            case "<":
                verify(symbol, "<");
                if (type.equals("logicky")) {
                    System.out.println("Semantic error: cannot use \"<\" for logical operations!");
                    isSemanticError = true;
                    op = null;
                }
                break;
            case "<=":
                verify(symbol, "<=");
                if (type.equals("logicky")) {
                    System.out.println("Semantic error: cannot use \"<=\" for logical operations!");
                    isSemanticError = true;
                    op = null;
                }
                break;
            case ">=":
                verify(symbol, ">=");
                if (type.equals("logicky")) {
                    System.out.println("Semantic error: cannot use \">=\" for logical operations!");
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

    private void logVyraz() {
        if (symbol.equals("hodnota")) {
            String value = token.getValue();
            verify(symbol, "hodnota");
            if (!value.equals("pravda") && !value.equals("nepravda")) {
                isSemanticError = true;
                System.out.println("Semantic error: cannot use type \"cislo\" for logical operations!");
            }
            if (!isSyntaxError && !isSemanticError) {
                output.put(number, number + "   LIT   0   " + (value.equals("pravda") ? "1" : "0"));
                number++;
            }
        } else if (symbol.equals("IDENTIFIKATOR")) {
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            if (!isSyntaxError && !isSemanticError) {
                checkIfIsUsableForOps(name, "logicky");
            }
            if (symbol.equals("(")) {
                volaniFunkce(name);
            } else {
                SymbolTableEntry entry = findVar(name);
                if (!isSyntaxError && !isSemanticError && entry != null) {
                    output.put(number, number + "   LOD   " + (curLevel - entry.getLevel()) + "   " +
                            entry.getPosition());
                    number++;
                }
            }
        } else {
            if (!symbol.equals("end")) {
                System.out.println("Error: expected \"value\" or \"function call\", was \"" + symbol + "\"");
                getNextSymbol();
            }
            isSyntaxError = true;
        }
    }

    private void matVyraz() {
        term();
        String val = matVyraz2();
        if (!isSyntaxError && !isSemanticError && val != null) {
            output.put(number, number + "   OPR   0   " + (val.equals("+") ? "2" : "3"));
            number++;
        }
    }

    private String matVyraz2() {
        String val = symbol;
        if (symbol.equals("+")) {
            verify(symbol, "+");
        } else if (symbol.equals("-")) {
            verify(symbol, "-");
        } else {
            return null;
        }
        term();
        String val2 = matVyraz2();
        if (!isSyntaxError && !isSemanticError && val2 != null) {
            output.put(number, number + "   OPR   0   " + (val2.equals("+") ? "2" : "3"));
            number++;
        }

        return val;
    }

    private void term() {
        faktor();
        String val = term2();
        if (!isSyntaxError && !isSemanticError && val != null) {
            output.put(number, number + "   OPR   0   " + (val.equals("*") ? "4" : "5"));
            number++;
        }
    }

    private String term2() {
        String val = symbol;
        if (symbol.equals("*")) {
            verify(symbol, "*");
        } else if (symbol.equals("/")) {
            verify(symbol, "/");
        } else {
            return null;
        }
        faktor();
        String val2 = term2();
        if (!isSyntaxError && !isSemanticError && val2 != null) {
            output.put(number, number + "   OPR   0   " + (val2.equals("*") ? "4" : "5"));
            number++;
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
                if (!isSyntaxError && !isSemanticError) {
                    checkIfIsUsableForOps(name, "cislo");
                }
                if (symbol.equals("(")) {
                    volaniFunkce(name);
                } else {
                    SymbolTableEntry entry = findVar(name);
                    if (!isSyntaxError && !isSemanticError && entry != null) {
                        output.put(number, number + "   LOD   " + (curLevel - entry.getLevel()) + "   " +
                                entry.getPosition());
                        number++;
                    }
                }
                break;
            case "hodnota":
                String value = token.getValue();
                verify(symbol, "hodnota");
                if (!isSyntaxError && !isSemanticError && !value.matches("-?\\d+") && !value.matches("0")) {
                    isSemanticError = true;
                    System.out.println("Semantic error: cannot use \"logicky\" for numeric operations!");
                }
                if (!isSyntaxError && !isSemanticError) {
                    output.put(number, number + "   LIT   0   " + Integer.parseInt(value));
                    number++;
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
        if (!isSyntaxError && !isSemanticError && val != null) {
            output.put(number, number + "   OPR   0   2");
            number++;
        }
    }

    private String slozPodm2() {
        String val = symbol;
        if (symbol.equals("||")) {
            verify(symbol, "||");
        } else {
            return null;
        }
        podmTerm();
        String val2 = slozPodm2();
        if (!isSyntaxError && !isSemanticError && val2 != null) {
            output.put(number, number + "   OPR   0   2");
            number++;
        }

        return val;
    }

    private void podmTerm() {
        podmFaktor();
        String val = podmTerm2();
        if (!isSyntaxError && !isSemanticError && val != null) {
            output.put(number, number + "   OPR   0   4");
            number++;
        }
    }

    private String podmTerm2() {
        String val = symbol;
        if (symbol.equals("&&")) {
            verify(symbol, "&&");
        } else {
            return null;
        }
        podmFaktor();
        String val2 = term2();
        if (!isSyntaxError && !isSemanticError && val2 != null) {
            output.put(number, number + "   OPR   0   4");
            number++;
        }

        return val;
    }

    private void podmFaktor() {
        String typeL = "";
        String typeR = "";
        if (symbol.equals("end")) {
            return;
        }
        if (symbol.equals("(")) {
            verify(symbol, "(");
            slozPodm();
            verify(symbol, ")");
            return;
        } else if (symbol.equals("!")) {
            verify(symbol, "!");
            if (!isSyntaxError && !isSemanticError) {
                output.put(number, number + "   LIT   0   1");
                number++;
            }
            verify(symbol, "(");
            podmFaktor();
            verify(symbol, ")");
            if (!isSyntaxError && !isSemanticError) {
                output.put(number, number + "   OPR   0   3");
                number++;
            }
            return;
        }
        if (symbol.equals("IDENTIFIKATOR")) {
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            SymbolTableEntry entry = findVar(name);
            if (entry != null) {
                typeL = entry.getType();
            }
            if (!isSyntaxError && !isSemanticError && entry != null) {
                output.put(number, number + "   LOD   " + (curLevel - entry.getLevel()) + "   " +
                        entry.getPosition());
                number++;
            }
        } else if (symbol.equals("hodnota")) {
            String value = token.getValue();
            verify(symbol, "hodnota");
            if (value.matches("-?\\d+") || !value.matches("0")) {
                typeL = "cislo";
            } else {
                typeL = "logicky";
            }

            if (!isSyntaxError && !isSemanticError) {
                if (typeL.equals("cislo")) {
                    output.put(number, number + "   LIT   0   " + value);
                } else {
                    output.put(number, number + "   LIT   0   " + (value.equals("pravda") ? "1" : "0"));
                }
                number++;
            }
        } else {
            verify(symbol, "value or IDENTIFIER");
        }

        String op = podmOperator(typeL);
        if (symbol.equals("end")) {
            return;
        }
        if (symbol.equals("IDENTIFIKATOR")) {
            String name = token.getValue();
            verify(symbol, "IDENTIFIKATOR");
            SymbolTableEntry entry = findVar(name);
            if (entry != null) {
                typeR = entry.getType();
            }
            if (!isSyntaxError && !isSemanticError && entry != null) {
                output.put(number, number + "   LOD   " + (curLevel - entry.getLevel()) + "   " +
                        entry.getPosition());
                number++;
            }
        } else if (symbol.equals("hodnota")) {
            String value = token.getValue();
            verify(symbol, "hodnota");
            if (value.matches("\\d+") || value.matches("0")) {
                typeR = "cislo";
            } else {
                typeR = "logicky";
            }

            if (!isSyntaxError && !isSemanticError) {
                if (typeR.equals("cislo")) {
                    output.put(number, number + "   LIT   0   " + value);
                } else {
                    output.put(number, number + "   LIT   0   " + (value.equals("pravda") ? "1" : "0"));
                }
                number++;
            }
        } else {
            verify(symbol, "value or IDENTIFIER");
        }

        if (!typeL.equals(typeR)) {
            isSemanticError = true;
            System.out.println("Semantic error: cannot compare two different types!");
        }

        if (!isSyntaxError && !isSemanticError && op != null) {
            int opNum = 0;
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
            output.put(number, number + "   OPR   0   " + opNum);
            number++;
        }
    }

    private void start() {
        if (symbol.equals("end")) {
            return;
        }
        String name = token.getValue();
        verify(symbol, "IDENTIFIKATOR");
        SymbolTableEntry entry = findFunction(name);
        verify(symbol, "(");
        verify(symbol, ")");
        verify(symbol, ";");
        if (entry != null && entry.getElementType().equals("func")) {
            isSemanticError = true;
            System.out.println("Semantic error: procedure with no parameters has to be called to start the program!");
        }
        if (!isSyntaxError && !isSemanticError && entry != null) {
            output.put(bodyNumber, bodyNumber + "   CAL   0   " + entry.getPosition());
            bodyNumber++;
            output.put(bodyNumber, bodyNumber + "   RET   0   0");
        }
    }

    //############################SEMANTIC##############################

    /**
     * Zkotroluje, zda záznam existuje v scopu
     *
     * @param entries     záznamy
     * @param name        jméno
     * @param elementType typ záznamu
     */
    private void checkIfExistsInScope(List<SymbolTableEntry> entries, String name, String elementType) {
        entries.forEach(entry -> {
            if (entry.getElementType().equals(elementType) && entry.getName().equals(name)) {
                isSemanticError = true;
                System.out.println("Element \"" + elementType + "\" name: \"" + name + "\" already defined in the" +
                        " scope!");
            }
        });
    }

    /**
     * Zkotroluje, zda je záznam použitelný pro operace
     *
     * @param name jméno
     * @param type datový typ
     */
    private void checkIfIsUsableForOps(String name, String type) {
        AtomicBoolean isFound = new AtomicBoolean(false);
        AtomicBoolean isSuitable = new AtomicBoolean(false);
        SymbolTable symbolTable = symbolTables.peek();

        checkAllEntries(name, type, isFound, isSuitable, symbolTable);

        if (!isFound.get() && !isSuitable.get() && symbolTables.size() > 1) {
            symbolTable = symbolTables.firstElement();
            checkAllEntries(name, type, isFound, isSuitable, symbolTable);
        }

        if (isFound.get() && !isSuitable.get()) {
            isSemanticError = true;
            System.out.println("Semantic error: IDENTIFICATION \"" + name + "\" has a wrong type of \"" +
                    type + "\"!");
        } else if (!isFound.get()) {
            isSemanticError = true;
            System.out.println("Semantic error: IDENTIFICATION \"" + name + "\" not found!");
        }
    }

    /**
     * Prohledá všechny záznamy ve scopu
     *
     * @param name        jméno
     * @param type        datový typ
     * @param isFound     informace, zda byl nalezen
     * @param isSuitable  infromace, zda je vhodný
     * @param symbolTable tabulka symbolů
     */
    private void checkAllEntries(String name, String type, AtomicBoolean isFound, AtomicBoolean isSuitable,
                                 SymbolTable symbolTable) {
        for (SymbolTableEntry entry : symbolTable.getEntries()) {
            if (entry.getName().equals(name)) {
                if (entry.getType().equals(type)) {
                    isSuitable.set(true);
                }
                isFound.set(true);
            }
        }
    }

    /**
     * Najde funkci
     *
     * @param name jméno
     * @return záznam s funkcí nebo null
     */
    private SymbolTableEntry findFunction(String name) {
        SymbolTable symbolTable = symbolTables.firstElement();
        for (SymbolTableEntry entry : symbolTable.getEntries()) {
            if (entry.getName().equals(name)) {
                if (entry.getElementType().equals("func") || entry.getElementType().equals("proc")) {
                    return entry;
                }
            }
        }

        isSemanticError = true;
        System.out.println("Semantic error: function \"" + name + "\" not found!");
        return null;
    }

    /**
     * Najde proměnnou
     *
     * @param name jméno
     * @return záznam s proměnnou nebo null
     */
    private SymbolTableEntry findVar(String name) {
        SymbolTable symbolTable = symbolTables.peek();
        for (SymbolTableEntry entry : symbolTable.getEntries()) {
            if (entry.getName().equals(name)) {
                if (entry.getElementType().equals("var")) {
                    return entry;
                }
            }
        }

        if (symbolTables.size() > 1) {
            symbolTable = symbolTables.firstElement();
            for (SymbolTableEntry entry : symbolTable.getEntries()) {
                if (entry.getName().equals(name)) {
                    if (entry.getElementType().equals("var")) {
                        return entry;
                    }
                }
            }
        }

        isSemanticError = true;
        System.out.println("Semantic error: variable \"" + name + "\" not found!");
        return null;
    }

    /**
     * Získá výstupní mapu
     * @return výstupní mapa
     */
    public Map<Integer, String> getOutput() {
        return output;
    }
}
