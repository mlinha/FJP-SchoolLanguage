package analyzer.lex.fsm;

/**
 * Automat pro klíčové slovo - zatimco
 */
public class WhileFSM extends FiniteStateMachine {

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
        return !(isError || isFinished);
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
            isFinished = true;
        }
        else {
            isError = true;
        }
    }
}
