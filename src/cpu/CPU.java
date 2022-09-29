package cpu;

import javafx.beans.property.*;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.Map;

import static java.util.Map.entry;

public class CPU {

    private Map<Integer, String> commandsMap = Map.ofEntries(
            entry(0x00, "EMPTY"),
            entry(0x01,"SATR"),
            entry(0x02,"SATM"),
            entry(0x03,"SATI"),
            entry(0x04,"LDAR"),
            entry(0x05,"LDAM"),
            entry(0x06,"LDAD"),
            entry(0x07,"LDAI"),
            entry(0x08,"XCH"),
            entry(0x09,"ADDR"),
            entry(0x0A,"ADDI"),
            entry(0x0B,"ADDC"),
            entry(0x0C,"INC"),
            entry(0x0D,"DEC"),
            entry(0x0E,"MUL"),
            entry(0x0F,"JNZ")
    );

    private IntegerProperty commandCounterReg;
    private IntegerProperty rawCommandReg;
    private StringProperty decodedCommandReg;
    private IntegerProperty AReg, BReg;
    private BooleanProperty carryFlagReg;
    private BooleanProperty zeroFlagReg;

    private Memory commandMemory, dataMemory;

    int commandLen = 8;
    int addrLen = 8;

    private boolean emptyReached;

    public CPU(){

        AReg = new SimpleIntegerProperty(0);
        BReg = new SimpleIntegerProperty(0);
        commandCounterReg = new SimpleIntegerProperty(0);
        rawCommandReg = new SimpleIntegerProperty(0);
        decodedCommandReg = new SimpleStringProperty("");
        carryFlagReg = new SimpleBooleanProperty(false);
        zeroFlagReg = new SimpleBooleanProperty(false);

        emptyReached = false;

        commandMemory = new Memory(64, 1, commandLen+addrLen, 2);
        dataMemory = new Memory(256, 8, 16, 16);

    }

    public void loadCommandMemory(ArrayList<Integer> memory){
        commandMemory.loadMemory(memory);
    }


    int command, adr;
    boolean jmp = false;

    public void executeProgram(){

        while (!emptyReached){
            executeInstruction();
        }

    }

    public void executeInstruction(){

        rawCommandReg.setValue(commandMemory.get(commandCounterReg.get()));

        decodeCommand();

        if(!jmp) {
            commandCounterReg.setValue(commandCounterReg.get() + 1);
        }
        jmp = false;
    }

    private void decodeCommand(){

        String rawCommand = String.format("%" + this.getWordLen() + "s", Integer.toBinaryString(rawCommandReg.get())).replace(' ', '0');
        System.out.println(rawCommand);

        command = Integer.parseInt(rawCommand.substring(0, commandLen),2);
        adr = Integer.parseInt(rawCommand.substring(commandLen, commandLen+addrLen),2);

        System.out.println(rawCommand.substring(0, commandLen) + " " + rawCommand.substring(commandLen, commandLen+addrLen));
        System.out.println(command+ " " + adr + " " + commandsMap.get(command));

        switch (commandsMap.get(command)){

            case "SATR": SATR(adr); break;
            case "SATM": SATM(adr); break;
            case "SATI": SATI(adr); break;

            case "LDAR": LDAR(adr); break;
            case "LDAM": LDAM(adr); break;
            case "LDAD": LDAD(adr); break;
            case "LDAI": LDAI(adr); break;
            case "XCH": XCH(); break;

            case "ADDR": ADDR(adr); break;
            case "ADDI": ADDI(adr); break;
            case "ADDC": ADDC(adr); break;
            case "INC": INC(adr); break;
            case "DEC": DEC(adr); break;
            case "MUL": MUL(); break;

            case "JNZ": JNZ(adr); break;

            default: EMPTY(); break;

        }

    }

    private void EMPTY(){

        emptyReached = true;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Эмулятор процессора");
        alert.setHeaderText("Нет инструкции");
        alert.setContentText("По адресу  " + String.format("%0" + 2 + "X", commandCounterReg.getValue())+"H" + " нет инструкции");

        alert.showAndWait();

    }



    private void IREG(int reg, int literal){
        decodedCommandReg.setValue("IREG R"+reg+", #"+String.format("%0" + 2 + "X", literal)+"H");
        dataMemory.set(reg, literal);
    }

    private void IMEM(int adr, int literal){
        decodedCommandReg.setValue("IMEM "+String.format("%0" + 2 + "X", adr)+"H, #"+String.format("%0" + 2 + "X", literal)+"H");
        dataMemory.set(adr, literal);
    }



    private void SATR(int reg){
        decodedCommandReg.setValue("SATR R"+reg);
        dataMemory.set(reg, AReg.get());
    }

    private void SATM(int adr){
        decodedCommandReg.setValue("SATR "+String.format("%0" + 2 + "X", adr)+"H");
        dataMemory.set(adr, AReg.get());
    }

    private void SATI(int reg){
        decodedCommandReg.setValue("SATR @R"+reg);
        dataMemory.set(dataMemory.get(reg), AReg.get());
    }



    private void LDAR(int reg){
        decodedCommandReg.setValue("LDAR R"+reg);
        AReg.set(dataMemory.get(reg));
    }

    private void LDAM(int adr){
        decodedCommandReg.setValue("LDAM "+String.format("%0" + 2 + "X", adr)+"H");
        AReg.set(dataMemory.get(adr));
    }

    private void LDAD(int literal){
        decodedCommandReg.setValue("LDAI #"+literal+"h");
        AReg.set(literal);
    }

    private void LDAI(int reg){
        decodedCommandReg.setValue("LDAI @R"+reg);
        AReg.set(dataMemory.get(dataMemory.get(reg)));
    }

    private void XCH(){
        decodedCommandReg.setValue("XCH AB");
        int buff = AReg.getValue();
        AReg.set(BReg.get());
        BReg.set(buff);
    }



    private void ADDR(int reg){

        decodedCommandReg.setValue("ADDR R"+reg);

        int result = AReg.get() + dataMemory.get(reg);
        if(result > 255){
            String buff = Integer.toBinaryString(result);
            result = Integer.parseInt(buff.substring(buff.length()-8),2);
            carryFlagReg.set(true);
        } else {
            carryFlagReg.set(false);
        }
        AReg.set(result);

    }

    private void ADDI(int reg){

        decodedCommandReg.setValue("ADDI @R"+reg);

        int result = AReg.get() + dataMemory.get(dataMemory.get(reg));
        if(result > 255){
            String buff = Integer.toBinaryString(result);
            result = Integer.parseInt(buff.substring(buff.length()-8),2);
            carryFlagReg.set(true);
        } else {
            carryFlagReg.set(false);
        }
        AReg.set(result);

    }

    private void ADDC(int reg){

        decodedCommandReg.setValue("ADDC R"+reg);

        int carry = carryFlagReg.get() ? 1 : 0;
        int result = AReg.get() + dataMemory.get(reg) + carry;
        if(result > 255){
            String buff = Integer.toBinaryString(result);
            result = Integer.parseInt(buff.substring(buff.length()-8),2);
            carryFlagReg.set(true);
        } else {
            carryFlagReg.set(false);
        }
        AReg.set(result);

    }

    private void INC(int reg){
        decodedCommandReg.setValue("INC R"+reg);
        dataMemory.set(reg, dataMemory.get(reg)+1);
    }

    private void DEC(int reg){
        decodedCommandReg.setValue("DEC R"+reg);
        int buff = dataMemory.get(reg) - 1;
        if(buff == 0) zeroFlagReg.set(true);
        dataMemory.set(reg, buff);
    }

    private void MUL(){

        decodedCommandReg.setValue("MUL AB");

        int result = AReg.get() * BReg.get();
        String res =  String.format("%" + 16 + "s", Integer.toBinaryString(result)).replace(' ', '0');
        int high = Integer.parseInt(res.substring(0, 8),2);
        int low = Integer.parseInt(res.substring(8, 16),2);
        System.out.println(res+ " " + high + " " + low);
        AReg.set(low);
        BReg.set(high);

    }



    private void JNZ(int literal){

        decodedCommandReg.setValue("JNZ "+ String.format("%0" + 2 + "X", literal)+"H");

        if(zeroFlagReg.get() == false){
            commandCounterReg.set(literal);
            jmp = true;
        }

    }



    public void reset(){

        dataMemory.erase();
        commandMemory.erase();

        AReg.set(0);
        BReg.set(0);
        commandCounterReg.set(0);
        rawCommandReg.set(0);
        decodedCommandReg.set("");
        carryFlagReg.set(false);
        zeroFlagReg.set(false);

        emptyReached = false;

    }


    public int getCommandLen() {
        return commandLen;
    }


    public int getAddrLen() {
        return addrLen;
    }

    public int getWordLen(){
        return commandLen+addrLen;
    }

    public Memory getCommandMemory(){
        return commandMemory;
    }

    public Memory getDataMemory(){
        return dataMemory;
    }



    public IntegerProperty getCommandCounterReg(){
        return commandCounterReg;
    }

    public IntegerProperty getRawCommandReg(){
        return rawCommandReg;
    }

    public StringProperty getDecodedCommandReg(){
        return decodedCommandReg;
    }

    public IntegerProperty getAReg(){
        return AReg;
    }

    public IntegerProperty getBReg(){
        return BReg;
    }

    public BooleanProperty getCarryFlagReg(){
        return carryFlagReg;
    }

    public BooleanProperty getZeroFlagReg(){
        return zeroFlagReg;
    }

}
