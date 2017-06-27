package com.github.randoapp.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.randoapp.Constants;
import com.github.randoapp.MainActivity;
import com.github.randoapp.R;
import com.github.randoapp.auth.EmailAndPasswordAuth;
import com.github.randoapp.auth.SkipAuth;
import com.github.randoapp.log.Log;
import com.github.randoapp.util.AccountUtil;
import com.github.randoapp.util.PermissionUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import static com.github.randoapp.Constants.CONTACTS_PERMISSION_REQUEST_CODE;

public class AuthFragment extends Fragment {

    private EditText emailText;
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

        initGoogleAuthButton(rootView);

        emailText = (EditText) rootView.findViewById(R.id.emailEditText);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestAccountsOnFirstLoad) {
            PermissionUtils.checkAndRequestMissingPermissions(getActivity(), CONTACTS_PERMISSION_REQUEST_CODE, Manifest.permission.GET_ACCOUNTS);
            requestAccountsOnFirstLoad = false;
        }
        Log.i(AuthFragment.class, this.toString());
        isGoogleLoginPressed = false;
        setEmailFromFirstAccount();
    }


    private void setEmailFromFirstAccount() {
        String[] accounts = AccountUtil.getAccountNames();
        if (accounts.length > 0) {
            emailText.setText(accounts[0]);
        }
    }

    private GoogleApiClient googleApiClient;

    private void initGoogleAuthButton (View rootView) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getContext(), "Problem with Google service. Please try again.", Toast.LENGTH_LONG).show();
                        Log.e(MainActivity.class, "API.google exception");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        SignInButton signInButton = (SignInButton) rootView.findViewById(R.id.google_sign_in_button);
        if (signInButton == null) {
            return;
        }

        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.google_sign_in_button:
                        signIn();
                        break;
                    // ...
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        this.startActivityForResult(signInIntent, Constants.GOOGLE_SIGN_IN);
    }

}
