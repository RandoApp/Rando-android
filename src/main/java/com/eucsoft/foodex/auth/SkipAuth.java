package com.eucsoft.foodex.auth;

import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.task.AnonymousSignupTask;
import com.eucsoft.foodex.task.callback.OnError;
import com.eucsoft.foodex.task.callback.OnOk;
import com.eucsoft.foodex.view.Progress;

import java.util.Map;

public class SkipAuth extends BaseAuth {

    public SkipAuth(AuthFragment authFragment) {
        super(authFragment);
    }

    @Override
    public void onClick(View v) {
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
