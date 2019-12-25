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

        String name = args[0].substring(0, args[0].indexOf("."));

        if(!isError) {
            System.out.println("No syntax and semantic errors detected.\nOutput file \"" + name + ".sl\" generated!");
        }
        else {
            File f = new File("tmp.txt");
            if(!f.delete()) {
                System.out.println("Error while deleting temporary file!");
            }

            return;
        }

        try {
            sort(name);
            File f = new File("tmp.txt");
            if(!f.delete()) {
                System.out.println("Error while deleting temporary file!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sort(String name) throws IOException {
        Map<Integer, String> data = new TreeMap<>();
        BufferedReader reader = new BufferedReader(new FileReader("tmp.txt"));
        String line;
        while((line = reader.readLine()) != null) {
            data.put(Integer.parseInt(line.substring(0, line.indexOf(" "))), line);
        }
        reader.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(name + ".sl"));

        for (String out : data.values()){
            writer.write(out);
            writer.newLine();
        }

        writer.close();
    }
}
