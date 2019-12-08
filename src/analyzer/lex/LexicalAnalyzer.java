package analyzer.lex;

import analyzer.Analyzer;

public class LexicalAnalyzer implements Analyzer {

    private String data;

    public LexicalAnalyzer(String data) {
        this.data = data;
    }

    @Override
    public boolean analyze() {

        return true;
    }
}
