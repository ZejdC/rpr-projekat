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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import rpr.projekat.zejd.Controllers.MainController;
import rpr.projekat.zejd.Controllers.RenameController;
import rpr.projekat.zejd.Models.DirectoryModel;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.ResourceBundle;

public class ListViewCell extends ListCell<ListViewCellElement> {
    @FXML
    private AnchorPane pane;
    @FXML
    private ImageView icon;
    @FXML
    private Label name;

    private FXMLLoader loader;

    private DirectoryModel model;

    private Deque<String> path;

    private MainController mainController;

    private ResourceBundle resourceBundle;

    public ListViewCell(DirectoryModel model, Deque<String> pathQueue, MainController mc, ResourceBundle resourceBundle) {
        this.model = model;
        path = pathQueue;
        mainController = mc;
        this.resourceBundle = resourceBundle;
    }

    public static BufferedImage toBufferedImage(java.awt.Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    @Override
    protected void updateItem(ListViewCellElement listViewCellElement, boolean empty) {
        ContextMenu cm1 = new ContextMenu();
        if(listViewCellElement != null && !listViewCellElement.getName().equals(". . .")) {
            MenuItem mi = new MenuItem(resourceBundle.getString("delete"));
            mi.setOnAction(actionEvent -> {
                switch (listViewCellElement.getType()){
                    case DIRECTORY:
                        model.deleteDirectory(path, listViewCellElement.getName());
                        Platform.runLater(() -> mainController.updateListView());
                        break;
                    case FILE:
                        model.deleteFile(path, listViewCellElement.getName());
                        Platform.runLater(() -> mainController.updateListView());
                        break;
                }
            });
            MenuItem mi2 = new MenuItem(resourceBundle.getString("rename"));
            mi2.setOnAction(new EventHandler<>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    RenameController rc = new RenameController(listViewCellElement.getName(), resourceBundle.getString("renameText"));
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/naming_dialog.fxml"));
                    loader.setController(rc);
                    Parent root = null;
                    try {
                        root = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Stage stage = new Stage();
                    stage.setTitle(resourceBundle.getString("rename"));
                    assert root != null;
                    stage.setScene(new Scene(root, 600, 300));
                    stage.setResizable(false);
                    stage.show();
                    stage.setOnHiding(windowEvent -> {
                        String newName = rc.getName();
                        if (newName == null) return;
                        switch (listViewCellElement.getType()) {
                            case FILE:
                                model.renameFile(path, listViewCellElement.getName(), newName);
                                Platform.runLater(() -> mainController.updateListView());
                                break;
                            case DIRECTORY:
                                model.renameDirectory(path, listViewCellElement.getName(), newName);
                                Platform.runLater(() -> mainController.updateListView());
                                break;
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
            cm1.getItems().clear();
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
                    BufferedImage bi = toBufferedImage(icon.getImage());
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
            setGraphic(pane);
        }
    }
}
