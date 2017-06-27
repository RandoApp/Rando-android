package com.github.randoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.randoapp.log.Log;
import com.github.randoapp.service.EmailAndPasswordAuthService;
import com.github.randoapp.service.GoogleAuthService;
import com.github.randoapp.service.SkipAuthService;
import com.github.randoapp.util.AccountUtil;
import com.github.randoapp.util.PermissionUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import static com.github.randoapp.Constants.CONTACTS_PERMISSION_REQUEST_CODE;

public class AuthActivity extends AppCompatActivity {

    private EditText emailText;
    private Button googleButton;
    public boolean isGoogleLoginPressed = false;
    private boolean requestAccountsOnFirstLoad = true;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        emailText = (EditText) this.findViewById(R.id.emailEditText);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getBaseContext(), "Google Signin failed", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void signUpClick(View view) {
        new EmailAndPasswordAuthService(this).process();
    }

    public void googleLoginClick(View view) {
        switch (view.getId()) {
            case R.id.google_sign_in_button:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, Constants.GOOGLE_SIGN_IN);
                break;
        }
    }

    public void skipLoginClick(View view) {
        new SkipAuthService(this).process();
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

    //Results from Google+ auth permission request activity:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if accept:
        if (resultCode == -1) {
            googleButton.performClick();
        }


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Constants.GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            new GoogleAuthService(this).process(result);
        }
    }

    private void setEmailFromFirstAccount() {
        String[] accounts = AccountUtil.getAccountNames();
        if (accounts.length > 0) {
            emailText.setText(accounts[0]);
        }
    }

}
