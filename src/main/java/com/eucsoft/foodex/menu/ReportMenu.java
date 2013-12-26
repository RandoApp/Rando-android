package com.eucsoft.foodex.menu;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.R;
import static com.eucsoft.foodex.Constants.*;

public class ReportMenu extends Menu {

    public static final int ID = R.id.action_report;

    public static boolean isReport = false;

    public ReportMenu(MenuItem item, Activity activity) {
        super(item, activity);
    }

    public void select () {
        isReport = !isReport;
        activity.sendBroadcast(new Intent().setAction(REPORT_BROADCAST));
        menuItem.setTitle(getMenuTitle());
    }


    public static void off() {
        ReportMenu.isReport = false;
        App.context.sendBroadcast(new Intent().setAction(REPORT_BROADCAST));
    }

    public static String getMenuTitle() {
        if (isReport) {
            return App.context.getResources().getString(R.string.report_off);
        } else {
            return App.context.getResources().getString(R.string.report);
        }

    }
}
