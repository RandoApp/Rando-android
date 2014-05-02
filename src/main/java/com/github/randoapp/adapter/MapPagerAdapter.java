package com.github.randoapp.adapter;

import android.widget.ImageView;

import com.github.randoapp.R;
import com.github.randoapp.log.Log;

public class MapPagerAdapter extends RandoPagerAdapter {

    public MapPagerAdapter(RandoPairsAdapter.ViewHolder holder, int imageSize) {
        super(holder, imageSize);
    }

    @Override
    protected void OnInstantiateLeft(ImageView imageView) {
        Log.d(MapPagerAdapter.class, "Set stranger map to loading state");
        imageView.setImageResource(R.drawable.rando_loading);
        holder.stranger.map = imageView;
    }

    @Override
    protected void OnInstantiateRight(ImageView imageView) {
        Log.d(MapPagerAdapter.class, "Set user map to loading state");
        imageView.setImageResource(R.drawable.rando_loading);
        holder.user.map = imageView;
    }

    @Override
    protected void recycle() {
        Log.d(MapPagerAdapter.class, "Recycle stranger and user maps");
        if (holder.stranger.map != null) {
            holder.stranger.map.setImageResource(R.drawable.rando_loading);
        }
        if (holder.user.map != null) {
            holder.user.map.setImageResource(R.drawable.rando_loading);
        }
    }

    @Override
    protected boolean isNeedLeftError() {
        return holder.stranger.needSetMapError;
    }

    @Override
    protected void setLeftError(ImageView view) {
        Log.d(ImagePagerAdapter.class, "Need set stranger map error");
        view.setImageResource(R.drawable.rando_error);
        holder.stranger.needSetMapError = false;
    }

    @Override
    protected boolean isNeedRightError() {
        return holder.user.needSetMapError;
    }

    @Override
    protected void setRightError(ImageView view) {
        Log.d(ImagePagerAdapter.class, "Need set user map error");
        view.setImageResource(R.drawable.rando_error);
        holder.user.needSetMapError = false;
    }

    @Override
    protected boolean isLeftBitmapInCache() {
        return holder.stranger.mapBitmap != null;
    }

    @Override
    protected void setLeftBitmapFromCache(ImageView view) {
        Log.d(ImagePagerAdapter.class, "Bitmap of stranger map found in memory cache");
        view.setImageBitmap(holder.stranger.mapBitmap);
        holder.stranger.mapBitmap = null;
        holder.stranger.needSetMapError = false;
    }

    @Override
    protected boolean isRightBitmapInCache() {
        return holder.user.mapBitmap != null;
    }

    @Override
    protected void setRightBitmapFromCache(ImageView view) {
        Log.d(ImagePagerAdapter.class, "Bitmap of user map found in memory cache");
        view.setImageBitmap(holder.user.mapBitmap);
        holder.user.mapBitmap = null;
        holder.user.needSetMapError = false;
    }

}
