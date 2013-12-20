package com.eucsoft.foodex.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;

public class Progress {

    private static ProgressDialog progress;

    public static void show(String message, Activity activity) {
        if (progress != null) {
            progress.hide();
        }
        progress = new ProgressDialog(activity, AlertDialog.THEME_HOLO_DARK);
        progress.setMessage(message);
        progress.show();
    }

    public static void show(String message) {
        show(message, MainActivity.activity);
    }

    public static void hide() {
        if (progress != null) {
            progress.hide();
        }
    }

    public static void showLoading() {
        show(App.context.getResources().getString(R.string.loadig_progress));
    }
}
