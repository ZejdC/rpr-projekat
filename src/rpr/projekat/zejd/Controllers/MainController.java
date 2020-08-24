package rpr.projekat.zejd.Controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import rpr.projekat.zejd.Models.*;
import rpr.projekat.zejd.Utility.DataType;
import rpr.projekat.zejd.Utility.ListViewCell;
import rpr.projekat.zejd.Utility.ListViewCellElement;
import rpr.projekat.zejd.Utility.OptionButtons;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static rpr.projekat.zejd.Utility.OptionButtons.*;

public class MainController {
    ResourceBundle resourceBundle = ResourceBundle.getBundle("translations");
    private DirectoryModel model;
    private MainController mc;
    @FXML
    private ScrollPane scrlPn;
    @FXML
    private VBox vbox;
    @FXML
    private ListView<ListViewCellElement> list;
    @FXML
    public void initialize(){
        mc = this;
        scrlPn.getStylesheets().add(getClass().getResource("/css/subjectbutton.css").toExternalForm());
        model = DirectoryModel.getInstance();
        updateSubjectsFromDatabase();
        list.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount()==2 && mouseEvent.getButton()== MouseButton.PRIMARY){
                    ListViewCellElement lvce = list.getSelectionModel().getSelectedItem();
                    if(lvce==null)return;
                    switch (lvce.getType()){
                        case FILE:
                            File file = model.getClickedFile(pathQueue,lvce.getName());
                            try {
                                Desktop.getDesktop().open(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case DIRECTORY:
                            if(lvce.getName().equals(". . .") && pathQueue.size()>1){
                                pathQueue.removeLast();
                            }
                            else if(!lvce.getName().equals(". . .")){
                                pathQueue.add(lvce.getName());
                            }
                            updateListView();
                            break;
                    }
                }
            }
        });
        list.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles() && pathQueue.size()>0) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });
        list.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                Dragboard db = dragEvent.getDragboard();
                File file = db.getFiles().get(0);
                if(file!=null && !file.isDirectory())
                    model.addFile(pathQueue,file.getName(),file);
                updateListView();
            }
        });
        list.setFixedCellSize(32);
        list.setCellFactory(new Callback<ListView<ListViewCellElement>, ListCell<ListViewCellElement>>() {
            @Override
            public ListCell<ListViewCellElement> call(ListView<ListViewCellElement> listViewCellElementListView) {
                ListCell<ListViewCellElement> cell = new ListViewCell(model, pathQueue, mc, resourceBundle);

                return cell;
            }
        });
    }
    int brojac = 1;
    boolean test = false;
    Button lastClicked = null;

    // THE DEQUE IS USED TO FIND DIRECTORIES IN THE DATABASE MORE EASILY AND RETURN TO PARENT DIRECTORIES
    private Deque<String> pathQueue = new LinkedList<>();

    public void updateListView(){
        ObservableList<ListViewCellElement> observableList = FXCollections.observableArrayList();
        //ADD A DIRECTORY FROM WHICH YOU CAN ACCESS THE PARENT DIRECTORY
        observableList.add(0,new ListViewCellElement(". . .", DataType.DIRECTORY));
        for(Directory d: model.getDirectoriesInCurrentFolder(pathQueue)){
            observableList.add(new ListViewCellElement(d.getName(),DataType.DIRECTORY));
        }
        for(Data d: model.getFilesInCurrentFolder(pathQueue)){
            observableList.add(new ListViewCellElement(d.getName(),DataType.FILE));
        }
        list.setItems(observableList);
    }

    private void refreshSubjects(){
        int size = vbox.getChildren().size();
        for(int i = 1; i < size; i++){
            vbox.getChildren().remove(1);
        }
        brojac = 1;
        test = false;
        lastClicked=null;
        updateSubjectsFromDatabase();
    }

    private void updateSubjectsFromDatabase(){
        ArrayList<Subject> list = model.getAllSubjects();
        for(Subject s: list){
            Button b = createSubjectButton(s.getName());
            vbox.getChildren().add(b);
        }
    }

    private Button createSubjectButton(String text){
        Button b = new Button(text);
        b.setId(text.toLowerCase());
        b.getStyleClass().add("subjectbutton");
        b.setOnAction((eh)->{
            // IF NO SUBJECT IS CURENTLY SELECTED
            if(!test){
                pathQueue.add(b.getText());
                lastClicked = (Button) eh.getSource();
                int number = vbox.getChildren().indexOf(lastClicked);
                vbox.getChildren().add(number+1, createOptionButton("Dodaj fajl", ADDFILE));
                vbox.getChildren().add(number+2, createOptionButton("Dodaj direktorij", ADDDIRECTORY));
                vbox.getChildren().add(number+3, createOptionButton("Dodaj internet fajl", ADDINTERNETFILE));
                vbox.getChildren().add(number+4, createOptionButton("Obriši predmet", DELETESUBJECT));
                updateListView();
                test = true;
            }
            // IF THE SUBJECT THAT IS SELECTED GETS CLICKED
            else if (lastClicked.equals(eh.getSource())){
                pathQueue.clear();
                int number = vbox.getChildren().indexOf(lastClicked);
                lastClicked = null;
                vbox.getChildren().remove(number+1);
                vbox.getChildren().remove(number+1);
                vbox.getChildren().remove(number+1);
                vbox.getChildren().remove(number+1);
                list.getItems().clear();
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
                pathQueue.clear();
                pathQueue.add(b.getText());
                updateListView();
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
                button.setText(resourceBundle.getString("addfilebtn"));
                button.setId("addfilebtn");
                button.setOnAction((handler)->{
                    FileChooser fileChooser = new FileChooser();
                    File file = fileChooser.showOpenDialog(new Stage());
                    if(file!=null)
                        model.addFile(pathQueue,file.getName(),file);
                    updateListView();
                });
                break;
            case ADDDIRECTORY:
                button.setText(resourceBundle.getString("adddirectorybtn"));
                button.setId("adddirectorybtn");
                button.setOnAction((handler)->{
                    AddDirectoryController ad = new AddDirectoryController(resourceBundle.getString("adddirectorytext"));
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/naming_dialog.fxml"),resourceBundle);
                    loader.setController(ad);
                    Parent root = null;
                    try {
                        root = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Stage stage = new Stage();
                    stage.setTitle(resourceBundle.getString("namingtitle"));
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
                                    updateListView();
                                }
                            });
                        }
                    });
                });
                break;
            case DELETESUBJECT:
                button.setText(resourceBundle.getString("deletesubjectbtn"));
                button.setId("deletesubjectbtn");
                button.setOnAction((handler)->{
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle(resourceBundle.getString("confirmdeletion"));
                    alert.setHeaderText(resourceBundle.getString("deletewarningtext"));
                    Optional<ButtonType> option = alert.showAndWait();
                    if(option.get()==null){
                    }
                    else if(option.get()==ButtonType.OK){
                        model.deleteSubject(pathQueue);
                        refreshSubjects();
                        list.getItems().clear();
                        pathQueue.clear();
                    }
                    else if(option.get()==ButtonType.CANCEL){
                    }
                });
                break;
            case ADDINTERNETFILE:
                button.setText(resourceBundle.getString("addinternetfilebtn"));
                button.setId("addinternetfilebtn");
                button.setOnAction((handler)->{
                    EnterURLController eu = new EnterURLController(resourceBundle.getString("addinternetfiletext"));
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/naming_dialog.fxml"),resourceBundle);
                    loader.setController(eu);
                    Parent root = null;
                    try {
                        root = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Stage stage = new Stage();
                    stage.setTitle(resourceBundle.getString("namingtitle"));
                    stage.setScene(new Scene(root,600,300));
                    stage.setResizable(false);
                    stage.show();
                    stage.setOnHiding(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent windowEvent) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    String link = eu.getName();
                                    if(link==null)return;
                                    new Thread(()->{
                                       try{
                                           URL url = new URL(link);
                                           String name = Paths.get(url.getFile()).getFileName().toString();
                                           InputStream is = url.openStream();
                                           File temp = File.createTempFile(name,"."+(name.split("\\."))[1]);
                                           temp.deleteOnExit();
                                           temp.setWritable(true);
                                           Files.copy(is,Paths.get(temp.toURI()), StandardCopyOption.REPLACE_EXISTING);
                                           model.addFile(pathQueue,name,temp);
                                           Platform.runLater(new Runnable() {
                                               @Override
                                               public void run() {
                                                   updateListView();
                                               }
                                           });
                                       } catch (MalformedURLException e) {
                                           e.printStackTrace();
                                       } catch (IOException e) {
                                           e.printStackTrace();
                                       }
                                    }).start();
                                }
                            });
                        }
                    });
                });
                break;
        }
        return button;
    }

    public void addSubject(ActionEvent actionEvent){
        AddSubjectController as = new AddSubjectController(resourceBundle.getString("addsubjecttext"));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/naming_dialog.fxml"),resourceBundle);
        loader.setController(as);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle(resourceBundle.getString("namingtitle"));
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
                        pathQueue.clear();
                        list.getItems().clear();
                    }
                });
            }
        });
    }
    public void toBosnian(ActionEvent actionEvent){
        setLanguage(new Locale("bs","BA"));
    }
    public void toEnglish(ActionEvent actionEvent){
        setLanguage(new Locale("en","EN"));
    }
    public void toGerman(ActionEvent actionEvent){
        setLanguage(new Locale("de","DE"));
    }
    private void setLanguage(Locale locale){
        Locale l = Locale.getDefault();
        if(l.equals(locale))return;
        pathQueue.clear();
        brojac = 1;
        test = false;
        lastClicked=null;
        Stage currentStage = (Stage) list.getScene().getWindow();
        Locale.setDefault(locale);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("translations");
        this.resourceBundle = resourceBundle;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_window.fxml"), resourceBundle);
        loader.setController(this);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentStage.setScene(new Scene(root, 750, 500));
        currentStage.setTitle("Subject Management System");
        currentStage.show();
    }
}
