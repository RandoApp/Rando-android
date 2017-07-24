package com.github.randoapp.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.randoapp.MainActivity;
import com.github.randoapp.R;
import com.github.randoapp.api.API;
import com.github.randoapp.api.listeners.ErrorResponseListener;
import com.github.randoapp.db.RandoDAO;
import com.github.randoapp.view.Progress;

import org.json.JSONObject;

public abstract class BaseAuthService {

    protected Activity activity;

    public BaseAuthService(Activity activity) {
        this.activity = activity;
    }

    public void done() {
        hideSoftKeyboard();
        clearDBForChangeAccount();

        showFetchUserProgress();
        API.syncUserAsync(activity.getBaseContext(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgress();
                startMainActivity();
            }
        }, new ErrorResponseListener(activity.getBaseContext()) {
            @Override
            public void onErrorResponse(VolleyError e) {
                hideProgress();
                Toast.makeText(activity, "Cannot fetch user. Pull up to force sync", Toast.LENGTH_LONG).show();
                startMainActivity();
            }
        });
    }

    private void hideSoftKeyboard() {
        if (activity != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View textFieldWithSoftKeyboard = activity.getCurrentFocus();
            if (textFieldWithSoftKeyboard != null) {
                inputManager.hideSoftInputFromWindow(textFieldWithSoftKeyboard.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void clearDBForChangeAccount() {
        RandoDAO.clearRandos(activity.getBaseContext());
        RandoDAO.clearRandoToUpload(activity.getBaseContext());
    }

    private void startMainActivity() {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public void showLoginProgress() {
        Progress.show(activity.getString(R.string.login_progress), activity);
    }

    public void showFetchUserProgress() {
        Progress.show(activity.getString(R.string.loading_user_progress), activity);
    }

    public void hideProgress() {
        Progress.hide();
    }

}
