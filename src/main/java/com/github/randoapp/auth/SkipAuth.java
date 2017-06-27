package com.github.randoapp.auth;

import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.github.randoapp.App;
import com.github.randoapp.api.API;
import com.github.randoapp.api.listeners.NetworkResultListener;
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
        API.anonymous(uuid, new NetworkResultListener() {
            @Override
            public void onOk(JSONObject response) {
                BaseAuth.done(authFragment.getActivity());
            }

            @Override
            public void onError(JSONObject error) {
                try {
                    Progress.hide();
                    String errorMessage = error == null ? error.getString("message") : "Error";
                    Toast.makeText(authFragment.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
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
