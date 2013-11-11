package com.eucsoft.foodex.listener;

import com.eucsoft.foodex.view.ObservableHorizontalScrollView;

/**
 * Created by xp-vit on 11/11/13.
 */
public interface HorizontalScrollViewListener {

    void onScrollChanged(ObservableHorizontalScrollView scrollView, int x, int y, int oldx, int oldy);

}
