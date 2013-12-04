package com.eucsoft.foodex.auth;

import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.task.BaseTask;
import com.eucsoft.foodex.task.SignupTask;
import com.eucsoft.foodex.view.Progress;

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
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (!isEmailCorrect(email)) {
            errors.put("error", authFragment.getResources().getString(R.string.emailShouldBe));
            onTaskResult(0, BaseTask.RESULT_ERROR, errors);
            return;
        }

        if (!isPasswordCorrect(password)) {
            errors.put("error", authFragment.getResources().getString(R.string.passwordShouldBe));
            onTaskResult(0, BaseTask.RESULT_ERROR, errors);
            return;
        }

        Progress.showLoading();

        SignupTask signupTask = new SignupTask(this);
        signupTask.execute(email, password);
    }

    private boolean isEmailCorrect(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordCorrect(String password) {
        return password.length() > 0;
    }

}
