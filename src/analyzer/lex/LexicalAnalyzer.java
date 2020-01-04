package analyzer.lex;

import analyzer.Analyzer;
import analyzer.Token;

import java.util.List;

/**
 * Spouštěč lexikální analýzy
 */
public class LexicalAnalyzer implements Analyzer {

    /**
     * Název souboru
     */
    private String fileName;

    /**
     * Seznam tokenů
     */
    private List<Token> tokens;

    /**
     * Konstruktor
     * @param fileName jméno souboru
     */
    public LexicalAnalyzer(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Spustí analýzu
     * @return informace, zda došlo k chybě
     */
    @Override
    public boolean analyze() {
        Lexer lexer = new Lexer();
        boolean retval = lexer.lex(fileName);
        tokens = lexer.getTokens();
        return retval;
    }

    /**
     * Získá seznam tokenů
     * @return seznam tokenů
     */
    public List<Token> getTokens() {
        return tokens;
    }
}
