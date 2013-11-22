package com.eucsoft.foodex.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;

import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;

public class Progress {

    private static ProgressDialog progress;

    public static void show () {
        if (progress != null) {
            progress.hide();
        }
        progress = new ProgressDialog(MainActivity.activity, AlertDialog.THEME_HOLO_DARK);
        progress.setMessage(MainActivity.activity.getResources().getString(R.string.loadig_progress));
        progress.show();
    }

    public static void hide() {
        if (progress != null) {
            progress.hide();
        }
    }

}
