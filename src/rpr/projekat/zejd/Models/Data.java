package rpr.projekat.zejd.Models;

public class Data extends Location{
    private Integer id, parent;

    public Data(String adress, Integer id, Integer parent) {
        super(adress);
        this.id = id;
        this.parent = parent;
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
