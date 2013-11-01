package com.eucsoft.foodex.view;

import android.app.ActionBar;
import android.app.WallpaperInfo;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.config.Configuration;

public class FoodView {

    private Uri food;
    private LayoutInflater inflater;
    private ViewGroup container;

    public FoodView(LayoutInflater inflater, ViewGroup container, Uri food)
    {
        this.inflater = inflater;
        this.food = food;
        this.container = container;
    }

    public View display() {
        FoodOrientedView foodOrientedView = createFoodOrientedView();
        foodOrientedView.display();

        View rootView = foodOrientedView.getRootView();
        return rootView;
    }


    private FoodOrientedView createFoodOrientedView () {
        if (container.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return new FoodLandscapeView(inflater, container, food);
        } else {
            return new FoodPortraitView(inflater, container, food);
        }
    }

}
