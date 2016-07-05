package com.app.vetscount.wrapper;

import java.util.ArrayList;

/**
 * Created by nitin on 11/5/16.
 */
public class BusinessWrapper {

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getBusinessid() {
        return businessid;
    }

    public void setBusinessid(String businessid) {
        this.businessid = businessid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    private String pincode = "";
    private String logo = "";
    private String businessid = "";
    private String phone = "";
    private String email = "";
    private String address = "";

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String category = "";
    private String name = "";
    private String domain = "";
    private String longitude = "";
    private String lat = "";

    public ArrayList<OfferWrapper> getBusinessOffersList() {
        return businessOffersList;
    }

    public void setBusinessOffersList(ArrayList<OfferWrapper> businessOffersList) {
        this.businessOffersList = businessOffersList;
    }

    ArrayList<OfferWrapper> businessOffersList = new ArrayList<>();
}
