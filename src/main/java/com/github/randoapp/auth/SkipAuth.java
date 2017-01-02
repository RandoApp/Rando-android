package com.github.randoapp.auth;

import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.task.AnonymousSignupTask;
import com.github.randoapp.task.callback.OnError;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.util.Analytics;
import com.github.randoapp.view.Progress;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Map;

public class SkipAuth extends BaseAuth {

    public SkipAuth(AuthFragment authFragment) {
        super(authFragment);
    }

    @Override
    public void onClick(View v) {
        Analytics.logLoginSkip(FirebaseAnalytics.getInstance(authFragment.getActivity()));
        Progress.showLoading();
        String uuid = createTemproryId();
        new AnonymousSignupTask(uuid)
            .onOk(new OnOk() {
                @Override
                public void onOk(Map<String, Object> data) {
                    done(authFragment.getActivity());
                }
            })
            .onError(new OnError() {
                @Override
                public void onError(Map<String, Object> data) {
                    Progress.hide();
                    if (data.get(Constants.ERROR) != null) {
                        Toast.makeText(authFragment.getActivity(), (CharSequence) data.get("error"), Toast.LENGTH_LONG).show();
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
