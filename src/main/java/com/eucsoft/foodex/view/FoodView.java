package com.eucsoft.foodex.view;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.eucsoft.foodex.config.Configuration;

public class FoodView {

    private Uri food;
    private View rootView;

    public FoodView(View rootView, Uri food) {
        this.rootView = rootView;
        this.food = food;
    }

    public View display() {
        FoodOrientedView foodOrientedView = createFoodOrientedView();
        foodOrientedView.display();

        View rootView = foodOrientedView.getRootView();
        return rootView;
    }


    public static int getLayoutFragmentResource(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return FoodLandscapeView.LAYOUT_FRAGMENT_RESOURCE;
        } else {
            return FoodPortraitView.LAYOUT_FRAGMENT_RESOURCE;
        }
    }

    private FoodOrientedView createFoodOrientedView () {
        if (rootView.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return new FoodLandscapeView(rootView, food);
        } else {
            return new FoodPortraitView(rootView, food);
        }
    }

}
