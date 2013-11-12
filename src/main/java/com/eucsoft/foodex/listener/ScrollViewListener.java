package com.eucsoft.foodex.listener;

import com.eucsoft.foodex.view.ObservableScrollView;

public interface ScrollViewListener {

    void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);

}