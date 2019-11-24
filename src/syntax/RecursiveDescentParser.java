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
            verify(symbol, "ident");
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

    private void modifikator() {
        if(symbol.equals("konst")) {
            verify(symbol, "konst");
        }
    }

    private void funkceProcedury() {
        if(symbol.equals("funkce")) {
            getNextSymbol();
            verify(symbol, "typ");
            verify(symbol, "ident");
            verify(symbol, "(");
            parametry();
            verify(symbol, ")");
            verify(symbol, "{");
            vnitrekFunkce();
            verify(symbol, "}");
            funkceProcedury();
        }
        else if(symbol.equals("procedura")) {
            getNextSymbol();
            verify(symbol, "ident");
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

    }

    private void vnitrekFunkce() {

    }

    private void vnitrekProcedury() {

    }

    private void viceAkci() {

    }

    private void rozhodnuti() {

    }

    private void cyklus() {

    }

    private void vyraz() {

    }

    private void podminka() {

    }

    private void slozitaPodminka() {

    }

    private void volaniFunkce() {

    }

    private void vstupHodnoty() {

    }

    private void vracHodnoty() {

    }

    private void zastaveni() {

    }

    private void prepinani() {

    }

    private void vicePripadu() {

    }

    private void typ() {

    }

    private void pole() {

    }
}
