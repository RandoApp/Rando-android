package com.eucsoft.foodex.menu;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.fragment.HomeWallFragment;

public class ReportMenu {

    public static final int ID =  R.id.action_report;

    public static boolean isOn = false;

    public void select () {
        isOn = !isOn;

        HomeWallFragment.foodPairsAdapter.notifyDataSetChanged();
    }
}
