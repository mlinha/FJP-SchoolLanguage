package app;

import analyzer.Analyzer;
import analyzer.Token;
import analyzer.lex.LexicalAnalyzer;
import analyzer.synsemgen.SyntaxSemanticAnalyzerGenerator;

import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // test
        boolean isError;
        List<String> a = new LinkedList<>();
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
        List<Token> tokens = ((LexicalAnalyzer) analyzer).getTokens();
        System.out.println();
        analyzer = new SyntaxSemanticAnalyzerGenerator(tokens);
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
