package analyzer.lex;

import analyzer.Analyzer;

import java.util.List;

public class LexicalAnalyzer implements Analyzer {

    private String data;
    private List<String> tokens;

    public LexicalAnalyzer(String data) {
        this.data = data;
    }

    @Override
    public boolean analyze() {
        Lexer lexer = new Lexer();
        lexer.lex();
        return false;
    }

    public List<String> getTokens() {
        return tokens;
    }
}
