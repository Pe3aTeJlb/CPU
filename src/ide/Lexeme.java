package ide;

public class Lexeme {

    private Terminal terminal;
    private String value;

    public Lexeme(Terminal t, String v){
        terminal = t;
        value = v;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public String getValue() {
        return value.toUpperCase().trim();
    }

}
