package com.app.vetscount.datacontroller;

import com.app.vetscount.wrapper.BusinessWrapper;
import com.app.vetscount.wrapper.CategoryWrapper;
import com.app.vetscount.wrapper.CityWrapper;
import com.app.vetscount.wrapper.OfferWrapper;
import com.app.vetscount.wrapper.StateWrapper;


import java.util.ArrayList;

/**
 * Created by nitin on 18/3/16.
 */
public class DataManager {

    public ArrayList<CategoryWrapper> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(ArrayList<CategoryWrapper> categoryList) {
        this.categoryList = categoryList;
    }

    ArrayList<CategoryWrapper> categoryList = new ArrayList<>();

    public ArrayList<StateWrapper> getStateList() {
        return stateList;
    }

    public void setStateList(ArrayList<StateWrapper> stateList) {
        this.stateList = stateList;
    }

    ArrayList<StateWrapper> stateList = new ArrayList<>();
    ArrayList<OfferWrapper> offerByCategoryList = new ArrayList<>();
    ArrayList<BusinessWrapper> businessList = new ArrayList<>();


    public ArrayList<BusinessWrapper> getSearchBusinessList() {
        return searchBusinessList;
    }

    public void setSearchBusinessList(ArrayList<BusinessWrapper> searchBusinessList) {
        this.searchBusinessList = searchBusinessList;
    }

    public ArrayList<OfferWrapper> getSearchOfferList() {
        return searchOfferList;
    }

    public void setSearchOfferList(ArrayList<OfferWrapper> searchOfferList) {
        this.searchOfferList = searchOfferList;
    }

    ArrayList<OfferWrapper> searchOfferList = new ArrayList<>();
    ArrayList<BusinessWrapper> searchBusinessList = new ArrayList<>();

//    public ArrayList<BusinessWrapper> getOffersList() {
//        return offersList;
//    }
//
//    public void setOffersList(ArrayList<BusinessWrapper> offersList) {
//        this.offersList = offersList;
//    }

//    ArrayList<BusinessWrapper> offersList = new ArrayList<>();

    public ArrayList<OfferWrapper> getOfferByCategoryList() {
        return offerByCategoryList;
    }

    public void setOfferByCategoryList(ArrayList<OfferWrapper> offerByCategoryList) {
        this.offerByCategoryList = offerByCategoryList;
    }

    public ArrayList<BusinessWrapper> getBusinessList() {
        return businessList;
    }

    public void setBusinessList(ArrayList<BusinessWrapper> businessList) {
        this.businessList = businessList;
    }

    public ArrayList<OfferWrapper> getofferList() {
        return offerList;
    }

    public void setofferList(ArrayList<OfferWrapper> offerList) {
        this.offerList = offerList;
    }


    public BusinessWrapper getBusinessWrapper() {
        return businessWrapper;
    }

    public void setBusinessWrapper(BusinessWrapper businessWrapper) {
        this.businessWrapper = businessWrapper;
    }

    private BusinessWrapper businessWrapper=new BusinessWrapper();
    ArrayList<OfferWrapper> offerList = new ArrayList<>();

    public ArrayList<CityWrapper> getCityList() {
        return cityList;
    }

    public void setCityList(ArrayList<CityWrapper> cityList) {
        this.cityList = cityList;
    }

    ArrayList<CityWrapper> cityList = new ArrayList<>();


    public static DataManager dmanager = null;

    public static DataManager getInstance() {
        if (dmanager == null)
            dmanager = new DataManager();
        return dmanager;
    }
}
