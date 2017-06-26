package com.github.randoapp.auth;

import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.randoapp.App;
import com.github.randoapp.api.API;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.util.Analytics;
import com.github.randoapp.view.Progress;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

public class SkipAuth extends BaseAuth {

    public SkipAuth(AuthFragment authFragment) {
        super(authFragment);
    }

    @Override
    public void onClick(View v) {
        Analytics.logLoginSkip(FirebaseAnalytics.getInstance(authFragment.getActivity()));
        Progress.showLoading();
        String uuid = createTemproryId();
        API.anonymous(uuid, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                BaseAuth.done(authFragment.getActivity());
            }
        },  new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Progress.hide();
                try {
                    if (error != null && error.networkResponse != null) {
                        String errorMessage = API.parseNetworkResponse(error.networkResponse).result.getString("message");
                        if (errorMessage != null) {
                            Toast.makeText(authFragment.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String createTemproryId() {
        return Settings.Secure.getString(App.context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
