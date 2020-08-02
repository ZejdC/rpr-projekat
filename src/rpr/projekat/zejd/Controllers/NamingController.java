package rpr.projekat.zejd.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NamingController {
    @FXML
    private TextField subject;

    @FXML
    public void initialize(){

    }

    public void confirm(ActionEvent ae){
        Node n = (Node) ae.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void cancel(ActionEvent ae){
        Node n = (Node) ae.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public String getName(){
        if(subject.getText().toString().equals(""))return null;
        return subject.getText().toString();
    }
}
