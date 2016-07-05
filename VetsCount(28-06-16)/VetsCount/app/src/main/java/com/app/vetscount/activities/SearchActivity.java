package com.app.vetscount.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class SearchActivity extends AppCompatActivity {

    @Bind(R.id.businessListView)
    ListView businessListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_action_bar)));

        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        if (getIntent().getStringExtra("title").contains("Offer")) {
            businessListView.setAdapter(new OffersAdapter(SearchActivity.this, DataManager.getInstance().getSearchOfferList()));

        } else {
            businessListView.setAdapter(new BusinessAdapter(SearchActivity.this, DataManager.getInstance().getSearchBusinessList()));

        }
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

    public class BusinessAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        ArrayList<BusinessWrapper> cats;
        // private Typeface tfLight;


        public BusinessAdapter(Context context, ArrayList<BusinessWrapper> cats) {
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
                    DataManager.getInstance().setBusinessWrapper(DataManager.getInstance().getSearchBusinessList().get(position));
                    startActivity(new Intent(SearchActivity.this, BusinessDetailsActivity.class));
                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView businessNameTxt, businessAddTxt;
            ImageView businessImgView;
        }

    }

    public class OffersAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        ArrayList<OfferWrapper> cats;
        // private Typeface tfLight;


        public OffersAdapter(Context context, ArrayList<OfferWrapper> cats) {
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

            holder.businessNameTxt.setText(cats.get(position).getOffersBusinessList().get(0).getName());
            holder.businessAddTxt.setText(cats.get(position).getOffersBusinessList().get(0).getAddress());
            holder.offerNameTxt.setText(cats.get(position).getTitle());

            if (cats.get(position).getType().equalsIgnoreCase("P")) {
                holder.offerTxtView.setText(cats.get(position).getRate() + "%\noff");
            } else {
                holder.offerTxtView.setText("$" + cats.get(position).getRate() + "\noff");

            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        DataManager.getInstance().setBusinessWrapper(DataManager.getInstance().getofferList().get(position).getOffersBusinessList().get(0));
                        startActivity(new Intent(SearchActivity.this, BusinessDetailsActivity.class).putExtra("isOffer",true));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
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
