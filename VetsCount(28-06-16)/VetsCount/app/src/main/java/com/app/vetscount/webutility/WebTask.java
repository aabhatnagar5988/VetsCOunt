package com.app.vetscount.webutility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.vetscount.application.MyApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by suarebits on 3/12/15.
 */
public class WebTask {
    String url;
    HashMap<String, String> params;
    WebCompleteTask webCompleteTask;
    int taskcode;
    Context context;
    JSONObject object;
    boolean isProgress = true;
    ProgressDialog progressDialog = null;

    public WebTask(Context context, String url, HashMap<String, String> params, WebCompleteTask webCompleteTask, int taskcode) {
        this.url = url;
        this.params = params;
        this.webCompleteTask = webCompleteTask;
        this.taskcode = taskcode;
        this.context = context;

        try {
            if (!MyApplication.getInstance().isConnectingToInternet(context)) {
                MyApplication.showMessage(context, "Please connect to working internet connection.");
                return;
            } else {
                MyApplication.getInstance().hideSoftKeyBoard((Activity) context);

                volleyStringRequest();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public WebTask(Context context, String url, HashMap<String, String> params, WebCompleteTask webCompleteTask, int taskcode, boolean isProgress) {
        this.url = url;
        this.params = params;
        this.webCompleteTask = webCompleteTask;
        this.taskcode = taskcode;
        this.context = context;
        this.isProgress = isProgress;
        if (!MyApplication.getInstance().isConnectingToInternet(context)) {
            MyApplication.showMessage(context, "Please connect to working internet connection.");
            return;
        } else {
            MyApplication.getInstance().hideSoftKeyBoard((Activity) context);

            volleyStringRequest();

        }

    }

    public WebTask(Context context, String url, JSONObject object, WebCompleteTask webCompleteTask, int taskcode) {
        this.url = url;
        this.object = object;
        this.webCompleteTask = webCompleteTask;
        this.taskcode = taskcode;
        this.context = context;

        if (!MyApplication.getInstance().isConnectingToInternet(context)) {
            MyApplication.showMessage(context, "Please connect to working internet connection.");
            return;
        } else {
            MyApplication.getInstance().hideSoftKeyBoard((Activity) context);

            volleyJsonRequest();

        }

    }


    public void volleyJsonRequest() {
        final ProgressDialog progressDialog = ProgressDialog.show(context, "", "Please wait....");
        final JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {


                            progressDialog.dismiss();
                            webCompleteTask.onComplete(response.toString(), taskcode);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                ,
                new Response.ErrorListener()

                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                    }
                }

        );
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(jsObjRequest);


    }

    public void volleyStringRequest() {
        if (isProgress) {
            progressDialog = ProgressDialog.show(context, "", "Please wait....");

            progressDialog.setCancelable(true);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                webCompleteTask.onComplete(response, taskcode);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null)

                    progressDialog.dismiss();
                webCompleteTask.onComplete(error.toString(), taskcode);


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params;

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header_param = new HashMap<>();
                header_param.put("app_token", "e10adc3949ba59abbe4543543vbv");
                return header_param;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(stringRequest);
    }
}
