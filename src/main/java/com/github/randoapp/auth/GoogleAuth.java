package com.github.randoapp.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.github.randoapp.App;
import com.github.randoapp.Constants;
import com.github.randoapp.MainActivity;
import com.github.randoapp.R;
import com.github.randoapp.api.API;
import com.github.randoapp.api.listeners.NetworkResultListener;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.log.Log;
import com.github.randoapp.network.VolleySingleton;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.util.AccountUtil;
import com.github.randoapp.util.Analytics;
import com.github.randoapp.util.PermissionUtils;
import com.github.randoapp.view.Progress;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.github.randoapp.Constants.GOOGLE_AUTH_SCOPE;
import static com.github.randoapp.Constants.GOOGLE_FAMILY_NAME_PARAM;
import static com.github.randoapp.Constants.GOOGLE_USER_INFO_URL;
import static com.github.randoapp.Constants.UPDATE_PLAY_SERVICES_REQUEST_CODE;

public class GoogleAuth extends BaseAuth implements View.OnTouchListener {

    private Button googleButton;
    private AuthFragment authFragment;
    private GoogleApiClient googleApiClient;

    public GoogleAuth(@NonNull AuthFragment authFragment, Button googleButton) {
        super(authFragment);
        this.authFragment = authFragment;
        this.googleButton = googleButton;
    }

    private void setButtonFocused() {
        googleButton.setBackgroundDrawable(authFragment.getResources().getDrawable(com.google.android.gms.R.drawable.common_google_signin_btn_text_dark_focused));
    }

    private void setButtonPressed() {
        googleButton.setBackgroundDrawable(authFragment.getResources().getDrawable(com.google.android.gms.R.drawable.common_google_signin_btn_text_dark_pressed));
    }

    private void setButtonNormal() {
        googleButton.setBackgroundDrawable(authFragment.getResources().getDrawable(com.google.android.gms.R.drawable.common_google_signin_btn_text_dark_normal));
    }

    @Override
    public void onClick(View v) {
        Analytics.logLoginGoogle(FirebaseAnalytics.getInstance(authFragment.getActivity()));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        googleApiClient = new GoogleApiClient.Builder(authFragment.getActivity())
                .enableAutoManage(authFragment.getActivity() /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(authFragment.getActivity(), "Problem with Google service. Please try again.", Toast.LENGTH_LONG).show();
                        Log.e(GoogleAuth.class, "API.google exception");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        SignInButton signInButton = (SignInButton) authFragment.getActivity().findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    // ...
                }
            }
        });

/*
        if (!PermissionUtils.checkAndRequestMissingPermissions(authFragment.getActivity(), Constants.CONTACTS_PERMISSION_REQUEST_CODE, android.Manifest.permission.GET_ACCOUNTS)) {
            String[] names = AccountUtil.getAccountNames();
            if (names.length == 1) {
                fetchUserToken(names[0]);
            } else if (names.length > 1) {
                selectAccount(names);
            } else if (names.length == 0) {
                Toast.makeText(authFragment.getActivity(), authFragment.getResources().getString(R.string.no_google_account), Toast.LENGTH_LONG).show();
                setButtonNormal();
            }
        } else {
            authFragment.isGoogleLoginPressed = true;
        }*/
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        authFragment.startActivityForResult(signInIntent, Constants.GOOGLE_SIGN_IN);
    }

    private void fetchUserToken(final String email) {
        Progress.showLoading();
        String token = "";
        String familyName = "";

        try {
            token = fetchToken(email);
            familyName = fetchFamilyName(token);
        } catch (final GooglePlayServicesAvailabilityException playEx) {
            authFragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
                    if (googleApiAvailability.isUserResolvableError(playEx.getConnectionStatusCode())) {
                        googleApiAvailability.getErrorDialog(authFragment.getActivity(), playEx.getConnectionStatusCode(), UPDATE_PLAY_SERVICES_REQUEST_CODE).show();
                    }
                }
            });
            Log.e(GoogleAuth.class, "Google Play service exception: ", playEx.getMessage());
        } catch (UserRecoverableAuthException userRecoverableException) {
            Log.e(GoogleAuth.class, "Start Google activity because we have UserRecoverableAuthException and user should fix this: ", userRecoverableException.getMessage());
            authFragment.startActivityForResult(userRecoverableException.getIntent(), Constants.GOOGLE_ACTIVITIES_AUTH_REQUEST_CODE);
            //Do not set any error to data, because we don't need change fragments before G+ activity done
        } catch (GoogleAuthException fatalException) {
            Toast.makeText(authFragment.getActivity(), "Problem with Google service. Please try again.", Toast.LENGTH_LONG).show();
            Log.e(GoogleAuth.class, "Unrecoverable error " + fatalException.getMessage());
            Progress.hide();
            return;
        } catch (IOException exc) {
            Toast.makeText(authFragment.getActivity(), "Problem with Google service. Please try again.", Toast.LENGTH_LONG).show();
            Log.e(GoogleAuth.class, "IOException when fetch google token: " + exc.getMessage());
            Progress.hide();
            return;
        } catch (Exception e) {
            Toast.makeText(authFragment.getActivity(), "Problem with Google service. Please try again.", Toast.LENGTH_LONG).show();
            Log.e(GoogleAuth.class, "API.google exception" + e.getMessage());
            Progress.hide();
            return;
        }

        API.google(email, token, familyName, new NetworkResultListener() {
            @Override
            public void onOk() {
                BaseAuth.done(authFragment.getActivity());
                Preferences.setAccount(email);
            }

            @Override
            public void onError(Exception error) {
                Progress.hide();
                String errorMessage = error == null ? error.getMessage(): "Error";
                Toast.makeText(authFragment.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void selectAccount(final CharSequence[] names) {
        AlertDialog.Builder selectAccountDialog = new AlertDialog.Builder(authFragment.getActivity());
        selectAccountDialog.setTitle(authFragment.getString(R.string.select_account));
        selectAccountDialog.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fetchUserToken((String) names[which]);
            }
        });
        selectAccountDialog.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            setButtonPressed();
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            setButtonNormal();
        } else {
            setButtonFocused();
        }
        return false;
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
            Log.w(GoogleAuth.class, "Interrupt fetch familyName call: ", e.getMessage());
        } catch (ExecutionException e) {
            Log.w(GoogleAuth.class, "Execution exception when fetch familyName call: ", e.getMessage());
        } catch (JSONException e) {
            Log.w(GoogleAuth.class, "JSON parse problem: ", e.getMessage());
        }
        return null;
    }
}
