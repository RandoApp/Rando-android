package com.github.randoapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.github.randoapp.R;


public class RandoLandingProgress extends View{
    private final int colorArc2;
    private Paint paint = new Paint();
    private int startAngle2 = 1;
    private RectF oval = new RectF();
    private int sweepAngle = -90;

    private float out_rad;
    private int colorArc;
    private int currentColor;
    private int currentBackgroundColor;

    private boolean stopAnimation = false;

    public RandoLandingProgress(Context context, int radius) {
        super(context);
        paint.setStyle(Paint.Style.STROKE);
        this.out_rad = radius == 0 ? 50 : radius - 8;
        this.colorArc = Color.parseColor("#AA68e4a2");
        this.colorArc2 = Color.parseColor("#AAdbd663");
        currentColor = colorArc;
        currentBackgroundColor = Color.parseColor("#66dce0df");
        post(animator);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((int) (getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_left) * 1.4));
        oval.set(getWidth() / 2 - out_rad, getHeight() / 2 - out_rad, getWidth() / 2 + out_rad, getHeight() / 2 + out_rad);
        paint.setColor(currentBackgroundColor);
        canvas.drawArc(oval, 0, 360, false, paint);
        paint.setColor(currentColor);
        canvas.drawArc(oval, sweepAngle, startAngle2, false, paint);
    }

    @Override
    public void clearAnimation() {
        super.clearAnimation();
        stopAnimation = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation = true;
    }

    private final Runnable animator = new Runnable() {
        @Override
        public void run() {
            if (startAngle2 < 360) {
                startAngle2 += 1;
            } else {
                startAngle2 = 0;
                if (currentColor == colorArc) {
                    currentColor = colorArc2;
                    currentBackgroundColor = colorArc;
                } else {
                    currentColor = colorArc;
                    currentBackgroundColor = colorArc2;
                }
            }
            invalidate();
            if (!stopAnimation) {
                postDelayed(this, 100);
            }
        }
    };

}
