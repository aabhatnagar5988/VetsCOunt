package com.app.vetscount.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.vetscount.R;
import com.app.vetscount.activities.BusinessCategoriesActivity;
import com.app.vetscount.activities.BusinessDetailsActivity;
import com.app.vetscount.activities.BusinessOffersActivity;
import com.app.vetscount.activities.TinyDB;
import com.app.vetscount.application.MyApplication;
import com.app.vetscount.datacontroller.Constants;
import com.app.vetscount.datacontroller.DataManager;
import com.app.vetscount.datacontroller.DataParser;
import com.app.vetscount.webutility.RequestCode;
import com.app.vetscount.webutility.WebCompleteTask;
import com.app.vetscount.webutility.WebTask;
import com.app.vetscount.webutility.WebUrls;
import com.app.vetscount.wrapper.BusinessWrapper;
import com.app.vetscount.wrapper.CategoryWrapper;
import com.app.vetscount.wrapper.CityWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class BusinessFragment extends Fragment implements WebCompleteTask {

    @Bind(R.id.businessListView)
    ListView businessListView;
    TinyDB tinyDB;


    public BusinessFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business, container, false);
        ButterKnife.bind(this, view);
        tinyDB = new TinyDB(getActivity());

        return view;
    }


    public void getBusiness() {
        JSONObject object = new JSONObject();
        try {
            object.put("categoryid", Integer.parseInt(((BusinessOffersActivity) getActivity()).categoryid));
            if(!TextUtils.isEmpty(((CityWrapper) tinyDB.getObject(Constants.CITY_OBJECT, CityWrapper.class)).getId()))
                object.put("cityid", ((CityWrapper) tinyDB.getObject(Constants.CITY_OBJECT, CityWrapper.class)).getId());

            else
            {
                object.put("cityname", ((CityWrapper) tinyDB.getObject(Constants.CITY_OBJECT, CityWrapper.class)).getCname());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        new WebTask(getActivity(), WebUrls.BASE_URL + WebUrls.URL_BUSINESS_BY_CATEGORY, object, BusinessFragment.this, RequestCode.CODE_CITY);
    }

    public void setData() {
        businessListView.setAdapter(new CategoryAdapter(getActivity(), DataManager.getInstance().getBusinessList()));

    }

    @Override
    public void onComplete(String response, int taskcode) {

        Log.e("Response", response);
        if (taskcode == RequestCode.CODE_CITY) {
            DataParser dp = new DataParser();
            if (dp.parseBusiness(response,false) == Constants.SUCCESS) {
                setData();
            } else {
                MyApplication.showMessage(getActivity(), "No business found!");
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public class CategoryAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        ArrayList<BusinessWrapper> cats;
        // private Typeface tfLight;


        public CategoryAdapter(Context context, ArrayList<BusinessWrapper> cats) {
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

                convertView = inflater.inflate(R.layout.custom_business_item, null);
                holder.businessNameTxt = (TextView) convertView
                        .findViewById(R.id.businessNameTxt);
                holder.businessAddTxt = (TextView) convertView
                        .findViewById(R.id.businessAddTxt);
                holder.businessImgView = (ImageView) convertView
                        .findViewById(R.id.businessImgView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.businessNameTxt.setText(cats.get(position).getName());
            holder.businessAddTxt.setText(cats.get(position).getAddress());

            MyApplication.loader.displayImage(cats.get(position).getLogo(), holder.businessImgView, MyApplication.options);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataManager.getInstance().setBusinessWrapper(DataManager.getInstance().getBusinessList().get(position));
                    startActivity(new Intent(getActivity(), BusinessDetailsActivity.class));
                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView businessNameTxt, businessAddTxt;
            ImageView businessImgView;
        }

    }

}
