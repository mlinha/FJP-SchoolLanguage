package analyzer.lex.fsm;

/**
 * Automat pro klíčové slovo - ,
 */
public class CommaFSM extends FiniteStateMachine {

    @Override
    public boolean nextState(char input) {
        if (currentState == 0) {
            state0(input);
        }
        return !(isError || isFinished);
    }

    private void state0(char input) {
        if(input == ',') {
            currentState = 1;
            isFinished = true;
        }
        else {
            isError = true;
        }
    }
}
