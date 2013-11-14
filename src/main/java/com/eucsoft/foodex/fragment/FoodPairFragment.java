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
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.animation.AnimationFactory;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.TaskResultListener;
import com.eucsoft.foodex.task.BaseTask;
import com.eucsoft.foodex.task.BonAppetitTask;
import com.eucsoft.foodex.task.DownloadFoodPicsTask;
import com.eucsoft.foodex.view.FoodPicsLayout;
import com.eucsoft.foodex.view.ObservableHorizontalScrollView;

import java.util.HashMap;

public class FoodPairFragment extends Fragment implements TaskResultListener {

    private FoodPair foodPair;
    private int foodImageSize;
    private FoodPicsLayout strangerFoodImage;
    private FoodPicsLayout userFoodImage;
    private ImageButton bonAppetitButton;
    private int displayWidth;

    private boolean isStrangerShown = true;
    private boolean animationInProgress = false;
    private boolean isMap = false;
    private boolean isAutoScrolled = false;
    private boolean isFingerScrolling = false;

    public static FoodPairFragment newInstance(FoodPair foodPair) {
        FoodPairFragment fragment = new FoodPairFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.FOOD_PAIR, foodPair);
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
        displayWidth = display.getWidth();

        Bundle bundle;
        if (savedInstanceState == null) {
            bundle = getArguments();
        } else {
            bundle = savedInstanceState;
        }
        foodPair = (FoodPair) bundle.getSerializable(Constants.FOOD_PAIR);

        final ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.food_pair_item, container, false);

        bonAppetitButton = (ImageButton) layout.findViewWithTag("bon_appetit_button");
        if (foodPair.stranger.isBonAppetit()) {
            bonAppetitButton.setImageResource(R.drawable.bonappetit2);
        }

        bonAppetitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStrangerShown && !foodPair.stranger.isBonAppetit()) {
                    bonAppetitButton.setImageResource(R.drawable.bonappetit2);
                    BonAppetitTask bonAppetitTask = new BonAppetitTask();
                    bonAppetitTask.setTaskResultListener(FoodPairFragment.this);
                    bonAppetitTask.execute(foodPair);
                }
            }
        });

        layout.setLayoutParams(getLayoutParams(layout.getContext().getResources().getConfiguration().orientation));

        foodImageSize = getFoodImageSize(layout.getContext().getResources().getConfiguration().orientation);
        final Animation[] rightToLeftAnimation = AnimationFactory.flipAnimation(foodImageSize, AnimationFactory.FlipDirection.LEFT_RIGHT, 600, null);
        final Animation[] leftToRightAnimation = AnimationFactory.flipAnimation(foodImageSize, AnimationFactory.FlipDirection.RIGHT_LEFT, 600, null);

        final ViewSwitcher viewSwitcher = (ViewSwitcher) layout.findViewWithTag("viewSwitcher");
        viewSwitcher.setOutAnimation(rightToLeftAnimation[0]);
        viewSwitcher.setInAnimation(rightToLeftAnimation[1]);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!animationInProgress) {
                    ObservableHorizontalScrollView scrollView = (ObservableHorizontalScrollView) viewSwitcher.findViewWithTag("strangerFood");
                    if (viewSwitcher.getCurrentView() != scrollView) {
                        viewSwitcher.showPrevious();
                        isStrangerShown = true;
                        if (foodPair.stranger.isBonAppetit()) {
                            bonAppetitButton.setImageResource(R.drawable.bonappetit2);
                        } else {
                            bonAppetitButton.setImageResource(R.drawable.bonappetit);
                        }
                    } else if (viewSwitcher.getCurrentView() == scrollView) {
                        viewSwitcher.showNext();
                        isStrangerShown = false;
                        if (foodPair.user.isBonAppetit()) {
                            bonAppetitButton.setImageResource(R.drawable.bonappetit2);
                        } else {
                            bonAppetitButton.setImageResource(R.drawable.bonappetit);
                        }
                    }
                }
            }
        };

        HorizontalScrollView.LayoutParams foodImagesLayout = new HorizontalScrollView.LayoutParams(foodImageSize, foodImageSize);
        strangerFoodImage = (FoodPicsLayout) layout.findViewWithTag("strangerImage");
        strangerFoodImage.setLayoutParams(foodImagesLayout);
        strangerFoodImage.setOnClickListener(onClickListener);
        strangerFoodImage.setImgSize(foodImageSize);
        userFoodImage = (FoodPicsLayout) layout.findViewWithTag("userImage");
        userFoodImage.setLayoutParams(foodImagesLayout);
        userFoodImage.setOnClickListener(onClickListener);
        userFoodImage.setImgSize(foodImageSize);
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
        rightToLeftAnimation[0].setAnimationListener(outAnimationListener);
        rightToLeftAnimation[1].setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewSwitcher.setOutAnimation(leftToRightAnimation[0]);
                viewSwitcher.setInAnimation(leftToRightAnimation[1]);
                animationInProgress = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        leftToRightAnimation[0].setAnimationListener(outAnimationListener);
        leftToRightAnimation[1].setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewSwitcher.setOutAnimation(rightToLeftAnimation[0]);
                viewSwitcher.setInAnimation(rightToLeftAnimation[1]);
                animationInProgress = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        DownloadFoodPicsTask downloadFoodPicsTask = new DownloadFoodPicsTask(this, layout.getContext());
        downloadFoodPicsTask.execute(foodPair);

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.FOOD_PAIR, foodPair);
    }

    @Override
    public void onTaskResult(int taskCode, long resultCode, HashMap<String, Object> data) {

        switch (taskCode) {
            case DownloadFoodPicsTask.TASK_ID:
                /*strangerFoodImage.add(FileUtil.getFoodPath(foodPair.stranger), foodImageSize);
                strangerFoodImage.add(FileUtil.getMapPath(foodPair.stranger), foodImageSize);
                userFoodImage.add(FileUtil.getFoodPath(foodPair.user), foodImageSize);
                userFoodImage.add(FileUtil.getMapPath(foodPair.user), foodImageSize);*/
                strangerFoodImage.setUser(foodPair.stranger);
                userFoodImage.setUser(foodPair.user);
                break;
            case BonAppetitTask.TASK_ID:
                FoodPair foodPair = (FoodPair) data.get(Constants.FOOD_PAIR);
                this.foodPair = foodPair;
                if (resultCode != BaseTask.RESULT_OK && isStrangerShown) {
                    bonAppetitButton.setImageResource(R.drawable.bonappetit);
                    Toast.makeText(MainActivity.context, R.string.photo_upload_failed, Toast.LENGTH_LONG);
                }
                break;
        }
    }

    private int getFoodImageSize(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return displayWidth / 2 - (Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_LEFT + Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_RIGHT);
        } else {
            return foodImageSize = displayWidth - Constants.FOOD_MARGIN_PORTRAIT;
        }
    }

    private LinearLayout.LayoutParams getLayoutParams(int orientation) {
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            linearParams.topMargin = Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_TOP;
            linearParams.leftMargin = Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_LEFT;
            linearParams.rightMargin = Constants.FOOD_MARGIN_LANDSCAPE_COLUMN_RIGHT;
        } else {
            linearParams.topMargin = Constants.FOOD_MARGIN_PORTRAIT_COLUMN_TOP;
            linearParams.leftMargin = Constants.FOOD_MARGIN_PORTRAIT_COLUMN_LEFT;
            linearParams.rightMargin = Constants.FOOD_MARGIN_PORTRAIT_COLUMN_RIGHT;
        }

        return linearParams;
    }

}
