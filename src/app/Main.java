package app;

import analyzer.Analyzer;
import analyzer.lex.LexicalAnalyzer;
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
        Analyzer analyzer = new LexicalAnalyzer(null);
        System.out.println("---------Running lexical analyzer---------");
        isError = analyzer.analyze(); // not functional
        if(!isError) {
            System.out.println("No lexical errors detected.");
        }
        else {
            return;
        }
        System.out.println();
        analyzer = new SyntaxAnalyzer(a);
        System.out.println("---------Running syntax analyzer---------");
        isError = analyzer.analyze();
        if(!isError) {
            System.out.println("No syntax errors detected.");
        }
        else {
            return;
        }
        System.out.println();
        // TODO: semantics
        // end of test
    }
}
