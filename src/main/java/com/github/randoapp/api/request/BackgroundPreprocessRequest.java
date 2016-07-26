package com.github.randoapp.api.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.randoapp.log.Log;
import com.github.randoapp.service.SyncService;

import org.json.JSONObject;

public class BackgroundPreprocessRequest extends JsonObjectRequest {


    Response.Listener<JSONObject> mBackgroundListener;
    Response.Listener<JSONObject> mListener;

    public BackgroundPreprocessRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> backgroundListener, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.mListener = listener;
        this.mBackgroundListener = backgroundListener;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        Log.i(SyncService.class, "Current Thread VolleyRequest.parseNetworkResponse: ", Thread.currentThread().toString());
        Response<JSONObject> jsonObject = super.parseNetworkResponse(response);
        mBackgroundListener.onResponse(jsonObject.result);
        return jsonObject;
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        if(mListener != null) {
            super.deliverResponse(response);
        }
    }
}
