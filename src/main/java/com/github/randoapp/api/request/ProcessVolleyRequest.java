package com.github.randoapp.api.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.randoapp.log.Log;
import com.github.randoapp.service.SyncService;

import org.json.JSONObject;

public class ProcessVolleyRequest extends JsonObjectRequest {


    Response.Listener<JSONObject> listener;

    public ProcessVolleyRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.GET,url, jsonRequest, listener, errorListener);
        this.listener = listener;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        Log.i(SyncService.class, "Current Thread VolleyRequest.parseNetworkResponse: ", Thread.currentThread().toString());
        listener.onResponse(super.parseNetworkResponse(response).result);
        return null;
    }
}
