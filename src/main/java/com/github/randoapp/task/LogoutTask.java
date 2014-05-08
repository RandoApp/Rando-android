package com.github.randoapp.task;

import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.api.API;
import com.github.randoapp.auth.GoogleAuth;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.facebook.Session;
import com.google.android.gms.auth.GoogleAuthUtil;

import java.util.HashMap;

import static com.github.randoapp.Constants.GOOGLE_AUTH_SCOPE;

public class LogoutTask extends BaseTask {

    private HashMap<String, Object> errors = new HashMap<String, Object>();

    @Override
    public Integer run() {
        try {
            logoutGoogle();
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

    private void logoutGoogle() {
        try {
            GoogleAuthUtil.invalidateToken(App.context, Preferences.getAuthToken());
        } catch (Exception e) {
            Log.w(LogoutTask.class, "Logout Google. ignored exception from GoogleAuthUtil.invalidateToken: ", e.getMessage());
        }
    }

}
