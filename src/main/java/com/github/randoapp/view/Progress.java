package com.github.randoapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

public class Progress {

    private static ProgressDialog progress;

    public static void show(String message, Activity activity) {
        if (progress != null) {
            progress.hide();
        }
        progress = new ProgressDialog(activity, AlertDialog.THEME_HOLO_DARK);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage(message);
        progress.show();
    }

    public static void hide() {
        if (progress != null) {
            progress.hide();
        }
    }

}
