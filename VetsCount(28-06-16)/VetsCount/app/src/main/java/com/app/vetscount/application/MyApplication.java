package com.app.vetscount.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.app.vetscount.R;
import com.crittercism.app.Crittercism;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


/**
 * Created by suarebits on 17/11/15.
 */
public class MyApplication extends MultiDexApplication {
    public static MyApplication myApplication = null;
    public static ImageLoader loader;

    public ImageLoaderConfiguration config;
    private static Context ctx;
    private String VETS_COUNT = "VETS_COUNT";
    public static SharedPreferences sp;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = new MyApplication();

        loader = ImageLoader.getInstance();
        loader.init(ImageLoaderConfiguration.createDefault(this));
        ctx = getApplicationContext();

        sp = ctx.getSharedPreferences(VETS_COUNT, 0);
        setImageLoaderConfig();
    }

    public void hideSoftKeyBoard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            // check if no view has focus:
            View v = activity.getCurrentFocus();
            if (v == null)
                return;

            inputManager.hideSoftInputFromWindow(v.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MyApplication getInstance() {
        if (myApplication == null) {
            myApplication = new MyApplication();
        }


        return myApplication;
    }

    public static String getUserData(String type) {

        String e = sp.getString(type, "");
        return e;
    }


    public static void saveUserData(String type, String value) {

        SharedPreferences.Editor editor = sp.edit();

        editor.putString(type, value);

        editor.commit();
    }

    public static boolean getUserDataBoolean(String type) {

        boolean e = sp.getBoolean(type, false);
        return e;
    }


    public static void saveUserDataBoolean(String type, boolean value) {

        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(type, value);

        editor.commit();
    }

    public void setImageLoaderConfig() {

        config = new ImageLoaderConfiguration.Builder(getApplicationContext())

                .memoryCacheSize(20 * 1024 * 1024)
                // 20 Mb
                .memoryCache(new LruMemoryCache(20 * 1024 * 1024))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public static DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.no_image_list).cacheInMemory(true)
            .showImageOnFail(R.drawable.no_image_list).cacheOnDisc(true).considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565).build();


    public static DisplayImageOptions options_details = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.no_image_profile).cacheInMemory(true)
            .showImageOnFail(R.drawable.no_image_profile).cacheOnDisc(true).considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565).build();

    public void produceAnimation(View v) {
        Animation alphaDown = new AlphaAnimation(1.0f, 0.3f);
        Animation alphaUp = new AlphaAnimation(0.3f, 1.0f);
        alphaDown.setDuration(1000);
        alphaUp.setDuration(500);
        alphaDown.setFillAfter(true);
        alphaUp.setFillAfter(true);
        v.startAnimation(alphaUp);
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;

    }

    public static void showMessage(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();

    }
}
