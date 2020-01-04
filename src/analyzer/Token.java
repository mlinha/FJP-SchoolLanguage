package analyzer;

/**
 * Token
 */
public class Token {

    /**
     * Jméno tokenu
     */
    private String name;

    /**
     * Hodnota tokenu, např. pro číselná hodnota
     */
    private String value;

    /**
     * Konstruktor
     * @param name jméno tokenu
     * @param value hodnota tokenu
     */
    public Token(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Získá jméno
     * @return jméno
     */
    public String getName() {
        return name;
    }

    /**
     * Získá hodnotu
     * @return hodnota
     */
    public String getValue() {
        return value;
    }
}
