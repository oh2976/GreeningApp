package com.example.greeningapp;

public class Review {
    private String rimage;
    private String rcontent;
    private float rscore;

    private String pname;
    private int pid;
    private  String pimg;
    private String username;

    private String productPrice;

    public Review() {

    }

    private String rdatetime;

    public String getRimage() {
        return rimage;
    }

    public void setRimage(String rimage) {
        this.rimage = rimage;
    }

    public String getRcontent() {
        return rcontent;
    }

    public void setRcontent(String rcontent) {
        this.rcontent = rcontent;
    }

    public float getRscore() {
        return rscore;
    }

    public void setRscore(float rscore) {
        this.rscore = rscore;
    }

    public String getRdatetime() {
        return rdatetime;
    }

    public void setRdatetime(String rdatetime) {
        this.rdatetime = rdatetime;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPimg() {
        return pimg;
    }

    public void setPimg(String pimg) {
        this.pimg = pimg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProductPrice() {
        return productPrice;
    }
    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}