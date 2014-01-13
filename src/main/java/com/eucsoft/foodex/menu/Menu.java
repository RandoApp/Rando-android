package com.eucsoft.foodex.menu;

import android.app.Activity;

public abstract class Menu {

    protected Activity activity;

    public Menu(Activity activity) {
        this.activity = activity;
    }

    public abstract void select();

}
