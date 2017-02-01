package com.github.randoapp.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

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

    private Bitmap initPaints(int width, int height, int center) {
        int size = Math.min(width,height);
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ALPHA_8);

        int radius = center - getContext().getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_left);

        Paint eraser = new Paint();
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK);
        canvas.drawCircle(center, center, radius, eraser);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int center = Math.min(width, height) / 2;

        Paint black = new Paint();
        black.setColor(Color.BLACK);

        Bitmap bitmap = initPaints(width, height, center);
        canvas.drawBitmap(bitmap, 0, height / 2 - center, null);
        canvas.drawRect(0, height / 2 + center, width, height, black);
        canvas.drawRect(0, 0, width, height / 2 - center, black);
        bitmap.recycle();
        bitmap = null;
    }
}
