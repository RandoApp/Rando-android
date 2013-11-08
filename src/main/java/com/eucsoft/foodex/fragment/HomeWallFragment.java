package com.eucsoft.foodex.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.eucsoft.foodex.MainActivity;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.TakePictureActivity;
import com.eucsoft.foodex.db.FoodDAO;
import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.listener.ScrollViewListener;
import com.eucsoft.foodex.view.FoodView;
import com.eucsoft.foodex.view.ObservableScrollView;

import java.util.List;

public class HomeWallFragment extends Fragment implements ScrollViewListener {

    private int currentPage = 0;
    private int totalPages;
    private int lastScrollPos = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(FoodView.getLayoutFragmentResource(container.getContext()), container, false);

        FoodDAO foodDAO = new FoodDAO(container.getContext());
        List<FoodPair> foods = foodDAO.getFoodPairsForPage(currentPage);
        totalPages = foodDAO.getPagesNumber();
        foodDAO.close();

        for (FoodPair foodPair : foods) {
            new FoodView(rootView, foodPair).display();
        }
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

        int orienatation = scrollView.getContext().getResources().getConfiguration().orientation;

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
            for (FoodPair foodPair : foods) {
                new FoodView(scrollView, foodPair).display();
            }
            if (currentPage + 1 == totalPages) {
                scrollView.addFinalBlock(orienatation);
            }
        }
    }
}
