package rpr.projekat.zejd.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class MainController {
    @FXML
    private Button addBtn;
    @FXML
    private ScrollPane scrlPn;
    @FXML
    private VBox vbox;
    @FXML
    public void initialize(){
        scrlPn.getStylesheets().add(getClass().getResource("/css/subjectbutton.css").toExternalForm());
    }
    int brojac = 1;
    boolean test = false;
    Button lastClicked = null;

    private Button createOptionButton(String text){
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(60);
        button.getStyleClass().add("optionsbutton");
        return button;
    }

    public void addSubject(ActionEvent actionEvent){
        NamingController nc = new NamingController();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_subject_dialog.fxml"));
        loader.setController(nc);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Naming the subject");
        stage.setScene(new Scene(root,600,600));
        stage.show();
        stage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        String name = nc.getName();
                        if(name==null)return;
                        Button b = new Button(name);
                        b.getStyleClass().add("subjectbutton");
                        b.setOnAction((eh)->{
                            if(!test){
                                lastClicked = (Button) eh.getSource();
                                int number = vbox.getChildren().indexOf(lastClicked);
                                vbox.getChildren().add(number+1, createOptionButton("Dodaj fajl"));
                                vbox.getChildren().add(number+2, createOptionButton("Dodaj internet fajl"));
                                vbox.getChildren().add(number+3, createOptionButton("Obriši predmet"));
                                test = true;
                            }else if (lastClicked.equals(eh.getSource())){
                                int number = vbox.getChildren().indexOf(lastClicked);
                                lastClicked = null;
                                vbox.getChildren().remove(number+1);
                                vbox.getChildren().remove(number+1);
                                vbox.getChildren().remove(number+1);
                                test = false;
                            }else{
                                int numberToDelete = vbox.getChildren().indexOf(lastClicked);
                                lastClicked = (Button) eh.getSource();
                                vbox.getChildren().remove(numberToDelete+1);
                                vbox.getChildren().remove(numberToDelete+1);
                                vbox.getChildren().remove(numberToDelete+1);
                                int numberToAdd = vbox.getChildren().indexOf(lastClicked);
                                vbox.getChildren().add(numberToAdd+1, createOptionButton("Dodaj fajl"));
                                vbox.getChildren().add(numberToAdd+2, createOptionButton("Dodaj internet fajl"));
                                vbox.getChildren().add(numberToAdd+3, createOptionButton("Obriši predmet"));
                                test = true;
                            }

                        });
                        b.setPrefWidth(200);
                        b.setPrefHeight(60);
                        vbox.getChildren().add(b);
                        brojac++;
                    }
                });
            }
        });


    }
}
