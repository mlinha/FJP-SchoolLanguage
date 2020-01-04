package analyzer.lex;

import analyzer.Token;
import analyzer.lex.fsm.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lexikální analyzátor
 */
public class Lexer {

    /**
     * Pozice FSM pro číselnou hodnotu
     */
    private final int INTVALPOS = 0;

    /**
     * Pozice FSM pro identifikátor
     */
    private final int IDENTPOS = 1;


    /**
     * List tokenů
     */
    private List<Token> tokens = new ArrayList<>();

    /**
     * List automatů
     */
    private List<FiniteStateMachine> finiteStateMachines = new ArrayList<>();

    /**
     * Pole znaků
     */
    private char[] chars;

    /**
     * Informace o tom, zda se jedná o konec
     */
    private boolean isEnd = false;

    /**
     * Provede lexikální analýzu
     * @param fileName název souboru
     * @return informace o tom, zda nastala chyba
     */
    protected boolean lex(String fileName) {
        if(!openAndLoad(fileName)) {
            return true;
        }

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
                    if(!finiteStateMachines.get(INTVALPOS).isError() || !finiteStateMachines.get(IDENTPOS).isError()) {
                        createToken(start, lastCorrect.get() - 1);
                    }
                    else {
                        createToken(start, lastCorrect.get());
                        lastCorrect.getAndIncrement();
                    }
                    break;
                }
                else if(active.get() == 0 && finished.get() == 0) {
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

    /**
     * Provede inicializaci seznamu automatů
     */
    private void initializeFSMList() {
        finiteStateMachines.add(INTVALPOS, new IntValFSM()); // 1
        finiteStateMachines.add(IDENTPOS, new IdentFSM()); // 2
        finiteStateMachines.add(new WhileFSM());
        finiteStateMachines.add(new ProcedureFSM());
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
        finiteStateMachines.add(new CommaFSM());
        finiteStateMachines.add(new LogOpFSM());
        finiteStateMachines.add(new NotFSM());
    }

    /**
     * Načte další znak
     * @param current pozice
     * @return znak
     */
    private char nextChar(int current) {
        if(current >= chars.length) {
            isEnd = true;
            return 'e';
        }
        else {
            return chars[current];
        }
    }

    /**
     * Vytvoří token
     * @param start start tokenu
     * @param end konec tokenu
     */
    private void createToken(int start, int end) {
        String value = new String(chars, start, end - start + 1).trim();

        Token token;
        if(value.equals("pravda") || value.equals("nepravda") || value.matches("-?\\d+")) {
            token = new Token("hodnota", value);
        }
        else if(checkIfIsIdent(value)) {
            token = new Token("IDENTIFIKATOR", value);
        }
        else {
            token = new Token(value, value);
        }

        tokens.add(token);
        System.out.println(token.getName());
    }

    /**
     * Načte soubor
     * @param filename název souboru
     * @return informace, zda nedošlo k chybě
     */
    private boolean openAndLoad(String filename) {
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
            System.out.println("Error: file couldn't be loaded!");
            chars = new char[0];
            return false;
        }
        return true;
    }

    /**
     * Zkontroluje, zda je token identifikátor
     * @param string token
     * @return informace, zda je token identifikátor
     */
    private boolean checkIfIsIdent(String string) {
        return !string.equals("pravda") && !string.equals("nepravda") && !string.equals("zatimco") &&
                !string.equals("{") && !string.equals("}") && !string.equals("(") && !string.equals(")") &&
                !string.equals(";") && !string.equals(":") && !string.equals("logicky") && !string.equals("cislo") &&
                !string.equals("funkce") && !string.equals("+") && !string.equals("procedura") && !string.equals("*") &&
                !string.equals("/") && !string.equals("-") && !string.equals("pokud") && !string.equals("pokudne") &&
                !string.equals("<=") && !string.equals(">=") && !string.equals("!=") && !string.equals("==") &&
                !string.equals("<") && !string.equals(">") && !string.equals("&&") && !string.equals("||") &&
                !string.equals("zastav") && !string.equals("vrat") && !string.equals("konst") && !string.equals("=") &&
                !string.equals(",") && !string.equals("!") && !string.matches("-?\\d+") &&
                !string.matches("0");
    }

    /**
     * Získá seznam tokenů
     * @return seznam tokenů
     */
    public List<Token> getTokens() {
        return tokens;
    }
}
