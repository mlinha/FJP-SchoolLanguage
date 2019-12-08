package analyzer.syntax;

import analyzer.Analyzer;

import java.util.LinkedList;
import java.util.List;

public class SyntaxAnalyzer implements Analyzer {

    private List<String> data;

    public SyntaxAnalyzer(List<String> data) {
        this.data = data;
    }

    @Override
    public boolean analyze() {
        RecursiveDescentParser parser = new RecursiveDescentParser(data);
        return parser.program();
    }
}
