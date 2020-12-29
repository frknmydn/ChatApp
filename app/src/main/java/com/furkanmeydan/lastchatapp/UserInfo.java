package com.furkanmeydan.lastchatapp;

public class UserInfo {
    String uid;
    String name;
    String photoURL;

    public UserInfo(String uid, String name, String photoURL) {
        this.uid = uid;
        this.name = name;
        this.photoURL = photoURL;
    }
    public UserInfo(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}
