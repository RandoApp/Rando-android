package com.github.randoapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.github.randoapp.fragment.HomeEmptyFragment;
import com.github.randoapp.fragment.HomeMenuFragment;

public class EmptyHomePagerAdapter extends FragmentStatePagerAdapter {

    public EmptyHomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment;

        if (position == 0){
            fragment = new HomeMenuFragment();
        } else {
            fragment = new HomeEmptyFragment();
        }
        return fragment;
    }
}
