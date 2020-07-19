package rpr.projekat.zejd.Controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MainController {
    @FXML
    private Button addBtn;
    @FXML
    private ScrollPane scrlPn;
    @FXML
    private VBox vbox;
    @FXML
    public void initialize(){

    }

    public void addSubject(ActionEvent actionEvent){
        Button b = new Button("Dodao");
        b.setPrefWidth(200);
        b.setPrefHeight(60);
        vbox.getChildren().add(b);

    }
}
