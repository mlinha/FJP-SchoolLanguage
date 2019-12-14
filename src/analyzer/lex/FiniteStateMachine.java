package analyzer.lex;

public interface FiniteStateMachine {

    void start();
    boolean nextState(char input);
    boolean isError();
    boolean isFinished();
}
