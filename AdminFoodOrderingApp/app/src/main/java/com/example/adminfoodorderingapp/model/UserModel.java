package com.example.adminfoodorderingapp.model;

public class UserModel {
    private String userName;
    private String email;
    private String password;
    private String namofresturant;

    public UserModel() {
    }

    public UserModel(String userName, String email, String password, String namofresturant) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.namofresturant = namofresturant;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNamofresturant() {
        return namofresturant;
    }

    public void setNamofresturant(String namofresturant) {
        this.namofresturant = namofresturant;
    }
}
