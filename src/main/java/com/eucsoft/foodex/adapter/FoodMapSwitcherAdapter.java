package com.eucsoft.foodex.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eucsoft.foodex.R;
import com.eucsoft.foodex.log.Log;

public class FoodMapSwitcherAdapter extends PagerAdapter {

    private FoodPairsAdapter.ViewHolder.UserHolder holder;
    private View.OnClickListener imageListener;

    public FoodMapSwitcherAdapter(FoodPairsAdapter.ViewHolder.UserHolder holder) {
        this.holder = holder;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        imageListener = listener;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return  view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setPadding(0, 0, 0, 0);
        imageView.setOnClickListener(imageListener);

        if (position == 0) {
            addFoodToHolder(imageView);
        } else {
            addMapToHolder(imageView);
        }

        checkCache(position, imageView);
        container.addView(imageView, 0);

        return imageView;
    }

    private void checkCache(int position, ImageView view) {
        if (position == 0) {
            if (holder.needSetFoodError && holder.foodBitmap == null) {
                Log.d(FoodMapSwitcherAdapter.class, "Need set food error");
                view.setImageResource(R.drawable.food_error);
                holder.needSetFoodError = false;
            }
            if (holder.foodBitmap != null) {
                Log.d(FoodMapSwitcherAdapter.class, "Bitmap of food found in memory cache");
                view.setImageBitmap(holder.foodBitmap);
                holder.foodBitmap = null;
                holder.needSetFoodError = false;
            }
        } else {
            if (holder.needSetMapError && holder.mapBitmap == null) {
                Log.d(FoodMapSwitcherAdapter.class, "Need set map error");
                view.setImageResource(R.drawable.map_error);
                holder.needSetMapError = false;
            }
            if (holder.mapBitmap != null) {
                Log.d(FoodMapSwitcherAdapter.class, "Bitmap of map found in memory cache");
                view.setImageBitmap(holder.mapBitmap);
                holder.mapBitmap = null;
                holder.needSetMapError = false;
            }
        }
    }

    public void addFoodToHolder(ImageView imageView) {
        Log.d(FoodMapSwitcherAdapter.class, "Set temporary food image");
        imageView.setImageResource(R.drawable.food_wait);
        holder.foodImage = imageView;
    }

    public void addMapToHolder(ImageView imageView) {
        Log.d(FoodMapSwitcherAdapter.class, "Set temporary map image");
        imageView.setImageResource(R.drawable.map_wait);
        holder.mapImage = imageView;
    }

    public void recycle(ImageView foodImage, ImageView mapImage) {
        Log.d(FoodMapSwitcherAdapter.class, "Recycle");
        foodImage.setImageResource(R.drawable.food_wait);
        mapImage.setImageResource(R.drawable.map_wait);
    }
}
