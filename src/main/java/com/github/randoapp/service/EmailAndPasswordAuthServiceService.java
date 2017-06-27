package com.github.randoapp.service;

import android.app.Activity;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.task.SignupTask;
import com.github.randoapp.task.callback.OnError;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.util.Analytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Map;

public class EmailAndPasswordAuthServiceService extends BaseAuthService {

    private EditText emailText;
    private EditText passwordText;

    public EmailAndPasswordAuthServiceService(Activity activity) {
        super(activity);
        this.emailText = (EditText) activity.findViewById(R.id.emailEditText);
        this.passwordText = (EditText) activity.findViewById(R.id.passwordEditText);
    }

    public void process () {
        Analytics.logLoginEmail(FirebaseAnalytics.getInstance(activity));
        String email = emailText.getText().toString();
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

        new SignupTask(email, password)
                .onOk(new OnOk() {
                    @Override
                    public void onOk(Map<String, Object> data) {
                        done();
                    }
                })
                .onError(new OnError() {
                    @Override
                    public void onError(Map<String, Object> data) {
                        hideLoginProgress();
                        if (data.get(Constants.ERROR) != null) {
                            Toast.makeText(activity, (CharSequence) data.get("error"), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .execute();
    }

    private boolean isEmailCorrect(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordCorrect(String password) {
        return password.length() > 0;
    }

}
