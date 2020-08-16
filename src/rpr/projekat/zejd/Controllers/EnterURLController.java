package rpr.projekat.zejd.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EnterURLController {
    @FXML
    private TextField subject;
    @FXML
    private Label header;

    @FXML
    public void initialize(){
        header.setText("Enter the URL of the file");
    }

    public void confirm(ActionEvent ae){
        Node n = (Node) ae.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void cancel(ActionEvent ae){
        subject.setText("");
        Node n = (Node) ae.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public String getName(){
        if(subject.getText().toString().equals(""))return null;
        return subject.getText().toString();
    }
}
