package com.github.randoapp.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.github.randoapp.Constants;
import com.github.randoapp.db.model.Rando;
import com.github.randoapp.fragment.HomeListFragment;
import com.github.randoapp.fragment.HomeMenuFragment;

public class HomePagerAdapter extends FragmentStatePagerAdapter {

    private Rando scrollToRando;

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

        if (position == 0) {
            fragment = new HomeMenuFragment();
        } else {
            fragment = new HomeListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.PAGE, position);
            if (scrollToRando != null)
                if (scrollToRando.status == Rando.Status.IN && position == 1) {
                    bundle.putSerializable(Constants.RANDO_PARAM, scrollToRando);
                } else if (scrollToRando.status == Rando.Status.OUT && position == 2) {
                    bundle.putSerializable(Constants.RANDO_PARAM, scrollToRando);
                }
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    public void setScrollToRando(Rando scrollToRando) {
        this.scrollToRando = scrollToRando;
    }
}
