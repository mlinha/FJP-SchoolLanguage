package analyzer.lex.fsm;

public class ForFSM extends FiniteStateMachine {

    @Override
    public boolean nextState(char input) {
        switch (currentState) {
            case 0:
                state0(input);
                break;
            case 1:
                state1(input);
                break;
            case 2:
                state2(input);
                break;
        }
        return !(isError || isFinished);
    }

    private void state0(char input) {
        if(input == 'p') {
            currentState = 1;
        }
        else {
            isError = true;
        }
    }

    private void state1(char input) {
        if(input == 'r') {
            currentState = 2;
        }
        else {
            isError = true;
        }
    }

    private void state2(char input) {
        if(input == 'o') {
            currentState = 3;
            isFinished = true;
        }
        else {
            isError = true;
        }
    }
}
