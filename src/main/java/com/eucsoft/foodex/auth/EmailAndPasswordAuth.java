package com.eucsoft.foodex.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.task.SignupTask;
import com.eucsoft.foodex.task.callback.OnError;
import com.eucsoft.foodex.task.callback.OnOk;
import com.eucsoft.foodex.util.AccountUtil;
import com.eucsoft.foodex.view.Progress;
import com.google.android.gms.auth.GoogleAuthUtil;

import java.util.Map;

public class EmailAndPasswordAuth extends BaseAuth {

    private EditText emailText;
    private EditText passwordText;

    public EmailAndPasswordAuth(View rootView, AuthFragment authFragment) {
        super(authFragment);
        this.emailText = (EditText) rootView.findViewById(R.id.emailEditText);
        this.passwordText = (EditText) rootView.findViewById(R.id.passwordEditText);

        setEmailFromAccount(emailText);
    }

    private void setEmailFromAccount(EditText emailText) {
        String[] accounts = AccountUtil.getAccountNames();
        if (accounts.length > 0) {
            emailText.setText(accounts[0]);
        }
    }

    @Override
    public void onClick(View v) {
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

        new SignupTask(email, password)
            .onOk(new OnOk() {
                @Override
                public void onOk(Map<String, Object> data) {
                    done(authFragment.getActivity());
                }
            })
            .onError(new OnError() {
                @Override
                public void onError(Map<String, Object> data) {
                    Progress.hide();
                    if (data.get(Constants.ERROR) != null) {
                        Toast.makeText(authFragment.getActivity(), (CharSequence) data.get("error"), Toast.LENGTH_LONG).show();
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
