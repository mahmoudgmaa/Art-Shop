package com.example.handshop.models;

public class User {
    private String id;
    private String email;
    private String imgUrl;
    private String firstName;
    private String fullName;
    private String lastName;
    private String password;
    private String userName;
    private String Bio;

    public User(String id, String email, String imgUrl, String firstName, String fullName, String lastName, String password, String userName, String bio) {
        this.id = id;
        this.email = email;
        this.imgUrl = imgUrl;
        this.firstName = firstName;
        this.fullName = fullName;
        this.lastName = lastName;
        this.password = password;
        this.userName = userName;
        Bio = bio;
    }
    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        Bio = bio;
    }
}