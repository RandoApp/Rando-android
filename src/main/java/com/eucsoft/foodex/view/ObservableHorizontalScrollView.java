package com.eucsoft.foodex.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import com.eucsoft.foodex.listener.HorizontalScrollViewListener;

public class ObservableHorizontalScrollView extends HorizontalScrollView {

    private HorizontalScrollViewListener scrollViewListener = null;

    private Runnable scrollerTask;
    private int initialPosition;

    private int newCheck = 300;
    private static final String TAG = "MyScrollView";

    public ObservableHorizontalScrollView(Context context) {
        this(context, null);
    }

    public ObservableHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initScrollerTask();
    }

    public ObservableHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initScrollerTask();
    }

    public void setScrollViewListener(HorizontalScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    private void initScrollerTask() {
        scrollerTask = new Runnable() {
            public void run() {

                int newPosition = getScrollX();
                if (initialPosition - newPosition == 0) {//has stopped

                    if (scrollViewListener != null) {

                        scrollViewListener.onScrollStopped(ObservableHorizontalScrollView.this);
                    }
                } else {
                    initialPosition = getScrollY();
                    ObservableHorizontalScrollView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
    }

    public void startScrollerTask() {
        initialPosition = getScrollX();
        postDelayed(scrollerTask, newCheck);
    }

}
