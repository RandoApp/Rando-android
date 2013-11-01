package com.eucsoft.foodex.view;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.config.Configuration;

import android.net.Uri;
import android.widget.RelativeLayout;

abstract class FoodOrientedView {

    protected Context context;
    protected Uri food;
    protected int displayWidth;
    protected int displayHeight;
    protected View rootView;

    public FoodOrientedView(Uri food, View rootView) {
        this.food = food;
        this.rootView = rootView;
        context = rootView.getContext();
        determineDisplaySize();
    }

    public View getRootView() {
        return rootView;
    }

    public abstract void display();

    private void determineDisplaySize() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        displayWidth = display.getWidth();
        displayHeight = display.getHeight();
    }

    protected ImageView createFoodImage(int width, int height) {
        ImageView foodImage = new ImageView(context);
        foodImage.setImageURI(null);
        foodImage.setImageURI(food);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);

        foodImage.setLayoutParams(layoutParams);

        return foodImage;
    }

    protected ImageButton createBonAppetitButton() {
        ImageButton bonAppetitButton = new ImageButton(context);
        bonAppetitButton.setImageResource(R.drawable.bonappetit2);
        bonAppetitButton.setBackgroundDrawable(null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Configuration.BON_APPETIT_BUTTON_SIZE, Configuration.BON_APPETIT_BUTTON_SIZE);
        bonAppetitButton.setLayoutParams(layoutParams);
        return bonAppetitButton;
    }

    protected RelativeLayout createRelativeLayout() {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeParams.topMargin = Configuration.BON_APPETIT_MARGIN_RIGHT;
        relativeLayout.setLayoutParams(relativeParams);
        return relativeLayout;
    }

    protected LinearLayout createLinerLayout(int topMargin, int leftMargin, int rightMargin) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearParams.topMargin = topMargin;
        linearParams.leftMargin = leftMargin;
        linearParams.rightMargin = rightMargin;
        linearLayout.setLayoutParams(linearParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }
}
