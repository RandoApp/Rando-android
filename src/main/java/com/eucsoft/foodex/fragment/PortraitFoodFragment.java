package com.eucsoft.foodex.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.util.FileUtil;

import java.util.HashMap;

public class PortraitFoodFragment extends Fragment implements TaskResultListener {

    private static BitmapFactory.Options decodeOptions;

    static {
        decodeOptions = new BitmapFactory.Options();
        decodeOptions.inDither = false;
        decodeOptions.inJustDecodeBounds = false;
        decodeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        decodeOptions.inSampleSize = 3;
        decodeOptions.inPurgeable = true;
    }

    private boolean showStranger = true;
    private boolean mapShown;

    private FoodPair foodPair;
    private ImageSwitcher foodImage;
    private View mOverscrollLeft;
    private View mOverscrollRight;

    private Animation mSlideInLeft;
    private Animation mSlideOutRight;
    private Animation mSlideInRight;
    private Animation mSlideOutLeft;
    private Animation mOverscrollLeftFadeOut;
    private Animation mOverscrollRightFadeOut;


    public static PortraitFoodFragment newInstance(FoodPair foodPair, boolean showStranger, boolean mapShown) {
        PortraitFoodFragment fragment = new PortraitFoodFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.FOOD_PAIR, foodPair);
        args.putBoolean(Constants.SHOW_STRANGER, showStranger);
        args.putBoolean(Constants.MAP_SHOWN, mapShown);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        WindowManager windowManager = (WindowManager) container.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int displayWidth = display.getWidth();

        Bundle bundle;
        if (savedInstanceState == null) {
            bundle = getArguments();
        } else {
            bundle = savedInstanceState;
        }
        foodPair = (FoodPair) bundle.getSerializable(Constants.FOOD_PAIR);
        showStranger = bundle.getBoolean(Constants.SHOW_STRANGER);
        mapShown = bundle.getBoolean(Constants.MAP_SHOWN);

        final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.food_pair_port, container, false);

        loadAnimations(layout.getContext());

        ImageButton bonAppetitButton = (ImageButton) layout.findViewWithTag("bon_appetit_button");

        bonAppetitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        foodImage = (ImageSwitcher) layout.findViewWithTag("image");

        mOverscrollLeft = layout.findViewById(R.id.overscroll_left);
        mOverscrollRight = layout.findViewById(R.id.overscroll_right);

        final int foodImageSize;
        if (layout.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            foodImageSize = displayWidth / 2 - (Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_LEFT + Constants.FOOD_MARGIN_PORTRAIT_COLUMN_RIGHT);

            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearParams.topMargin = Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_TOP;
            linearParams.leftMargin = Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_LEFT;
            linearParams.rightMargin = Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_RIGHT;
            layout.setLayoutParams(linearParams);
        } else {
            foodImageSize = displayWidth - Constants.FOOD_MARGIN_PORTRAIT;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(foodImageSize, foodImageSize);
        foodImage.setLayoutParams(layoutParams);

        foodImage.setFactory(new ImageSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                // Create a new ImageView set it's properties
                ImageView imageView = new ImageView(layout.getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(foodImageSize, foodImageSize));
                return imageView;
            }
        });


        final GestureDetector gestureDetector = new GestureDetector(container.getContext(), new SwipeListener());
        foodImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        String filename = getFileToShow(mapShown);
        if (filename != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(filename, decodeOptions);
            Drawable d = new BitmapDrawable(container.getResources(), bitmap);
            foodImage.setImageDrawable(d);
        }
        return layout;
    }

    private void showMap(boolean showMap) {
        if (showMap && mapShown) {
            mOverscrollRight.setVisibility(View.VISIBLE);
            mOverscrollLeft.startAnimation(mOverscrollRightFadeOut);
            return;
        }

        if (!showMap && !mapShown) {
            mOverscrollLeft.setVisibility(View.VISIBLE);
            mOverscrollLeft.startAnimation(mOverscrollLeftFadeOut);
            return;
        }
        foodImage.setInAnimation(showMap ? mSlideInRight : mSlideInLeft);
        foodImage.setOutAnimation(showMap ? mSlideOutLeft : mSlideOutRight);

        String filePath = getFileToShow(showMap);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, decodeOptions);
        Drawable d = new BitmapDrawable(bitmap);
        foodImage.setImageDrawable(d);
    }

    private String getFileToShow(boolean showMap) {
        String filePath = null;
        if (showMap) {
            mapShown = true;
            if (showStranger) {
                if (FileUtil.isMapExists(foodPair.user))
                    filePath = FileUtil.getMapPath(foodPair.user);
            } else {
                if (FileUtil.isMapExists(foodPair.stranger))
                    filePath = FileUtil.getMapPath(foodPair.stranger);
            }
        } else {
            mapShown = false;
            if (showStranger) {
                if (FileUtil.isFoodExists(foodPair.stranger))
                    filePath = FileUtil.getFoodPath(foodPair.stranger);
            } else {
                if (FileUtil.isFoodExists(foodPair.user))
                    filePath = FileUtil.getFoodPath(foodPair.user);
            }
        }
        return filePath;
    }

    private class SwipeListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 50;
        private static final int SWIPE_MAX_OFF_PATH = 350;
        private static final int SWIPE_THRESHOLD_VELOCITY = 50;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    showMap(true);
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    showMap(false);
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            PortraitFoodFragment portraitFoodFragment = PortraitFoodFragment.newInstance(foodPair, !showStranger, mapShown);
            transaction.setCustomAnimations(R.anim.food_flip_out, R.anim.food_flip_in);
            transaction.replace((int) foodPair.id, portraitFoodFragment);
            transaction.commit();
            return false;
        }
    }

    private void loadAnimations(Context context) {
        // Animations
        mSlideInLeft = AnimationUtils.loadAnimation(context,
                android.R.anim.slide_in_left);
        mSlideOutRight = AnimationUtils.loadAnimation(context,
                android.R.anim.slide_out_right);
        mSlideInRight = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
        mSlideOutLeft = AnimationUtils.loadAnimation(context, R.anim.slide_out_left);
        mOverscrollLeftFadeOut = AnimationUtils
                .loadAnimation(context, R.anim.fade_out);
        mOverscrollRightFadeOut = AnimationUtils.loadAnimation(context,
                R.anim.fade_out);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.FOOD_PAIR, foodPair);
        outState.putBoolean(Constants.SHOW_STRANGER, showStranger);
        outState.putBoolean(Constants.MAP_SHOWN, mapShown);
    }

    @Override
    public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {
        String filename = FileUtil.getFoodPath(((FoodPair) data.get(Constants.FOOD_PAIR)).stranger);
        Bitmap bitmap = BitmapFactory.decodeFile(filename, decodeOptions);
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        foodImage.setImageDrawable(d);
    }
}
