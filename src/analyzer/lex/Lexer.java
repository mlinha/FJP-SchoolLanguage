package analyzer.lex;

import analyzer.Token;
import analyzer.lex.fsm.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Lexer {

    private List<Token> tokens = new ArrayList<>();
    private List<FiniteStateMachine> finiteStateMachines = new ArrayList<>();
    private char[] chars;
    private boolean isEnd = false;

    protected boolean lex(String fileName) {
        openAndLoad(fileName);
        initializeFSMList();

        boolean isError = false;

        AtomicInteger lastCorrect = new AtomicInteger(0);
        AtomicInteger active = new AtomicInteger();
        AtomicInteger finished = new AtomicInteger();

        while(!isEnd) {
            finiteStateMachines.forEach(FiniteStateMachine::start);

            int start = lastCorrect.get();
            int current = lastCorrect.get();
            char lastChar;

            active.set(finiteStateMachines.size());
            finished.set(0);

            while(true) {
                lastChar = nextChar(current);
                if(isEnd) {
                    break;
                }

                if(lastChar == ' ' && active.get() == 1) {
                    if(current != start) {
                        createToken(start, current);
                    }
                    lastCorrect.set(current + 1);
                    break;
                }

                int finalCurrent = current;
                char finalLastChar = lastChar;
                finiteStateMachines.forEach(finiteStateMachine -> {
                    if(!finiteStateMachine.isError() && !finiteStateMachine.isFinished()) {
                        if(!finiteStateMachine.nextState(finalLastChar)) {
                            active.getAndDecrement();
                            if(finiteStateMachine.isFinished()) {
                                lastCorrect.set(finalCurrent);
                                finished.getAndIncrement();
                            }
                        }
                    }
                });

                current++;

                if(active.get() == 0 && finished.get() > 0) {
                    if(!finiteStateMachines.get(19).isError() || !finiteStateMachines.get(20).isError()) {
                        createToken(start, lastCorrect.get() - 1);
                    }
                    else {
                        createToken(start, lastCorrect.get());
                        lastCorrect.getAndIncrement();
                    }
                    break;
                }
                else if(active.get() == 0 && finished.get() == 0) {
                    // TODO: error
                    if(lastChar != ' ') {
                        System.out.println("Unexpected token: \"" + new String(chars, start, current - start)
                                + "\"!");
                        isError = true;
                    }
                    lastCorrect.getAndIncrement();
                    break;
                }
            }
        }

        return isError;
    }

    private void initializeFSMList() {
        finiteStateMachines.add(new WhileFSM());
        finiteStateMachines.add(new ProcedureFSM());
        finiteStateMachines.add(new CaseFSM());
        finiteStateMachines.add(new ForFSM());
        finiteStateMachines.add(new SwitchFSM());
        finiteStateMachines.add(new ReturnFSM());
        finiteStateMachines.add(new ColonFSM());
        finiteStateMachines.add(new ParenFSM());
        finiteStateMachines.add(new OpFSM());
        finiteStateMachines.add(new ConstFSM());
        finiteStateMachines.add(new CondOpEqFSM());
        finiteStateMachines.add(new CondOpLMFSM());
        finiteStateMachines.add(new IfFSM());
        finiteStateMachines.add(new ElseFSM());
        finiteStateMachines.add(new FunctionFSM());
        finiteStateMachines.add(new BoolFSM());
        finiteStateMachines.add(new TrueFSM());
        finiteStateMachines.add(new FalseFSM());
        finiteStateMachines.add(new IntFSM());
        finiteStateMachines.add(new IntValFSM());
        finiteStateMachines.add(new IdentFSM());
        finiteStateMachines.add(new CommaFSM());
    }

    private char nextChar(int current) {
        if(current >= chars.length) {
            isEnd = true;
            return 'e';
        }
        else {
            return chars[current];
        }
    }

    private void createToken(int start, int end) {
        String value = new String(chars, start, end - start + 1).trim();

        Token token;
        if(value.equals("pravda") || value.equals("nepravda") || value.matches("\\d+")) {
            token = new Token("hodnota", value);
        }
        else if(checkIfIsIdent(value)) {
            token = new Token("IDENTIFIKATOR", value);
        }
        else {
            token = new Token(value, value);
        }

        tokens.add(token);
        // test
        System.out.println(token.getName());
    }

    private void openAndLoad(String filename) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(" ");
            }
            reader.close();
            chars = stringBuilder.toString().replaceAll("\\s+\n\r\t", " ").trim().toCharArray();
        }
        catch(Exception e) {
            // TODO: error
        }

    }

    private boolean checkIfIsIdent(String string) {
        return !string.equals("pravda") && !string.equals("nepravda") && !string.equals("zatimco") && !string.equals("pro") &&
                !string.equals("{") && !string.equals("}") && !string.equals("(") && !string.equals(")") &&
                !string.equals(";") && !string.equals(":") && !string.equals("logicky") && !string.equals("cislo") &&
                !string.equals("pripad") && !string.equals("prepinac") && !string.equals("funkce") && !string.equals("+") &&
                !string.equals("procedura") && !string.equals("*") && !string.equals("/") && !string.equals("-") &&
                !string.equals("pokud") && !string.equals("pokudne") && !string.equals("<=") && !string.equals(">=") &&
                !string.equals("!=") && !string.equals("==") && !string.equals("<") && !string.equals(">") &&
                !string.equals("&&") && !string.equals("||") && !string.equals("zastav") && !string.equals("vrat") &&
                !string.equals("konst") && !string.equals("=") && !string.equals(",") && !string.matches("\\d+");
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
