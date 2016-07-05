package com.app.vetscount.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.vetscount.R;
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
import com.app.vetscount.wrapper.OfferWrapper;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BusinessCategoriesActivity extends AppCompatActivity implements WebCompleteTask {

    @Bind(R.id.categoryListView)
    ListView categoryListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_category);
        ButterKnife.bind(this);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_action_bar)));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setIcon(R.drawable.logo_actionbar);

        getCategories();

    }

    @Override
    protected void onResume() {
        super.onResume();
        DataManager.getInstance().setBusinessList(new ArrayList<BusinessWrapper>());
        DataManager.getInstance().setOfferByCategoryList(new ArrayList<OfferWrapper>());
    }

    private void getCategories() {
        JSONObject object = new JSONObject();

        new WebTask(BusinessCategoriesActivity.this, WebUrls.BASE_URL + WebUrls.URL_CATEGORY, object, BusinessCategoriesActivity.this, RequestCode.CODE_CITY);
    }

    @Override
    public void onComplete(String response, int taskcode) {

        Log.e("Response", response);
        if (taskcode == RequestCode.CODE_CITY) {
            DataParser dp = new DataParser();
            if (dp.parseCategory(response) == Constants.SUCCESS) {
                categoryListView.setAdapter(new CategoryAdapter(BusinessCategoriesActivity.this, DataManager.getInstance().getCategoryList()));
            } else {
                MyApplication.showMessage(BusinessCategoriesActivity.this, Constants.ERROR_MESSAGE);
            }
        }

    }

    public class CategoryAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        ArrayList<CategoryWrapper> cats;
        // private Typeface tfLight;


        public CategoryAdapter(Context context, ArrayList<CategoryWrapper> cats) {
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

                convertView = inflater.inflate(R.layout.custom_business_category_item, null);
                holder.categoryTxt = (TextView) convertView
                        .findViewById(R.id.categoryTxt);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.categoryTxt.setText(cats.get(position).getBname());
            if (position % 2 == 0) {
                holder.categoryTxt.setBackgroundColor(getResources().getColor(R.color.blue_light));

            } else {
                holder.categoryTxt.setBackgroundColor(getResources().getColor(R.color.orange));

            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    MyApplication.saveUserData(Constants.CATEGORY_ID, cats.get(position).getBid());
                    startActivity(new Intent(BusinessCategoriesActivity.this, BusinessOffersActivity.class));

                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView categoryTxt;

        }

    }

}
