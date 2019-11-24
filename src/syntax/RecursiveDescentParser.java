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
        symbol = null;
    }

    private void verify(String symbol, String with) {
        if(symbol.equals(with)) {
            getNextSymbol();
        }
    }

    public void program() {
        getNextSymbol();
        globPromenne();
        funkceProcedury();
    }

    private void globPromenne() {
        while(!symbol.equals("procedura") && !symbol.equals("funkce")) {
            if(symbol.equals("konst")) {
                getNextSymbol();
            }
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
        }
    }

    private void funkceProcedury() {
        while(symbol.equals("funkce")) {
            getNextSymbol();
            verify(symbol, "navratova hodnota");
            verify(symbol, "ident");
            verify(symbol, "(");
            parametry();
            verify(symbol, ")");
            verify(symbol, "{");
            vnitrekFunkce();
            verify(symbol, "}");
        }
    }

    private void parametry() {

    }

    private void vnitrekFunkce() {

    }
}
