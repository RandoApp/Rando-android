package com.eucsoft.foodex.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eucsoft.foodex.R;

public class FoodMapSwitcherAdapter extends PagerAdapter {

    FoodPairsAdapter.ViewHolder holder;

    public FoodMapSwithcherAdapter(FoodPairsAdapter.ViewHolder holder) {
        this.holder = holder;
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
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if (position == 0){
            imageView.setImageResource(R.drawable.f);
            holder.foodImage = imageView;
        } else {
            imageView.setImageResource(R.drawable.f);
            holder.mapImage = imageView;
        }

        container.addView(imageView, 0);
        return imageView;
    }

}
