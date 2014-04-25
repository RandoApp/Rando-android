package com.github.randoapp.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.makeramen.RoundedImageView;

import com.github.randoapp.R;
import com.github.randoapp.log.Log;

public class RandoMapSwitcherAdapter extends PagerAdapter {

    private RandoPairsAdapter.ViewHolder.UserHolder holder;
    private View.OnClickListener imageListener;

    public RandoMapSwitcherAdapter(RandoPairsAdapter.ViewHolder.UserHolder holder) {
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
        RelativeLayout layout = new RelativeLayout(container.getContext());

        RoundedImageView imageView = new RoundedImageView(container.getContext());
        imageView.setOval(true);

        imageView.setPadding(0, 0, 0, 0);
        imageView.setOnClickListener(imageListener);

        if (position == 0) {
            addRandoToHolder(imageView);
        } else {
            addMapToHolder(imageView);
        }

        checkCache(position, imageView);

        layout.addView(imageView);

        View view = new View(container.getContext());
        view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        layout.addView(view);
        container.addView(layout, 0);

        return layout;
    }

    private void checkCache(int position, ImageView view) {
        if (holder.needSetPairing && holder.randoBitmap == null) {
            Log.d(RandoMapSwitcherAdapter.class, "Need set pairing");
            view.setImageResource(R.drawable.pair);
            holder.needSetPairing = false;
            return;
        }

        if (position == 0) {
            if (holder.needSetImageError && holder.randoBitmap == null) {
                Log.d(RandoMapSwitcherAdapter.class, "Need set food error");
                view.setImageResource(R.drawable.image_error);
                holder.needSetImageError = false;
            }
            if (holder.randoBitmap != null) {
                Log.d(RandoMapSwitcherAdapter.class, "Bitmap of food found in memory cache");
                view.setImageBitmap(holder.randoBitmap);
                holder.randoBitmap = null;
                holder.needSetImageError = false;
            }
        } else {
            if (holder.needSetMapError && holder.mapBitmap == null) {
                Log.d(RandoMapSwitcherAdapter.class, "Need set map error");
                view.setImageResource(R.drawable.map_error);
                holder.needSetMapError = false;
            }
            if (holder.mapBitmap != null) {
                Log.d(RandoMapSwitcherAdapter.class, "Bitmap of map found in memory cache");
                view.setImageBitmap(holder.mapBitmap);
                holder.mapBitmap = null;
                holder.needSetMapError = false;
            }
        }
    }

    public void addRandoToHolder(ImageView imageView) {
        Log.d(RandoMapSwitcherAdapter.class, "Set temporary rando image");
        imageView.setImageResource(R.drawable.image_wait);
        holder.randoImage = imageView;
    }

    public void addMapToHolder(ImageView imageView) {
        Log.d(RandoMapSwitcherAdapter.class, "Set temporary map image");
        imageView.setImageResource(R.drawable.map_wait);
        holder.mapImage = imageView;
    }

    public void recycle(ImageView foodImage, ImageView mapImage) {
        Log.d(RandoMapSwitcherAdapter.class, "Recycle");
        foodImage.setImageResource(R.drawable.image_wait);
        mapImage.setImageResource(R.drawable.map_wait);
    }
}
