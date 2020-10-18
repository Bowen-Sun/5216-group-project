package comp5216.sydney.edu.au.myapplication.notes;

import java.io.Serializable;

public class Announcement implements Serializable {
    private String title;
    private String name;
    private String content;
    private long data;
    private String ownerID;

    public Announcement(String title, String name, String content, long data, String ownerID) {
        this.title = title;
        this.name = name;
        this.content = content;
        this.data = data;
        this.ownerID = ownerID;
    }

    public Announcement() {
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

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }
}
