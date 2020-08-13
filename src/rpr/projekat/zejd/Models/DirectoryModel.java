package rpr.projekat.zejd.Models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Queue;

import static java.sql.Types.INTEGER;

public class DirectoryModel {
    private Connection con = null;

    private PreparedStatement dodajPredmet, dajSvePredmete, dodajDirektorij, dajPredmetPoImenu, dodajFajl, dajIDDirektorijaPoImenu;
    private PreparedStatement dajKorijenskiDirektorijPredmeta, dajDirektorij;

    public DirectoryModel(){
        try {
            con = DriverManager.getConnection("jdbc:sqlite:files.db");
            con.prepareStatement("SELECT * FROM subject");
        } catch (SQLException e) {
            try {
                Statement pom = con.createStatement();
                pom.execute("CREATE TABLE subject(id integer primary key autoincrement, name varchar(50) unique)");
                pom.execute("CREATE TABLE directory(id integer primary key autoincrement, name varchar(50), parentid integer nullable references directory(id), subjectid integer nullable references subject(id))");
                pom.execute("CREATE TABLE file(id integer primary key autoincrement, name varchar(50), parentid integer references directory(id), content blob)");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            dodajPredmet = con.prepareStatement("INSERT INTO subject(name) VALUES(?)");
            dajSvePredmete = con.prepareStatement("SELECT * FROM subject");
            dodajDirektorij = con.prepareStatement("INSERT INTO directory(name,parentid,subjectid)VALUES(?,?,?)");
            dajPredmetPoImenu = con.prepareStatement("SELECT * FROM subject WHERE name=?");
            dodajFajl = con.prepareStatement("INSERT INTO file(name,parentid,content)VALUES(?,?,?)");
            dajIDDirektorijaPoImenu = con.prepareStatement("SELECT * FROM directory WHERE name=?");
            dajKorijenskiDirektorijPredmeta = con.prepareStatement("SELECT d.id, d.name, d.parentid, d.subjectid" +
                                                                       "FROM directory d, subject s WHERE d.subjectid=s.id AND s.name = ?");
            dajDirektorij = con.prepareStatement("SELECT * FROM directory WHERE parentid=? AND name=?");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    // RESULTSET -> ID, NAME, PARENT, SUBJECT
    private ArrayList<Directory> getDirectoriesFromResultSet(ResultSet rs){
        ArrayList<Directory> directories = new ArrayList<>();
        try {
            while (rs.next()){
                directories.add(new Directory(rs.getInt(1),rs.getInt(3),rs.getInt(4),rs.getString(2)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return directories;
    }

    private Directory getDirectory(Integer parent, String name){
        try {
            dajDirektorij.setInt(1,parent);
            dajDirektorij.setString(2,name);
            getDirectoriesFromResultSet(dajDirektorij.executeQuery()).get(0);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private Directory getDirectory(Deque<String> path){
        try {
            String subject = path.pop();
            dajKorijenskiDirektorijPredmeta.setString(1,subject);
            ResultSet resultSet = dajKorijenskiDirektorijPredmeta.executeQuery();
            //WILL ONLY HAVE ONE FOLDER
            Directory root = getDirectoriesFromResultSet(resultSet).get(0);
            while(!path.isEmpty()){
                root = getDirectory(root.getId(),path.pop());
            }
            return root;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private byte[] readFile(File f){
        ByteArrayOutputStream bos = null;
        try {
            FileInputStream fis = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len; (len = fis.read(buffer)) != -1;) {
                bos.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e2) {
            System.err.println(e2.getMessage());
        }
        return bos != null ? bos.toByteArray() : null;
    }

    public Subject getSubjectByName(String name){
        try {
            dajPredmetPoImenu.setString(1,name);
            ResultSet rs = dajPredmetPoImenu.executeQuery();
            if(!rs.next())return null;
            Subject subject = new Subject(rs.getInt(1),rs.getString(2));
            rs.close();
            return subject;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
    //ADDS A NEW SUBJECT AND CREATES THE ROOT DIRECTORY
    public void addSubject(String name){
        try {
            dodajPredmet.setString(1,name);
            dodajPredmet.execute();
            dodajPredmet.clearParameters();
            Subject s = getSubjectByName(name);
            if(s==null) System.out.println("ERROR");
            addDirectory(s.getId(),null,s.getName());
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
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return returnVal;
    }

    public void addDirectory(Deque<String> path, String name){
        Directory d = getDirectory(path);
        try {
            dodajDirektorij.setString(1,name);
            dodajDirektorij.setInt(2,d.getId());
            dodajDirektorij.setInt(3,d.getSubject());
            dodajDirektorij.execute();
            dodajDirektorij.clearParameters();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addFile(Deque<String> parent, String name, File f){
        Integer parentid = getDirectory(parent).getId();
        if(parentid==-1){
            System.out.println("ERROR");
            return;
        }
        try {
            dodajFajl.setString(1,name);
            dodajFajl.setInt(2,parentid);
            dodajFajl.setBytes(3,readFile(f));
            dodajFajl.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void addDirectory(Integer parent, Integer subject, String name) {
        try {
            dodajFajl.setString(1,name);
            dodajFajl.setInt(2,parent);
            if(subject==null)
                dodajFajl.setNull(3,INTEGER);
            else
                dodajFajl.setInt(3,subject);
            dodajFajl.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteSubject(Deque<String> path) {

    }
}
