package rpr.projekat.zejd.Models;

public class Directory extends Location{
    private Integer id, parent, subject;

    public Directory(Integer id, Integer parent, Integer subject, String name) {
        super(name);
        this.id = id;
        this.parent = parent;
        this.subject = subject;
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
}
