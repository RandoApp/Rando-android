package com.github.randoapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.github.randoapp.R;

public class RoundProgress extends View {

    private Paint paint = new Paint();
    private float startAngle2 = 1;
    private int sweepAngle = -90;
    private RectF oval = new RectF();

    private float in_rad;
    private int currentColor;
    private int currentBackgroundColor;

    private int[] colors = {Color.parseColor("#AA68e4a2"), Color.parseColor("#AAdbd663")};
    private int currentColorId = 0;

    private boolean stopAnimation = false;

    public RoundProgress(Context context, float radius) {
        super(context);
        paint.setStyle(Paint.Style.STROKE);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        this.in_rad = radius == 0 ? 50 : radius - 8;
        currentColor = colors[currentColorId];
        currentBackgroundColor = Color.parseColor("#66dce0df");
        paint.setStrokeWidth((int) (getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_left) * 1.4));
        post(animator);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);
        oval.set(getWidth() / 2 - in_rad, getHeight() / 2 - in_rad, getWidth() / 2 + in_rad, getHeight() / 2 + in_rad);
        paint.setColor(currentBackgroundColor);
        canvas.drawArc(oval, startAngle2 + sweepAngle, 360 - startAngle2, false, paint);
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
                startAngle2 += 0.5;
            } else {
                startAngle2 = 0;
                currentBackgroundColor = colors[currentColorId];
                currentColorId = (currentColorId + 1) % colors.length;
                currentColor = colors[currentColorId];
            }
            invalidate();
            if (!stopAnimation) {
                postDelayed(this, 50);
            }
        }
    };

}
