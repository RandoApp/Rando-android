package com.github.randoapp.auth;

import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.randoapp.R;
import com.github.randoapp.api.API;
import com.github.randoapp.api.listeners.NetworkResultListener;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.util.Analytics;
import com.github.randoapp.view.Progress;
import com.google.firebase.analytics.FirebaseAnalytics;

public class EmailAndPasswordAuth extends BaseAuth {

    private EditText emailText;
    private EditText passwordText;

    public EmailAndPasswordAuth(View rootView, AuthFragment authFragment) {
        super(authFragment);
        this.emailText = (EditText) rootView.findViewById(R.id.emailEditText);
        this.passwordText = (EditText) rootView.findViewById(R.id.passwordEditText);
    }

    @Override
    public void onClick(View v) {
        Analytics.logLoginEmail(FirebaseAnalytics.getInstance(authFragment.getActivity()));
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (!isEmailCorrect(email)) {
            Toast.makeText(authFragment.getActivity(), authFragment.getResources().getString(R.string.email_should_be), Toast.LENGTH_LONG).show();
            return;
        }

        if (!isPasswordCorrect(password)) {
            Toast.makeText(authFragment.getActivity(), authFragment.getResources().getString(R.string.password_should_be), Toast.LENGTH_LONG).show();
            return;
        }

        Progress.showLoading();
        Preferences.setAccount(email);

        API.signup(email, password, new NetworkResultListener() {

            @Override
            public void onOk() {
                BaseAuth.done(authFragment.getActivity());
            }

            @Override
            public void onError(Exception error) {
                Progress.hide();
                String errorMessage = error == null ? error.getMessage() : "Error";
                Toast.makeText(authFragment.getActivity(), errorMessage, Toast.LENGTH_LONG).show();
            }

        });
    }

    private boolean isEmailCorrect(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordCorrect(String password) {
        return password.length() > 0;
    }

}
