package com.eucsoft.foodex.task;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.log.Log;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

public class GoogleAuthTask extends BaseTask {

    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";

    private String email;

    public GoogleAuthTask(String email) {
        this.email = email;
    }

    @Override
    public Integer run() {
        try {
            String token = fetchToken(email);
            if (token != null) {
                data.put("email", email);
                data.put("token", token);
                return OK;
            } else {
                data.put("error", "Problem with Google service. Please try again.");
                return ERROR;
            }
        } catch (GooglePlayServicesAvailabilityException playEx) {
            data.put("error", "Problem with Google service. Please try again.");
            Log.e(GoogleAuthTask.class, "Google Play service exception: ", playEx.getMessage());
        } catch (UserRecoverableAuthException userRecoverableException) {
            data.put("error", "Problem with Google service. Please, fix problem and try again.");
            Log.e(GoogleAuthTask.class, "Unable to authenticate, but the user can fix this: ", userRecoverableException.getMessage());
        } catch (GoogleAuthException fatalException) {
            data.put("error", "Problem with Google service. Please try again.");
            Log.e(GoogleAuthTask.class, "Unrecoverable error " + fatalException.getMessage());
        } catch (IOException ex) {
            data.put("error", "Problem with Google service. Please try again.");
            Log.e(GoogleAuthTask.class, "IOException when fetch google token: " + ex.getMessage());
        }

        return ERROR;
    }

    private String fetchToken(String email) throws IOException, GoogleAuthException {
        return GoogleAuthUtil.getToken(App.context, email, SCOPE);
    }

}
