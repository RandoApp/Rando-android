package com.eucsoft.foodex.com.eucsoft.foodex.view;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
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

        ImageView foodImage = new ImageView(context);
        foodImage.setImageResource(drawableFood);

        RelativeLayout relativeLayout = createRelativeLayout(context);

        RelativeLayout.LayoutParams bonAppetitParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bonAppetitParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        ImageButton bonAppetitButton = createBonAppetitButton(context);

        relativeLayout.addView(bonAppetitButton, bonAppetitParams);
        linearLayout.addView(foodImage);
        linearLayout.addView(relativeLayout);
    }

    private LinearLayout createLinerLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams marginParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        marginParams.topMargin = 10;
        marginParams.rightMargin = 15;
        marginParams.leftMargin = 15;
        linearLayout.setLayoutParams(marginParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }

    private RelativeLayout createRelativeLayout(Context context) {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relativeParams.topMargin = 5;
        relativeLayout.setLayoutParams(relativeParams);
        return relativeLayout;
    }

    private ImageButton createBonAppetitButton(Context context) {
        ImageButton bonAppetitButton = new ImageButton(context);
        bonAppetitButton.setImageResource(R.drawable.bonappetit2);
        return bonAppetitButton;
    }

    public View getView() {
        return linearLayout;
    }

}
