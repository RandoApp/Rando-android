package com.github.randoapp.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.randoapp.R;
import com.github.randoapp.auth.EmailAndPasswordAuth;
import com.github.randoapp.auth.GoogleAuth;
import com.github.randoapp.auth.SkipAuth;
import com.github.randoapp.log.Log;
import com.github.randoapp.util.AccountUtil;
import com.github.randoapp.util.GooglePlayServicesUtil;
import com.github.randoapp.util.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import static android.view.View.VISIBLE;
import static com.github.randoapp.Constants.CONTACTS_PERMISSION_REQUEST_CODE;
import static com.google.android.gms.common.ConnectionResult.SUCCESS;

public class AuthFragment extends Fragment {

    private EditText emailText;
    private Button googleButton;
    public boolean isGoogleLoginPressed = false;
    private boolean requestAccountsOnFirstLoad = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.auth, container, false);

        TextView textViewSkipLink = (TextView) rootView.findViewById(R.id.textViewSkipLink);
        textViewSkipLink.setOnClickListener(new SkipAuth(this));

        Button signupButton = (Button) rootView.findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new EmailAndPasswordAuth(rootView, this));

        emailText = (EditText) rootView.findViewById(R.id.emailEditText);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestAccountsOnFirstLoad) {
            PermissionUtils.checkAndRequestMissingPermissions(getActivity(), CONTACTS_PERMISSION_REQUEST_CODE, Manifest.permission.GET_ACCOUNTS);
            requestAccountsOnFirstLoad = false;
        } else if (isGoogleLoginPressed && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            googleButton.performClick();
        }
        Log.i(AuthFragment.class, this.toString());
        isGoogleLoginPressed = false;
        setEmailFromFirstAccount();
    }


    //Results from Google+ auth permission request activity:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if accept:
        if (resultCode == -1) {
            GoogleAuth googleAuthListener = new GoogleAuth(this, googleButton);
            googleAuthListener.onClick(googleButton);
        }
    }

    private void setEmailFromFirstAccount() {
        String[] accounts = AccountUtil.getAccountNames();
        if (accounts.length > 0) {
            emailText.setText(accounts[0]);
        }
    }
}
