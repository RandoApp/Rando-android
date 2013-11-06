package com.eucsoft.foodex.view;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.db.model.FoodPair;

class FoodLandscapeView extends FoodOrientedView {

    public static final int LAYOUT_FRAGMENT_RESOURCE = R.layout.homeland;
    private static boolean odd = true;

    public FoodLandscapeView(View rootView, FoodPair foodPair) {
        super(foodPair, rootView);
    }

    @Override
    public void display() {
        LinearLayout column1 = (LinearLayout) rootView.findViewById(R.id.column1);
        LinearLayout column2 = (LinearLayout) rootView.findViewById(R.id.column2);

        resizeColumns(column1, column2);

        LinearLayout linearLayout = createLinerLayout(Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_TOP,
                Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_LEFT, Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_RIGHT);

        int foodImageSize = displayWidth / 2 - (Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_LEFT + Constants.FOOD_MARGIN_PORTRAIT_COLUMN_RIGHT);
        ImageView foodImage = createFoodImage(foodImageSize, foodImageSize);

        RelativeLayout relativeLayout = createRelativeLayout();
        ImageButton bonAppetitButton = createBonAppetitButton();

        RelativeLayout.LayoutParams bonAppetitParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bonAppetitParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        relativeLayout.addView(bonAppetitButton, bonAppetitParams);
        linearLayout.addView(foodImage);
        linearLayout.addView(relativeLayout);

        if (odd) {
            column1.addView(linearLayout);
            odd = false;
        } else {
            column2.addView(linearLayout);
            odd = true;
        }
    }

    private void resizeColumns(LinearLayout column1, LinearLayout column2) {
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(displayWidth / 2, LinearLayout.LayoutParams.MATCH_PARENT);
        column1.setLayoutParams(linearParams);
        column2.setLayoutParams(linearParams);
    }

}
