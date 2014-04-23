package com.github.randoapp.menu;

import android.app.Activity;
import android.view.MenuItem;

public abstract class Menu {

    protected Activity activity;
    protected MenuItem menuItem;

    public Menu(MenuItem menuItem, Activity activity) {
        this.activity = activity;
        this.menuItem = menuItem;
    }

    public abstract void select();

}
