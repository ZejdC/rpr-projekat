package rpr.projekat.zejd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rpr.projekat.zejd.Controllers.MainController;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_window.fxml"));
        loader.setController(new MainController());
        Parent root = loader.load();
        //Parent root = FXMLLoader.load(getClass().getResource("main_window.fxml"));
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(250);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 750, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
