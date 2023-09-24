package com.example.greeningapp;

public class MyPoint {
    private String userName;
    private String pointName;
    private int point;
    private String pointDate;
    private String type;

    public MyPoint() {

    }

    public MyPoint(String userName, String pointName, int point, String pointDate, String type) {
        this.userName = userName;
        this.pointName = pointName;
        this.point = point;
        this.pointDate = pointDate;
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getPointDate() {
        return pointDate;
    }

    public void setPointDate(String pointDate) {
        this.pointDate = pointDate;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }
}
