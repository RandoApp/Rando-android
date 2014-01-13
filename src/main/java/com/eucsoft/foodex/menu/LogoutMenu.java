package com.eucsoft.foodex.menu;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.task.LogoutTask;
import com.eucsoft.foodex.view.Progress;

import java.util.Map;

public class LogoutMenu extends Menu {

    public static final int ID = R.id.action_logout;

    public LogoutMenu(MenuItem item, Activity activity) {
        super(item, activity);
    }

    public void select() {
        Progress.show(App.context.getResources().getString(R.string.logout_progress));
        new LogoutTask(new TaskResultListener() {
            @Override
            public void onTaskResult(int taskCode, long resultCode, Map<String, Object> data) {
                FragmentManager fragmentManager = ((ActionBarActivity) activity).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_screen, new AuthFragment()).commit();
                Progress.hide();
            }
        }).execute();
    }
}
