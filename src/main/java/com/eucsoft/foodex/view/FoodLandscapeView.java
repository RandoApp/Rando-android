package com.eucsoft.foodex.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eucsoft.foodex.R;

public class FoodLandscapeView {

    private static boolean odd = false;
    private boolean isColumnResized = false;

    public FoodLandscapeView(LinearLayout column1, LinearLayout column2, int drawableFood)
    {
        Context context = column1.getContext();
        if (!isColumnResized) {
            resizeColumns(column1, column2);
        }

        LinearLayout linearLayout = createLinerLayout(context);
        ImageView foodImage = createFoodImage(context, drawableFood);

        RelativeLayout relativeLayout = createRelativeLayout(context);
        ImageButton bonAppetitButton = createBonAppetitButton(context);

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
        WindowManager windowManager = (WindowManager) column1.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = display.getWidth();

        int columnWidth = width/2;

        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(columnWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        column1.setLayoutParams(linearParams);
        column2.setLayoutParams(linearParams);
    }


    private ImageView createFoodImage(Context context, int drawableFood) {
        ImageView foodImage = new ImageView(context);
        foodImage.setImageResource(drawableFood);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width/2 - 20, width/2 - 20);
        foodImage.setLayoutParams(layoutParams);

        return foodImage;
    }

    private LinearLayout createLinerLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearParams.topMargin = 15;
        linearParams.leftMargin = 10;
        linearParams.rightMargin = 10;
        linearLayout.setLayoutParams(linearParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }

    private RelativeLayout createRelativeLayout(Context context) {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeParams.topMargin = 5;
        relativeLayout.setLayoutParams(relativeParams);
        return relativeLayout;
    }

    private ImageButton createBonAppetitButton(Context context) {
        ImageButton bonAppetitButton = new ImageButton(context);
        bonAppetitButton.setImageResource(R.drawable.bonappetit2);
        bonAppetitButton.setBackgroundDrawable(null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
        bonAppetitButton.setLayoutParams(layoutParams);
        return bonAppetitButton;
    }


}
