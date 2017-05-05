package com.github.randoapp.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import org.json.JSONObject;

public abstract class BaseAuthService {

    protected Activity activity;
    private ProgressDialog loginProgress;
    private ProgressDialog fetchUserProgress;

    public BaseAuthService(Activity activity) {
        this.activity = activity;
    }

    public void done() {
        hideLoginProgress();

        hideSoftKeyboard();
        clearDBForChangeAccount();

        showFetchUserProgress();
        API.syncUserAsync(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideFetchUserProgress();
                startMainActivity();
            }
        }, new ErrorResponseListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                hideFetchUserProgress();
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
        RandoDAO.clearRandos();
        RandoDAO.clearRandoToUpload();
    }

    private void startMainActivity() {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    public void showLoginProgress() {
        if (loginProgress != null) {
            hideLoginProgress();
        }

        loginProgress = new ProgressDialog(activity, AlertDialog.THEME_HOLO_DARK);
        loginProgress.setMessage(activity.getString(R.string.login_progress));
        loginProgress.show();
    }

    public void hideLoginProgress() {
        if (loginProgress != null) {
            loginProgress.hide();
        }
    }

    public void showFetchUserProgress() {
        if (fetchUserProgress != null) {
            hideFetchUserProgress();
        }

        fetchUserProgress = new ProgressDialog(activity, AlertDialog.THEME_HOLO_DARK);
        fetchUserProgress.setMessage(activity.getString(R.string.loading_user_progress));
        fetchUserProgress.show();
    }

    public void hideFetchUserProgress() {
        if (fetchUserProgress != null) {
            fetchUserProgress.hide();
        }
    }

}
