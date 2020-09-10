package rpr.projekat.zejd.Utility;

import java.io.File;

public class ListViewCellElement {
    private String name;
    private DataType type;

    public ListViewCellElement(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getName()+"("+getType().toString()+")";
    }
}
