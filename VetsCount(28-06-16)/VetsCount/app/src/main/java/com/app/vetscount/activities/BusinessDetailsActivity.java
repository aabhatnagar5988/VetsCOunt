package com.app.vetscount.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.vetscount.R;
import com.app.vetscount.application.MyApplication;
import com.app.vetscount.datacontroller.DataManager;
import com.app.vetscount.wrapper.BusinessWrapper;
import com.app.vetscount.wrapper.OfferWrapper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BusinessDetailsActivity extends AppCompatActivity {

    @Bind(R.id.businessImgView)
    ImageView businessImgView;
    @Bind(R.id.businessNameTxt)
    TextView businessNameTxt;
    @Bind(R.id.phoneTxt)
    TextView phoneTxt;
    @Bind(R.id.emailTxt)
    TextView emailTxt;
    @Bind(R.id.websiteTxt)
    TextView websiteTxt;
    @Bind(R.id.categoryTxt)
    TextView categoryTxt;
    @Bind(R.id.addressTxt)
    TextView addressTxt;
    @Bind(R.id.navigateRV)
    RippleView navigateRV;
    @Bind(R.id.offersListView)
    ListView offersListView;


    BusinessWrapper wrapper = new BusinessWrapper();
    @butterknife.Bind(R.id.offerFromTxt)
    TextView offerFromTxt;
    public final static int REQUEST_ACCESS_LOCATION = 1;
    private boolean isOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_detail);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_action_bar)));

        getSupportActionBar().setTitle("Back");

        isOffer = getIntent().getBooleanExtra("isOffer", false);
        setData();

    }

    private void setData() {

        wrapper = DataManager.getInstance().getBusinessWrapper();

        offerFromTxt.setText("Offers from " + wrapper.getName());
        businessNameTxt.setText(wrapper.getName());
        addressTxt.setText(wrapper.getAddress());
        phoneTxt.setText(wrapper.getPhone());
        emailTxt.setText(wrapper.getEmail());
        websiteTxt.setText(wrapper.getDomain());
        categoryTxt.setText(wrapper.getCategory());

        if (isOffer) {
            if (DataManager.getInstance().getofferList().isEmpty()) {
                offerFromTxt.setVisibility(View.GONE);
            } else
                offersListView.setAdapter(new CategoryAdapter(BusinessDetailsActivity.this, DataManager.getInstance().getofferList()));
        } else {
            if (wrapper.getBusinessOffersList().isEmpty()) {
                offerFromTxt.setVisibility(View.GONE);
            } else
                offersListView.setAdapter(new CategoryAdapter(BusinessDetailsActivity.this, wrapper.getBusinessOffersList()));
        }
        MyApplication.loader.displayImage(wrapper.getLogo(), businessImgView, MyApplication.options_details);


        navigateRV.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
//                String uriString = "http://maps.google.com/maps?saddr="
//                        + Constants.CURRENT_LATITUDE + ","
//                        + Constants.CURRENT_LONGITUDE + "&daddr=" + latitude + ","
//                        + longitude;
//                Intent navigation = new Intent(Intent.ACTION_VIEW,
//                        Uri.parse(uriString));
//                startActivity(navigation);

                try {

                    if (TextUtils.isEmpty(wrapper.getLat())  ||
                            TextUtils.isEmpty(wrapper.getLongitude()) ) {
                        MyApplication.showMessage(BusinessDetailsActivity.this, "Location not available!");
                    } else {
                        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            buildAlertMessageNoGps();
                        } else {
                            navigateToMap();
                        }
                    }
//                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(BusinessDetailsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(BusinessDetailsActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                return false;
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(BusinessDetailsActivity.this,
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

    private void navigateToMap() {
        try {

            Location location = null;
            if (checkPermissions()) {
                location = getCurrentLocation();
            }
            if (location != null) {
                String uriString = "http://maps.google.com/maps?saddr="
                        + location.getLatitude() + ","
                        + location.getLongitude() + "&daddr=" + Double.parseDouble(wrapper.getLat()) + ","
                        + Double.parseDouble(wrapper.getLongitude());
                Intent navigation = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(uriString));
                startActivity(navigation);
            }
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

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    navigateToMap();
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

    private Location getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();


        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));


        Log.e("Location", location.toString());
        return location;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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

    public class CategoryAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        ArrayList<OfferWrapper> cats;
        // private Typeface tfLight;


        public CategoryAdapter(Context context, ArrayList<OfferWrapper> cats) {
            this.context = context;
            this.cats = cats;
            inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return cats.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                convertView = inflater.inflate(R.layout.custom_offer_item, null);
                holder.businessNameTxt = (TextView) convertView
                        .findViewById(R.id.businessNameTxt);
                holder.businessAddTxt = (TextView) convertView
                        .findViewById(R.id.businessAddTxt);
                holder.offerTxtView = (TextView) convertView
                        .findViewById(R.id.offerTxtView);
                holder.offerNameTxt = (TextView) convertView
                        .findViewById(R.id.offerNameTxt);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.businessNameTxt.setText(wrapper.getName());
            holder.businessAddTxt.setText(wrapper.getAddress());
            holder.offerNameTxt.setText(cats.get(position).getTitle());

            if (cats.get(position).getType().equalsIgnoreCase("P")) {
                holder.offerTxtView.setText(cats.get(position).getRate() + "%\noff");
            } else {
                holder.offerTxtView.setText("$" + cats.get(position).getRate() + "\noff");

            }

//            MyApplication.loader.displayImage(cats.get(position).getLogo(), holder.businessImgView, MyApplication.options);
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    DataManager.getInstance().setBusinessWrapper(DataManager.getInstance().getOffersList().get(position));
//                    startActivity(new Intent(getActivity(), BusinessDetailsActivity.class));
//                }
//            });
            return convertView;
        }

        private class ViewHolder {
            TextView businessNameTxt, offerNameTxt, businessAddTxt,
                    offerTxtView;
        }

    }
}
