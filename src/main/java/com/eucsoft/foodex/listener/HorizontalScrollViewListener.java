package com.eucsoft.foodex.listener;

import com.eucsoft.foodex.view.ObservableHorizontalScrollView;

public interface HorizontalScrollViewListener {
    void onScrollChanged(ObservableHorizontalScrollView scrollView, int x, int y, int oldx, int oldy);
}
