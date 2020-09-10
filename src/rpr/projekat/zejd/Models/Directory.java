package rpr.projekat.zejd.Models;

public class Directory extends Location{
    public Directory(Integer id, Integer parent, Integer subject, String name) {
        super(name, id, parent, subject);
    }
}
