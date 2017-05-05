package com.github.randoapp.service;

import android.app.Activity;
import android.provider.Settings;
import android.widget.Toast;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.task.AnonymousSignupTask;
import com.github.randoapp.task.callback.OnError;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.util.Analytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Map;

public class SkipAuthService extends BaseAuthService {

    public SkipAuthService(Activity activity) {
        super(activity);
    }

    public void process() {
        Analytics.logLoginSkip(FirebaseAnalytics.getInstance(activity));
        showLoginProgress();
        String uuid = createTemproryId();
        new AnonymousSignupTask(uuid)
            .onOk(new OnOk() {
                @Override
                public void onOk(Map<String, Object> data) {
                    done();
                }
            })
            .onError(new OnError() {
                @Override
                public void onError(Map<String, Object> data) {
                    hideLoginProgress();
                    if (data.get(Constants.ERROR) != null) {
                        Toast.makeText(activity, (CharSequence) data.get("error"), Toast.LENGTH_LONG).show();
                    }
                }
            })
            .execute();
    }

    private String createTemproryId() {
        return Settings.Secure.getString(App.context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
