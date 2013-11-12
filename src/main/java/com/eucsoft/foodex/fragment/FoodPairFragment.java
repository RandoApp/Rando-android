package com.eucsoft.foodex.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.animation.AnimationFactory;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.HorizontalScrollViewListener;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.task.DownloadFoodPicsTask;
import com.eucsoft.foodex.util.FileUtil;
import com.eucsoft.foodex.view.FoodPicsLayout;
import com.eucsoft.foodex.view.ObservableHorizontalScrollView;

import java.util.HashMap;

public class FoodPairFragment extends Fragment implements TaskResultListener, HorizontalScrollViewListener {

    private boolean showStranger = true;
    private boolean mapShown;

    private FoodPair foodPair;
    private int foodImageSize;
    private FoodPicsLayout strangerFoodImage;
    private FoodPicsLayout userFoodImage;

    private boolean animationInProgress = false;

    public static FoodPairFragment newInstance(FoodPair foodPair, boolean showStranger, boolean mapShown) {
        FoodPairFragment fragment = new FoodPairFragment();
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
        setRetainInstance(false);
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

        final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.food_pair_item, container, false);

        ImageButton bonAppetitButton = (ImageButton) layout.findViewWithTag("bon_appetit_button");

        bonAppetitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (layout.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            foodImageSize = displayWidth / 2 - (Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_LEFT + Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_RIGHT);

            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            linearParams.topMargin = Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_TOP;
            linearParams.leftMargin = Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_LEFT;
            linearParams.rightMargin = Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_RIGHT;
            layout.setLayoutParams(linearParams);
        } else {
            foodImageSize = displayWidth - Constants.FOOD_MARGIN_PORTRAIT;
        }
        HorizontalScrollView.LayoutParams layoutParams = new HorizontalScrollView.LayoutParams(foodImageSize, foodImageSize);

        final ViewSwitcher viewSwitcher = (ViewSwitcher) layout.findViewWithTag("viewSwitcher");

        final Animation[] animations1 = AnimationFactory.flipAnimation(foodImageSize, AnimationFactory.FlipDirection.LEFT_RIGHT, 600, null);
        final Animation[] animations2 = AnimationFactory.flipAnimation(foodImageSize, AnimationFactory.FlipDirection.RIGHT_LEFT, 600, null);
        viewSwitcher.setOutAnimation(animations1[0]);
        viewSwitcher.setInAnimation(animations1[1]);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!animationInProgress) {
                    ObservableHorizontalScrollView scrollView = (ObservableHorizontalScrollView) viewSwitcher.findViewWithTag("strangerFood");
                    if (viewSwitcher.getCurrentView() != scrollView) {
                        viewSwitcher.showPrevious();
                    } else if (viewSwitcher.getCurrentView() == scrollView) {
                        viewSwitcher.showNext();
                    }
                }
            }
        };

        strangerFoodImage = (FoodPicsLayout) layout.findViewWithTag("strangerImage");
        strangerFoodImage.setLayoutParams(layoutParams);
        strangerFoodImage.setOnClickListener(onClickListener);
        userFoodImage = (FoodPicsLayout) layout.findViewWithTag("image");
        userFoodImage.setLayoutParams(layoutParams);
        userFoodImage.setOnClickListener(onClickListener);

        Animation.AnimationListener outAnimationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animationInProgress = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };
        animations1[0].setAnimationListener(outAnimationListener);
        animations1[1].setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewSwitcher.setOutAnimation(animations2[0]);
                viewSwitcher.setInAnimation(animations2[1]);
                animationInProgress = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        animations2[0].setAnimationListener(outAnimationListener);
        animations2[1].setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewSwitcher.setOutAnimation(animations1[0]);
                viewSwitcher.setInAnimation(animations1[1]);
                animationInProgress = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        DownloadFoodPicsTask downloadFoodPicsTask = new DownloadFoodPicsTask(this, layout.getContext());
        downloadFoodPicsTask.execute(foodPair);

        /*final ObservableHorizontalScrollView scrollView = (ObservableHorizontalScrollView) layout.findViewWithTag("scroll");
        scrollView.setScrollViewListener(this);*/
        return layout;
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
        strangerFoodImage.add(FileUtil.getFoodPath(foodPair.stranger), foodImageSize);
        strangerFoodImage.add(FileUtil.getMapPath(foodPair.stranger), foodImageSize);
        userFoodImage.add(FileUtil.getFoodPath(foodPair.user), foodImageSize);
        userFoodImage.add(FileUtil.getMapPath(foodPair.user), foodImageSize);
    }

    @Override
    public void onScrollChanged(final ObservableHorizontalScrollView scrollView, int x, int y, int oldx, int oldy) {
        int width = scrollView.getWidth();
        int scrollWidth = scrollView.getChildAt(0).getWidth();

        if (scrollWidth - width - 20 <= x) {
            mapShown = true;
        } else {
            mapShown = false;
        }
    }
}
