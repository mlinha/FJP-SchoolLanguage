package analyzer.lex.fsm;

/**
 * Automat pro klíčové slovo - závorky
 */
public class ParenFSM extends FiniteStateMachine {

    @Override
    public boolean nextState(char input) {
        if (currentState == 0) {
            state0(input);
        }
        return !(isError || isFinished);
    }

    private void state0(char input) {
        if(input == '(') {
            currentState = 1;
            isFinished = true;
        }
        else if(input == ')') {
            currentState = 1;
            isFinished = true;
        }
        else if(input == '{') {
            currentState = 1;
            isFinished = true;
        }
        else if(input == '}') {
            currentState = 1;
            isFinished = true;
        }
        else {
            isError = true;
        }
    }
}
