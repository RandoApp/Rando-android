package com.eucsoft.foodex.view;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.db.model.FoodPair;

class FoodPortraitView extends FoodOrientedView {

    public static final int LAYOUT_FRAGMENT_RESOURCE = R.layout.homeport;

    public FoodPortraitView(View rootView, FoodPair foodPair) {
        super(foodPair, rootView);
    }

    @Override
    public void display() {
        LinearLayout foodContainer = (LinearLayout) rootView.findViewById(R.id.foodContainer);
        LinearLayout layoutWithFood = buildLayout();
        foodContainer.addView(layoutWithFood);
    }

    private LinearLayout buildLayout() {
        LinearLayout linearLayout = createLinerLayout(Constants.FOOD_MARGIN_PORTRAIT_COLUMN_TOP,
                Constants.FOOD_MARGIN_PORTRAIT_COLUMN_LEFT, Constants.FOOD_MARGIN_PORTRAIT_COLUMN_RIGHT);

        int foodImageSize = displayWidth - Constants.FOOD_MARGIN_PORTRAIT;
        ImageSwitcher foodImage = createFoodImage(foodImageSize, foodImageSize);

        RelativeLayout relativeLayout = createRelativeLayout();
        ImageButton bonAppetitButton = createBonAppetitButton();

        RelativeLayout.LayoutParams bonAppetitParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bonAppetitParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        relativeLayout.addView(bonAppetitButton, bonAppetitParams);
        linearLayout.addView(foodImage);
        linearLayout.addView(relativeLayout);

        return linearLayout;
    }

}
