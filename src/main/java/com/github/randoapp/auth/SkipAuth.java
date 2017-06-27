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
            public void onOk() {
                BaseAuth.done(authFragment.getActivity());
            }

            @Override
            public void onError(Exception error) {
                Progress.hide();
                String errorMessage = error == null ? error.getMessage(): "Error";
                Toast.makeText(authFragment.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private String createTemproryId() {
        return Settings.Secure.getString(App.context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
