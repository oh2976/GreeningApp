package com.example.greeningapp;

public class Review {
    private int rid;
    private String rtitle;
    private String rcontent;
    private float rscore;
    private String rdatetime;
    private String userid;
    private int pid;
    private String pname;

    public Review() {
    }

    public Review(int rid, String rtitle, String rcontent, float rscore, String rdatetime, String userid, int pid, String pname) {
        this.rid = rid;
        this.rtitle = rtitle;
        this.rcontent = rcontent;
        this.rscore = rscore;
        this.rdatetime = rdatetime;
        this.userid = userid;
        this.pid = pid;
        this.pname = pname;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getRtitle() {
        return rtitle;
    }

    public void setRtitle(String rtitle) {
        this.rtitle = rtitle;
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }
}
