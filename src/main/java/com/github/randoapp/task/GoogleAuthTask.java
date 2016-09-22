package com.github.randoapp.task;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.api.API;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.log.Log;
import com.github.randoapp.network.VolleySingleton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.github.randoapp.Constants.GOOGLE_AUTH_SCOPE;
import static com.github.randoapp.Constants.GOOGLE_FAMILY_NAME_PARAM;
import static com.github.randoapp.Constants.GOOGLE_USER_INFO_URL;

public class GoogleAuthTask extends BaseTask {

    private String email;
    private String token;
    private String familyName;
    private AuthFragment authFragment;


    public GoogleAuthTask(String email, AuthFragment authFragment) {
        this.email = email;
        this.authFragment = authFragment;
    }

    public GoogleAuthTask(String email, String token, String familyName, AuthFragment authFragment) {
        this.email = email;
        this.authFragment = authFragment;
        this.token = token;
        this.familyName = familyName;
    }

    @Override
    public Integer run() {
        try {

            if (token != null && familyName != null) {
                API.google(email, token, familyName);
                return OK;
            }
        } catch (GooglePlayServicesAvailabilityException playEx) {
            Log.e(GoogleAuthTask.class, "Google Play service exception: ", playEx.getMessage());
            return ERROR;
        } catch (UserRecoverableAuthException userRecoverableException) {
            Log.e(GoogleAuthTask.class, "Start Google activity because we have UserRecoverableAuthException and user should fix this: ", userRecoverableException.getMessage());
            authFragment.startActivityForResult(userRecoverableException.getIntent(), Constants.GOOGLE_ACTIVITIES_AUTH_REQUEST_CODE);
            //Do not set any error to data, because we don't need change fragments before G+ activity done
            return ERROR;
        } catch (GoogleAuthException fatalException) {
            data.put("error", "Problem with Google service. Please try again.");
            Log.e(GoogleAuthTask.class, "Unrecoverable error " + fatalException.getMessage());
        } catch (IOException exc) {
            data.put("error", "Problem with Google service. Please try again.");
            Log.e(GoogleAuthTask.class, "IOException when fetch google token: " + exc.getMessage());
        } catch (Exception e) {
            data.put("error", e.getMessage());
            Log.e(GoogleAuthTask.class, "API.google exception" + e.getMessage());
        }

        return ERROR;
    }

    private String fetchToken(String email) throws IOException, GoogleAuthException {
        return GoogleAuthUtil.getToken(App.context, email, GOOGLE_AUTH_SCOPE);
    }

    private String fetchFamilyName(String token) {
        if (token == null) return null;

        try {
            String url = GOOGLE_USER_INFO_URL + token;
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            VolleySingleton.getInstance().getRequestQueue().add(new JsonObjectRequest(Request.Method.GET, url, null, future, future));
            JSONObject response = future.get();
            return response.getString(GOOGLE_FAMILY_NAME_PARAM);
        } catch (InterruptedException e) {
            Log.w(GoogleAuthTask.class, "Interrupt fetch familyName call: ", e.getMessage());
        } catch (ExecutionException e) {
            Log.w(GoogleAuthTask.class, "Execution exception when fetch familyName call: ", e.getMessage());
        } catch (JSONException e) {
            Log.w(GoogleAuthTask.class, "JSON parse problem: ", e.getMessage());
        }
        return null;
    }

}
