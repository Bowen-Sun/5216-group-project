package comp5216.sydney.edu.au.myapplication.notes;

import java.io.Serializable;

public class Note implements Serializable {


    private String title;
    private String name;
    private String content;
    private long data;
    private String ownerID;

    public Note(String title, String content, Long data,String ownerID,String name) {
        this.title = title;
        this.content = content;
        this.data = data;
        this.ownerID = ownerID;
        this.name = name;
    }

    public Note() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }
}
