package com.app.vetscount.activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;

import com.app.vetscount.R;
import com.app.vetscount.application.MyApplication;
import com.app.vetscount.datacontroller.Constants;

public class SplahActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splah);


        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (TextUtils.isEmpty(MyApplication.getUserData(Constants.CATEGORY_ID))) {
                startActivity(new Intent(SplahActivity.this, CityStateActivity.class));
//                } else {
//                    startActivity(new Intent(SplahActivity.this, BusinessOffersActivity.class));
//                }
                finish();
            }
        }, 2500);
    }

}
