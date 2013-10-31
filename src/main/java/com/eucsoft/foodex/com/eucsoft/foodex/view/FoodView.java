package com.eucsoft.foodex.com.eucsoft.foodex.view;

import android.app.ActionBar;
import android.app.WallpaperInfo;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eucsoft.foodex.R;

public class FoodView {

    LinearLayout linearLayout;

    public FoodView(Context context, int drawableFood)
    {
        this.linearLayout = createLinerLayout(context);
        ImageView foodImage = createFoodImage(context, drawableFood);

        RelativeLayout relativeLayout = createRelativeLayout(context);
        ImageButton bonAppetitButton = createBonAppetitButton(context);

        RelativeLayout.LayoutParams bonAppetitParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bonAppetitParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        relativeLayout.addView(bonAppetitButton, bonAppetitParams);
        linearLayout.addView(foodImage);
        linearLayout.addView(relativeLayout);
    }

    private ImageView createFoodImage(Context context, int drawableFood) {
        ImageView foodImage = new ImageView(context);
        foodImage.setImageResource(drawableFood);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width-30, width-30);
        foodImage.setLayoutParams(layoutParams);

        return foodImage;
    }

    private LinearLayout createLinerLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearParams.topMargin = 15;
        linearParams.leftMargin = 15;
        linearParams.rightMargin = 15;
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

    public View getView() {
        return linearLayout;
    }

}
