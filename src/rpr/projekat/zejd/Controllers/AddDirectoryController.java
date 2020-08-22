package rpr.projekat.zejd.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddDirectoryController {
    String placeholder;
    @FXML
    private TextField subject;
    @FXML
    private Label header;

    public AddDirectoryController(String adddirectorytext) {
        placeholder = adddirectorytext;
    }

    @FXML
    public void initialize(){
        header.setText(placeholder);
    }

    public void confirm(ActionEvent ae){
        if(subject.getText().contains(".")){
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText("The directory name can't contain '.'");
            a.show();
            return;
        }
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
