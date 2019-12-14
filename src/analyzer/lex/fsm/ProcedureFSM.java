package analyzer.lex.fsm;

public class ProcedureFSM extends FiniteStateMachine {

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
            case 4:
                state4(input);
                break;
            case 5:
                state5(input);
                break;
            case 6:
                state6(input);
                break;
            case 7:
                state7(input);
                break;
            case 8:
                state8(input);
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
        }
        else {
            isError = true;
        }
    }

    private void state3(char input) {
        if(input == 'c') {
            currentState = 4;
        }
        else {
            isError = true;
        }
    }

    private void state4(char input) {
        if(input == 'e') {
            currentState = 5;
        }
        else {
            isError = true;
        }
    }

    private void state5(char input) {
        if(input == 'd') {
            currentState = 6;
        }
        else {
            isError = true;
        }
    }

    private void state6(char input) {
        if(input == 'u') {
            currentState = 7;
        }
        else {
            isError = true;
        }
    }

    private void state7(char input) {
        if(input == 'r') {
            currentState = 8;
        }
        else {
            isError = true;
        }
    }

    private void state8(char input) {
        if(input == 'a') {
            currentState = 9;
            isFinished = true;
        }
        else {
            isError = true;
        }
    }
}
