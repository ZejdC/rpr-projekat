package rpr.projekat.zejd.Models;

public abstract class Location {
    private String name;
    private Integer id,parent,subject;

    public Location(String name, Integer id, Integer parent, Integer subject) {
        this.name = name;
        this.id = id;
        this.parent = parent;
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
