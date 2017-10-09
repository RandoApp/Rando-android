package com.github.randoapp.view;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.animation.Animation;

import com.github.randoapp.animation.AnimationFactory;
import com.github.randoapp.animation.OnAnimationEnd;


public class FlipImageView extends AppCompatImageView {

    private Animation[] leftToRightAnimation;

    public FlipImageView(Context context) {
        super(context);
    }

    public FlipImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FlipImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        leftToRightAnimation = AnimationFactory.flipAnimation(w, AnimationFactory.FlipDirection.LEFT_RIGHT, 150, null);
    }

    public void flipView(final int imageResource, final int backgroundResource, final OnAnimationEnd onAnimationEnd) {
        final Animation anim_out = leftToRightAnimation[0];
        final Animation anim_in = leftToRightAnimation[1];
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //Do nothing
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //Do nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setImageResource(imageResource);
                if (backgroundResource > 0) {
                    setBackgroundResource(backgroundResource);
                }
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //Do nothing
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        //Do nothing
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (onAnimationEnd != null) {
                            onAnimationEnd.onEnd();
                        }
                    }
                });
                startAnimation(anim_in);
            }
        });
        startAnimation(anim_out);
    }

}
