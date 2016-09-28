package com.github.randoapp.auth;

import android.Manifest;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.randoapp.Constants;
import com.github.randoapp.R;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.task.SignupTask;
import com.github.randoapp.task.callback.OnError;
import com.github.randoapp.task.callback.OnOk;
import com.github.randoapp.util.AccountUtil;
import com.github.randoapp.util.PermissionUtils;
import com.github.randoapp.view.Progress;

import java.util.Map;

import static com.github.randoapp.Constants.CONTACTS_PERMISSION_REQUEST_CODE;

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
        if (!PermissionUtils.checkAndRequestMissingPermissions(authFragment.getActivity(), CONTACTS_PERMISSION_REQUEST_CODE, Manifest.permission.GET_ACCOUNTS)) {
            String[] accounts = AccountUtil.getAccountNames();
            if (accounts.length > 0) {
                emailText.setText(accounts[0]);
            }
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
