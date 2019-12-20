package analyzer.lex;

import analyzer.Analyzer;
import analyzer.Token;

import java.util.List;

public class LexicalAnalyzer implements Analyzer {

    private String fileName;
    private List<Token> tokens;

    public LexicalAnalyzer(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean analyze() {
        Lexer lexer = new Lexer();
        boolean retval = lexer.lex(fileName);
        tokens = lexer.getTokens();
        return retval;
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
