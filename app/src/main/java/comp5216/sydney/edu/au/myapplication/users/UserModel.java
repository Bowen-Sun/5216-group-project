package comp5216.sydney.edu.au.myapplication.users;

public class UserModel {
    private String uid;
    private String imageurl;
    private String name;


    public UserModel(String uid, String imageurl, String name) {
        this.uid = uid;
        this.imageurl = imageurl;
        this.name = name;
    }

    public UserModel(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
