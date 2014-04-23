package com.github.randoapp.task;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.api.API;
import com.github.randoapp.preferences.Preferences;
import com.facebook.Session;

import java.util.HashMap;

public class LogoutTask extends BaseTask {

    private HashMap<String, Object> errors = new HashMap<String, Object>();

    @Override
    public Integer run() {
        try {
            logoutFacebook();
            API.logout();
            Preferences.removeAuthToken();
            return OK;
        } catch (Exception exc) {
            data.put(Constants.ERROR, exc.getMessage());
            return ERROR;
        }
    }

    private void logoutFacebook() {
        if (Session.getActiveSession() != null) {
            Session.getActiveSession().closeAndClearTokenInformation();
        } else {
            Session session = Session.openActiveSessionFromCache(App.context);
            if (session != null) {
                session.closeAndClearTokenInformation();
            }
        }
    }

}
