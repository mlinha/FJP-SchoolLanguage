package app;

import analyzer.Analyzer;
import analyzer.Token;
import analyzer.lex.LexicalAnalyzer;
import analyzer.synsemgen.SyntaxSemanticAnalyzerGenerator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

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
        Analyzer analyzer = new LexicalAnalyzer("C:\\Users\\Michal\\SchoolLanguage\\src\\file.txt");
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
        System.out.println("---------Running syntax and semantic analyzer and code generator---------");
        isError = analyzer.analyze();
        if(!isError) {
            System.out.println("No syntax errors detected.");
        }
        else {
            return;
        }


        try {
            sort();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println();
        // TODO: semantics
        // end of test
    }

    private static void sort() throws IOException {
        Map<Integer, String> data = new TreeMap<>();
        BufferedReader reader = new BufferedReader(new FileReader("out.txt"));
        String line;
        while((line = reader.readLine()) != null) {
            data.put(Integer.parseInt(line.substring(0, line.indexOf(" "))), line);
        }
        reader.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter("fin.txt"));

        for (String out : data.values()){
            writer.write(out);
            writer.newLine();
        }

        writer.close();
    }
}
