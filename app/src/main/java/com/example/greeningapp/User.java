package com.example.greeningapp;

public class User {
    private String idToken;
    private String emailId;
    private String password;
    private String username;
    private String phone;
    private String postcode;
    private String address;
    private int spoint;
    private int upoint;
    private String regdate;
    private String doquiz;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSpoint() {
        return spoint;
    }

    public void setSpoint(int spoint) {
        this.spoint = spoint;
    }

    public int getUpoint() {
        return upoint;
    }

    public void setUpoint(int upoint) {
        this.upoint = upoint;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDoquiz() {
        return doquiz;
    }

    public void setDoquiz(String doquiz) {
        this.doquiz = doquiz;
    }

}