package com.app.vetscount.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.vetscount.R;
import com.app.vetscount.application.MyApplication;
import com.app.vetscount.datacontroller.Constants;
import com.app.vetscount.datacontroller.DataManager;
import com.app.vetscount.datacontroller.DataParser;
import com.app.vetscount.textstyle.RegularTextView;
import com.app.vetscount.webutility.GotLocationCallback;
import com.app.vetscount.webutility.LegacyLastLocationFinder;
import com.app.vetscount.webutility.RequestCode;
import com.app.vetscount.webutility.WebCompleteTask;
import com.app.vetscount.webutility.WebTask;
import com.app.vetscount.webutility.WebUrls;
import com.app.vetscount.wrapper.CityWrapper;
import com.app.vetscount.wrapper.StateWrapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CityStateActivity extends AppCompatActivity implements WebCompleteTask,
        GotLocationCallback {


    TinyDB tinydb;
    public final static int REQUEST_ACCESS_LOCATION = 1;
    @Bind(R.id.autoDetectLocationRV)
    RelativeLayout autoDetectLocationRV;
    @Bind(R.id.orTxt)
    RegularTextView orTxt;
    //    @Bind(R.id.stateAutoTxtView)
//    AutoCompleteTextView stateAutoTxtView;
//    @Bind(R.id.cityAutoTxtView)
//    AutoCompleteTextView cityAutoTxtView;
    @Bind(R.id.nextRV)
    RippleView nextRV;
    @Bind(R.id.stateSpinner)
    Spinner stateSpinner;
    @Bind(R.id.citySpinner)
    Spinner citySpinner;
    ArrayList<String> statesList = new ArrayList<>();
    ArrayList<String> cityList = new ArrayList<>();
    private Location mLastLocation;

    private String cityNameAutoDetect = "", stateNameAutoDetect = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_state);
        ButterKnife.bind(this);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_action_bar)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        DataManager.getInstance().setStateList(new ArrayList<StateWrapper>());
        DataManager.getInstance().setCityList(new ArrayList<CityWrapper>());
        getSupportActionBar().setIcon(R.drawable.logo_actionbar);
        cityList.add("Select City");
        statesList.add("Select State");

        ArrayAdapter<String> stateArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_state, statesList); //selected item will look like a spinner set from XML
//                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateArrayAdapter);
        ArrayAdapter<String> cityArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_state, cityList); //selected item will look like a spinner set from XML
//                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityArrayAdapter);
        tinydb = new TinyDB(CityStateActivity.this);

//        if (DataManager.getInstance().getStateList().isEmpty())
        getStates();
        setData();
//        else
//            setData();
        autoDetectLocationRV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                Criteria criteria = new Criteria();
//
//
//                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && mLastLocation == null) {
                    buildAlertMessageNoGps();
                } else
                    getCurrentLocation();
            }
        });
//        autoDetectLocationRV.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
//            @Override
//            public void onComplete(RippleView rippleView) {
//
//            }
//        });
//        stateAutoTxtView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                try {
////                    tinydb.putObject(Constants.STATE_OBJECT, DataManager.getInstance().getStateList().get(i));
//                    cityAutoTxtView.setText("");
//                    getCities(DataManager.getInstance().getStateList().get(i).getId());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                cityAutoTxtView.setText("");
                try {
                    cityList = new ArrayList<String>();
                    cityList.add("Select City");
                    ArrayAdapter<String> cityArrayAdapter = new ArrayAdapter<String>(CityStateActivity.this, R.layout.spinner_item_state, cityList); //selected item will look like a spinner set from XML
//                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(cityArrayAdapter);
                    if (stateSpinner.getSelectedItemPosition() != 0 && statesList.contains(stateSpinner.getSelectedItem().toString()))
                        getCities(DataManager.getInstance().getStateList().get(statesList.indexOf(stateSpinner.getSelectedItem().toString()) - 1).getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
////                cityAutoTxtView.setText("");
//                ArrayAdapter<String> cityArrayAdapter = new ArrayAdapter<String>(CityStateActivity.this, R.layout.spinner_item_state, cityList); //selected item will look like a spinner set from XML
////                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                citySpinner.setAdapter(cityArrayAdapter);
//                getCities(DataManager.getInstance().getStateList().get(i).getId());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        cityAutoTxtView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                try {
////                    tinydb.putObject(Constants.CITY_OBJECT, DataManager.getInstance().getCityList().get(i));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//
//
//        });
        nextRV.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (stateSpinner.getSelectedItemPosition() == 0) {
                    MyApplication.showMessage(CityStateActivity.this, "Please select state");
                } else if (citySpinner.getSelectedItemPosition() == 0) {
                    MyApplication.showMessage(CityStateActivity.this, "Please select city");
                } else {


                    try {
                        boolean isStateValid = false;

                        if (!DataManager.getInstance().getStateList().isEmpty()) {
                            ArrayList<StateWrapper> stateList = DataManager.getInstance().getStateList();

                            for (int i = 0; i < stateList.size(); i++) {
                                if (stateList.get(i).getSname().equalsIgnoreCase(stateSpinner.getSelectedItem().toString())) {
                                    tinydb.putObject(Constants.STATE_OBJECT, DataManager.getInstance().getStateList().get(i));
                                    isStateValid = true;
                                    break;
                                }
                            }
                        }

                        boolean isCityValid = false;

                        if (!DataManager.getInstance().getCityList().isEmpty()) {
                            ArrayList<CityWrapper> cityList = DataManager.getInstance().getCityList();

                            for (int i = 0; i < cityList.size(); i++) {
                                if (cityList.get(i).getCname().equalsIgnoreCase(citySpinner.getSelectedItem().toString())) {

                                    tinydb.putObject(Constants.CITY_OBJECT, DataManager.getInstance().getCityList().get(i));
                                    isCityValid = true;

                                    break;
                                }
                            }
                        }
                        startActivity(new Intent(CityStateActivity.this, BusinessCategoriesActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });



    }




    @Override
    protected void onResume() {
        super.onResume();
        getCurrentLocation();

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(CityStateActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(CityStateActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                return false;
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(CityStateActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_LOCATION);
                return false;

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            return true;
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    boolean isLaunched=false;
    private void getCityState(Location location) {
        try {


            if (location != null) {
                Geocoder gcd = new Geocoder(CityStateActivity.this, Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    cityNameAutoDetect = addresses.get(0).getLocality();
                    stateNameAutoDetect = addresses.get(0).getAdminArea();

                    if (statesList.contains(stateNameAutoDetect)) {
                      /*  stateSpinner.setSelection(statesList.indexOf(stateNameAutoDetect));
                        if (cityList.contains(cityNameAutoDetect))
                            citySpinner.setSelection(cityList.indexOf(cityNameAutoDetect));*/

                       /* if (!DataManager.getInstance().getStateList().isEmpty()) {
                            ArrayList<StateWrapper> stateList = DataManager.getInstance().getStateList();

                            for (int i = 0; i < stateList.size(); i++) {
                                if (stateList.get(i).getSname().equalsIgnoreCase(stateNameAutoDetect)) {
                                    tinydb.putObject(Constants.STATE_OBJECT, DataManager.getInstance().getStateList().get(i));

                                    break;
                                }
                            }
                        }*/


                        CityWrapper wrapper=new CityWrapper();
                        wrapper.setCname(cityNameAutoDetect);
                        wrapper.setId("");

                        tinydb.putObject(Constants.CITY_OBJECT, wrapper);

                        if(!isLaunched) {
                            isLaunched=true;
                            startActivity(new Intent(CityStateActivity.this, BusinessCategoriesActivity.class));
                        }



                    } else
                        MyApplication.showMessage(CityStateActivity.this, "Location not detected!");
                }
            } else
                MyApplication.showMessage(CityStateActivity.this, "Location not detected!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   getCurrentLocation();

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void getCurrentLocation() {
        isGpson();
    }

    private void setData() {
        try {
            StateWrapper stateWrapper = (StateWrapper) tinydb.getObject(Constants.STATE_OBJECT, StateWrapper.class);
            if (stateWrapper != null) {
                ArrayList<StateWrapper> wrapperList = DataManager.getInstance().getStateList();
                statesList = new ArrayList<>();
                statesList.add("Select State");
                for (int i = 0; i < wrapperList.size(); i++) {
                    statesList.add(wrapperList.get(i).getSname());
                }
                if (stateWrapper != null) {
                    if (statesList.contains(stateWrapper.getSname()))

                        stateSpinner.setSelection(statesList.indexOf(stateWrapper.getSname()));
                }
                CityWrapper cityWrapper = (CityWrapper) tinydb.getObject(Constants.CITY_OBJECT, CityWrapper.class);
                if (cityWrapper != null) {

                    ArrayList<CityWrapper> citywrapperList = DataManager.getInstance().getCityList();
                    cityList = new ArrayList<>();
                    cityList.add("Select City");
                    for (int i = 0; i < citywrapperList.size(); i++) {
                        cityList.add(citywrapperList.get(i).getCname());
                    }
                    if (cityWrapper != null) {
                        if (cityList.contains(cityWrapper.getCname()))
                            citySpinner.setSelection(cityList.indexOf(cityWrapper.getCname()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStates() {
        JSONObject object = new JSONObject();


        new WebTask(CityStateActivity.this, WebUrls.BASE_URL + WebUrls.URL_STATE, object, CityStateActivity.this, RequestCode.CODE_STATE);
    }

    private void getCities(String stateid) {
        try {
            JSONObject object = new JSONObject();

            object.put("stateid", Integer.parseInt(stateid));
            new WebTask(CityStateActivity.this, WebUrls.BASE_URL + WebUrls.URL_CITY, object, CityStateActivity.this, RequestCode.CODE_CITY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onComplete(String response, int taskcode) {

        Log.e("Response", response);
        if (taskcode == RequestCode.CODE_STATE) {
            DataParser dp = new DataParser();
            if (dp.parseState(response) == Constants.SUCCESS) {


                ArrayList<StateWrapper> wrapperList = DataManager.getInstance().getStateList();
                statesList = new ArrayList<>();
                statesList.add("Select State");
                for (int i = 0; i < wrapperList.size(); i++) {
                    statesList.add(wrapperList.get(i).getSname());
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_state, statesList); //selected item will look like a spinner set from XML
//                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stateSpinner.setAdapter(spinnerArrayAdapter);

//                stateAutoTxtView.setAdapter(new StateWrapperAdapter(CityStateActivity.this, R.layout.custom_autocomplete_text, DataManager.getInstance().getStateList()));
            } else {
                MyApplication.showMessage(CityStateActivity.this, Constants.ERROR_MESSAGE);
            }
        } else {
            if (taskcode == RequestCode.CODE_CITY) {
                DataParser dp = new DataParser();
                if (dp.parseCity(response) == Constants.SUCCESS) {


                    ArrayList<CityWrapper> wrapperList = DataManager.getInstance().getCityList();
                    cityList = new ArrayList<>();
                    cityList.add("Select City");
                    for (int i = 0; i < wrapperList.size(); i++) {
                        cityList.add(wrapperList.get(i).getCname());
                    }
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_state, cityList); //selected item will look like a spinner set from XML
//                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(spinnerArrayAdapter);
                    if (!TextUtils.isEmpty(cityNameAutoDetect) && cityList.contains(cityNameAutoDetect))
                        citySpinner.setSelection(cityList.indexOf(cityNameAutoDetect));
//                    cityAutoTxtView.setAdapter(new CityWrapperAdapter(CityStateActivity.this, R.layout.custom_autocomplete_text, DataManager.getInstance().getCityList()));
                } else {
                    MyApplication.showMessage(CityStateActivity.this, Constants.ERROR_MESSAGE);
                }
            }
        }

    }




    @Override
    public void gotLocation(double lat, double lang) {

        Location location=new Location("");
        location.setLatitude(lat);
        location.setLongitude(lang);

        /*location.setLatitude(40.78474);
        location.setLongitude(-73.96496);*/


        getCityState(location);
    }

    public class StateWrapperAdapter extends ArrayAdapter<StateWrapper> {
        private final String MY_DEBUG_TAG = "StateWrapperAdapter";
        private ArrayList<StateWrapper> items;
        private ArrayList<StateWrapper> itemsAll;
        private ArrayList<StateWrapper> suggestions;
        private int viewResourceId;

        public StateWrapperAdapter(Context context, int viewResourceId, ArrayList<StateWrapper> items) {
            super(context, viewResourceId, items);
            this.items = items;
            this.itemsAll = (ArrayList<StateWrapper>) items.clone();
            this.suggestions = new ArrayList<StateWrapper>();
            this.viewResourceId = viewResourceId;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(viewResourceId, null);
            }
            StateWrapper StateWrapper = items.get(position);
            if (StateWrapper != null) {
                TextView StateWrapperNameLabel = (TextView) v.findViewById(R.id.nameTxt);
                if (StateWrapperNameLabel != null) {
//              Log.i(MY_DEBUG_TAG, "getView StateWrapper Name:"+StateWrapper.getName());
                    StateWrapperNameLabel.setText(StateWrapper.getSname());
                }
            }
            return v;
        }

        @Override
        public Filter getFilter() {
            return nameFilter;
        }

        Filter nameFilter = new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                String str = ((StateWrapper) (resultValue)).getSname();
                return str;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                try {
                    if (constraint != null) {
                        suggestions.clear();
                        for (StateWrapper StateWrapper : itemsAll) {
                            if (StateWrapper.getSname().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                                suggestions.add(StateWrapper);
                            }
                        }
                        FilterResults filterResults = new FilterResults();
                        filterResults.values = suggestions;
                        filterResults.count = suggestions.size();
                        return filterResults;
                    } else {
                        return new FilterResults();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<StateWrapper> filteredList = (ArrayList<StateWrapper>) results.values;
                if (results != null && results.count > 0) {
                    clear();
                    for (StateWrapper c : filteredList) {
                        add(c);
                    }
                    notifyDataSetChanged();
                }
            }
        };

    }

    public class CityWrapperAdapter extends ArrayAdapter<CityWrapper> {
        private final String MY_DEBUG_TAG = "StateWrapperAdapter";
        private ArrayList<CityWrapper> items;
        private ArrayList<CityWrapper> itemsAll;
        private ArrayList<CityWrapper> suggestions;
        private int viewResourceId;

        public CityWrapperAdapter(Context context, int viewResourceId, ArrayList<CityWrapper> items) {
            super(context, viewResourceId, items);
            this.items = items;
            this.itemsAll = (ArrayList<CityWrapper>) items.clone();
            this.suggestions = new ArrayList<CityWrapper>();
            this.viewResourceId = viewResourceId;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(viewResourceId, null);
            }
            CityWrapper StateWrapper = items.get(position);
            if (StateWrapper != null) {
                TextView StateWrapperNameLabel = (TextView) v.findViewById(R.id.nameTxt);
                if (StateWrapperNameLabel != null) {
//              Log.i(MY_DEBUG_TAG, "getView StateWrapper Name:"+StateWrapper.getName());
                    StateWrapperNameLabel.setText(StateWrapper.getCname());
                }
            }
            return v;
        }

        @Override
        public Filter getFilter() {
            return nameFilter;
        }

        Filter nameFilter = new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                String str = ((CityWrapper) (resultValue)).getCname();
                return str;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                try {
                    if (constraint != null) {
                        suggestions.clear();
                        for (CityWrapper StateWrapper : itemsAll) {
                            if (StateWrapper.getCname().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                                suggestions.add(StateWrapper);
                            }
                        }
                        FilterResults filterResults = new FilterResults();
                        filterResults.values = suggestions;
                        filterResults.count = suggestions.size();
                        return filterResults;
                    } else {
                        return new FilterResults();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                try {
                    ArrayList<CityWrapper> filteredList = (ArrayList<CityWrapper>) results.values;
                    if (results != null && results.count > 0) {
                        clear();
                        for (CityWrapper c : filteredList) {
                            add(c);
                        }
                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

    }


    GotLocationCallback callback;
    class GetLocation extends AsyncTask<Integer, Integer, Integer> {
        Location location;
        String address = "";


        @Override
        protected Integer doInBackground(Integer... params) {
            // TODO Auto-generated method stub
            LegacyLastLocationFinder lfinder = new LegacyLastLocationFinder(
                   CityStateActivity.this, callback);
            {
                location = lfinder.getLastBestLocation(100, 100);
            }


            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (location != null) {
                gotLocation(location.getLatitude(), location.getLongitude());
            }
        }

    }

    LocationManager locationManager;

    private void isGpson() {
        // TODO Auto-generated method stub
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        callback = this;
        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGPSEnabled || isNetworkEnabled) {
            if (ActivityCompat.checkSelfPermission(CityStateActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CityStateActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return ;
            }
            Location l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			/*SingleShotLocationProvider.requestSingleUpdate(this,
					   new SingleShotLocationProvider.LocationCallback() {
					     @Override public void onNewLocationAvailable(GPSCoordinates location) {
					     gotLocation(location.latitude, location.longitude);
					     }
					   });*/

            if (l == null) {
                l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (l == null)
                    new GetLocation().execute();
                else
                    gotLocation(l.getLatitude(), l.getLongitude());

            } else {
                gotLocation(l.getLatitude(), l.getLongitude());
            }
        } else {
            showSettingsAlert();
        }
    }


    public void showSettingsAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        finish();
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

}
