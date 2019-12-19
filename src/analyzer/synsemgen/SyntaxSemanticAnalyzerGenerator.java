package analyzer.synsemgen;

import analyzer.Analyzer;
import analyzer.Token;

import java.util.List;

public class SyntaxSemanticAnalyzerGenerator implements Analyzer {

    private List<Token> tokens;

    public SyntaxSemanticAnalyzerGenerator(List<Token> data) {
        this.tokens = data;
    }

    @Override
    public boolean analyze() {
        return analyzeAndGenerate();
    }

    public boolean analyzeAndGenerate() {
        RecursiveDescentParser parser = new RecursiveDescentParser(tokens);
        return parser.program();
    }
}
