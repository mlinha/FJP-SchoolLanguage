package app;

import analyzer.Analyzer;
import analyzer.syntax.RecursiveDescentParser;
import analyzer.syntax.SyntaxAnalyzer;

import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
        // test
        boolean isError;
        LinkedList<String> a = new LinkedList<>();
        a.add("konst");
        a.add("cislo");
        a.add("IDENTIFIKATOR");
        a.add("=");
        a.add("=");
        a.add(";");
        a.add("funkce");
        Analyzer analyzer = new SyntaxAnalyzer(a);
        isError = analyzer.analyze();
        if(isError) {

        }
        // end of test
    }
}
