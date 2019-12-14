package analyzer.lex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Lexer {

    private List<FiniteStateMachine> finiteStateMachines = new ArrayList<>();
    private char[] chars;
    private boolean isEnd = false;

    protected void lex() {
        openAndLoad("");
        initializeFSMList();

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
                    createToken(start, lastCorrect.get());
                    lastCorrect.getAndIncrement();
                    break;
                }
                else if(active.get() == 0 && finished.get() == 0) {
                    // TODO: error
                    System.out.println("Unknown token!");
                    lastCorrect.getAndIncrement();
                    break;
                }
            }
        }
    }

    private void initializeFSMList() {
        finiteStateMachines.add(new WhileFSM());
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
        System.out.println(new String(chars, start, end - start + 1));
    }

    private void openAndLoad(String filename) {
        StringBuilder stringBuilder = new StringBuilder();
        // test
        stringBuilder.append("zatimco zaticmco zatimco");
        chars = stringBuilder.toString().toCharArray();
        // end of test
        /*
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line.replaceAll("[ \n\r\t]", ""));
            }
            reader.close();
            chars = stringBuilder.toString().toCharArray();
        }
        catch(Exception e) {
            // TODO: error
        }
        */
    }
}
