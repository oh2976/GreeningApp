package com.example.greeningapp;

import java.io.Serializable;

public class Product implements Serializable {
    private String pimg;
    private String pname;
    private int pprice;
    private String pdetailimg;
    private int pid;
    private int stock;
    private int category;

    public Product(){
    }

    public String getPimg() {
        return pimg;
    }

    public void setPimg(String pimg) {
        this.pimg = pimg;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public int getPprice() {
        return pprice;
    }

    public void setPprice(int pprice) {
        this.pprice = pprice;
    }

    public String getPdetailimg() {
        return pdetailimg;
    }

    public void setPdetailimg(String pdetailimg) {
        this.pdetailimg = pdetailimg;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
