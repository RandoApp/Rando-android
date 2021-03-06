package com.github.randoapp;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.randoapp.api.API;
import com.github.randoapp.api.beans.Error;
import com.github.randoapp.api.listeners.NetworkResultListener;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.db.model.Statistics;
import com.github.randoapp.log.Log;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.service.ContactUsService;
import com.github.randoapp.service.EmailAndPasswordAuthService;
import com.github.randoapp.service.GoogleAuthService;
import com.github.randoapp.service.SkipAuthService;
import com.github.randoapp.util.AccountUtil;
import com.github.randoapp.util.Analytics;
import com.github.randoapp.util.PermissionUtils;
import com.github.randoapp.view.Progress;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.fabric.sdk.android.Fabric;

import static com.github.randoapp.Constants.CONTACTS_PERMISSION_REQUEST_CODE;

public class AuthActivity extends AppCompatActivity {

    private EditText emailText;
    private boolean requestAccountsOnFirstLoad = true;
    private GoogleApiClient googleApiClient;
    private FirebaseAnalytics firebaseAnalytics;
    private Progress authProgress = new Progress(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_auth);
        emailText = (EditText) this.findViewById(R.id.emailEditText);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Fabric.with(this, new Crashlytics());
        initGoogleButton();
        if ((boolean) getIntent().getExtras().get(Constants.LOGOUT_ACTIVITY)) {
            fullLogout();
        }
    }

    private void initGoogleButton() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        if (ConnectionResult.CANCELED == connectionResult.getErrorCode()) {
                            //Just skip Update Google Play services dialog
                            return;
                        }
                        Toast.makeText(getBaseContext(), "Google Signin failed with code: " + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
                    }
                })
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if ((boolean) getIntent().getExtras().get(Constants.LOGOUT_ACTIVITY)) {
                            logoutGoogle();
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.logLoginGoogle(firebaseAnalytics);
                if (authProgress != null) {
                    authProgress.show(getString(R.string.login_progress));
                }

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, Constants.GOOGLE_SIGN_IN);
            }
        });
    }

    public void contactUsClick(View view) {
        new ContactUsService().openContactUsActivity(view.getContext());
    }

    public void signUpClick(View view) {
        new EmailAndPasswordAuthService(this, authProgress).process();
    }

    public void skipLoginClick(View view) {
        new SkipAuthService(this, authProgress).process();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestAccountsOnFirstLoad) {
            PermissionUtils.checkAndRequestMissingPermissions(this, CONTACTS_PERMISSION_REQUEST_CODE, android.Manifest.permission.GET_ACCOUNTS);
            requestAccountsOnFirstLoad = false;
        }
        Log.i(AuthActivity.class, this.toString());
        setEmailFromFirstAccount();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.LOGOUT_ACTIVITY_RESULT) {
            logoutGoogle();
        } else if (requestCode == Constants.GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            new GoogleAuthService(this, authProgress).process(result);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            this.finishAffinity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setEmailFromFirstAccount() {
        String[] accounts = AccountUtil.getAccountNames(getBaseContext());
        if (accounts.length > 0) {
            emailText.setText(accounts[0]);
        }
    }


    private void fullLogout() {
        try {
            Analytics.logLogout(firebaseAnalytics);
            if (authProgress != null) {
                authProgress.show(getString(R.string.logout_progress));
            }
            API.logout(getBaseContext(), new NetworkResultListener(getBaseContext()) {
                @Override
                public void onOk() {
                    logout();
                }

                @Override
                public void onError(Error error) {
                    this.onFail(error);
                }

                @Override
                public void onFail(Error error) {
                    logout();
                }
            });

        } catch (Exception e) {
            Log.w(AuthActivity.class, "Logout failed: ", e.getMessage());
        }
    }

    private void logout() {
        try {
            Preferences.removeAuthToken(getBaseContext());
            Preferences.removeAccount(getBaseContext());
            Preferences.removeLocation(getBaseContext());
            RandoDAO.clearRandos(getBaseContext());
            RandoDAO.clearRandoToUpload(getBaseContext());
            Preferences.setUserStatistics(getBaseContext(), Statistics.of(0,0));
        } catch (Exception e) {
            Log.w(AuthActivity.class, "Logout failed: ", e.getMessage());
        } finally {
            if (authProgress != null) {
                authProgress.hide();
            }
        }
    }

    private void logoutGoogle() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.i(BroadcastReceiver.class, "Google Signed out.");
                    }
                });
    }

}
