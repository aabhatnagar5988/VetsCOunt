package com.app.vetscount.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.vetscount.R;
import com.app.vetscount.application.MyApplication;
import com.app.vetscount.datacontroller.Constants;
import com.app.vetscount.datacontroller.DataManager;
import com.app.vetscount.datacontroller.DataParser;
import com.app.vetscount.fragments.TabsFragment;
import com.app.vetscount.webutility.RequestCode;
import com.app.vetscount.webutility.WebCompleteTask;
import com.app.vetscount.webutility.WebTask;
import com.app.vetscount.webutility.WebUrls;
import com.app.vetscount.wrapper.CategoryWrapper;
import com.app.vetscount.wrapper.CityWrapper;
import com.app.vetscount.wrapper.StateWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BusinessOffersActivity extends AppCompatActivity implements WebCompleteTask {
    @Bind(R.id.frameContainer)
    FrameLayout frameContainer;
    @Bind(R.id.viewOnMapRV)
    RippleView viewOnMapRV;
    private TabsFragment tabsFragment;
    private boolean isOfferSelected = true;
    public String categoryid = "";
    private Spinner stateSpinner, citySpinner;
    private Spinner categorySpinner;
    public boolean isOfferTabSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_offers);
        ButterKnife.bind(this);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_action_bar)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setIcon(R.drawable.logo_actionbar);

        categoryid = MyApplication.getUserData(Constants.CATEGORY_ID);
        viewOnMapRV.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                if (isOfferSelected && DataManager.getInstance().getofferList().isEmpty() || !isOfferSelected && DataManager.getInstance().getBusinessList().isEmpty()) {
                    MyApplication.showMessage(BusinessOffersActivity.this, "No Locations Found!");
                } else

                    startActivity(new Intent(BusinessOffersActivity.this, LocationOnMapActivity.class).putExtra("isOffer", isOfferTabSelected));
            }
        });
        loadTabsFragment();
    }


    public void searchDialog() {

        try {
            try {
                final Dialog dialog = new Dialog(BusinessOffersActivity.this,
                        android.R.style.Theme);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(R.layout.dialog_search);

                getWindow().setLayout(LayoutParams.FILL_PARENT,
                        LayoutParams.FILL_PARENT);
                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                final TextView nameTxtView = (TextView) dialog.findViewById(R.id.nameTxtView);
                final Spinner lookingForSpinner = (Spinner) dialog.findViewById(R.id.lookingForSpinner);
                stateSpinner = (Spinner) dialog.findViewById(R.id.stateSpinner);
                citySpinner = (Spinner) dialog.findViewById(R.id.citySpinner);
                categorySpinner = (Spinner) dialog.findViewById(R.id.categorySpinner);
                RippleView searchRV = (RippleView) dialog.findViewById(R.id.searchRV);


                searchRV.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        if (lookingForSpinner.getSelectedItemPosition() == 0) {
                            isOfferSelected = true;
                        } else {
                            isOfferSelected = false;
                        }

                        dialog.dismiss();
                        getSearchedData(nameTxtView.getText().toString());
                    }
                });


                String spinnerArray[] = new String[]{"Offer", "Business"};

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, spinnerArray); //selected item will look like a spinner set from XML
//                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lookingForSpinner.setAdapter(spinnerArrayAdapter);
                dialog.show();
                dialog.getWindow()
                        .setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                if (DataManager.getInstance().getStateList().isEmpty()) {
                    getStates();
                } else if (DataManager.getInstance().getCategoryList().isEmpty()) {
//                    stateSpinner.setAdapter(new StateWrapperAdapter(BusinessOffersActivity.this, R.layout.custom_autocomplete_text, DataManager.getInstance().getStateList()));
                    ArrayList<String> statesList = new ArrayList<>();
                    ArrayList<StateWrapper> statesWrapperList = DataManager.getInstance().getStateList();
                    for (int i = 0; i < statesWrapperList.size(); i++) {
                        statesList.add(statesWrapperList.get(i).getSname());
                    }

                    ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, statesList); //selected item will look like a spinner set from XML
//                    categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    stateSpinner.setAdapter(categoryArrayAdapter);

                    getCategories();
                } else {
//                    stateSpinner.setAdapter(new StateWrapperAdapter(BusinessOffersActivity.this, R.layout.custom_autocomplete_text, DataManager.getInstance().getStateList()));
                    ArrayList<String> statesList = new ArrayList<>();
                    ArrayList<StateWrapper> statesWrapperList = DataManager.getInstance().getStateList();
                    for (int i = 0; i < statesWrapperList.size(); i++) {
                        statesList.add(statesWrapperList.get(i).getSname());
                    }

                    ArrayAdapter<String> statesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, statesList); //selected item will look like a spinner set from XML
//                    categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    stateSpinner.setAdapter(statesAdapter);

                    ArrayList<String> categoryNamesList = new ArrayList<>();
                    ArrayList<CategoryWrapper> categoryList = DataManager.getInstance().getCategoryList();
                    for (int i = 0; i < categoryList.size(); i++) {
                        categoryNamesList.add(categoryList.get(i).getBname());
                    }

                    ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categoryNamesList); //selected item will look like a spinner set from XML
//                    categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(categoryArrayAdapter);
//                    categoryAutoTxtView.setAdapter(new CategoryAdapter(BusinessOffersActivity.this, R.layout.custom_autocomplete_text, DataManager.getInstance().getCategoryList()));

                }

                stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                cityAutoTxtView.setText("");
                        try {
                            ArrayList<String> cityList = new ArrayList<String>();
                            cityList.add("Select City");
                            ArrayAdapter<String> cityArrayAdapter = new ArrayAdapter<String>(BusinessOffersActivity.this, R.layout.spinner_item_state, cityList); //selected item will look like a spinner set from XML
//                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            citySpinner.setAdapter(cityArrayAdapter);
                            ArrayList<String> statesList = new ArrayList<>();
                            ArrayList<StateWrapper> statesWrapperList = DataManager.getInstance().getStateList();
                            for (int j = 0; j < statesWrapperList.size(); j++) {
                                statesList.add(statesWrapperList.get(j).getSname());
                            }
                            if (stateSpinner.getSelectedItemPosition() != 0 && statesList.contains(stateSpinner.getSelectedItem().toString()))
                                getCities(DataManager.getInstance().getStateList().get(statesList.indexOf(stateSpinner.getSelectedItem().toString())).getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            } catch (Exception e) {

                e.printStackTrace();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onComplete(String response, int taskcode) {

        Log.e("Response", response);
        if (taskcode == RequestCode.CODE_STATE) {
            if (DataManager.getInstance().getCategoryList().isEmpty()) {
                getCategories();
            }
            DataParser dp = new DataParser();
            if (dp.parseState(response) == Constants.SUCCESS) {
                ArrayList<String> statesList = new ArrayList<>();
                ArrayList<StateWrapper> statesWrapperList = DataManager.getInstance().getStateList();
                for (int i = 0; i < statesWrapperList.size(); i++) {
                    statesList.add(statesWrapperList.get(i).getSname());
                }

                ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, statesList); //selected item will look like a spinner set from XML
//                    categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                stateSpinner.setAdapter(categoryArrayAdapter);
            } else {
                MyApplication.showMessage(BusinessOffersActivity.this, Constants.ERROR_MESSAGE);
            }
        } else if (taskcode == RequestCode.CODE_CITY) {
            DataParser dp = new DataParser();
            if (dp.parseCity(response) == Constants.SUCCESS) {
                ArrayList<String> statesList = new ArrayList<>();
                ArrayList<CityWrapper> statesWrapperList = DataManager.getInstance().getCityList();
                for (int i = 0; i < statesWrapperList.size(); i++) {
                    statesList.add(statesWrapperList.get(i).getCname());
                }

                ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, statesList); //selected item will look like a spinner set from XML
//                    categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                citySpinner.setAdapter(categoryArrayAdapter);
            } else {
                MyApplication.showMessage(BusinessOffersActivity.this, Constants.ERROR_MESSAGE);
            }

        } else if (taskcode == RequestCode.CODE_CATEGORY) {
            DataParser dp = new DataParser();
            if (dp.parseCategory(response) == Constants.SUCCESS) {


                ArrayList<String> categoryNamesList = new ArrayList<>();
                ArrayList<CategoryWrapper> categoryList = DataManager.getInstance().getCategoryList();
                for (int i = 0; i < categoryList.size(); i++) {
                    categoryNamesList.add(categoryList.get(i).getBname());
                }

                ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categoryNamesList); //selected item will look like a spinner set from XML
//                categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(categoryArrayAdapter);
            } else {
                MyApplication.showMessage(BusinessOffersActivity.this, Constants.ERROR_MESSAGE);
            }

        } else if (taskcode == RequestCode.SEARCH) {
            DataParser dp = new DataParser();

            if (isOfferSelected) {


                if (dp.parseOffer(response, true) == Constants.SUCCESS) {


                    startActivity(new Intent(BusinessOffersActivity.this, SearchActivity.class).putExtra("title", "Searched Offers"));
                } else {
                    MyApplication.showMessage(BusinessOffersActivity.this, "No offers found!");
                }

            } else {
                if (dp.parseBusiness(response, true) == Constants.SUCCESS) {


                    startActivity(new Intent(BusinessOffersActivity.this, SearchActivity.class).putExtra("title", "Searched Business"));
                } else {
                    MyApplication.showMessage(BusinessOffersActivity.this, "No business found!");
                }

            }
        }


    }

    private void getCategories() {
        JSONObject object = new JSONObject();

        new WebTask(BusinessOffersActivity.this, WebUrls.BASE_URL + WebUrls.URL_CATEGORY, object, BusinessOffersActivity.this, RequestCode.CODE_CATEGORY);
    }


    private void getSearchedData(String name) {
        try {
            JSONObject object = new JSONObject();
            object.put("name", name);
            if (isOfferSelected)
                object.put("looking_for", "O");
            else
                object.put("looking_for", "B");


            if (citySpinner.getSelectedItemPosition() > 0) {

                ArrayList<CityWrapper> list = DataManager.getInstance().getCityList();

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getCname().equalsIgnoreCase(citySpinner.getSelectedItem().toString())) {
                        object.put("cityid", Integer.parseInt(list.get(i).getId()));
                        break;
                    }
                }


            }
            if (!TextUtils.isEmpty(categorySpinner.getSelectedItem().toString())) {

                ArrayList<CategoryWrapper> list = DataManager.getInstance().getCategoryList();

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getBname().equalsIgnoreCase(categorySpinner.getSelectedItem().toString())) {
                        object.put("categoryid", Integer.parseInt(list.get(i).getBid()));
                        break;
                    }
                }


            }
            new WebTask(BusinessOffersActivity.this, WebUrls.BASE_URL + WebUrls.URL_ADVANCED_SEARCH, object, BusinessOffersActivity.this, RequestCode.SEARCH);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_search:
                searchDialog();
                return true;
            case R.id.action_location:
                startActivity(new Intent(BusinessOffersActivity.this, CityStateActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                return true;
            case R.id.action_contact_us:
                startActivity(new Intent(BusinessOffersActivity.this, ContactUsActivity.class));
                return true;
            case R.id.action_about_us:
                startActivity(new Intent(BusinessOffersActivity.this, AboutUsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_offers_business, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void loadTabsFragment() {

        // if (tabsFragment != null)
        // hideFragment(tabsFragment.tonePacksFragment.categoryRingtonesFragment);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        isOfferTabSelected = true;
        tabsFragment = new TabsFragment();

        ft.add(frameContainer.getId(), tabsFragment);
        // ft.addToBackStack(null);
        ft.commit();


    }

    private void getStates() {
        JSONObject object = new JSONObject();


        new WebTask(BusinessOffersActivity.this, WebUrls.BASE_URL + WebUrls.URL_STATE, object, BusinessOffersActivity.this, RequestCode.CODE_STATE);
    }

    private void getCities(String stateid) {
        try {
            JSONObject object = new JSONObject();

            object.put("stateid", Integer.parseInt(stateid));
            new WebTask(BusinessOffersActivity.this, WebUrls.BASE_URL + WebUrls.URL_CITY, object, BusinessOffersActivity.this, RequestCode.CODE_CITY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class CategoryAdapter extends ArrayAdapter<CategoryWrapper> {
        private final String MY_DEBUG_TAG = "StateWrapperAdapter";
        private ArrayList<CategoryWrapper> items;
        private ArrayList<CategoryWrapper> itemsAll;
        private ArrayList<CategoryWrapper> suggestions;
        private int viewResourceId;

        public CategoryAdapter(Context context, int viewResourceId, ArrayList<CategoryWrapper> items) {
            super(context, viewResourceId, items);
            this.items = items;
            this.itemsAll = (ArrayList<CategoryWrapper>) items.clone();
            this.suggestions = new ArrayList<CategoryWrapper>();
            this.viewResourceId = viewResourceId;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(viewResourceId, null);
            }
            CategoryWrapper StateWrapper = items.get(position);
            if (StateWrapper != null) {
                TextView StateWrapperNameLabel = (TextView) v.findViewById(R.id.nameTxt);
                if (StateWrapperNameLabel != null) {
//              Log.i(MY_DEBUG_TAG, "getView StateWrapper Name:"+StateWrapper.getName());
                    StateWrapperNameLabel.setText(StateWrapper.getBname());
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
                String str = ((CategoryWrapper) (resultValue)).getBname();
                return str;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    suggestions.clear();
                    for (CategoryWrapper StateWrapper : itemsAll) {
                        if (StateWrapper.getBname().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
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
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<CategoryWrapper> filteredList = (ArrayList<CategoryWrapper>) results.values;
                if (results != null && results.count > 0) {
                    clear();
                    for (CategoryWrapper c : filteredList) {
                        add(c);
                    }
                    notifyDataSetChanged();
                }
            }
        };

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
            CityWrapper CityWrapper = items.get(position);
            if (CityWrapper != null) {
                TextView CityWrapperNameLabel = (TextView) v.findViewById(R.id.nameTxt);
                if (CityWrapperNameLabel != null) {
//              Log.i(MY_DEBUG_TAG, "getView CityWrapper Name:"+CityWrapper.getName());
//                    CityWrapperNameLabel.setText(CityWrapper.getCname());
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
                if (constraint != null) {
                    suggestions.clear();
                    for (CityWrapper CityWrapper : itemsAll) {
                        if (CityWrapper.getCname().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            suggestions.add(CityWrapper);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<CityWrapper> filteredList = (ArrayList<CityWrapper>) results.values;
                if (results != null && results.count > 0) {
                    clear();
                    for (CityWrapper c : filteredList) {
                        add(c);
                    }
                    notifyDataSetChanged();
                }
            }
        };

    }
}
