package com.eucsoft.foodex.auth;

import android.provider.Settings;
import android.view.View;

import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.task.AnonymousSignupTask;
import com.eucsoft.foodex.view.Progress;

public class SkipAuth extends BaseAuth  {

    public SkipAuth (AuthFragment authFragment) {
        super(authFragment);
    }

    @Override
    public void onClick(View v) {
        Progress.showLoading();
        String uuid = createTemproryId();
        new AnonymousSignupTask(this).execute(uuid);
    }

    private String createTemproryId() {
        return Settings.Secure.getString(MainActivity.context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
