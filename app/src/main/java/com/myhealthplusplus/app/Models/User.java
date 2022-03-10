package com.myhealthplusplus.app.Models;

public class User {
    String name;
    String email;
    String uid;
    String profilePicture;

    public User(String name, String email, String uid, String profilePicture) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.profilePicture = profilePicture;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
