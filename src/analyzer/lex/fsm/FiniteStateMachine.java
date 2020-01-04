package analyzer.lex.fsm;

/**
 * Abstraktní třída konečného automatu
 */
public abstract class FiniteStateMachine {

    /**
     * Aktuální stav
     */
    protected int currentState;

    /**
     * Informace, zda je automat v chybovém stavu
     */
    protected boolean isError;

    /**
     * Informace, zda se automat dostal do koncového stavu
     */
    protected boolean isFinished;

    /**
     * Získá, zda je automat v chybovém stavu
     * @return zda je automat v chybovém stavu
     */
    public boolean isError() {
        return isError;
    }

    /**
     * Získá, zda je automat v koncovém stavu
     * @return zda je automat v koncovém stavu
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Spustí automat
     */
    public void start() {
        currentState = 0;
        isFinished = false;
        isError = false;
    }

    /**
     * Přesune automat do dalšího stavu
     * @param input znak
     * @return informace, zda automat neskončil
     */
    abstract public boolean nextState(char input);
}
