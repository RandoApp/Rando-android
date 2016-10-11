package com.github.randoapp.task;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.api.API;
import com.github.randoapp.preferences.Preferences;

public class AnonymousSignupTask extends BaseTask {

    private String uuid;

    public AnonymousSignupTask(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public Integer run() {
        try {
            API.anonymous(uuid);
            Preferences.setAccount(App.context.getResources().getString(R.string.anonymous));
            return OK;
        } catch (Exception exc) {
            data.put(Constants.ERROR, exc.getMessage());
            return ERROR;
        }
    }

}
