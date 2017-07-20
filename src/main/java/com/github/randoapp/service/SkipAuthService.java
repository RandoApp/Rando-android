package com.github.randoapp.service;

import android.app.Activity;
import android.provider.Settings;
import android.widget.Toast;

import com.github.randoapp.api.API;
import com.github.randoapp.api.beans.Error;
import com.github.randoapp.api.listeners.NetworkResultListener;
import com.github.randoapp.util.Analytics;
import com.google.firebase.analytics.FirebaseAnalytics;

public class SkipAuthService extends BaseAuthService {

    public SkipAuthService(Activity activity) {
        super(activity);
    }

    public void process() {
        Analytics.logLoginSkip(FirebaseAnalytics.getInstance(activity));
        showLoginProgress();
        String uuid = createTemproryId();
        API.anonymous(uuid, activity.getBaseContext(), new NetworkResultListener() {
            @Override
            public void onOk() {
                done();
            }

            @Override
            public void onError(Error error) {
                hideLoginProgress();
                String errorMessage = error != null ? error.buildMessage(activity.getBaseContext()) : "Error";
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private String createTemproryId() {
        return Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
