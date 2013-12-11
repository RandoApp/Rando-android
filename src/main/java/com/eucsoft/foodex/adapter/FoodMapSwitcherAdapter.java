package com.eucsoft.foodex.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.eucsoft.foodex.App;
import com.eucsoft.foodex.R;

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

        if (position == 0){
            //imageView.setImageUrl(holder., App.getInstance(container.getContext()).getRequestQueue());
            addFoodToHolder(imageView);
        } else {
            addMapToHolder(imageView);
        }

        checkCache(position, imageView);

        container.addView(imageView, 0);
        return imageView;
    }

    private void checkCache (int position, ImageView view) {
        if (position == 0) {
            if (holder.foodBitmap != null) {
                view.setImageBitmap(holder.foodBitmap);
                holder.foodBitmap = null;
            }
        } else {
            if (holder.mapBitmap != null) {
                view.setImageBitmap(holder.mapBitmap);
                holder.mapBitmap = null;
            }
        }
    }

    public void addFoodToHolder(ImageView imageView) {
        imageView.setImageResource(R.drawable.bonappetit2);
        holder.foodImage = imageView;
    }

    public void addMapToHolder(ImageView imageView) {
        imageView.setImageResource(R.drawable.bonappetit2);
        holder.mapImage = imageView;
    }

    public void recycle(ImageView foodImage, ImageView mapImage) {
        foodImage.setImageResource(R.drawable.bonappetit2);
        mapImage.setImageResource(R.drawable.bonappetit2);
    }
}
