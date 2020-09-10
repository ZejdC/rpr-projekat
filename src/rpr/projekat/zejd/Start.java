package rpr.projekat.zejd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rpr.projekat.zejd.Controllers.MainController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Start extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle resourceBundle = ResourceBundle.getBundle("translations");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_window.fxml"),resourceBundle);
        loader.setController(new MainController());
        Parent root = loader.load();
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(250);

        primaryStage.setTitle("Subject Management System");
        primaryStage.setScene(new Scene(root, 750, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
