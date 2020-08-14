package rpr.projekat.zejd.Models;

import rpr.projekat.zejd.Utility.ListViewCellElement;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import static java.sql.Types.INTEGER;

public class DirectoryModel {
    private Connection con = null;

    private PreparedStatement dodajPredmet, dajSvePredmete, dodajDirektorij, dajPredmetPoImenu, dodajFajl, dajIDDirektorijaPoImenu;
    private PreparedStatement dajKorijenskiDirektorijPredmeta, dajDirektorij,dajDirektorijeUDirektoriju, dajFajloveUDirektoriju, dajFajlUDirektoriju;

    private PreparedStatement obrisiPredmet, obrisiFajlovePredmeta, obrisiDatotekePredmeta;

    public DirectoryModel(){
        try {
            con = DriverManager.getConnection("jdbc:sqlite:files.db");
            con.prepareStatement("SELECT * FROM subject");
        } catch (SQLException e) {
            try {
                Statement pom = con.createStatement();
                pom.execute("CREATE TABLE subject(id integer primary key autoincrement, name varchar(50) unique)");
                pom.execute("CREATE TABLE directory(id integer primary key autoincrement, name varchar(50), parentid integer nullable references directory(id), subjectid integer nullable references subject(id))");
                pom.execute("CREATE TABLE file(id integer primary key autoincrement, name varchar(50), parentid integer references directory(id), subjectid integer references subject(id), content blob)");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            dodajPredmet = con.prepareStatement("INSERT INTO subject(name) VALUES(?)");
            dajSvePredmete = con.prepareStatement("SELECT * FROM subject");
            dodajDirektorij = con.prepareStatement("INSERT INTO directory(name,parentid,subjectid)VALUES(?,?,?)");
            dajPredmetPoImenu = con.prepareStatement("SELECT * FROM subject WHERE name=?");
            dodajFajl = con.prepareStatement("INSERT INTO file(name,parentid,subjectid,content)VALUES(?,?,?,?)");
            dajIDDirektorijaPoImenu = con.prepareStatement("SELECT * FROM directory WHERE name=?");
            dajKorijenskiDirektorijPredmeta = con.prepareStatement("SELECT d.id, d.name, d.parentid, d.subjectid" +
                                                                       " FROM directory d, subject s WHERE d.subjectid=s.id AND s.name = ?");
            dajDirektorij = con.prepareStatement("SELECT * FROM directory WHERE parentid=? AND name=?");
            obrisiPredmet = con.prepareStatement("DELETE FROM subject WHERE id=?");
            obrisiFajlovePredmeta = con.prepareStatement("DELETE FROM file WHERE subjectid=?");
            obrisiDatotekePredmeta = con.prepareStatement("DELETE FROM directory WHERE subjectid=?");
            dajDirektorijeUDirektoriju = con.prepareStatement("SELECT * FROM directory WHERE parentid=?");
            dajFajloveUDirektoriju = con.prepareStatement("SELECT * FROM file WHERE parentid=?");
            dajFajlUDirektoriju = con.prepareStatement("SELECT * FROM file WHERE parentid=? AND name=?");
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
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return directories;
    }
    // RESULTSET -> ID, NAME, PARENT, SUBJECT, CONTENT
    private ArrayList<Data> getDataFromResultSet(ResultSet rs){
        ArrayList<Data> data = new ArrayList<>();
        try {
            while(rs.next()){
                data.add(new Data(rs.getString(2),rs.getInt(1),rs.getInt(3),rs.getInt(4),rs.getBytes(5)));
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return data;
    }
    // RESULTSET -> ID, NAME
    private ArrayList<Subject> getSubjectsFromResultSet(ResultSet rs){
        ArrayList<Subject> subjects = new ArrayList<>();
        try {
            while(rs.next()){
                subjects.add(new Subject(rs.getInt(1),rs.getString(2)));
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return subjects;
    }

    private Directory getDirectory(Integer parent, String name){
        try {
            dajDirektorij.setInt(1,parent);
            dajDirektorij.setString(2,name);
            return getDirectoriesFromResultSet(dajDirektorij.executeQuery()).get(0);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private Directory getParentDirectory(Deque<String> path){
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

    private Subject getSubject(String name){
        try {
            dajPredmetPoImenu.setString(1,name);
            ResultSet resultSet = dajPredmetPoImenu.executeQuery();
            return getSubjectsFromResultSet(resultSet).get(0);

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
    //ADDS A NEW SUBJECT AND CREATES THE ROOT DIRECTORY
    public void addSubject(String name){
        try {
            dodajPredmet.setString(1,name);
            dodajPredmet.execute();
            dodajPredmet.clearParameters();
            Subject s = getSubject(name);
            if(s==null) System.out.println("ERROR");
            addDirectory(null,s.getId(),s.getName());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<Subject> getAllSubjects(){
        try {
            ResultSet resultSet = dajSvePredmete.executeQuery();
            return getSubjectsFromResultSet(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void addDirectory(Deque<String> path, String name){
        Deque<String> deepCopy = new LinkedList<>(path);
        Directory d = getParentDirectory(deepCopy);
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

    public void addFile(Deque<String> path, String name, File f){
        Deque<String> deepCopy = new LinkedList<>(path);
        Directory directory = getParentDirectory(deepCopy);
        if(directory.getId()==-1){
            System.out.println("ERROR");
            return;
        }
        try {
            dodajFajl.setString(1,name);
            dodajFajl.setInt(2,directory.getId());
            dodajFajl.setInt(3,directory.getSubject());
            dodajFajl.setBytes(4,readFile(f));
            dodajFajl.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void addDirectory(Integer parent, Integer subject, String name) {
        try {
            dodajDirektorij.setString(1,name);
            if(parent == null)
                dodajDirektorij.setNull(2,INTEGER);
            else
                dodajDirektorij.setInt(2,parent);
            if(subject==null)
                dodajDirektorij.setNull(3,INTEGER);
            else
                dodajDirektorij.setInt(3,subject);
            dodajDirektorij.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void deleteSubject(Deque<String> path) {
        Deque<String> deepCopy = new LinkedList<>(path);
        Subject subject = getSubject(deepCopy.pop());
        if(subject==null){
            System.out.println("ERROR");
        }
        try {
            obrisiPredmet.setInt(1,subject.getId());
            obrisiDatotekePredmeta.setInt(1, subject.getId());
            obrisiFajlovePredmeta.setInt(1,subject.getId());

            obrisiFajlovePredmeta.execute();
            obrisiDatotekePredmeta.execute();
            obrisiPredmet.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<Directory> getDirectoriesInCurrentFolder(Deque<String> path) {
        Deque<String> deepCopy = new LinkedList<>(path);
        ArrayList<Directory> list = new ArrayList<>();
        Directory parent = getParentDirectory(deepCopy);
        try {
            dajDirektorijeUDirektoriju.setInt(1,parent.getId());
            list = getDirectoriesFromResultSet(dajDirektorijeUDirektoriju.executeQuery());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    public ArrayList<Data> getFilesInCurrentFolder(Deque<String> path) {
        Deque<String> deepCopy = new LinkedList<>(path);
        ArrayList<Data> list = new ArrayList<>();
        Directory parent = getParentDirectory(deepCopy);
        try {
            dajFajloveUDirektoriju.setInt(1,parent.getId());
            list = getDataFromResultSet(dajFajloveUDirektoriju.executeQuery());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    public File getClickedFile(Deque<String> path, String name) {
        Deque<String> deepCopy = new LinkedList<>(path);
        Directory parent = getParentDirectory(deepCopy);
        File tempFile = null;
        try {
            dajFajlUDirektoriju.setInt(1,parent.getId());
            dajFajlUDirektoriju.setString(2,name);
            Data data = getDataFromResultSet(dajFajlUDirektoriju.executeQuery()).get(0);
            tempFile = File.createTempFile(name,"."+(name.split("\\."))[1]);
            tempFile.setWritable(true);
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(data.getContent());
            fos.close();
            tempFile.deleteOnExit();
            dajFajlUDirektoriju.clearParameters();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return tempFile;
    }
}
