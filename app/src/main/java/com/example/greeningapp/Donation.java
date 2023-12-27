package com.example.greeningapp;

import java.io.Serializable;

public class Donation implements Serializable {
    private int donationid;
    private String donationname;
    private String donationstart;
    private String donationend;
    private String donationimg;
    private String donationdetailimg;
    private int point;

    public Donation() {

    }

    public int getDonationid() {
        return donationid;
    }

    public void setDonationid(int donationid) {
        this.donationid = donationid;
    }

    public String getDonationname() {
        return donationname;
    }

    public void setDonationname(String donationname) {
        this.donationname = donationname;
    }

    public String getDonationstart() {
        return donationstart;
    }

    public void setDonationstart(String donationstart) {
        this.donationstart = donationstart;
    }

    public String getDonationend() {
        return donationend;
    }

    public void setDonationend(String donationend) {
        this.donationend = donationend;
    }

    public String getDonationimg() {
        return donationimg;
    }

    public void setDonationimg(String donationimg) {
        this.donationimg = donationimg;
    }

    public String getDonationdetailimg() {
        return donationdetailimg;
    }

    public void setDonationdetailimg(String donationdetailimg) {
        this.donationdetailimg = donationdetailimg;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}

