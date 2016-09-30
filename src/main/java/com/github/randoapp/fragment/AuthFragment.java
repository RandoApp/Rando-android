package com.github.randoapp.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.github.randoapp.util.AccountUtil;
import com.github.randoapp.util.GooglePlayServicesUtil;
import com.github.randoapp.util.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import static android.view.View.VISIBLE;
import static com.github.randoapp.Constants.CONTACTS_PERMISSION_REQUEST_CODE;
import static com.google.android.gms.common.ConnectionResult.SUCCESS;

public class AuthFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.auth, container, false);

        TextView textViewSkipLink = (TextView) rootView.findViewById(R.id.textViewSkipLink);
        textViewSkipLink.setOnClickListener(new SkipAuth(this));

        Button signupButton = (Button) rootView.findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new EmailAndPasswordAuth(rootView, this));

        createGoogleAuthButton(rootView);

        EditText emailText = (EditText) rootView.findViewById(R.id.emailEditText);
        setEmailFromAccount(emailText);

        return rootView;
    }

    private void createGoogleAuthButton(View rootView) {
        try {
            int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(rootView.getContext());
            if (status == SUCCESS
                    || (status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED && !GooglePlayServicesUtil.isGPSVersionLowerThanRequired(getActivity().getPackageManager()))) {
                Button googleButton = (Button) rootView.findViewById(R.id.googleAuthButton);
                googleButton.setVisibility(VISIBLE);
                googleButton.setBackgroundDrawable(getResources().getDrawable(com.google.android.gms.R.drawable.common_google_signin_btn_text_dark_normal));
                googleButton.setText(getResources().getString(com.google.android.gms.R.string.common_signin_button_text_long));

                GoogleAuth googleAuthListener = new GoogleAuth(this, googleButton);
                googleButton.setOnTouchListener(googleAuthListener);
                googleButton.setOnClickListener(googleAuthListener);
            }
        } catch (Exception exc) {
            //Paranoiac try catch wrapper
        }
    }

    //Results from Google+ auth permission request activity:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if accept:
        if (resultCode == -1) {
            Button googleButton = (Button) getActivity().findViewById(R.id.googleAuthButton);
            GoogleAuth googleAuthListener = new GoogleAuth(this, googleButton);
            googleAuthListener.onClick(googleButton);
        }
    }

    private void setEmailFromAccount(EditText emailText) {
        if (!PermissionUtils.checkAndRequestMissingPermissions(getActivity(), CONTACTS_PERMISSION_REQUEST_CODE, Manifest.permission.GET_ACCOUNTS)) {
            String[] accounts = AccountUtil.getAccountNames();
            if (accounts.length > 0) {
                emailText.setText(accounts[0]);
            }
        }
    }

}
