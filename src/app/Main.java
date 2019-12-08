package app;

import analyzer.Analyzer;
import analyzer.syntax.RecursiveDescentParser;
import analyzer.syntax.SyntaxAnalyzer;

import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {
        boolean isError;
        LinkedList<String> a = new LinkedList<>();
        a.add("konst");
        a.add("typ");
        a.add("ident");
        a.add("=");
        a.add("hodnota");
        a.add(";");
        a.add("funkce");
        Analyzer analyzer = new SyntaxAnalyzer(a);
        isError = analyzer.analyze();
        if(isError) {

        }
    }
}
