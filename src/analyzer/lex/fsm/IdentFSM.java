package analyzer.lex.fsm;

public class IdentFSM extends FiniteStateMachine {

    @Override
    public boolean nextState(char input) {
        switch (currentState) {
            case 0:
                state0(input);
                break;
            case 1:
                state1(input);
                break;
        }
        return !(isError || isFinished);
    }

    private void state0(char input) {
        if(Character.toString(input).matches("[a-zA-Z]")) {
            currentState = 1;
        }
        else {
            isError = true;
        }
    }

    private void state1(char input) {
        if(Character.toString(input).matches("[a-zA-Z0-9]")) {
            currentState = 1;
        }
        else if(input == ' ' || input == ';' || input == ',' || input == '(' || input == ')') {
            currentState = 2;
            isFinished = true;
        }
        else {
            isError = true;
        }
    }
}
