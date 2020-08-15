package rpr.projekat.zejd.Utility;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ListViewCell extends ListCell<ListViewCellElement> {
    @FXML
    private GridPane gridpane;
    @FXML
    private ImageView icon;
    @FXML
    private Label name;

    private FXMLLoader loader;

    @Override
    protected void updateItem(ListViewCellElement listViewCellElement, boolean empty) {
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
                    File temp = null;
                    try {
                        temp = File.createTempFile(listViewCellElement.getName(),"."+listViewCellElement.getName().split("\\.")[1]);
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
                    this.icon.setFitHeight(16);
                    this.icon.setFitWidth(16);
                    this.icon.setImage(new Image(String.valueOf(getClass().getResource("/Images/directory.png"))));
            }
            setText(null);
            setGraphic(gridpane);
        }
    }
}
