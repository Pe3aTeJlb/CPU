package cpu;


import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


import java.util.ArrayList;

public class Memory {

    private ArrayList<IntegerProperty> memory = new ArrayList<>();
    private int memSize, columnNum, lineNum, cellSize, radix;
    private ArrayList<TableColumn<HexDataModel, Integer>> columns = new ArrayList<>();
    private TableView<HexDataModel> memTable;

    public Memory(int memSize, int columnNum, int cellSize, int radix){

        this.memSize = memSize;
        this.columnNum = columnNum;
        this.cellSize = cellSize;
        this.radix = radix;
        this.lineNum = memSize / columnNum;

        for (int i = 0; i < memSize; i++){
            memory.add(new SimpleIntegerProperty(0));
        }

    }

    public void loadMemory(ArrayList<Integer> newMem){

        if(newMem.size() < memory.size()){
            int diff = memory.size() - newMem.size();
            for(int i = 0; i < diff; i++){
                newMem.add(0);
            }
        }

        for(int i = 0; i < memSize; i++){
            memory.get(i).setValue(newMem.get(i));
        }

        memTable.refresh();

    }

    public IntegerProperty getProperty(int adr){
        return memory.get(adr);
    }

    public Integer get(int adr){
        highLightCell(adr);
        return memory.get(adr).getValue();
    }

    public void set(int adr, int literal){
        highLightCell(adr);
        memory.get(adr).setValue(literal);
        memTable.refresh();
    }

    public void erase(){

        for (int i = 0; i < memSize; i++){
            memory.get(i).set(0);
        }

        memTable.refresh();

    }



    private void highLightCell(int adr){
        int line = Math.floorDiv(adr, columnNum);
        int column = adr % columnNum;
        memTable.getSelectionModel().select(line, columns.get(column));
        memTable.requestFocus();
    }

    public void assignTableView(TableView<HexDataModel> memTable, boolean cellSelection){

        this.memTable = memTable;
        this.memTable.getSelectionModel().setCellSelectionEnabled(cellSelection);
        this.memTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        ObservableList<HexDataModel> list = FXCollections.observableArrayList();
        for(int i = 0; i < lineNum; i++){
            list.add(new HexDataModel(i * columnNum));
        }


        //init table
        //Addr column
        TableColumn<HexDataModel, Integer> addr = new TableColumn<>("Address");
        addr.setCellValueFactory(new PropertyValueFactory<>("Address"));
        addr.setCellFactory(param -> {

            TableCell<HexDataModel, Integer> cell = new TableCell<>() {

                @Override
                protected void updateItem(Integer item, boolean empty) {

                    if (item == getItem()) return;

                    super.updateItem(item, empty);

                    if (item == null) {
                        super.setText(null);
                        super.setGraphic(null);
                    } else {
                        super.setText(String.format("%0"+2+"X",item));
                        super.setGraphic(null);
                    }

                }

            };


            cell.setAlignment(Pos.CENTER);

            return cell;

        });


        addr.setSortable(false);
        addr.setReorderable(false);
        memTable.getColumns().add(addr);

        //Data column
        for (int i = 0; i < columnNum; i++){

            int j = i;

            TableColumn<HexDataModel, Integer> hex = new TableColumn<>("x"+i);
            hex.setCellValueFactory(data -> data.getValue().getMem(j));
            hex.setCellFactory(param -> {

                TableCell<HexDataModel, Integer> cell = new TableCell<>() {

                    @Override
                    protected void updateItem(Integer item, boolean empty) {

                        if (item == getItem()) return;

                        super.updateItem(item, empty);

                        if (item == null) {
                            super.setText(null);
                            super.setGraphic(null);
                        } else {
                            if(radix == 2){
                                super.setText(String.format("%" + cellSize + "s", Integer.toBinaryString(item)).replace(' ', '0'));
                            } else if(radix == 16) {
                                super.setText(String.format("%0" + cellSize/8 + "X", item));
                            }
                            super.setGraphic(null);
                        }

                    }

                };


                cell.setAlignment(Pos.CENTER);

                return cell;
            });

            hex.setSortable(false);
            hex.setReorderable(false);
            columns.add(hex);
            memTable.getColumns().add(hex);

            memTable.getItems().setAll(list);

        }

    }

    public class HexDataModel {

        private final int adr;

        public HexDataModel(int adr) {
            this.adr = adr;
        }

        public Integer getAdr(int i){
            return adr+i;
        }

        public int getAddress() {
            return adr;
        }

        public String toString() {
            return String.format("%X",adr);
        }

        public SimpleObjectProperty<Integer> getMem(int i) {
            return new SimpleObjectProperty<>(memory.get(adr + i).getValue());
        }

    }

}
