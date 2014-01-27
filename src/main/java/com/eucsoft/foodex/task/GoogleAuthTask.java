package com.eucsoft.foodex.task;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.task.BaseTask;
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
            data.put("token", token);
            return OK;
        } catch (IOException ex) {
            data.put("error", "Following Error occured, please try again. " + ex.getMessage());
            return ERROR;
        }
    }

    private String fetchToken(String email) throws IOException {
      try {
        return GoogleAuthUtil.getToken(App.context, email, SCOPE);
      } catch (GooglePlayServicesAvailabilityException playEx) {
      } catch (UserRecoverableAuthException userRecoverableException) {
        // Unable to authenticate, but the user can fix this.
        // Forward the user to the appropriate activity.
      } catch (GoogleAuthException fatalException) {
//        onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
      }
      return null;
    }

}
