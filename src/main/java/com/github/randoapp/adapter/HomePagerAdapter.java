package com.github.randoapp.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.github.randoapp.Constants;
import com.github.randoapp.fragment.HomeListFragment;
import com.github.randoapp.fragment.HomeMenuFragment;

public class HomePagerAdapter extends FragmentStatePagerAdapter {

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment;

        if (position == 0){
            fragment = new HomeMenuFragment();
        } else {
            fragment = new HomeListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.PAGE, position);
            fragment.setArguments(bundle);
        }
        return fragment;
    }
}
