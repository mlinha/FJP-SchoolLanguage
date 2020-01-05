package app;

import analyzer.Analyzer;
import analyzer.Token;
import analyzer.lex.LexicalAnalyzer;
import analyzer.synsemgen.SyntaxSemanticAnalyzerGenerator;

import java.io.*;
import java.util.*;

/**
 * Hlavní třída programu
 */
public class Main {

    /**
     * Spouštěcí metoda programu
     * @param args argumenty
     */
    public static void main(String[] args) {
        boolean isError;
        if(args.length == 0) {
            System.out.println("SchoolLanguage compiler.\nRun as java -jar slc.jar <filename>");
            return;
        }
        Analyzer analyzer = new LexicalAnalyzer(args[0]);
        System.out.println("---------Running lexical analyzer---------");
        isError = analyzer.analyze();
        if(!isError) {
            System.out.println("No lexical errors detected.");
        }
        else {
            return;
        }
        List<Token> tokens = ((LexicalAnalyzer) analyzer).getTokens();
        System.out.println();
        analyzer = new SyntaxSemanticAnalyzerGenerator(tokens);
        System.out.println("---------Running syntax and semantic analyzer and code generator---------");
        isError = analyzer.analyze();

        String name;
        int index = args[0].indexOf(".");
        if(index <= 0) {
            name = args[0];
        }
        else {
            name = args[0].substring(0, args[0].indexOf("."));
        }

        if(!isError) {
            System.out.println("No syntax and semantic errors detected.\nOutput file \"" + name + ".sl\" generated!");
        }
        else {
            return;
        }

        try {
            writeOutput(name, ((SyntaxSemanticAnalyzerGenerator) analyzer).getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Vypíše soubor
     * @param name jméno souboru
     * @param data mapa s příkazy
     * @throws IOException chyba
     */
    private static void writeOutput(String name, Map<Integer, String> data) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(name + ".sl"));

        for (String out : data.values()){
            writer.write(out);
            writer.newLine();
        }

        writer.close();
    }
}
