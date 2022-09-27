package ide;

import cpu.CPU;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class IDE {

    private TextArea ideTxtArea;
    private String programText;
    private BooleanProperty compiled = new SimpleBooleanProperty(false);
    private BooleanProperty modified = new SimpleBooleanProperty(false);
    private Lexer lexer;

    private CPU cpu;
    private Map<String, Integer> commandsMap = Map.ofEntries(
            entry("EMPTY", 0x00),
            entry("IREG", 0x01),
            entry("IMEM", 0x02),
            entry("SATR", 0x03),
            entry("SATM", 0x04),
            entry("SATI", 0x05),
            entry("LDAR", 0x06),
            entry("LDAD", 0x07),
            entry("LDAI", 0x08),
            entry("XCH", 0x09),
            entry("ADDR", 0x0A),
            entry("ADDI", 0x0B),
            entry("ADDC", 0x0C),
            entry("INC", 0x0D),
            entry("MUL", 0x0E),
            entry("DJNZ", 0x0F)
    );

    public IDE(TextArea ideTxtArea, Button compileBtn, Button runBtn, Button stepBtn, CPU cpu) {

        this.ideTxtArea = ideTxtArea;
        this.cpu = cpu;
        lexer = new Lexer();

        ideTxtArea.setOnKeyTyped(keyEvent -> checkForUpdate());

        compileBtn.setOnAction(event -> compile());

        runBtn.disableProperty().bind(compiled.not());
        runBtn.setOnAction(event -> cpu.executeProgram());

        stepBtn.disableProperty().bind(compiled.not());
        stepBtn.setOnAction(event -> cpu.executeInstruction());

    }

    private void compile() {

        simulationReset();

        programText = ideTxtArea.getText();

        if (lexer.readProgram(programText)){
            //lexer.print();
            cpu.loadCommandMemory(toBinaryCommands());
            compiled.set(true);
        }

    }

    private ArrayList<Integer> toBinaryCommands(){

        ArrayList<Integer> commands = new ArrayList<>();
        Map<String, Integer> marksMap = new HashMap<>();

        String command, literal, address, commandWord;
        for(Lexeme lexeme: lexer.getLexemes()){

            if(lexeme.getTerminal().getName().equals("MARK")) {
                literal = lexeme.getValue().replace(':', ' ').trim();
                marksMap.put(literal, commands.size());
            } else {

                String[] lit = lexeme.getValue().split(",");
                String[] op = lit[0].split("(\\s)+");
                command = String.format("%" + cpu.getCommandLen() + "s", Integer.toBinaryString(commandsMap.get(op[0]))).replace(' ', '0');
                address = String.format("%" + cpu.getAddrLen() + "s",
                        Integer.toBinaryString(Integer.parseInt(op[1].replaceAll("(?i)(@?R{1}|H{1})", ""), 16))
                ).replace(' ', '0');


                if (lit.length > 1) {

                    if(lexeme.getTerminal().getName().equals("DJNZ")){
                        literal =  String.format("%" + cpu.getLiteralLen() + "s",
                                Integer.toBinaryString(marksMap.get(lit[1].trim()))
                        ).replace(' ', '0');
                    } else {
                        literal = String.format("%" + cpu.getLiteralLen() + "s",
                                Integer.toBinaryString(Integer.parseInt(lit[1].trim().replace('#', ' ').replace('H', ' ').trim(), 16))
                        ).replace(' ', '0');
                    }
                } else {
                    literal = String.format("%" + cpu.getLiteralLen() + "s", "").replace(' ', '0');
                }

                commandWord = command + literal + address;
                //System.out.println(lexeme.getValue() + " " + commandWord);
                commands.add(Integer.parseInt(commandWord, 2));

            }


        }

        return commands;

    }

    private void checkForUpdate(){

        modified.set(!ideTxtArea.getText().equals(programText));
        if(modified.get()){
            compiled.set(false);
        }

    }

    public void simulationReset(){
        compiled.set(false);
        cpu.reset();
    }

}
