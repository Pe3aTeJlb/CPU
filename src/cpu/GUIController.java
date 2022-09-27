package cpu;

import ide.IDE;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;

public class GUIController {

    @FXML
    private AnchorPane root;


    //ALU

    @FXML
    private TextField accumHexTxtFld;

    @FXML
    private TextField accumDecTxtFld;

    @FXML
    private TextField accumBinTxtFld;


    @FXML
    private TextField bccumHexTxtFld;

    @FXML
    private TextField bccumDecTxtFld;

    @FXML
    private TextField bccumBinTxtFld;


    @FXML
    private RadioButton carryFlagRdBtn;



    //Memory

    @FXML
    private TableView<Memory.HexDataModel> commandMemTblVw;

    @FXML
    private TableView<Memory.HexDataModel> dataMemTblVw;



    //Command Regs

    @FXML
    private TextField commandCounterTxtFld;

    @FXML
    private TextField commandRegTxtFld;

    @FXML
    private TextField commandDecodTxtFld;

    private CPU cpu;

    //RON

    @FXML
    private TextField Reg0;

    @FXML
    private TextField Reg1;

    @FXML
    private TextField Reg2;

    @FXML
    private TextField Reg3;

    @FXML
    private TextField Reg4;

    @FXML
    private TextField Reg5;

    @FXML
    private TextField Reg6;

    @FXML
    private TextField Reg7;



    //IDE

    @FXML
    private Button compileBtn;

    @FXML
    private Button runBtn;

    @FXML
    private Button stepBtn;

    @FXML
    private TextArea ideTxtArea;

    private IDE ide;



    //Other
    @FXML
    private MenuBar menuBar;
    private Stage commandStage;
    private Window window;

    @FXML
    public void initialize(){

        initCPU();
        initIDE();

        initMenuBar();

    }

    public void postInitialization(){
        window = root.getScene().getWindow();
        commandStage.initOwner(window);
    }

    public void initCPU(){

        cpu = new CPU();

        cpu.getDataMemory().assignTableView(dataMemTblVw, true);
        cpu.getCommandMemory().assignTableView(commandMemTblVw, false);


        //Bind registers
        NumberStringConverter converter = new NumberStringConverter(){
            @Override
            public String toString(Number value) {
                return String.format("%0" + 2 + "X", (int)value);
            }
        };
        Reg0.textProperty().bindBidirectional(cpu.getDataMemory().getProperty(0), converter);
        Reg1.textProperty().bindBidirectional(cpu.getDataMemory().getProperty(1), converter);
        Reg2.textProperty().bindBidirectional(cpu.getDataMemory().getProperty(2), converter);
        Reg3.textProperty().bindBidirectional(cpu.getDataMemory().getProperty(3), converter);
        Reg4.textProperty().bindBidirectional(cpu.getDataMemory().getProperty(4), converter);
        Reg5.textProperty().bindBidirectional(cpu.getDataMemory().getProperty(5), converter);
        Reg6.textProperty().bindBidirectional(cpu.getDataMemory().getProperty(6), converter);
        Reg7.textProperty().bindBidirectional(cpu.getDataMemory().getProperty(7), converter);



        NumberStringConverter accumHexConverter = new NumberStringConverter(){
            @Override
            public String toString(Number value) {
                return String.format("%0" + 2 + "X", (int)value);
            }
        };
        NumberStringConverter accumDecConverter = new NumberStringConverter();
        NumberStringConverter accumBinConverter = new NumberStringConverter(){
            @Override
            public String toString(Number value) {
                return String.format("%" + 8 + "s", Integer.toBinaryString((int)value)).replace(' ', '0');
            }
        };

        accumHexTxtFld.textProperty().bindBidirectional(cpu.getAReg(), accumHexConverter);
        accumDecTxtFld.textProperty().bindBidirectional(cpu.getAReg(), accumDecConverter);
        accumBinTxtFld.textProperty().bindBidirectional(cpu.getAReg(), accumBinConverter);

        bccumHexTxtFld.textProperty().bindBidirectional(cpu.getBReg(), accumHexConverter);
        bccumDecTxtFld.textProperty().bindBidirectional(cpu.getBReg(), accumDecConverter);
        bccumBinTxtFld.textProperty().bindBidirectional(cpu.getBReg(), accumBinConverter);



        NumberStringConverter rawCommandConverter = new NumberStringConverter(){
            @Override
            public String toString(Number value) {
                return String.format("%" + cpu.getWordLen() + "s", Integer.toBinaryString((int)value)).replace(' ', '0');
            }
        };
        commandCounterTxtFld.textProperty().bindBidirectional(cpu.getCommandCounterReg(), accumHexConverter);
        commandRegTxtFld.textProperty().bindBidirectional(cpu.getRawCommandReg(), rawCommandConverter);
        commandDecodTxtFld.textProperty().bindBidirectional(cpu.getDecodedCommandReg());

        carryFlagRdBtn.selectedProperty().bind(cpu.getCarryFlagReg());

    }

    public void initIDE(){
        ide = new IDE(ideTxtArea, compileBtn, runBtn, stepBtn, cpu);
    }

    public void initMenuBar(){

        Menu file = new Menu();
        file.setText("Файл");

        MenuItem closeItem = new MenuItem();
        closeItem.setText("Закрыть");
        closeItem.setOnAction(event -> {
            //close ide
            Platform.exit();
            System.exit(0);
        });

        file.getItems().addAll(
                closeItem
        );



        Menu simulation = new Menu();
        simulation.setText("Моделирование");

        MenuItem resetSimulation = new MenuItem();
        resetSimulation.setText("Сбросить модеирование");
        resetSimulation.setOnAction(event -> {
            ide.simulationReset();
        });

        simulation.getItems().addAll(
                resetSimulation
        );



        Menu help = new Menu();
        help.setText("Справка");

        MenuItem commandItem = new MenuItem();
        commandItem.setText("Система команд");
        createCommandStage();
        commandItem.setOnAction(event -> commandStage.show());

        MenuItem aboutItem = new MenuItem();
        aboutItem.setText("О программе");
        aboutItem.setOnAction(event -> {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Эмулятор процессора");
            alert.setHeaderText("Об авторах");
            alert.setContentText("Автор: студент группы ИВМО-01-22 Щепухин Денис Олегович \n" +
                    "РТУ МИРЭА 2022");

            alert.showAndWait();

        });

        help.getItems().addAll(
                commandItem,
                new SeparatorMenuItem(),
                aboutItem
        );

        menuBar.getMenus().addAll(
                file,
                simulation,
                help
        );

    }

    public void createCommandStage(){

        commandStage = new Stage();
        commandStage.hide();
        commandStage.setAlwaysOnTop(true);

        commandStage.setTitle("Система команд");

        AnchorPane root = new AnchorPane();

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setText("\tEMPTY - пустая команда\n" +
                "\n" +
                "Операции взаимодействия между регистрами/ячейками памяти\n" +
                "\n" +
                "\tIREG R0, #00h (immediate to register) - пересылка в регистр константы\n" +
                "\tIMEM 00h, #00h (immediate to memory) - пересылка в память константы\n" +
                "\n" +
                "\tSATR R0 - сохранение аккумулятора в регистр\n" +
                "\tSATM 00h - сохранение аккумулятора по адресу в памяти\n" +
                "\tSATI @R0 - сохранение аккумулятора по косвенному адресу\n" +
                "\n" +
                "\tLDAR R0 - загрузка в аккумулятор из регистра\n" +
                "\tLDAM 00h - загрузка в аккумулятор из памяти\n" +
                "\tLDAI @R0 - загрузка в аккумулятор из памяти по косвенному адресу\n" +
                "\tXCH AB - обмен между регистрами A B\n" +
                "\n" +
                "Арифметические операци:\n" +
                "\n" +
                "\tADDR R0 - сложение аккумулятора с регистром\n" +
                "\tADDI @R0 - сложение аккумулятора с косвенно адресуемым байтом\n" +
                "\tADDC R0 - сложение аккумулятора с регистром и переносом\n" +
                "\tINC R0 - инкремент регистра\n" +
                "\tMUL AB - умножить AB - результат (B)(A) <= (A)*(B)\n" +
                "\n" +
                "Операции условного перехода:\n" +
                "\n" +
                "\tDJNZ R0, loop - дек регистра и переход если не 0");

        root.getChildren().add(textArea);

        AnchorPane.setLeftAnchor(textArea, 0.0);
        AnchorPane.setTopAnchor(textArea, 0.0);
        AnchorPane.setRightAnchor(textArea, 0.0);
        AnchorPane.setBottomAnchor(textArea, 0.0);

        commandStage.setScene(new Scene(root, 450, 600));

    }

}
