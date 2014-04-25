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

    private Paint blackBackgroundPaint;
    private Paint circlePaint;
    private int circleSize;

    public CircleMaskView(Context context) {
        super(context);
        initPaints(context);
    }

    public CircleMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints(context);
    }

    public CircleMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints(context);
    }

    private void initPaints (Context context) {
        blackBackgroundPaint = new Paint();
        blackBackgroundPaint.setColor(Color.BLACK);

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
        canvas.drawRect(0, 0, getWidth(), getHeight(), blackBackgroundPaint);
        canvas.drawOval(new RectF(0,0, circleSize, circleSize), circlePaint);
    }
}
