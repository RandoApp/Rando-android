package com.github.randoapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

public class Progress {

    private ProgressDialog progress;
    private Activity activity;

    private Progress() {

    }

    public Progress(Activity activity) {
        this.activity = activity;
    }

    public void show(String message, Activity activity) {
        if (progress != null) {
            progress.setMessage(message);
        } else {
            progress = new ProgressDialog(activity, AlertDialog.THEME_HOLO_DARK);
            progress.setCanceledOnTouchOutside(false);
            progress.setMessage(message);
            progress.show();
        }
    }

    public void hide() {
        if (progress != null) {
            progress.hide();
        }
    }

}
