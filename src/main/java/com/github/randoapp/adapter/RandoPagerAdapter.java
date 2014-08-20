package com.github.randoapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.randoapp.R;
import com.github.randoapp.log.Log;
import com.makeramen.RoundedImageView;


public abstract class RandoPagerAdapter extends PagerAdapter {

    protected RandoPairsAdapter.ViewHolder holder;
    private View.OnClickListener imageListener;
    private int listener;

    public RandoPagerAdapter(RandoPairsAdapter.ViewHolder holder, int imageSize) {
        super();
        this.holder = holder;
        this.listener = imageSize;
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
        return view == o;
    }

    protected abstract void OnInstantiateLeft(ImageView imageView);
    protected abstract void OnInstantiateRight(ImageView imageView);
    protected abstract void recycle();

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RelativeLayout layout = new RelativeLayout(container.getContext());

        RoundedImageView imageView = new RoundedImageView(container.getContext());
        imageView.setOval(true);

        imageView.setPadding(0, 0, 0, 0);
        imageView.setOnClickListener(imageListener);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(listener, listener));

        layout.addView(imageView);

        if (position == 0) {
            OnInstantiateLeft(imageView);
        } else {
            OnInstantiateRight(imageView);
        }

        if (isNeedPairing()) {
            setPairing(imageView);
        } else {
            checkCache(position, imageView);
        }

        container.addView(layout);
        return layout;
    }

    private boolean isNeedPairing() {
        return holder.needSetPairing;
    }

    private void setPairing(ImageView view) {
        Log.d(RandoPagerAdapter.class, "Set pairing");
        holder.needSetPairing = false;
        view.setImageResource(R.drawable.rando_pairing);
    }

    private void checkCache(int position, ImageView view) {
        if (position == 0) {
            if (isLeftBitmapInCache()) {
                setLeftBitmapFromCache(view);
            } else if (isNeedLeftError()) {
                setLeftError(view);
            }
        } else {
            if (isRightBitmapInCache()) {
                setRightBitmapFromCache(view);
            } else if (isNeedRightError()) {
                setRightError(view);
            }
        }
    }

    protected abstract boolean isNeedLeftError();
    protected abstract void setLeftError(ImageView view);

    protected abstract boolean isNeedRightError();
    protected abstract void setRightError(ImageView view);

    protected abstract boolean isLeftBitmapInCache();
    protected abstract void setLeftBitmapFromCache(ImageView view);

    protected abstract boolean isRightBitmapInCache();
    protected abstract void setRightBitmapFromCache(ImageView view);

}
