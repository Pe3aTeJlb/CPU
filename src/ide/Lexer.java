package ide;


import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private  List<Terminal> TERMINALS = List.of(

			new Terminal("IREG", "(?i)^(\\s)*(ireg){1}(\\s)+r[0-7]+,(\\s)*(#+[0-9a-f]{2}+h)"),
			new Terminal("IMEM", "(?i)^(\\s)*(imem){1}(\\s)+[0-9a-f]{2}+h+,(\\s)*(#+[0-9a-f]{2}+h)"),

			new Terminal("ACCUM", "(?i)^(\\s)*(satr|ldar){1}(\\s)+(r[0-7]{1})(\\s)*"),
            new Terminal("ACCUM", "(?i)^(\\s)*(satm|ldad){1}(\\s)+([0-9a-f]{2}+h)(\\s)*"),
            new Terminal("ACCUM", "(?i)^(\\s)*(sati|ldai){1}(\\s)+(@r[0-7]{1})(\\s)*"),
            new Terminal("ACCUM", "(?i)^(\\s)*(xch){1}(\\s)+(ab)(\\s)*"),

            new Terminal("ADD", "(?i)^(\\s)*(addr|addc){1}(\\s)+(r[0-7]{1})(\\s)*"),
            new Terminal("ADD", "(?i)^(\\s)*(addi){1}(\\s)+(@r[0-7]{1})(\\s)*"),
			
			new Terminal("INC", "(?i)^(\\s)*(inc){1}(\\s)+(r[0-7]{1})(\\s)*"),
			new Terminal("MUL", "(?i)^(\\s)*(mul){1}(\\s)+(ab)(\\s)*"),
			new Terminal("DJNZ", "(?i)^(\\s)*(djnz){1}(\\s)+(r[0-7]{1}|)+,(\\s)*+([A-Za-z])+$"),

            new Terminal("EMPTY", "(?i)^(\\s)*empty(\\s)*$"),

            new Terminal("MARK", "^(\\s)*([A-Za-z]*:{1})(\\s)*+$"),
            new Terminal("WS", "^(\\s)*$"),
            new Terminal("COMMENTARY", "(^(\\s)*(//.*)$)|(^[(\\t)(\\s)]*$)")

    );

    private ArrayList<String> marks;
    private ArrayList<Lexeme> lexemes;
    private int curLine;

    public boolean readProgram(String programText) {

        lexemes = new ArrayList<>();
        marks = new ArrayList<>();

        String[] strings = programText.split("\n");

        for (int i = 0; i < strings.length; i++){
            curLine = i;
            Lexeme lexeme = extractNextLexeme(new StringBuilder(strings[i]));

            if(lexeme == null) return false;

            if (!lexeme.getTerminal().getName().equals("WS") &&
                    !lexeme.getTerminal().getName().equals("COMMENTARY")){

                if(lexeme.getTerminal().getName().equals("MARK")){

                    if(!marks.contains(lexeme.getValue())){
                        marks.add(lexeme.getValue());
                        lexemes.add(lexeme);
                    } else {

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Эмулятор процессора");
                        alert.setHeaderText("Синтаксическая ошибка");
                        alert.setContentText("Метка " + lexeme.getValue() + " уже определена в программе");

                        alert.showAndWait();

                        return false;

                    }

                } else {
                    lexemes.add(lexeme);
                }

            }
        }

        return true;

    }

    private Lexeme extractNextLexeme(StringBuilder input) {

        if (anyTerminalMatches(input)) {

            List<Terminal> terminals = lookupTerminals(input);

            return new Lexeme(getPrioritizedTerminal(terminals), input.toString());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Эмулятор процессора");
            alert.setHeaderText("Синтаксическая ошибка");
            alert.setContentText("Синтаксическая ошибка в операторе " + input + " в строке " + curLine);

            alert.showAndWait();
            return null;
        }

    }

    private Terminal getPrioritizedTerminal(List<Terminal> terminals) {

        Terminal prioritizedTerminal = terminals.get(0);

        for (Terminal terminal : terminals) {

            if (terminal.getPriority() > prioritizedTerminal.getPriority()) {
                prioritizedTerminal = terminal;
            }

        }

        return prioritizedTerminal;

    }

    private boolean anyTerminalMatches(StringBuilder buffer) {
        return lookupTerminals(buffer).size() != 0;
    }

    private List<Terminal> lookupTerminals(StringBuilder buffer) {

        List<Terminal> terminals = new ArrayList<>();

        for (Terminal terminal : TERMINALS) {

            if (terminal.matches(buffer)) {
                terminals.add(terminal);
            }

        }

        return terminals;
    }

    private StringBuilder lookupInput(String[] args) {

        if (args.length == 0) {
            throw new IllegalArgumentException("Input string not found");
        }

        StringBuilder buff = new StringBuilder();

        for (String arg : args) {
            buff.append(arg).append(" ");
        }

        return buff;

    }

    public ArrayList<Lexeme> getLexemes(){
        return lexemes;
    }

    public void print() {

        for (Lexeme lexeme : lexemes) {
            System.out.printf("[%s, %s]%n",
                    lexeme.getTerminal().getName(),
                    lexeme.getValue());
        }

    }

}
