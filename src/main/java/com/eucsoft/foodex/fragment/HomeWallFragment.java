package com.eucsoft.foodex.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.eucsoft.foodex.App;
import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.TakePictureActivity;
import com.eucsoft.foodex.adapter.FoodPairsAdapter;
import com.eucsoft.foodex.twowaygrid.async.AsyncTwoWayGridView;
import com.eucsoft.foodex.twowaygrid.async.FoodItemLoader;
import com.eucsoft.foodex.twowaygrid.async.ItemManager;

import uk.co.senab.bitmapcache.BitmapLruCache;


public class HomeWallFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.homeport, container, false);

        AsyncTwoWayGridView gridView = (AsyncTwoWayGridView) rootView.findViewById(R.id.main_grid);

        BitmapLruCache cache = App.getInstance(container.getContext()).getBitmapCache();
        FoodItemLoader loader = new FoodItemLoader(cache);

        ItemManager.Builder builder = new ItemManager.Builder(loader);
        builder.setPreloadItemsEnabled(true).setPreloadItemsCount(5);
        builder.setThreadPoolSize(4);
        gridView.setItemManager(builder.build());
        gridView.setAdapter(new FoodPairsAdapter(container.getContext()));

        int delta;
        ImageButton takePictureButton = (ImageButton) rootView.findViewById(R.id.cameraButton);
        int takePictureButtonHeight = takePictureButton.getHeight();

        if (container.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridView.setPadding(Constants.FOOD_PADDING_LANDSCAPE_COLUMN_LEFT, Constants.FOOD_PADDING_LANDSCAPE_COLUMN_TOP, Constants.FOOD_PADDING_LANDSCAPE_COLUMN_RIGHT, Constants.FOOD_PADDING_LANDSCAPE_COLUMN_BOTTOM);
            delta = takePictureButtonHeight;
            gridView.setNumColumns(2);
        } else {
            gridView.setNumColumns(1);
            gridView.setPadding(Constants.FOOD_PADDING_PORTRAIT_COLUMN_LEFT, Constants.FOOD_PADDING_PORTRAIT_COLUMN_TOP, Constants.FOOD_PADDING_PORTRAIT_COLUMN_RIGHT, Constants.FOOD_PADDING_PORTRAIT_COLUMN_BOTTOM);
            delta = takePictureButtonHeight - Constants.BON_APPETIT_BUTTON_SIZE;
        }
        container.setPadding(0, 0, 0, delta);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(rootView.getContext(), TakePictureActivity.class);
                startActivityForResult(intent, 100);
            }
        });
        return rootView;
    }
}
