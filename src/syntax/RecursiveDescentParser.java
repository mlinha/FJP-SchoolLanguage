package syntax;

import java.util.Iterator;
import java.util.List;

public class RecursiveDescentParser {
    private Iterator<String> iterator;
    private String symbol;

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
            System.out.println("error");
        }
    }

    public void program() {
        getNextSymbol();
        globPromenne();
        funkceProcedury();
    }

    private void globPromenne() {
        if(!symbol.equals("procedura") && !symbol.equals("funkce")) {
            modifikator();
            verify(symbol, "typ");
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
            }
            verify(symbol, ";");
            globPromenne();
        }
    }

    private void lokPromenne() {
        if(!symbol.equals("akce")) { // TODO: action names
            verify(symbol, "typ");
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
            verify(symbol, "typ");
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
        verify(symbol, "typ");
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
        if(symbol.equals("pokud")) {
            rozhodnuti();
        }
        else if(symbol.equals("IDENTIFIKATOR")) {
            verify(symbol, "IDENTIFIKATOR");
            // TODO vyraz, volani
        }
        else if(symbol.equals("prepinac")) {
            prepinani();
        }
        else {
            cyklus();
        }
        viceAkci();
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
        if(symbol.equals("zatimco")) {
            verify(symbol, "zatimco");
            verify(symbol, "(");
            slozitaPodminka();
            verify(symbol, ")");
            verify(symbol, "{");
            viceAkci();
            verify(symbol, "}");
        }
        else if(symbol.equals("delej")) {
            verify(symbol, "delej");
            verify(symbol, "{");
            viceAkci();
            verify(symbol, "}");
            verify(symbol, "zatimco");
            verify(symbol, "(");
            slozitaPodminka();
            verify(symbol, ")");
            verify(symbol, ";");

        }
        else if(symbol.equals("pro")) {
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
        }
        else if(symbol.equals("prokazdy")) {
            verify(symbol, "prokazdy");
            verify(symbol, "(");
            verify(symbol, "pole");
            verify(symbol, ")");
            verify(symbol, "{");
            viceAkci();
            verify(symbol, "}");

        }
        else if(symbol.equals("opakuj")) {
            verify(symbol, "opakuj");
            verify(symbol, "{");
            viceAkci();
            verify(symbol, "}");
            verify(symbol, "dokud");
            verify(symbol, "(");
            slozitaPodminka();
            verify(symbol, ")");
            verify(symbol, ";");
        }
    }

    private void vyraz() {

    }

    private void podminka() {
        verify(symbol, "IDENTIFIKATOR");
        operator();
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
                // TODO error
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
                    // TODO error
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

    }

    private void pole() {

    }

    private void operator() {

    }

    private void negace() {
        verify(symbol, "!");
    }
}
