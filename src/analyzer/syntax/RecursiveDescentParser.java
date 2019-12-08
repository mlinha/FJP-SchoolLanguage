package analyzer.syntax;

import java.util.Iterator;
import java.util.List;

public class RecursiveDescentParser {
    private Iterator<String> iterator;
    private String symbol;

    private boolean isError = false;

    public  RecursiveDescentParser(List<String> data) {
        iterator = data.iterator();
    }

    private void getNextSymbol() {
        if (iterator.hasNext()) {
            symbol = iterator.next();
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
            System.out.println("Error: expected + \"" + with + "\", was \"" + symbol);
            isError = true;
        }
    }

    public boolean program() {
        getNextSymbol();
        globPromenne();
        funkceProcedury();

        return isError;
    }

    private void globPromenne() {
        if(!symbol.equals("procedura") && !symbol.equals("funkce")) {
            modifikator();
            typ();
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "=");
            if(symbol.equals("hodnota")) {
                verify(symbol, "hodnota");
            }
            else if(symbol.equals("volani funkce")) {
                verify(symbol, "volani funkce");
            }
            else {
                System.out.println("Error: global variable definition not correct (" + symbol + ")");
                isError = true;
            }
            verify(symbol, ";");
            globPromenne();
        }
    }

    private void lokPromenne() {
        if(!symbol.equals("akce")) { // TODO: action names
            typ();
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "=");
            if(symbol.equals("hodnota")) {
                verify(symbol, "hodnota");
            }
            else if(symbol.equals("volani funkce")) {
                verify(symbol, "volani funkce");
            }
            else {
                // TODO: error
                System.out.println("Error: local variable definition not correct (" + symbol + ")");
                isError = true;
            }
            verify(symbol, ";");
            lokPromenne();
        }
    }

    private void modifikator() {
        if(symbol.equals("konst")) {
            verify(symbol, "konst");
        }
    }

    private void funkceProcedury() {
        if(symbol.equals("funkce")) {
            verify(symbol, "funkce");
            typ();
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "(");
            parametry();
            verify(symbol, ")");
            verify(symbol, "{");
            vnitrekFunkce();
            verify(symbol, "}");
            funkceProcedury();
        }
        else if(symbol.equals("procedura")) {
            verify(symbol, "procedura");
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "(");
            parametry();
            verify(symbol, ")");
            verify(symbol, "{");
            vnitrekProcedury();
            verify(symbol, "}");
            funkceProcedury();
        }
    }

    private void parametry() {
        typ();
        verify(symbol, "IDENTIFIKATOR");
        if(symbol.equals(",")) {
            verify(symbol, ",");
            parametry();
        }
    }

    private void vnitrekFunkce() {
        lokPromenne();
        viceAkci();
        vracHodnoty();
    }

    private void vnitrekProcedury() {
        lokPromenne();
        viceAkci();
    }

    private void viceAkci() {
        switch (symbol) {
            case "pokud":
                rozhodnuti();
                viceAkci();
                break;
            case "IDENTIFIKATOR":
                verify(symbol, "IDENTIFIKATOR");
                viceAkci();
                // TODO vyraz, volani
                break;
            case "prepinac":
                prepinani();
                viceAkci();
                break;
            case "zatimco":
            case "delej":
            case "pro":
            case "prokazdy":
            case "opakuj":
                cyklus();
                viceAkci();
                break;
        }
    }

    private void rozhodnuti() {
        verify(symbol, "pokud");
        verify(symbol, "(");
        slozitaPodminka();
        verify(symbol, ")");
        verify(symbol, "{");
        viceAkci();
        verify(symbol, "}");
        if(symbol.equals("pokudne")) {
            verify(symbol, "pokudne");
            verify(symbol, "{");
            viceAkci();
            verify(symbol, "}");
        }
    }

    private void cyklus() {
        switch (symbol) {
            case "zatimco":
                verify(symbol, "zatimco");
                verify(symbol, "(");
                slozitaPodminka();
                verify(symbol, ")");
                verify(symbol, "{");
                viceAkci();
                verify(symbol, "}");
                break;
            case "delej":
                verify(symbol, "delej");
                verify(symbol, "{");
                viceAkci();
                verify(symbol, "}");
                verify(symbol, "zatimco");
                verify(symbol, "(");
                slozitaPodminka();
                verify(symbol, ")");
                verify(symbol, ";");

                break;
            case "pro":
                verify(symbol, "pro");
                verify(symbol, "(");
                vyraz();
                verify(symbol, ";");
                podminka();
                verify(symbol, ";");
                vyraz();
                verify(symbol, ")");
                verify(symbol, "{");
                viceAkci();
                verify(symbol, "}");
                break;
            case "prokazdy":
                verify(symbol, "prokazdy");
                verify(symbol, "(");
                verify(symbol, "pole");
                verify(symbol, ")");
                verify(symbol, "{");
                viceAkci();
                verify(symbol, "}");

                break;
            case "opakuj":
                verify(symbol, "opakuj");
                verify(symbol, "{");
                viceAkci();
                verify(symbol, "}");
                verify(symbol, "dokud");
                verify(symbol, "(");
                slozitaPodminka();
                verify(symbol, ")");
                verify(symbol, ";");
                break;
        }
    }

    private void vyraz() {

    }

    private void podminka() {
        verify(symbol, "IDENTIFIKATOR");
        podmOperator();
        verify(symbol, "IDENTIFIKATOR");
    }

    private void slozitaPodminka() {
        if(symbol.equals("!")) {
            negace();
            verify(symbol, "(");
            slozitaPodminka();
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
                verify(symbol, "(");
                slozitaPodminka();
                verify(symbol, ")");
            }
            else {
                verify(symbol, "(");
                slozitaPodminka();
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
                    verify(symbol, "(");
                    slozitaPodminka();
                    verify(symbol, ")");
                }
                else {
                    verify(symbol, "(");
                    slozitaPodminka();
                    verify(symbol, ")");
                }
            }
        }
    }

    private void volaniFunkce() {
        verify(symbol, "IDENTIFIKATOR");
        verify(symbol, "(");
        if(!symbol.equals(")")) {
            vstupHodnoty();
        }
        verify(symbol, ")");
        verify(symbol, ";");
    }

    private void vstupHodnoty() {
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
        verify(symbol, "zastav");
        verify(symbol, ";");
    }

    private void prepinani() {
        verify(symbol, "prepinac");
        verify(symbol, "(");
        vyraz();
        verify(symbol, ")");
        verify(symbol, "{");
        vicePripadu();
        verify(symbol, "}");
    }

    private void vicePripadu() {
        verify(symbol, "pripad");
        vyraz();
        verify(symbol, ":");
        viceAkci();
        zastaveni();
        if(symbol.equals("pripad")) {
            vicePripadu();
        }
    }

    private void typ() {
        switch (symbol) {
            case "cislo":
                verify(symbol, "cislo");
                break;
            case "hodnota":
                verify(symbol, "hodnota");
                break;
            case "text":
                verify(symbol, "text");
                break;
            case "znak":
                verify(symbol, "znak");
                break;
        }
    }

    private void pole() {

    }

    private void operator() {

    }

    private void podmOperator() {
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
        verify(symbol, "!");
    }
}
