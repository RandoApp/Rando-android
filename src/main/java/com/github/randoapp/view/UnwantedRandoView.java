package com.github.randoapp.view;

import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.github.randoapp.R;
import com.makeramen.roundedimageview.RoundedImageView;


public class UnwantedRandoView extends RelativeLayout {

    private final Animation animation;
    private boolean stopAnimation = false;

    public UnwantedRandoView(Context context) {
        super(context);
        RoundedImageView roundedImageView = new RoundedImageView(context);
        roundedImageView.setImageResource(R.drawable.ic_boring);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        roundedImageView.setLayoutParams(layoutParams);
        roundedImageView.setOval(true);
        addView(roundedImageView);
        animation = AnimationUtils.loadAnimation(context, R.anim.show_hide_infinity);
        startAnimation(animation);
    }

    @Override
    protected void onDetachedFromWindow() {
        clearAnimation();
        stopAnimation = true;
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (stopAnimation) {
            stopAnimation = false;
            startAnimation(animation);
        }
    }
}
