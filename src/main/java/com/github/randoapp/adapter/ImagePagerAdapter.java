package com.github.randoapp.adapter;

import android.widget.ImageView;

import com.github.randoapp.R;
import com.github.randoapp.log.Log;

import static com.github.randoapp.adapter.RandoPairsAdapter.ViewHolder;

public class ImagePagerAdapter extends RandoPagerAdapter {

    public ImagePagerAdapter(ViewHolder holder, int imageSize) {
        super(holder, imageSize);
    }

    @Override
    protected void OnInstantiateLeft(ImageView imageView) {
        Log.d(ImagePagerAdapter.class, "Set stranger image to loading state");
        imageView.setImageResource(R.drawable.rando_loading);
        holder.stranger.image = imageView;
    }

    @Override
    protected void OnInstantiateRight(ImageView imageView) {
        Log.d(ImagePagerAdapter.class, "Set user image to loading state");
        imageView.setImageResource(R.drawable.rando_loading);
        holder.user.image = imageView;
    }

    @Override
    protected void recycle() {
        Log.d(ImagePagerAdapter.class, "Recycle stranger and user images");
        if (holder.stranger.image != null) {
            holder.stranger.image.setImageResource(R.drawable.rando_loading);
        }
        if (holder.user.image != null) {
            holder.user.image.setImageResource(R.drawable.rando_loading);
        }
    }

    @Override
    protected boolean isNeedLeftError() {
        return holder.stranger.needSetImageError;
    }

    @Override
    protected void setLeftError(ImageView view) {
        Log.d(ImagePagerAdapter.class, "Need set stranger image error");
        view.setImageResource(R.drawable.rando_error);
        holder.stranger.needSetImageError = false;
    }

    @Override
    protected boolean isNeedRightError() {
        return holder.user.needSetImageError;
    }

    @Override
    protected void setRightError(ImageView view) {
        Log.d(ImagePagerAdapter.class, "Need set user image error");
        view.setImageResource(R.drawable.rando_error);
        holder.user.needSetImageError = false;
    }

    @Override
    protected boolean isLeftBitmapInCache() {
        return holder.stranger.imageBitmap != null;
    }

    @Override
    protected void setLeftBitmapFromCache(ImageView view) {
        Log.d(ImagePagerAdapter.class, "Bitmap of stranger image found in memory cache");
        view.setImageBitmap(holder.stranger.imageBitmap);
        holder.stranger.imageBitmap = null;
        holder.stranger.needSetImageError = false;
    }

    @Override
    protected boolean isRightBitmapInCache() {
        return holder.user.imageBitmap != null;
    }

    @Override
    protected void setRightBitmapFromCache(ImageView view) {
        Log.d(ImagePagerAdapter.class, "Bitmap of user image found in memory cache");
        view.setImageBitmap(holder.user.imageBitmap);
        holder.user.imageBitmap = null;
        holder.user.needSetImageError = false;
    }

}
