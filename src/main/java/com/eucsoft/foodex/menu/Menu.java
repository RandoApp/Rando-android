package com.eucsoft.foodex.menu;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

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

    public Menu(Activity activity) {
        this.activity = activity;
    }

    public abstract void select ();

}
