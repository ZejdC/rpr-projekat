package rpr.projekat.zejd.Controllers;

import javafx.application.Platform;
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
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static rpr.projekat.zejd.Utility.OptionButtons.*;

public class MainController {
    ResourceBundle resourceBundle = ResourceBundle.getBundle("translations");
    private DirectoryModel model;
    private MainController mainController;
    private final int numberOfOptions = OptionButtons.values().length;
    @FXML
    private ScrollPane scrlPn;
    @FXML
    private VBox vbox;
    @FXML
    private ListView<ListViewCellElement> list;
    @FXML
    private Button bosnianBtn;
    @FXML
    private Button englishBtn;
    @FXML
    private Button germanBtn;
    @FXML
    public void initialize(){
        mainController = this;
        scrlPn.getStylesheets().add(getClass().getResource("/css/subjectbutton.css").toExternalForm());
        model = DirectoryModel.getInstance();
        bosnianBtn.setTooltip(new Tooltip("Change the language of the application to Bosnian"));
        englishBtn.setTooltip(new Tooltip("Change the language of the application to English"));
        germanBtn.setTooltip(new Tooltip("Change the language of the application to German"));
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
                            if( Desktop.isDesktopSupported() )
                            {
                                new Thread(() -> {
                                    try {
                                        Desktop.getDesktop().open(file);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }).start();
                            }
                            break;
                        case DIRECTORY:
                            if(lvce.getName().equals(". . .") && pathQueue.size()>1){
                                pathQueue.removeLast();
                            }
                            else if(!lvce.getName().equals(". . .")){
                                pathQueue.add(lvce.getName());
                            }
                            Platform.runLater(() -> updateListView());
                            break;
                    }
                }
            }
        });
        list.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles() && pathQueue.size()>0) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });
        list.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            for(File file: db.getFiles()){
                if(file!=null && !file.isDirectory())
                    model.addFile(pathQueue,file.getName(),file);
            }
            updateListView();
        });
        list.setFixedCellSize(32);
        list.setCellFactory(factory -> new ListViewCell(model, pathQueue, mainController, resourceBundle));
    }
    boolean optionButtonsVisible = false;
    Button lastButtonClicked = null;

    // THE DEQUE IS USED TO FIND DIRECTORIES IN THE DATABASE MORE EASILY AND RETURN TO PARENT DIRECTORIES
    private final Deque<String> pathQueue = new LinkedList<>();

    public void updateListView(){
        list.getItems().clear();
        //ADD A DIRECTORY FROM WHICH YOU CAN ACCESS THE PARENT DIRECTORY
        list.getItems().add(0,new ListViewCellElement(". . .", DataType.DIRECTORY));
        for(Directory d: model.getDirectoriesInCurrentFolder(pathQueue)){
            list.getItems().add(new ListViewCellElement(d.getName(),DataType.DIRECTORY));
        }
        for(Data d: model.getFilesInCurrentFolder(pathQueue)){
            list.getItems().add(new ListViewCellElement(d.getName(),DataType.FILE));
        }
    }

    private void refreshSubjects(){
        vbox.getChildren().subList(1,vbox.getChildren().size()).clear();
        optionButtonsVisible = false;
        lastButtonClicked =null;
        updateSubjectsFromDatabase();
    }

    private void updateSubjectsFromDatabase(){
        ArrayList<Subject> list = model.getAllSubjects();
        for(Subject s: list){
            Button b = createSubjectButton(s.getName());
            vbox.getChildren().add(b);
        }
    }
    private void removeOptionButtons(int indexOfFirstButton){
        for(int i = 0; i < numberOfOptions; i++){
            vbox.getChildren().remove(indexOfFirstButton+1);
        }
    }
    private void addOptionButtons(int indexOfFirstButton){
        vbox.getChildren().add(indexOfFirstButton+1, createOptionButton(ADDFILE));
        vbox.getChildren().add(indexOfFirstButton+2, createOptionButton(ADDDIRECTORY));
        vbox.getChildren().add(indexOfFirstButton+3, createOptionButton(ADDINTERNETFILE));
        vbox.getChildren().add(indexOfFirstButton+4, createOptionButton(DELETESUBJECT));
    }

    private Button createSubjectButton(String text){
        Button newButton = new Button(text);
        newButton.setId(text.toLowerCase());
        newButton.getStyleClass().add("subjectbutton");
        newButton.setOnAction((eh)->{
            if(!optionButtonsVisible){
                pathQueue.add(newButton.getText());
                lastButtonClicked = (Button) eh.getSource();
                int indexOfButton = vbox.getChildren().indexOf(lastButtonClicked);
                addOptionButtons(indexOfButton);
                updateListView();
                optionButtonsVisible = true;
            }
            else if (lastButtonClicked.equals(eh.getSource())){
                pathQueue.clear();
                int indexOfButton = vbox.getChildren().indexOf(lastButtonClicked);
                lastButtonClicked = null;
                removeOptionButtons(indexOfButton);
                list.getItems().clear();
                optionButtonsVisible = false;
            }
            else{
                int indexToDelete = vbox.getChildren().indexOf(lastButtonClicked);
                lastButtonClicked = (Button) eh.getSource();
                removeOptionButtons(indexToDelete);
                int indexToAdd = vbox.getChildren().indexOf(lastButtonClicked);
                addOptionButtons(indexToAdd);
                optionButtonsVisible = true;
                pathQueue.clear();
                pathQueue.add(newButton.getText());
                updateListView();
            }
        });
        newButton.setPrefWidth(200);
        newButton.setPrefHeight(60);
        return newButton;
    }

    private Button createOptionButton(OptionButtons op){
        Button button = new Button();
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
                    assert root != null;
                    stage.setScene(new Scene(root,600,300));
                    stage.setResizable(false);
                    stage.show();
                    stage.setOnHiding(windowEvent -> Platform.runLater(() -> {
                        String name = ad.getName();
                        if(name==null)return;
                        model.addDirectory(pathQueue,name);
                        updateListView();
                    }));
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
                    if(option.isPresent() && option.get()==ButtonType.OK){
                        model.deleteSubject(pathQueue);
                        refreshSubjects();
                        list.getItems().clear();
                        pathQueue.clear();
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
                    assert root != null;
                    stage.setScene(new Scene(root,600,300));
                    stage.setResizable(false);
                    stage.show();
                    stage.setOnHiding(windowEvent -> Platform.runLater(() -> {
                        String link = eu.getName();
                        if(link==null)return;
                        new Thread(()->{
                           try{
                               URL url = new URL(link);
                               String name = Paths.get(url.getFile().trim()).getFileName().toString();
                               InputStream is = url.openStream();
                               File temp = File.createTempFile(name,"."+(name.split("\\."))[1]);
                               temp.deleteOnExit();
                               temp.setWritable(true);
                               Files.copy(is,Paths.get(temp.toURI()), StandardCopyOption.REPLACE_EXISTING);
                               model.addFile(pathQueue,name,temp);
                               Platform.runLater(this::updateListView);
                           } catch (IOException|InvalidPathException e) {
                               Platform.runLater(()->{
                                   Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                   alert.setHeaderText("Invalid URL!");
                                   alert.show();
                               });
                               e.printStackTrace();
                           }
                        }).start();
                    }));
                });
                break;
        }
        return button;
    }

    public void addSubject(ActionEvent actionEvent){
        AddSubjectController addSubjectController = new AddSubjectController(resourceBundle.getString("addsubjecttext"));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/naming_dialog.fxml"),resourceBundle);
        loader.setController(addSubjectController);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle(resourceBundle.getString("namingtitle"));
        assert root != null;
        stage.setScene(new Scene(root,600,300));
        stage.setResizable(false);
        stage.show();
        stage.setOnHiding(windowEvent -> Platform.runLater(() -> {
            String name = addSubjectController.getName();
            if(name==null)return;
            model.addSubject(name);
            refreshSubjects();
            pathQueue.clear();
            list.getItems().clear();
        }));
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
        optionButtonsVisible = false;
        lastButtonClicked =null;
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
        assert root != null;
        currentStage.setScene(new Scene(root, 750, 500));
        currentStage.setTitle("Subject Management System");
        currentStage.show();
    }
}
