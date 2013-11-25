package com.eucsoft.foodex.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.eucsoft.foodex.db.model.FoodPair;
import com.eucsoft.foodex.twowaygrid.async.FoodItemLoader;

public class FoodMapPager extends ViewPager {

    private OnClickListener onClickListener;
    //private FoodPairAdapter foodPairAdapter;
    private FoodItemLoader foodItemLoader;

    public FoodMapPager(Context context) {
        this(context, null);
        initAdapter();
    }

    public FoodMapPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAdapter();
    }

    public void setUser(FoodPair.User user) {
        //foodPairAdapter.setUser(user);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private void initAdapter() {
    }
}