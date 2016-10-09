package com.github.randoapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.github.randoapp.R;

public class CircleMaskView extends View {


    public CircleMaskView(Context context) {
        super(context);
    }

    public CircleMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Bitmap initPaints(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.TRANSPARENT);

        int radius = Math.min(width, height) / 2;
        int heightCenter = height / 2;

        Paint eraser = new Paint();
        eraser.setColor(0xFFFFFFFF);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);
        canvas.drawCircle(radius, heightCenter, radius - getContext().getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_left), eraser);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = initPaints(canvas.getWidth(), canvas.getHeight());
        canvas.drawBitmap(bitmap, 0, 0, null);
        bitmap.recycle();
        bitmap = null;
    }
}
