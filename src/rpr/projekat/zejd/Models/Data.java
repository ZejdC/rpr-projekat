package rpr.projekat.zejd.Models;

import java.sql.Blob;

public class Data extends Location{
    private byte[] content;

    public Data(String name, Integer id, Integer parent, Integer subject, byte[] b) {
        super(name, id, parent, subject);
        content = b;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
