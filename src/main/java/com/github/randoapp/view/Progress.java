package com.github.randoapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import com.github.randoapp.App;
import com.github.randoapp.MainActivity;
import com.github.randoapp.R;

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

    public static void show(String message) {
        show(message, MainActivity.activity);
    }

    public static void hide() {
        if (progress != null) {
            progress.hide();
        }
    }

    public static void showLoading() {
        show(App.context.getResources().getString(R.string.loading_progress));
    }

    public static void showLogin(Activity activity) {
        show(activity.getString(R.string.login_progress), activity);
    }

    public static void showLoading(Activity activity) {
        show(activity.getString(R.string.loading_progress), activity);
    }
}
