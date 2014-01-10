package com.eucsoft.foodex.menu;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.fragment.AuthFragment;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.task.LogoutTask;
import com.eucsoft.foodex.view.Progress;

import java.util.Map;

public abstract class Menu {

    protected Activity activity;
    protected MenuItem menuItem;

    public Menu(MenuItem menuItem, Activity activity) {
        this.activity = activity;
        this.menuItem = menuItem;
    }

    public abstract void select ();

}
