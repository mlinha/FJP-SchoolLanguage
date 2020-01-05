package analyzer.synsemgen;

import analyzer.Analyzer;
import analyzer.Token;

import java.util.List;
import java.util.Map;

/**
 * Provádí analýzu
 */
public class SyntaxSemanticAnalyzerGenerator implements Analyzer {

    /**
     * Seznam tokenů
     */
    private List<Token> tokens;

    /**
     * Mapa příkazů
     */
    private Map<Integer, String> data;

    /**
     * Vytvoří analyzátor
     * @param data tokeny
     */
    public SyntaxSemanticAnalyzerGenerator(List<Token> data) {
        this.tokens = data;
    }

    /**
     * Spustí analýzu
     * @return informace zda nedošlo k chybě
     */
    @Override
    public boolean analyze() {
        return analyzeAndGenerate();
    }

    /**
     * Provede analýzu a generování
     * @return informace zda nedošlo k chybě
     */
    public boolean analyzeAndGenerate() {
        RecursiveDescentParser parser = new RecursiveDescentParser(tokens);
        data = parser.getOutput();
        return parser.program();
    }

    /**
     * Získá mapu příkazů
     * @return mapa příkazů
     */
    public Map<Integer, String> getData() {
        return data;
    }
}
