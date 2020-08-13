package rpr.projekat.zejd.Models;

import java.sql.Blob;

public class Data extends Location{
    private Integer id, parent;
    private Blob content;

    public Data(String name, Integer id, Integer parent, Blob b) {
        super(name);
        this.id = id;
        this.parent = parent;
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
}
