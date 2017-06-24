package com.campus_alert_v1;


public class Report {

    private String title;
    private String desc;
    private String image;
    private String location;
    private String username;

    public Report() {

    }

    public Report(String title, String desc, String image, String username, String location) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.location = location;
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String title) {this.location = title;}

    public String getUsername() { return username; }


    public void setUsername(String username) {this.username = username;}

}
