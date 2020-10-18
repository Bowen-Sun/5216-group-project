package comp5216.sydney.edu.au.myapplication.notes;

import java.io.Serializable;

public class Reply implements Serializable {
    private String name;
    private String content;
    private long data;
    private String ownerName;
    private String ownerID;

    public Reply(String name, String content, Long data, String ownerName, String ownerID) {
        this.name = name;
        this.content = content;
        this.data = data;
        this.ownerName = ownerName;
        this.ownerID = ownerID;
    }

    public Reply() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void SetOwnerID(String ownerID) {
        ownerID = ownerID;
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
