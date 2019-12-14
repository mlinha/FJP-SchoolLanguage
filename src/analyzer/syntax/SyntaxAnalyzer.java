package analyzer.syntax;

import analyzer.Analyzer;
import analyzer.Token;

import java.util.List;

public class SyntaxAnalyzer implements Analyzer {

    private List<Token> tokens;

    public SyntaxAnalyzer(List<Token> data) {
        this.tokens = data;
    }

    @Override
    public boolean analyze() {
        RecursiveDescentParser parser = new RecursiveDescentParser(tokens);
        return parser.program();
    }
}
