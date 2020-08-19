package rpr.projekat.zejd.Utility;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import rpr.projekat.zejd.Controllers.MainController;
import rpr.projekat.zejd.Controllers.RenameController;
import rpr.projekat.zejd.Models.DirectoryModel;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Deque;

public class ListViewCell extends ListCell<ListViewCellElement> {
    @FXML
    private GridPane gridpane;
    @FXML
    private ImageView icon;
    @FXML
    private Label name;

    private FXMLLoader loader;

    private DirectoryModel model;

    private Deque<String> path;

    private MainController mainController;

    public ListViewCell(DirectoryModel model, Deque<String> pathQueue, MainController mc) {
        this.model = model;
        path = pathQueue;
        mainController = mc;
    }

    @Override
    protected void updateItem(ListViewCellElement listViewCellElement, boolean empty) {
        ContextMenu cm1 = new ContextMenu();
        if(listViewCellElement != null && !listViewCellElement.getName().equals(". . .")) {
            MenuItem mi = new MenuItem("Delete");
            mi.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    switch (listViewCellElement.getType()){
                        case DIRECTORY:
                            model.deleteDirectory(path, listViewCellElement.getName());
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    mainController.updateListView();
                                }
                            });
                            break;
                        case FILE:
                            model.deleteFile(path, listViewCellElement.getName());
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    mainController.updateListView();
                                }
                            });
                            break;
                    }
                }
            });
            MenuItem mi2 = new MenuItem("Rename");
            mi2.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    RenameController rc = new RenameController(listViewCellElement.getName());
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/naming_dialog.fxml"));
                    loader.setController(rc);
                    Parent root = null;
                    try {
                        root = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Rename");
                    stage.setScene(new Scene(root,600,300));
                    stage.setResizable(false);
                    stage.show();
                    stage.setOnHiding(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent windowEvent) {
                            String newName = rc.getName();
                            if(newName == null)return;
                            switch (listViewCellElement.getType()){
                                case FILE:
                                    model.renameFile(path,listViewCellElement.getName(),newName);
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            mainController.updateListView();
                                        }
                                    });
                                    break;
                                case DIRECTORY:
                                    model.renameDirectory(path,listViewCellElement.getName(),newName);
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            mainController.updateListView();
                                        }
                                    });
                                    break;
                            }
                        }
                    });

                }
            });
            cm1.getItems().addAll(mi,mi2);
        }
        setPrefHeight(32);
        super.updateItem(listViewCellElement, empty);
        if(empty || listViewCellElement==null){
            setText(null);
            setGraphic(null);
        }
        else{
            if(loader == null){
                loader = new FXMLLoader(getClass().getResource("/fxml/list_view_cell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            name.setText(listViewCellElement.getName());
            switch (listViewCellElement.getType()){
                case FILE:
                    setContextMenu(cm1);
                    File temp = null;
                    String[] array = listViewCellElement.getName().split("\\.");
                    try {
                        temp = File.createTempFile(listViewCellElement.getName(),"."+array[array.length-1]);
                        temp.deleteOnExit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(temp);
                    BufferedImage bi = (BufferedImage) icon.getImage();
                    Image fxIcon = SwingFXUtils.toFXImage(bi, null);
                    this.icon.setFitHeight(16);
                    this.icon.setFitWidth(16);
                    this.icon.setImage(fxIcon);
                    break;
                case DIRECTORY:
                    setContextMenu(cm1);
                    this.icon.setFitHeight(16);
                    this.icon.setFitWidth(16);
                    this.icon.setImage(new Image(String.valueOf(getClass().getResource("/Images/directory.png"))));
            }
            setText(null);
            setGraphic(gridpane);
        }
    }
}
