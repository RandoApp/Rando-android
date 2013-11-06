package com.eucsoft.foodex.view;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;

import com.eucsoft.foodex.db.model.FoodPair;

public class FoodView {

    private FoodPair foodPair;
    private View rootView;

    public FoodView(View rootView, FoodPair foodPair) {
        this.rootView = rootView;
        this.foodPair = foodPair;
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
            return new FoodLandscapeView(rootView, foodPair);
        } else {
            return new FoodPortraitView(rootView, foodPair);
        }
    }

}
