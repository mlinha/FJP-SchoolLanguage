package analyzer.lex;

import analyzer.Analyzer;
import analyzer.Token;

import java.util.List;

public class LexicalAnalyzer implements Analyzer {

    private String data;
    private List<Token> tokens;

    public LexicalAnalyzer(String data) {
        this.data = data;
    }

    @Override
    public boolean analyze() {
        Lexer lexer = new Lexer();
        boolean retval = lexer.lex();
        tokens = lexer.getTokens();
        return retval;
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
