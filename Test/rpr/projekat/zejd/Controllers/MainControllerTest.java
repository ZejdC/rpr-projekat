package rpr.projekat.zejd.Controllers;

import static javafx.beans.binding.Bindings.when;
import static org.junit.jupiter.api.Assertions.*;

import javafx.beans.value.ObservableBooleanValue;
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
import org.testfx.util.WaitForAsyncUtils;
import rpr.projekat.zejd.Models.Data;
import rpr.projekat.zejd.Models.DirectoryModel;
import rpr.projekat.zejd.Utility.DataType;
import rpr.projekat.zejd.Utility.ListViewCellElement;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

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
    @Test @Order(5)
    void deleteDirectory(FxRobot robot){
        robot.clickOn("RPR");
        robot.doubleClickOn("Renamed");
        robot.rightClickOn("TestDir2");
        robot.clickOn(ResourceBundle.getBundle("translations").getString("delete"));
        ListView list = robot.lookup("#list").queryListView();
        assertEquals(1,list.getItems().size());
    }
    //TESTS AFTER THIS MIGHT FAIL IF INTERNET IT NOT CONNECTED OR IS TOO SLOW
    @Test @Order(6)
    void addInternetFile(FxRobot robot){
        robot.clickOn("RPR");
        robot.clickOn("#addinternetfilebtn");
        String link = "https://static.toiimg.com/thumb/msid-67586673,width-800,height-600,resizemode-75,imgsize-3918697,pt-32,y_pad-40/67586673.jpg";
        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        robot.clickOn("#subject").write(link);
        robot.clickOn("#okBtn");
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ListView list = robot.lookup("#list").queryListView();
        assertEquals(3,list.getItems().size());
    }
    @Test @Order(7)
    void renameFile(FxRobot robot){
        robot.clickOn("RPR");
        String link = "https://static.toiimg.com/thumb/msid-67586673,width-800,height-600,resizemode-75,imgsize-3918697,pt-32,y_pad-40/67586673.jpg";
        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        robot.rightClickOn(Paths.get(url.getFile()).getFileName().toString());
        robot.clickOn(ResourceBundle.getBundle("translations").getString("rename"));
        robot.write("catto");
        robot.clickOn("#okBtn");
    }
    @Test @Order(8)
    void deleteSubject(FxRobot robot){
        robot.clickOn("RPR");
        robot.clickOn("#deletesubjectbtn");
        robot.clickOn("OK");
        VBox lista = robot.lookup("#vbox").queryAs(VBox.class);
        assertEquals(1,lista.getChildren().size());
    }
}