package com.github.randoapp.animation;

import android.view.animation.Interpolator;

public class LinearWithPauseInterpolator implements Interpolator {

    private float pause;
    private float cycleDuration;
    private float pausesCount;


    public LinearWithPauseInterpolator(float pause, float cycleDuration, float pausesCount) {
        this.pause = pause;
        this.cycleDuration = cycleDuration;
        this.pausesCount = pausesCount;
    }

    @Override
    public float getInterpolation(float t) {
        float d = ((t * cycleDuration*pausesCount) % cycleDuration);
        if (d < pause){
            float nt = d/pause;
            return (float)(Math.sin(2 * 1 * Math.PI * nt));
        } else {
            return 1;
        }
    }
}
