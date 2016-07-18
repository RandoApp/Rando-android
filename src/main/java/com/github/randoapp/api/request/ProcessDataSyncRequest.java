package com.github.randoapp.api.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class ProcessDataSyncRequest extends JsonObjectRequest {

    public ProcessDataSyncRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, url, null, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        return super.parseNetworkResponse(response);
    }
}
