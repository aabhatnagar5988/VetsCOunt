package com.app.vetscount.datacontroller;

import com.app.vetscount.wrapper.BusinessWrapper;
import com.app.vetscount.wrapper.CategoryWrapper;
import com.app.vetscount.wrapper.CityWrapper;
import com.app.vetscount.wrapper.OfferWrapper;
import com.app.vetscount.wrapper.StateWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nitin on 11/5/16.
 */
public class DataParser {


    public int parseCategory(String response) {
        try {
            JSONArray arr = new JSONArray(response);

            ArrayList<CategoryWrapper> wrapperList = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {

                CategoryWrapper wrapper = new CategoryWrapper();

                JSONObject object = arr.getJSONObject(i);
                wrapper.setBid(checkJsonValue(object, "bid"));
                wrapper.setBname(checkJsonValue(object, "bname"));
                wrapperList.add(wrapper);
            }
            DataManager.getInstance().setCategoryList(wrapperList);
            return Constants.SUCCESS;

        } catch (JSONException e) {
            e.printStackTrace();
            return Constants.FAILURE;

        }

    }

    public int parseState(String response) {
        try {
            JSONArray arr = new JSONArray(response);

            ArrayList<StateWrapper> wrapperList = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {

                StateWrapper wrapper = new StateWrapper();

                JSONObject object = arr.getJSONObject(i);
                wrapper.setId(checkJsonValue(object, "id"));
                wrapper.setSname(checkJsonValue(object, "sname"));
                wrapperList.add(wrapper);
            }
            DataManager.getInstance().setStateList(wrapperList);
            return Constants.SUCCESS;

        } catch (JSONException e) {
            e.printStackTrace();
            return Constants.FAILURE;

        }

    }

    public int parseCity(String response) {
        try {
            JSONArray arr = new JSONArray(response);

            ArrayList<CityWrapper> wrapperList = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {

                CityWrapper wrapper = new CityWrapper();

                JSONObject object = arr.getJSONObject(i);
                wrapper.setId(checkJsonValue(object, "id"));
                wrapper.setCname(checkJsonValue(object, "cname"));
                wrapperList.add(wrapper);
            }
            DataManager.getInstance().setCityList(wrapperList);
            return Constants.SUCCESS;

        } catch (JSONException e) {
            e.printStackTrace();
            return Constants.FAILURE;

        }

    }

    public int parseBusiness(String response, boolean isSearch) {
        try {
            JSONArray arr = new JSONArray(response);

            ArrayList<BusinessWrapper> wrapperList = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {

                BusinessWrapper wrapper = new BusinessWrapper();

                JSONObject object = arr.getJSONObject(i);
                wrapper.setBusinessid(checkJsonValue(object, "id"));
                wrapper.setAddress(checkJsonValue(object, "address"));
                wrapper.setDomain(checkJsonValue(object, "domain"));
                wrapper.setEmail(checkJsonValue(object, "email"));
                wrapper.setLogo(checkJsonValue(object, "logo"));
                wrapper.setName(checkJsonValue(object, "name"));
                wrapper.setPhone(checkJsonValue(object, "phone"));
                wrapper.setPincode(checkJsonValue(object, "pincode"));
                wrapper.setLat(checkJsonValue(object, "lat"));
                wrapper.setLongitude(checkJsonValue(object, "long"));
                wrapper.setCategory(checkJsonValue(object, "category"));

                if (object.has("offers")) {

                    ArrayList<OfferWrapper> offerwrapperList = new ArrayList<>();
                    JSONArray offerarr = object.getJSONArray("offers");
                    for (int j = 0; j < offerarr.length(); j++) {

                        OfferWrapper offerwrapper = new OfferWrapper();
                        JSONObject innerobject = offerarr.getJSONObject(j);
                        offerwrapper.setId(checkJsonValue(innerobject, "id"));
                        offerwrapper.setType(checkJsonValue(innerobject, "type"));
                        offerwrapper.setTitle(checkJsonValue(innerobject, "title"));
                        offerwrapper.setRate(checkJsonValue(innerobject, "rate"));
                        offerwrapperList.add(offerwrapper);
                    }
                    wrapper.setBusinessOffersList(offerwrapperList);
                }
                wrapperList.add(wrapper);
            }
            if (isSearch) {
                DataManager.getInstance().setSearchBusinessList(wrapperList);
            } else {
                DataManager.getInstance().setBusinessList(wrapperList);
            }
            return Constants.SUCCESS;

        } catch (JSONException e) {
            e.printStackTrace();
            return Constants.FAILURE;

        }

    }

    public int parseOffer(String response, boolean isSearch) {
        try {
            JSONArray arr = new JSONArray(response);

            ArrayList<OfferWrapper> wrapperList = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {


                JSONObject object = arr.getJSONObject(i);
                OfferWrapper offerwrapper = new OfferWrapper();
                offerwrapper.setId(checkJsonValue(object, "id"));
                offerwrapper.setType(checkJsonValue(object, "type"));
                offerwrapper.setTitle(checkJsonValue(object, "title"));
                offerwrapper.setRate(checkJsonValue(object, "rate"));
                //   offerwrapper.setLat(checkJsonValue(object, "lat"));
             //   offerwrapper.setLongitude(checkJsonValue(object, "long"));

                if (object.has("business")) {

                    ArrayList<BusinessWrapper> businesswrapperList = new ArrayList<>();
                    JSONArray offerarr = object.getJSONArray("business");
                    for (int j = 0; j < offerarr.length(); j++) {

                        BusinessWrapper businesswrapper = new BusinessWrapper();
                        JSONObject innerobject = offerarr.getJSONObject(j);
                        businesswrapper.setBusinessid(checkJsonValue(innerobject, "businessid"));
                        businesswrapper.setAddress(checkJsonValue(innerobject, "address"));
                        businesswrapper.setDomain(checkJsonValue(innerobject, "domain"));
                        businesswrapper.setEmail(checkJsonValue(innerobject, "email"));
                        businesswrapper.setCategory(checkJsonValue(innerobject, "category"));
                        businesswrapper.setLogo(checkJsonValue(innerobject, "logo"));
                        businesswrapper.setLat(checkJsonValue(innerobject, "lat"));
                        businesswrapper.setLongitude(checkJsonValue(innerobject, "long"));

                           offerwrapper.setLat(checkJsonValue(innerobject, "lat"));
                           offerwrapper.setLongitude(checkJsonValue(innerobject, "long"));

                        businesswrapper.setName(checkJsonValue(innerobject, "name"));
                        businesswrapper.setPhone(checkJsonValue(innerobject, "phone"));
                        businesswrapper.setPincode(checkJsonValue(innerobject, "pincode"));
                        businesswrapperList.add(businesswrapper);

                    }
                    offerwrapper.setOffersBusinessList(businesswrapperList);
                }
                wrapperList.add(offerwrapper);
            }
            if (isSearch) {
                DataManager.getInstance().setSearchOfferList(wrapperList);
            } else {
                DataManager.getInstance().setofferList(wrapperList);
            }
            return Constants.SUCCESS;

        } catch (JSONException e) {
            e.printStackTrace();
            return Constants.FAILURE;

        }

    }


    private String checkJsonValue(JSONObject object, String value) {
        try {
            if (object.has(value))
                return object.getString(value);
            else
                return "";
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

    }

}
