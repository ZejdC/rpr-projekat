package rpr.projekat.zejd.Models;

import java.sql.Blob;

public class Data extends Location{
    private Integer id, parent, subject;
    private Blob content;

    public Data(String name, Integer id, Integer parent, Integer subject, Blob b) {
        super(name);
        this.id = id;
        this.parent = parent;
        this.subject = subject;
        content = b;
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

    public Blob getContent() {
        return content;
    }

    public void setContent(Blob content) {
        this.content = content;
    }
}
