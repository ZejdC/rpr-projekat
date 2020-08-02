package rpr.projekat.zejd.Models;

public class Directory {
    private Integer id, parent, subject;
    private String adress;

    public Directory(Integer id, Integer parent, Integer subject, String adress) {
        this.id = id;
        this.parent = parent;
        this.subject = subject;
        this.adress = adress;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public Integer getSubject() {
        return subject;
    }

    public void setSubject(Integer subject) {
        this.subject = subject;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
