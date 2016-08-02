package com.github.randoapp.task;

import com.facebook.Session;
import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.api.API;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.google.android.gms.auth.GoogleAuthUtil;

public class LogoutTask extends BaseTask {

    @Override
    public Integer run() {
        try {
            logoutGoogle();
            logoutFacebook();
            API.logout();
            Preferences.removeAuthToken();
            RandoDAO.clearRandos();
            RandoDAO.clearRandoToUpload();

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
