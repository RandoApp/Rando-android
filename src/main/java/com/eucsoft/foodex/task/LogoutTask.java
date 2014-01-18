package com.eucsoft.foodex.task;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.api.API;
import com.eucsoft.foodex.preferences.Preferences;
import com.facebook.Session;

import java.util.HashMap;

public class LogoutTask extends BaseTask {

    private HashMap<String, Object> errors = new HashMap<String, Object>();

    @Override
    public Integer run() {
        try {
            logoutFaceBook();
            API.logout();
            Preferences.removeAuthToken();
            return OK;
        } catch (Exception exc) {
            data.put(Constants.ERROR, exc.getMessage());
            return ERROR;
        }
    }

    private void logoutFaceBook() {
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
