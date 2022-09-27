package cpu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("cpu/cpu.fxml"));

        Parent root = null;

        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setTitle("Эмулятор процессора");
        primaryStage.setScene(new Scene(root, 800, 600));

        GUIController c = loader.getController();
        c.postInitialization();

        primaryStage.show();
    }

}
