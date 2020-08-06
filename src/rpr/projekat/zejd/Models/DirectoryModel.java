package rpr.projekat.zejd.Models;

import java.sql.*;

public class DirectoryModel {
    private Connection con = null;

    private PreparedStatement dodajPredmet;

    public DirectoryModel(){
        try {
            con = DriverManager.getConnection("jdbc:sqlite:files.db");
            con.prepareStatement("SELECT * FROM subject");
        } catch (SQLException e) {
            try {
                Statement pom = con.createStatement();
                pom.execute("CREATE TABLE subject(id integer primary key autoincrement, name varchar(50))");
                //pom.execute("CREATE TABLE root(id integer primary key, subjectid integer references subject(id))");
                pom.execute("CREATE TABLE directory(id integer primary key, parentid integer nullable references directory(id), subjectid integer nullable references subject(id), adress varchar(50))");
                pom.execute("CREATE TABLE file(id integer primary key, parentid integer references directory(id), adress varchar(50))");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            dodajPredmet = con.prepareStatement("INSERT INTO subject(name) VALUES(?)");
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
}
