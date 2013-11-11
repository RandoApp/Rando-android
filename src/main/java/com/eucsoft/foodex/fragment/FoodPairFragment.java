package com.eucsoft.foodex.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.R;
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
    private FoodPicsLayout foodImage;

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

        ImageButton bonAppetitButton = (ImageButton) layout.findViewWithTag("bon_appetit_button");

        bonAppetitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
        HorizontalScrollView.LayoutParams layoutParams = new HorizontalScrollView.LayoutParams(foodImageSize, foodImageSize);
        foodImage = (FoodPicsLayout) layout.findViewWithTag("image");
        foodImage.setLayoutParams(layoutParams);
        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                FoodPairFragment foodPairFragment = FoodPairFragment.newInstance(foodPair, !showStranger, mapShown);
                transaction.setCustomAnimations(R.anim.food_flip_out, R.anim.food_flip_in);
                transaction.replace((int) foodPair.id, foodPairFragment);
                transaction.commit();
            }
        });

        DownloadFoodPicsTask downloadFoodPicsTask = new DownloadFoodPicsTask(this, layout.getContext());
        downloadFoodPicsTask.execute(foodPair);

        final ObservableHorizontalScrollView scrollView = (ObservableHorizontalScrollView) layout.findViewWithTag("scroll");
        scrollView.setScrollViewListener(this);
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

        if (showStranger) {
            foodImage.add(FileUtil.getFoodPath(foodPair.stranger), foodImageSize);
            foodImage.add(FileUtil.getMapPath(foodPair.stranger), foodImageSize);
        } else {
            foodImage.add(FileUtil.getFoodPath(foodPair.user), foodImageSize);
            foodImage.add(FileUtil.getMapPath(foodPair.user), foodImageSize);
        }
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

