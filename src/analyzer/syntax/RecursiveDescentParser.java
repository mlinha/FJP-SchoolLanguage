package analyzer.syntax;

import java.util.Iterator;
import java.util.List;

public class RecursiveDescentParser {
    private Iterator<String> iterator;
    private String symbol;

    private boolean isError = false;

    protected RecursiveDescentParser(List<String> data) {
        iterator = data.iterator();
    }

    private void getNextSymbol() {
        if(iterator.hasNext()) {
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
            if(!symbol.equals("end")) {
                System.out.println("Error: expected + \"" + with + "\", was \"" + symbol + "\"");
            }
            getNextSymbol();
            isError = true;
        }
    }

    protected boolean program() {
        getNextSymbol();
        globPromenne();
        funkceProcedury();

        return isError;
    }

    private void globPromenne() {
        if(symbol.equals("end")) {
            return;
        }
        if(!symbol.equals("procedura") && !symbol.equals("funkce")) {
            modifikator();
            if(symbol.equals("end")) {
                return;
            }
            typ();
            if(symbol.equals("end")) {
                return;
            }
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "=");
            if(symbol.equals("hodnota")) {
                verify(symbol, "hodnota");
            }
            else if(symbol.equals("volani funkce")) {
                verify(symbol, "volani funkce");
            }
            else {
                if(!symbol.equals("end")) {
                    System.out.println("Error: expected \"value\" or \"function call\", was \"" + symbol + "\"");
                    getNextSymbol();
                }
                isError = true;
            }
            verify(symbol, ";");
            globPromenne();
        }
    }

    private void lokPromenne() {
        if(symbol.equals("end")) {
            return;
        }
        if(!symbol.equals("akce")) { // TODO: action names
            typ();
            if(symbol.equals("end")) {
                return;
            }
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "=");
            if(symbol.equals("hodnota")) {
                verify(symbol, "hodnota");
            }
            else if(symbol.equals("volani funkce")) {
                verify(symbol, "volani funkce");
            }
            else {
                if(!symbol.equals("end")) {
                    System.out.println("Error: expected \"value\" or \"function call\", was \"" + symbol + "\"");
                    getNextSymbol();
                }
                isError = true;
            }
            verify(symbol, ";");
            lokPromenne();
        }
    }

    private void modifikator() {
        if(symbol.equals("end")) {
            return;
        }
        if(symbol.equals("konst")) {
            verify(symbol, "konst");
        }
    }

    private void funkceProcedury() {
        if(symbol.equals("end")) {
            return;
        }
        if(symbol.equals("funkce")) {
            verify(symbol, "funkce");
            typ();
            if(symbol.equals("end")) {
                return;
            }
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "(");
            parametry();
            if(symbol.equals("end")) {
                return;
            }
            verify(symbol, ")");
            verify(symbol, "{");
            vnitrekFunkce();
            if(symbol.equals("end")) {
                return;
            }
            verify(symbol, "}");
            funkceProcedury();
        }
        else if(symbol.equals("procedura")) {
            verify(symbol, "procedura");
            verify(symbol, "IDENTIFIKATOR");
            verify(symbol, "(");
            parametry();
            if(symbol.equals("end")) {
                return;
            }
            verify(symbol, ")");
            verify(symbol, "{");
            vnitrekProcedury();
            verify(symbol, "}");
            funkceProcedury();
        }
    }

    private void parametry() {
        if(symbol.equals("end")) {
            return;
        }
        typ();
        if(symbol.equals("end")) {
            return;
        }
        verify(symbol, "IDENTIFIKATOR");
        if(symbol.equals(",")) {
            verify(symbol, ",");
            parametry();
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
            case "prepinac":
                prepinani();
                if(symbol.equals("end")) {
                    return;
                }
                viceAkci();
                break;
            case "zatimco":
            case "delej":
            case "pro":
            case "prokazdy":
            case "opakuj":
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
            case "delej":
                verify(symbol, "delej");
                verify(symbol, "{");
                viceAkci();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, "}");
                verify(symbol, "zatimco");
                verify(symbol, "(");
                slozitaPodminka();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, ")");
                verify(symbol, ";");

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
            case "prokazdy":
                verify(symbol, "prokazdy");
                verify(symbol, "(");
                verify(symbol, "pole");
                verify(symbol, ")");
                verify(symbol, "{");
                viceAkci();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, "}");

                break;
            case "opakuj":
                verify(symbol, "opakuj");
                verify(symbol, "{");
                viceAkci();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, "}");
                verify(symbol, "dokud");
                verify(symbol, "(");
                slozitaPodminka();
                if(symbol.equals("end")) {
                    return;
                }
                verify(symbol, ")");
                verify(symbol, ";");
                break;
        }
    }

    private void vyraz() {
        if(symbol.equals("end")) {
            return;
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

    private void typ() {
        if(symbol.equals("end")) {
            return;
        }
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
        if(symbol.equals("end")) {
            return;
        }

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
}
