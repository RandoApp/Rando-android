package com.github.randoapp.service;

import android.app.Activity;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.github.randoapp.R;
import com.github.randoapp.api.API;
import com.github.randoapp.api.beans.Error;
import com.github.randoapp.api.listeners.NetworkResultListener;
import com.github.randoapp.preferences.Preferences;
import com.github.randoapp.util.Analytics;
import com.google.firebase.analytics.FirebaseAnalytics;

public class EmailAndPasswordAuthService extends BaseAuthService {

    private EditText emailText;
    private EditText passwordText;

    public EmailAndPasswordAuthService(Activity activity) {
        super(activity);
        this.emailText = (EditText) activity.findViewById(R.id.emailEditText);
        this.passwordText = (EditText) activity.findViewById(R.id.passwordEditText);
    }

    public void process() {
        Analytics.logLoginEmail(FirebaseAnalytics.getInstance(activity));
        final String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (!isEmailCorrect(email)) {
            Toast.makeText(activity, activity.getString(R.string.email_should_be), Toast.LENGTH_LONG).show();
            return;
        }

        if (!isPasswordCorrect(password)) {
            Toast.makeText(activity, activity.getString(R.string.password_should_be), Toast.LENGTH_LONG).show();
            return;
        }

        showLoginProgress();

        API.signup(email, password, activity.getBaseContext(), new NetworkResultListener() {

            @Override
            public void onOk() {
                Preferences.setAccount(activity.getBaseContext(), email);
                done();
            }

            @Override
            public void onError(Error error) {
                hideLoginProgress();
                String errorMessage = error != null ? error.buildMessage(activity.getBaseContext()) : "Error";
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
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
