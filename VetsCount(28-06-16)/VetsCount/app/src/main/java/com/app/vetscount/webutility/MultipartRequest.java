package com.app.vetscount.webutility;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;


public class MultipartRequest extends Request<String> {

    MultipartEntity entity = new MultipartEntity();

    String FILE_PART_NAME = "";

    private final Response.Listener<String> mListener;
    private File file;
    private final HashMap<String, String> params;
    ArrayList<File> fileList;
    ArrayList<String> fileTags;

    public MultipartRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener, File file, HashMap<String, String> params, String FILE_PART_NAME) {
        super(Method.POST, url, errorListener);

        mListener = listener;
        this.file = file;
        this.params = params;
        this.FILE_PART_NAME = FILE_PART_NAME;
        buildMultipartEntity();
    }

    public MultipartRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener, ArrayList<File> fileList, ArrayList<String> fileTags, HashMap<String, String> params) {
        super(Method.POST, url, errorListener);

        mListener = listener;
        this.fileList = fileList;
        this.params = params;
        this.fileTags = fileTags;
        multipleMultipartEntity();
    }

    private void buildMultipartEntity() {
        entity.addPart(FILE_PART_NAME, new FileBody(file));
        try {
            for (String key : params.keySet()) {
                entity.addPart(key, new StringBody(params.get(key)));
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    private void multipleMultipartEntity() {

        for (int i = 0; i < fileTags.size(); i++) {
            entity.addPart(fileTags.get(i), new FileBody(fileList.get(i)));
        }


        try {
            for (String key : params.keySet()) {
                entity.addPart(key, new StringBody(params.get(key)));
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    /**
     * copied from Android StringRequest class
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}