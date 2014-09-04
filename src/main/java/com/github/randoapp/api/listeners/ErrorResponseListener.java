package com.github.randoapp.api.listeners;

import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.log.Log;

public class ErrorResponseListener implements Response.ErrorListener {
    @Override
    public void onErrorResponse(VolleyError e) {
        if (e.networkResponse != null)
            Log.d(ErrorResponseListener.class, "Network Error", "" + e.networkResponse.statusCode + " " + String.valueOf(e.networkResponse.data));
        // Handle your error types accordingly.For Timeout & No connection error, you can show 'retry' button.
        // For AuthFailure, you can re login with user credentials.
        // For ClientError, 400 & 401, Errors happening on client side when sending api request.
        // In this case you can check how client is forming the api and debug accordingly.
        // For ServerError 5xx, you can do retry or handle accordingly.
        if (e instanceof AuthFailureError) {
            Intent intent = new Intent(Constants.AUTH_FAILURE_BROADCAST_EVENT);
            App.context.sendBroadcast(intent);
        } else if (e instanceof NetworkError) {
        } else if (e instanceof ServerError) {
        } else if (e instanceof ParseError) {
        } else if (e instanceof NoConnectionError) {
        } else if (e instanceof TimeoutError) {
        }
    }
}