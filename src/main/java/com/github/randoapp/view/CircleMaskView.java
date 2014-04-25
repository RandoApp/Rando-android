package com.github.randoapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class CircleMaskView extends View {

    private Paint backGroundPaint;
    private Paint circlePaint;
    private int circleSize;

    public CircleMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        backGroundPaint = new Paint();
        backGroundPaint.setColor(Color.BLACK);

        circlePaint = new Paint();
        circlePaint.setAlpha(255);
        circlePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        circleSize = Math.min(display.getHeight(), display.getWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), backGroundPaint);
        canvas.drawOval(new RectF(0,0, circleSize, circleSize), circlePaint);
    }
}
