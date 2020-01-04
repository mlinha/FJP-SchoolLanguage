package analyzer.lex.fsm;

/**
 * Automat pro čislo
 */
public class IntValFSM extends FiniteStateMachine {

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
        if(Character.toString(input).matches("[1-9]")) {
            currentState = 1;
        }
        else if(Character.toString(input).matches("0")) {
            currentState = 2;
        }
        else if(input == '-') {
            currentState = 3;
        }
        else {
            isError = true;
        }
    }

    private void state1(char input) {
        if(Character.toString(input).matches("[0-9]")) {
            currentState = 1;
        }
        else if(input == ' ' || input == ';' || input == ',' || input == ')' || input == '+' || input == '*' ||
                input == '-' || input == '/') {
            currentState = 4;
            isFinished = true;
        }
        else {
            isError = true;
        }
    }

    private void state2(char input) {
        if(input == ' ' || input == ';' || input == ',' || input == ')' || input == '+' || input == '*' ||
                input == '-' || input == '/') {
            currentState = 1;
            isFinished = true;
        }
        else {
            isError = true;
        }
    }

    private void state3(char input) {
        if(Character.toString(input).matches("[1-9]")) {
            currentState = 1;
        }
        else {
            isError = true;
        }
    }

}
