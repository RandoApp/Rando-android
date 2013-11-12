package com.eucsoft.foodex.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.eucsoft.foodex.Constants;
import com.eucsoft.foodex.R;
import com.eucsoft.foodex.listener.ScrollViewListener;

public class ObservableScrollView extends ScrollView {

    private ScrollViewListener scrollViewListener = null;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public void addFinalBlock(int orientation) {
        int delta;
        RelativeLayout relativeLayout = (RelativeLayout) this.getParent();
        int takePictureButtonHeight = (relativeLayout.findViewById(R.id.cameraButton)).getHeight();
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            delta = takePictureButtonHeight;
        } else {
            delta = takePictureButtonHeight - Constants.BON_APPETIT_BUTTON_SIZE;
        }
        LinearLayout foodContainer = (LinearLayout) this.findViewById(R.id.foodContainer);
        View finalBlock = new View(this.getContext());
        ObservableScrollView.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, delta);
        finalBlock.setLayoutParams(layoutParams);
        foodContainer.addView(finalBlock);
    }
}