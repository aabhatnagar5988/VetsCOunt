package com.app.vetscount.wrapper;

/**
 * Created by nitin on 11/5/16.
 */
public class CategoryWrapper {

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    private String bid = "", bname = "";
}
