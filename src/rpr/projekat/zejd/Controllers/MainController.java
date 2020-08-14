package rpr.projekat.zejd.Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rpr.projekat.zejd.Models.DirectoryModel;
import rpr.projekat.zejd.Models.Location;
import rpr.projekat.zejd.Models.Subject;
import rpr.projekat.zejd.Utility.OptionButtons;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static rpr.projekat.zejd.Utility.OptionButtons.*;

public class MainController {
    DirectoryModel model;
    @FXML
    private ScrollPane scrlPn;
    @FXML
    private VBox vbox;
    @FXML
    private ListView<Location> list;
    @FXML
    public void initialize(){
        scrlPn.getStylesheets().add(getClass().getResource("/css/subjectbutton.css").toExternalForm());
        model = new DirectoryModel();
        updateSubjectsFromDatabase();
        updateListView();
    }
    int brojac = 1;
    boolean test = false;
    Button lastClicked = null;

    // THE DEQUE IS USED TO FIND DIRECTORIES IN THE DATABASE MORE EASILY
    // THE STACK IS USED TO NAVIGATE BACK TO THE PARENT DIRECTORY

    private Deque<String> pathQueue = new LinkedList<>();
    private Stack<String> pathStack = new Stack<>();

    private void updateListView(){

    }

    private void refreshSubjects(){
        int size = vbox.getChildren().size();
        for(int i = 1; i < size; i++){
            vbox.getChildren().remove(1);
        }
        brojac = 1;
        test = false;
        updateSubjectsFromDatabase();
    }

    private void updateSubjectsFromDatabase(){
        ArrayList<Subject> list = model.getAllSubjects();
        for(Subject s: list){
            Button b = createSubjectButton(s.getName());
            vbox.getChildren().add(b);
        }
    }

    String currentSubject = "";
    String currentDirectory = "";

    private Button createSubjectButton(String text){
        Button b = new Button(text);
        b.getStyleClass().add("subjectbutton");
        b.setOnAction((eh)->{
            // IF NO SUBJECT IS CURENTLY SELECTED
            if(!test){
                pathQueue.add(b.getText());
                pathStack.add(b.getText());
                lastClicked = (Button) eh.getSource();
                int number = vbox.getChildren().indexOf(lastClicked);
                vbox.getChildren().add(number+1, createOptionButton("Dodaj fajl", ADDFILE));
                vbox.getChildren().add(number+2, createOptionButton("Dodaj direktorij", ADDDIRECTORY));
                vbox.getChildren().add(number+3, createOptionButton("Dodaj internet fajl", ADDINTERNETFILE));
                vbox.getChildren().add(number+4, createOptionButton("Obriši predmet", DELETESUBJECT));
                test = true;
            }
            // IF THE SUBJECT THAT IS SELECTED GETS CLICKED
            else if (lastClicked.equals(eh.getSource())){
                pathQueue.clear();
                pathStack.clear();
                int number = vbox.getChildren().indexOf(lastClicked);
                lastClicked = null;
                vbox.getChildren().remove(number+1);
                vbox.getChildren().remove(number+1);
                vbox.getChildren().remove(number+1);
                vbox.getChildren().remove(number+1);
                test = false;
            }
            // IF A SUBJECT IS SELECTED AND ANOTHER SUBJECT GETS CLICKED
            else{
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
                pathStack.clear();
                pathQueue.clear();
                pathQueue.add(b.getText());
                pathStack.add(b.getText());
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

                    if(file!=null)
                        model.addFile(pathQueue,file.getName(),file);
                });
                break;
            case ADDDIRECTORY:
                button.setOnAction((handler)->{
                    AddDirectoryController ad = new AddDirectoryController();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/naming_dialog.fxml"));
                    loader.setController(ad);
                    Parent root = null;
                    try {
                        root = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Naming the subject");
                    stage.setScene(new Scene(root,600,300));
                    stage.setResizable(false);
                    stage.show();
                    stage.setOnHiding(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent windowEvent) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    String name = ad.getName();
                                    if(name==null)return;
                                    model.addDirectory(pathQueue,name);
                                    refreshSubjects();
                                }
                            });
                        }
                    });
                });
                break;
            case DELETESUBJECT:
                button.setOnAction((handler)->{
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm deletion");
                    alert.setHeaderText("Are you sure you want to delete this subject including all files belonging to the subject?");
                    Optional<ButtonType> option = alert.showAndWait();
                    if(option.get()==null){
                    }
                    else if(option.get()==ButtonType.OK){
                        model.deleteSubject(pathQueue);
                        refreshSubjects();
                    }
                    else if(option.get()==ButtonType.CANCEL){
                    }
                });
                break;
            case ADDINTERNETFILE:
                button.setOnAction((handler)->{
                    // TODO
                });
                break;
        }
        return button;
    }

    public void addSubject(ActionEvent actionEvent){
        AddSubjectController as = new AddSubjectController();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/naming_dialog.fxml"));
        loader.setController(as);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Naming the subject");
        stage.setScene(new Scene(root,600,300));
        stage.setResizable(false);
        stage.show();
        stage.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        String name = as.getName();
                        if(name==null)return;
                        model.addSubject(name);
                        refreshSubjects();
                    }
                });
            }
        });
    }
}
