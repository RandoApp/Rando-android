package com.eucsoft.foodex.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.animation.AnimationFactory;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.task.DownloadFoodPicsTask;
import com.eucsoft.foodex.util.FileUtil;

import java.util.HashMap;

abstract class FoodOrientedView implements TaskResultListener {

    protected Context context;
    protected FoodPair foodPair;
    protected int displayWidth;
    protected int displayHeight;
    protected View rootView;
    protected ImageSwitcher foodImage;
    private static BitmapFactory.Options decodeOptions;

    private boolean isUserFoodShown = false;

    static {
        decodeOptions = new BitmapFactory.Options();
        decodeOptions.inDither = false;
        decodeOptions.inJustDecodeBounds = false;
        decodeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        decodeOptions.inSampleSize = 3;
        decodeOptions.inPurgeable = true;
    }

    public FoodOrientedView(FoodPair foodPair, View rootView) {
        this.foodPair = foodPair;
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

    protected ImageSwitcher createFoodImage(final int width, final int height) {
        foodImage = new ImageSwitcher(context);

        foodImage.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                // Create a new ImageView set it's properties
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(width, height));
                return imageView;
            }
        });

        AnimationFactory.flipTransition(foodImage, AnimationFactory.FlipDirection.RIGHT_LEFT);
        foodImage.setAnimateFirstView(false);

        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath;
                if (isUserFoodShown) {
                    filePath = FileUtil.getFoodPath(foodPair.stranger);
                } else {
                    filePath = FileUtil.getFoodPath(foodPair.user);
                }
                isUserFoodShown = !isUserFoodShown;
                Bitmap bitmap = BitmapFactory.decodeFile(filePath, decodeOptions);
                Drawable d = new BitmapDrawable(bitmap);
                foodImage.setImageDrawable(d);
            }
        });

        foodImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                float y = MotionEventCompat.getY(event, 0);
                if (y < -30) {
                    //TODO: SHould be MAP
                    String filePath;
                    if (isUserFoodShown) {
                        filePath = FileUtil.getFoodPath(foodPair.stranger);
                    } else {
                        filePath = FileUtil.getFoodPath(foodPair.user);
                    }
                    isUserFoodShown = !isUserFoodShown;
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath, decodeOptions);
                    Drawable d = new BitmapDrawable(bitmap);
                    foodImage.setImageDrawable(d);

                }

                return false;
            }
        });

        DownloadFoodPicsTask downloadFoodPicsTask = new DownloadFoodPicsTask(this, context);
        downloadFoodPicsTask.execute(foodPair);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);

        foodImage.setLayoutParams(layoutParams);

        return foodImage;
    }

    protected ImageButton createBonAppetitButton() {
        ImageButton bonAppetitButton = new ImageButton(context);
        bonAppetitButton.setImageResource(R.drawable.bonappetit2);
        bonAppetitButton.setBackgroundDrawable(null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Constants.BON_APPETIT_BUTTON_SIZE, Constants.BON_APPETIT_BUTTON_SIZE);
        bonAppetitButton.setLayoutParams(layoutParams);
        return bonAppetitButton;
    }

    protected RelativeLayout createRelativeLayout() {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeParams.topMargin = Constants.BON_APPETIT_MARGIN_RIGHT;
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

    @Override
    public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {
        String filename = (String) data.get(Constants.FILENAME);
        Bitmap bitmap = BitmapFactory.decodeFile(filename, decodeOptions);
        Drawable d = new BitmapDrawable(bitmap);
        foodImage.setImageDrawable(d);
    }
}
