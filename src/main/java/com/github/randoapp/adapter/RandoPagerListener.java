package com.github.randoapp.adapter;

import android.support.v4.view.ViewPager;
import android.view.View;

public class RandoPagerListener implements ViewPager.OnPageChangeListener {

    private RandoPairsAdapter.ViewHolder holder;

    public RandoPagerListener(RandoPairsAdapter.ViewHolder holder) {
        this.holder = holder;
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        holder.homeIcon.setDisplayedChild(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
