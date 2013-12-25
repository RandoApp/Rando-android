package com.eucsoft.foodex.menu;

import android.view.View;
import android.widget.Button;

import com.eucsoft.foodex.R;

public class ReportMenu {

    public static final int ID =  R.id.action_report;
    private View decorView;

    public static boolean isOn = false;

    public ReportMenu(View decorView) {
        this.decorView = decorView;
    }

    public void select () {
        isOn = !isOn;

        Button button = (Button) decorView.getRootView().findViewWithTag("report_dialog");
        button.setVisibility(View.VISIBLE);
        button.setText("QQQQQQQQQQQQQQQQQQQ");
    }
}
