/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.vetscount.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.app.vetscount.R;
import com.app.vetscount.application.MyApplication;
import com.app.vetscount.datacontroller.DataManager;
import com.app.vetscount.wrapper.BusinessWrapper;
import com.app.vetscount.wrapper.OfferWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class LocationOnMapActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMapReadyCallback {


    ArrayList<BusinessWrapper> businessWrapperList;
    ArrayList<OfferWrapper> offersWrapperList;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    double latitude = 0, longitude = 0;
    private GoogleMap mMap;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_location_demo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_action_bar)));

        getSupportActionBar().setTitle("Back");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private Location getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        return location;
    }


    private int getDistance(double lat1, double long1, double lat2, double long2) {
        Location selected_location = new Location("locationA");
        selected_location.setLatitude(lat1);
        selected_location.setLongitude(long1);
        Location near_locations = new Location("locationA");
        near_locations.setLatitude(lat2);
        near_locations.setLongitude(long2);

        double distance = selected_location.distanceTo(near_locations);
        return (int) (distance / 1000);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
//        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition cameraPosition) {
//                latitude = cameraPosition.target.latitude + "";
//                longitude = cameraPosition.target.longitude + "";
//            }
//        });

        try {
            if (getIntent().getBooleanExtra("isOffer", false)) {
                offersWrapperList = DataManager.getInstance().getofferList();


                for (int i = 0; i < offersWrapperList.size(); i++) {
                    if (!(TextUtils.isEmpty(offersWrapperList.get(i).getLat()) && TextUtils.isEmpty(offersWrapperList.get(i).getLat()))) {

                        latitude = Double.parseDouble(offersWrapperList.get(i).getLat());
                        longitude = Double.parseDouble(offersWrapperList.get(i).getLongitude());

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .anchor(0.5f, 0.5f)
                                .title(offersWrapperList.get(i).getTitle())
                        );
                    }
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10.0f));

            } else {

                businessWrapperList = DataManager.getInstance().getBusinessList();


                for (int i = 0; i < businessWrapperList.size(); i++) {

                    if (!(TextUtils.isEmpty(businessWrapperList.get(i).getLat()) && TextUtils.isEmpty(businessWrapperList.get(i).getLat()))) {
                        latitude = Double.parseDouble(businessWrapperList.get(i).getLat());
                        longitude = Double.parseDouble(businessWrapperList.get(i).getLongitude());

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .anchor(0.5f, 0.5f)
                                .title(businessWrapperList.get(i).getName())
                        );
                    }

                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10.0f));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        mMap.setOnMyLocationButtonClickListener(this);
//        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Permission to access the location is missing.
//            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
//                    Manifest.permission.ACCESS_FINE_LOCATION, true);
//        } else if (mMap != null) {
        // Access to the location has been granted to the app.
        try {
            String arr[] = getIntent().getStringExtra("location").split(",");

            if (arr.length == 2) {
                latitude = Double.parseDouble(arr[0]);
                longitude = Double.parseDouble(arr[1]);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10.0f));
                Location location = getCurrentLocation();
                if (location != null) {
                    double distance = getDistance(location.getLatitude(), location.getLongitude(), Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(getIntent().getStringExtra("title")).snippet("Distance : " + distance + " kms"));

                } else {


                    MyApplication.showMessage(LocationOnMapActivity.this, "Turn on your gps to get distance!");
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(getIntent().getStringExtra("title")));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
//        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
//        if (mPermissionDenied) {
//            // Permission was not granted, display error dialog.
////            showMissingPermissionError();
////            mPermissionDenied = false;
//        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
//    private void showMissingPermissionError() {
//        PermissionUtils.PermissionDeniedDialog
//                .newInstance(true).show(getSupportFragmentManager(), "dialog");
//    }

}
