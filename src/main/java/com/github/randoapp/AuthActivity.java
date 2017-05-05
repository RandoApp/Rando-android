package com.github.randoapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.randoapp.service.EmailAndPasswordAuthServiceService;
import com.github.randoapp.service.GoogleAuthService;
import com.github.randoapp.service.SkipAuthService;
import com.github.randoapp.log.Log;
import com.github.randoapp.util.AccountUtil;
import com.github.randoapp.util.GooglePlayServicesUtil;
import com.github.randoapp.util.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import static android.view.View.VISIBLE;
import static com.github.randoapp.Constants.CONTACTS_PERMISSION_REQUEST_CODE;
import static com.google.android.gms.common.ConnectionResult.SUCCESS;

public class AuthActivity extends AppCompatActivity {

    private EditText emailText;
    private Button googleButton;
    public boolean isGoogleLoginPressed = false;
    private boolean requestAccountsOnFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        emailText = (EditText) this.findViewById(R.id.emailEditText);
        showGoogleButtonIfGoogleAuthIsSupported();
    }

    private void showGoogleButtonIfGoogleAuthIsSupported() {
        try {
            int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
            if (status == SUCCESS
                    || (status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED && !GooglePlayServicesUtil.isGPSVersionLowerThanRequired(this.getPackageManager()))) {
                googleButton = (Button) this.findViewById(R.id.googleAuthButton);
                googleButton.setVisibility(VISIBLE);
                googleButton.setBackgroundDrawable(this.getResources().getDrawable(com.google.android.gms.R.drawable.common_google_signin_btn_text_dark_normal));
                googleButton.setText(this.getString(com.google.android.gms.R.string.common_signin_button_text_long));
            }
        } catch (Exception exc) {
            //Paranoiac try catch wrapper
        }
    }

    public void signUpClick(View view) {
        new EmailAndPasswordAuthServiceService(this).process();
    }

    public void googleLoginClick(View view) {
        new GoogleAuthService(this).process();
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
        } else if (isGoogleLoginPressed && ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            googleButton.performClick();
        }
        Log.i(AuthActivity.class, this.toString());
        isGoogleLoginPressed = false;
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
    }

    private void setEmailFromFirstAccount() {
        String[] accounts = AccountUtil.getAccountNames();
        if (accounts.length > 0) {
            emailText.setText(accounts[0]);
        }
    }
}
