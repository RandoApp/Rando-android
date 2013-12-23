package com.eucsoft.foodex.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

public class BaseActivity extends Activity {

    private ProgressDialog progress;

    protected void showProgressbar(String message) {
        if (progress != null) {
            progress.hide();
        }

        progress = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);
        progress.setMessage(message);
        progress.show();

    }

    protected void hideProgressbar() {
        if (progress != null) {
            progress.hide();
        }
    }
}
