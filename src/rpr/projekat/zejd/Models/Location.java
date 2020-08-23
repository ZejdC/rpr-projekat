package rpr.projekat.zejd.Models;

public abstract class Location {
    private String name;

    public Location(String adress) {
        this.name = adress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
