package rpr.projekat.zejd.Controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rpr.projekat.zejd.Models.DirectoryModel;
import rpr.projekat.zejd.Models.Subject;
import rpr.projekat.zejd.Utility.OptionButtons;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static rpr.projekat.zejd.Utility.OptionButtons.*;

public class MainController {
    DirectoryModel model;
    @FXML
    private ScrollPane scrlPn;
    @FXML
    private VBox vbox;
    @FXML
    public void initialize(){
        scrlPn.getStylesheets().add(getClass().getResource("/css/subjectbutton.css").toExternalForm());
        model = new DirectoryModel();
        updateSubjectsFromDatabase();

    }
    private String currentpath = System.getProperty("user.home")+"/"+"SubjectManagementSystem";
    int brojac = 1;
    boolean test = false;
    Button lastClicked = null;

    private void refreshSubjects(){
        int size = vbox.getChildren().size();
        for(int i = 1; i < size; i++){
            vbox.getChildren().remove(1);
        }
        brojac = 1;
        updateSubjectsFromDatabase();
    }

    private void updateSubjectsFromDatabase(){
        ObservableList<Subject> list = model.getAllSubjects();
        for(Subject s: list){
            Button b = createSubjectButton(s.getName());
            vbox.getChildren().add(b);
        }
    }

    private Button createSubjectButton(String text){
        Button b = new Button(text);
        b.getStyleClass().add("subjectbutton");
        b.setOnAction((eh)->{
            if(!test){
                lastClicked = (Button) eh.getSource();
                int number = vbox.getChildren().indexOf(lastClicked);
                vbox.getChildren().add(number+1, createOptionButton("Dodaj fajl", ADDFILE));
                vbox.getChildren().add(number+2, createOptionButton("Dodaj direktorij", ADDDIRECTORY));
                vbox.getChildren().add(number+3, createOptionButton("Dodaj internet fajl", ADDINTERNETFILE));
                vbox.getChildren().add(number+4, createOptionButton("Obriši predmet", DELETESUBJECT));
                test = true;
            }else if (lastClicked.equals(eh.getSource())){
                int number = vbox.getChildren().indexOf(lastClicked);
                lastClicked = null;
                vbox.getChildren().remove(number+1);
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
                vbox.getChildren().remove(numberToDelete+1);
                int numberToAdd = vbox.getChildren().indexOf(lastClicked);
                vbox.getChildren().add(numberToAdd+1, createOptionButton("Dodaj fajl", ADDFILE));
                vbox.getChildren().add(numberToAdd+2, createOptionButton("Dodaj direktorij", ADDDIRECTORY));
                vbox.getChildren().add(numberToAdd+3, createOptionButton("Dodaj internet fajl", ADDINTERNETFILE));
                vbox.getChildren().add(numberToAdd+4, createOptionButton("Obriši predmet", DELETESUBJECT));
                test = true;
            }

        });
        b.setPrefWidth(200);
        b.setPrefHeight(60);
        brojac++;
        return b;
    }

    private Button createOptionButton(String text, OptionButtons op){
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(60);
        button.getStyleClass().add("optionsbutton");
        switch (op){
            case ADDFILE:
                button.setOnAction((handler)->{
                    FileChooser fileChooser = new FileChooser();
                    File file = fileChooser.showOpenDialog(new Stage());
                });
        }
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
                        model.addSubject(name);
                        refreshSubjects();
                    }
                });
            }
        });


    }
}
