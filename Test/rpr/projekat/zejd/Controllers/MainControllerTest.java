package rpr.projekat.zejd.Controllers;

import static javafx.beans.binding.Bindings.when;
import static org.junit.jupiter.api.Assertions.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import rpr.projekat.zejd.Models.Data;
import rpr.projekat.zejd.Models.DirectoryModel;
import rpr.projekat.zejd.Utility.DataType;
import rpr.projekat.zejd.Utility.ListViewCellElement;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

@ExtendWith(ApplicationExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MainControllerTest {
    FXMLLoader loader;
    @Start
    public void start(Stage stage){
        File f = new File("files.db");
        f.delete();

        ResourceBundle resourceBundle = ResourceBundle.getBundle("translations");
        this.loader = new FXMLLoader(getClass().getResource("/fxml/main_window.fxml"),resourceBundle);
        loader.setController(new MainController());
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Korisnici");
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.show();
        stage.toFront();
    }
    @Test @Order(1)
    void addSubject(FxRobot robot){
        robot.clickOn("#addBtn");
        robot.clickOn("#subject").write("RPR");
        robot.clickOn("#okBtn");
        VBox lista = robot.lookup("#vbox").queryAs(VBox.class);
        Button subject = (Button) lista.getChildren().get(1);
        assertEquals(subject.getText(),"RPR");
    }
    @Test @Order(2)
    void addDirectory(FxRobot robot){
        robot.clickOn("RPR");
        robot.clickOn("#adddirectorybtn");
        robot.clickOn("#subject").write("TestDir");
        robot.clickOn("#okBtn");
        ListView list = robot.lookup("#list").queryListView();
        ListViewCellElement lvce = (ListViewCellElement) list.getItems().get(1);
        assertEquals("TestDir",lvce.getName());
        assertEquals(DataType.DIRECTORY, lvce.getType());
    }
    @Test @Order(3)
    void addDirectoryInDirectory(FxRobot robot){
        robot.clickOn("RPR");
        robot.doubleClickOn("TestDir");
        robot.clickOn("#adddirectorybtn");
        robot.clickOn("#subject").write("TestDir2");
        robot.clickOn("#okBtn");
        ListView list = robot.lookup("#list").queryListView();
        ListViewCellElement lvce = (ListViewCellElement) list.getItems().get(1);
        assertEquals("TestDir2",lvce.getName());
        assertEquals(DataType.DIRECTORY, lvce.getType());
    }
    @Test @Order(4)
    void renameDirectory(FxRobot robot){
        robot.clickOn("RPR");
        robot.rightClickOn("TestDir");
        robot.clickOn(ResourceBundle.getBundle("translations").getString("rename"));
        robot.doubleClickOn("#subject").write("Renamed");
        robot.clickOn("#okBtn");
        ListView list = robot.lookup("#list").queryListView();
        ListViewCellElement lvce = (ListViewCellElement) list.getItems().get(1);
        assertEquals("Renamed",lvce.getName());
        assertEquals(DataType.DIRECTORY, lvce.getType());
    }
}