package com.github.randoapp.task;

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
            API.logout();
            Preferences.removeAuthToken();
            Preferences.removeAccount();
            Preferences.removeLocation();
            RandoDAO.clearRandos();
            RandoDAO.clearRandoToUpload();

            return OK;
        } catch (Exception exc) {
            data.put(Constants.ERROR, exc.getMessage());
            return ERROR;
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
