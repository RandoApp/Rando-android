package com.github.randoapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import com.github.randoapp.R;


public class RandoLandingProgress extends View {
    private float outRad;
    private float floatRadius;
    private Bitmap randoBitmap;

    private float x;
    private float y;

    private float dx = 2;

    private boolean stopAnimation = false;

    public RandoLandingProgress(Context context, float radius) {
        super(context);
        this.outRad = radius == 0 ? 50 : radius - 8;

        this.floatRadius = (float) (outRad / 2.000);
        x = outRad - floatRadius;
        y = outRad - floatRadius;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        randoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher, options);

        post(animator);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(randoBitmap, x, y, null);
    }

    @Override
    public void clearAnimation() {
        super.clearAnimation();
        stopAnimation = true;
        cleanBitmap();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
    }

    private void cleanBitmap() {
        if (randoBitmap != null) {
            randoBitmap.recycle();
            randoBitmap = null;
        }
    }

    private final Runnable animator = new Runnable() {
        @Override
        public void run() {
            if (x <= 3*floatRadius){
                dx = 1;
            }
            x +=dx;
            y = (float) Math.sqrt((double) (floatRadius*floatRadius +
                    x*x));
            invalidate();
            if (!stopAnimation) {
                postDelayed(this, 50);
            }
        }
    };

}
