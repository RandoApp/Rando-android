package com.eucsoft.foodex.menu;

import android.app.Activity;
import android.content.Intent;

import com.eucsoft.foodex.R;

import static com.eucsoft.foodex.Constants.REPORT_BROADCAST;

public class ReportMenu extends Menu {

    public static final int ID = R.id.action_report;

    public ReportMenu(Activity activity) {
        super(activity);
    }

    public void select() {
        activity.sendBroadcast(new Intent().setAction(REPORT_BROADCAST));
    }
}
