package com.example.greeningapp;

public class Calendar {
    private int day;
    private int month;
    private int year;
    private boolean attendanceCompleted;

    public Calendar(int day, int month, int year, boolean attendanceCompleted) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.attendanceCompleted = attendanceCompleted;
    }

    public int getDay() {
        return day;
    }

    public Calendar() {}

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isAttendanceCompleted() {
        return attendanceCompleted;
    }

    public void setAttendanceCompleted(boolean attendanceCompleted) {
        this.attendanceCompleted = attendanceCompleted;
    }

    public void setTimeInMillis(long selectedDateInMillis) {
    }
}
