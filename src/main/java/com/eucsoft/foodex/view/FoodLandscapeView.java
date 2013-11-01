package com.eucsoft.foodex.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eucsoft.foodex.R;

class FoodLandscapeView extends FoodOrientedView {

    private static boolean odd = false;

    public FoodLandscapeView(LayoutInflater inflater, ViewGroup container, Uri food) {
        super(food);
        init(inflater.inflate(R.layout.homeland2, container, false));
    }

    @Override
    public void display() {
        LinearLayout column1 = (LinearLayout) rootView.findViewById(R.id.column1);
        LinearLayout column2 = (LinearLayout) rootView.findViewById(R.id.column2);

        resizeColumns(column1, column2);

        LinearLayout linearLayout = createLinerLayout();
        ImageView foodImage = createFoodImage(displayWidth / 2 - 20, displayHeight / 2 - 20);

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


    private LinearLayout createLinerLayout() {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearParams.topMargin = 15;
        linearParams.leftMargin = 10;
        linearParams.rightMargin = 10;
        linearLayout.setLayoutParams(linearParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }

    private RelativeLayout createRelativeLayout() {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeParams.topMargin = 5;
        relativeLayout.setLayoutParams(relativeParams);
        return relativeLayout;
    }

}
