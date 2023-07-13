package com.example.greeningapp;

public class MyPoint {
    private String userName;
    private String donationName;
    private int donationPoint;
    private String donationDate;

    public MyPoint() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDonationName() {
        return donationName;
    }

    public void setDonationName(String donationName) {
        this.donationName = donationName;
    }

    public int getDonationPoint() {
        return donationPoint;
    }

    public void setDonationPoint(int donationPoint) {
        this.donationPoint = donationPoint;
    }

    public String getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(String donationDate) {
        this.donationDate = donationDate;
    }
}

