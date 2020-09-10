package rpr.projekat.zejd.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RenameController {
    String oldName;
    String headerText;
    @FXML
    private TextField subject;
    @FXML
    private Label header;

    @FXML
    public void initialize(){
        header.setText(headerText);
        subject.setText(oldName);
    }

    public RenameController(String oldName, String headerText){
        this.oldName = oldName;
        this.headerText = headerText;
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
        if(subject.getText().equals("") || subject.getText().equals(oldName))return null;
        return subject.getText().toString();
    }
}
