package rpr.projekat.zejd.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DirectoryModel {
    private Connection con = null;

    private PreparedStatement dodajPredmet, dajSvePredmete;

    public DirectoryModel(){
        try {
            con = DriverManager.getConnection("jdbc:sqlite:files.db");
            con.prepareStatement("SELECT * FROM subject");
        } catch (SQLException e) {
            try {
                Statement pom = con.createStatement();
                pom.execute("CREATE TABLE subject(id integer primary key autoincrement, name varchar(50) unique)");
                //pom.execute("CREATE TABLE root(id integer primary key, subjectid integer references subject(id))");
                pom.execute("CREATE TABLE directory(id integer primary key, parentid integer nullable references directory(id), subjectid integer nullable references subject(id))");
                pom.execute("CREATE TABLE file(id integer primary key, parentid integer references directory(id), content blob)");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            dodajPredmet = con.prepareStatement("INSERT INTO subject(name) VALUES(?)");
            dajSvePredmete = con.prepareStatement("SELECT * FROM subject");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addSubject(String name){
        try {
            dodajPredmet.setString(1,name);
            dodajPredmet.execute();
            dodajPredmet.clearParameters();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ObservableList<Subject> getAllSubjects(){
        ObservableList<Subject> returnVal = FXCollections.observableArrayList();
        try {
            ResultSet resultSet = dajSvePredmete.executeQuery();
            while (resultSet.next()){
                returnVal.add(new Subject(resultSet.getInt(1),resultSet.getString(2)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return returnVal;
    }
}
