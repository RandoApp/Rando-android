package com.eucsoft.foodex.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.TakePictureActivity;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.ScrollViewListener;
import com.eucsoft.foodex.task.DownloadFoodPicsTask;
import com.eucsoft.foodex.util.FileUtil;
import com.eucsoft.foodex.view.FoodView;
import com.eucsoft.foodex.view.ObservableScrollView;

import java.util.List;

public class HomeWallFragment extends Fragment implements ScrollViewListener {

    private int currentPage = 0;
    private int totalPages;
    private int lastScrollPos = 0;
    private boolean odd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(FoodView.getLayoutFragmentResource(container.getContext()), container, false);

        FoodDAO foodDAO = new FoodDAO(container.getContext());
        List<FoodPair> foods = foodDAO.getFoodPairsForPage(currentPage);
        totalPages = foodDAO.getPagesNumber();
        foodDAO.close();

        resizeColumnsIfNeeded(rootView);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        for (FoodPair foodPair : foods) {
            insertFood(rootView, foodPair, fragmentTransaction);
        }
        fragmentTransaction.commit();
        ImageButton takePictureButton = (ImageButton) rootView.findViewById(R.id.cameraButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(rootView.getContext(), TakePictureActivity.class);
                startActivityForResult(intent, 100);
            }
        });
        ((ObservableScrollView) rootView.findViewById(R.id.scroll_view)).setScrollViewListener(this);
        return rootView;
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {

        WindowManager windowManager = (WindowManager) MainActivity.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int displayHeight = display.getHeight();

        int totalHeight = scrollView.getChildAt(0).getHeight();
        if (totalHeight - y < displayHeight + 1
                && currentPage < totalPages
                && (lastScrollPos == 0 || y - lastScrollPos > displayHeight)) {
            lastScrollPos = y;
            currentPage++;
            FoodDAO foodDAO = new FoodDAO(MainActivity.context);
            List<FoodPair> foods = foodDAO.getFoodPairsForPage(currentPage);
            foodDAO.close();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            for (FoodPair foodPair : foods) {
                insertFood(scrollView, foodPair, transaction);
            }
            transaction.commit();
            if (currentPage + 1 == totalPages) {
                scrollView.addFinalBlock(scrollView.getContext().getResources().getConfiguration().orientation);
            }
        }
    }

    private void insertFood(View rootView, FoodPair foodPair, FragmentTransaction transaction) {
        LinearLayout linearLayout = new LinearLayout(rootView.getContext());
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setId((int) foodPair.id);
        LinearLayout foodContainer;

        if (rootView.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            foodContainer = (LinearLayout) rootView.findViewById(R.id.foodContainer);
        } else {
            if (odd) {
                foodContainer = (LinearLayout) rootView.findViewById(R.id.column1);
                odd = false;
            } else {
                foodContainer = (LinearLayout) rootView.findViewById(R.id.column2);
                odd = true;
            }
        }
        foodContainer.addView(linearLayout);

        PortraitFoodFragment portraitFoodFragment = PortraitFoodFragment.newInstance(foodPair, true, false);
        transaction.add(linearLayout.getId(), portraitFoodFragment, "foodItem");

        if (!FileUtil.areFilesExist(foodPair)) {
            DownloadFoodPicsTask downloadFoodPicsTask = new DownloadFoodPicsTask(portraitFoodFragment, rootView.getContext());
            downloadFoodPicsTask.execute(foodPair);
        }
    }

    private void resizeColumnsIfNeeded(View container) {

        if (container.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            WindowManager windowManager = (WindowManager) container.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            int displayWidth = display.getWidth();

            LinearLayout column1 = (LinearLayout) container.findViewById(R.id.column1);
            LinearLayout column2 = (LinearLayout) container.findViewById(R.id.column2);

            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(displayWidth / 2, LinearLayout.LayoutParams.MATCH_PARENT);
            column1.setLayoutParams(linearParams);
            column2.setLayoutParams(linearParams);
        }
    }
}
