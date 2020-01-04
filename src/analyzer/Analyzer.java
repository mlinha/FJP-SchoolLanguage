package analyzer;

/**
 * Rozhraní pro analyzátory
 */
public interface Analyzer {

    /**
     * Provádí analýzu
     * @return informace, zda nedošlo k chybě
     */
    boolean analyze();
}
