package com.github.randoapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.randoapp.R;
import com.github.randoapp.api.API;
import com.github.randoapp.auth.EmailAndPasswordAuth;
import com.github.randoapp.auth.SkipAuth;
import com.github.randoapp.log.Log;
import com.github.randoapp.util.GPSUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import static android.view.View.VISIBLE;
import static com.google.android.gms.common.ConnectionResult.SUCCESS;

public class AuthFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 444;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton googleButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.auth, container, false);

        TextView textViewSkipLink = (TextView) rootView.findViewById(R.id.textViewSkipLink);
        textViewSkipLink.setOnClickListener(new SkipAuth(this));

        Button signupButton = (Button) rootView.findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new EmailAndPasswordAuth(rootView, this));

        createGoogleAuthButton(rootView);

        return rootView;
    }

    private void createGoogleAuthButton(View rootView) {
            int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(rootView.getContext());
            if (status == SUCCESS
                    || (status == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED && !GPSUtil.isGPSVersionLowerThanRequired(getActivity().getPackageManager()))) {
                googleButton = (SignInButton) rootView.findViewById(R.id.googleAuthButton);
                googleButton.setVisibility(VISIBLE);
                googleButton.setSize(SignInButton.SIZE_WIDE);
                //googleButton.setBackgroundDrawable(getResources().getDrawable(com.google.android.gms.R.drawable.common_google_signin_btn_text_dark_normal));
                //googleButton. setText(getResources().getString(com.google.android.gms.R.string.common_signin_button_text_long));

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                googleButton.setScopes(gso.getScopeArray());
                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .enableAutoManage(getActivity(), this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();
                googleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                    }
                });

            }
    }

    //Results from Google+ auth permission request activity:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            Log.d(AuthFragment.class, "Handle SignIn Result:" + result.isSuccess()+ result.getStatus());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                try {
                    API.google(acct.getEmail(), acct.getIdToken(), acct.getFamilyName());
                } catch (Exception e){

                }
            } else {
                // Signed out, show unauthenticated UI.
            }
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //googleButton.setVisibility(View.GONE);
        Log.d(AuthFragment.class,"onConnectionFailed: ", connectionResult.toString(), " "+connectionResult.getErrorCode());
    }
}
