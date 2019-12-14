package analyzer.lex.fsm;

public abstract class FiniteStateMachine {

    protected int currentState;
    protected boolean isError;
    protected boolean isFinished;

    public boolean isError() {
        return isError;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void start() {
        currentState = 0;
        isFinished = false;
        isError = false;
    }

    abstract public boolean nextState(char input);
}
