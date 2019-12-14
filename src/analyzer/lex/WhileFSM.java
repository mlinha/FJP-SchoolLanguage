package analyzer.lex;

public class WhileFSM implements FiniteStateMachine {

    private int currentState;
    private boolean isError;
    private boolean isFinnished;

    @Override
    public void start() {
        currentState = 0;
        isFinnished = false;
        isError = false;
    }

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
        }
        return !(isError || isFinnished);
    }

    @Override
    public boolean isError() {
        return isError;
    }

    @Override
    public boolean isFinished() {
        return isFinnished;
    }

    private void state0(char input) {
        if(input == 'z') {
            currentState = 1;
        }
        else {
            isError = true;
        }
    }

    private void state1(char input) {
        if(input == 'a') {
            currentState = 2;
        }
        else {
            isError = true;
        }
    }

    private void state2(char input) {
        if(input == 't') {
            currentState = 3;
        }
        else {
            isError = true;
        }
    }

    private void state3(char input) {
        if(input == 'i') {
            currentState = 4;
        }
        else {
            isError = true;
        }
    }

    private void state4(char input) {
        if(input == 'm') {
            currentState = 5;
        }
        else {
            isError = true;
        }
    }

    private void state5(char input) {
        if(input == 'c') {
            currentState = 6;
        }
        else {
            isError = true;
        }
    }

    private void state6(char input) {
        if(input == 'o') {
            currentState = 7;
            isFinnished = true;
        }
        else {
            isError = true;
        }
    }
}
