package com.github.randoapp.menu;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.github.randoapp.App;
import com.github.randoapp.R;
import com.github.randoapp.fragment.AuthFragment;
import com.github.randoapp.task.LogoutTask;
import com.github.randoapp.task.callback.OnDone;
import com.github.randoapp.view.Progress;

import java.util.Map;

public class LogoutMenu extends Menu {

    public static final int ID = R.id.action_logout;

    public LogoutMenu(MenuItem item, Activity activity) {
        super(item, activity);
    }

    public void select() {
        Progress.show(App.context.getResources().getString(R.string.logout_progress));
        new LogoutTask()
            .onDone(new OnDone() {
                @Override
                public void onDone(Map<String, Object> data) {
                    FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_screen, new AuthFragment()).commit();
                    Progress.hide();
                }
            })
            .execute();
    }
}
