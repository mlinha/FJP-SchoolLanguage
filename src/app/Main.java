package app;

import syntax.RecursiveDescentParser;

import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {

        LinkedList<String> a = new LinkedList<>();
        a.add("konst");
        a.add("typ");
        a.add("ident");
        a.add("=");
        a.add("hodnota");
        a.add(";");
        a.add("funkce");
        RecursiveDescentParser i = new RecursiveDescentParser(a);
        i.program();
    }
}
