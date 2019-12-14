package analyzer.lex.fsm;

public class ReturnFSM extends FiniteStateMachine {

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
            case 3:
                state3(input);
                break;
        }
        return !(isError || isFinished);
    }

    private void state0(char input) {
        if(input == 'v') {
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
        if(input == 'a') {
            currentState = 3;
        }
        else {
            isError = true;
        }
    }

    private void state3(char input) {
        if(input == 't') {
            currentState = 4;
            isFinished = true;
        }
        else {
            isError = true;
        }
    }
}
