package com.example.restaurantapp.model;

import com.google.gson.annotations.SerializedName;

public class User {
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("usertype")
    private String usertype;
    
    @SerializedName("firstname")
    private String firstname;
    
    @SerializedName("lastname")
    private String lastname;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("contact")
    private String contact;
    
    // Constructors
    public User() {
    }
    
    // Constructor for login check (minimal fields)
    public User(String username, String password, String usertype) {
        this.username = username;
        this.password = password;
        this.usertype = usertype;
    }
    
    // Full constructor for registration
    public User(String username, String password, String usertype, 
                String firstname, String lastname, String email, String contact) {
        this.username = username;
        this.password = password;
        this.usertype = usertype;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.contact = contact;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUsertype() {
        return usertype;
    }
    
    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }
    
    public String getFirstname() {
        return firstname;
    }
    
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    public String getLastname() {
        return lastname;
    }
    
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getContact() {
        return contact;
    }
    
    public void setContact(String contact) {
        this.contact = contact;
    }
}

