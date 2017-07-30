package com.github.randoapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

public class Progress {

    private ProgressDialog progress;
    private Activity activity;
    private boolean isShowing = false;

    public Progress(Activity activity) {
        this.activity = activity;
    }

    public void show(String message) {
        if (progress != null) {
            progress.setMessage(message);
            show();
        } else {
            progress = new ProgressDialog(activity, AlertDialog.THEME_HOLO_DARK);
            progress.setCanceledOnTouchOutside(false);
            progress.setMessage(message);
            show();
        }
    }

    private void show() {
        if (!isShowing) {
            progress.show();
            isShowing = true;
        }
    }

    public void hide() {
        isShowing = false;
        if (progress != null) {
            progress.hide();
        }
    }

}
