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

    private Bitmap initPaints(int size, int center) {
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
        int size = Math.min(width,height);
        int center = size / 2;
        int biggerSize = Math.max(width,height);

        Paint black = new Paint();
        black.setColor(Color.BLACK);

        Bitmap bitmap = initPaints(size, center);
        if (height > width) {
            canvas.drawBitmap(bitmap, 0, biggerSize / 2 - center, null);
            canvas.drawRect(0, biggerSize / 2 + center, size, biggerSize, black);
            canvas.drawRect(0, 0, size, biggerSize / 2 - center, black);
        } else {
            canvas.drawBitmap(bitmap, biggerSize / 2 - center, 0, null);
            canvas.drawRect(biggerSize / 2 + center, 0, biggerSize, size, black);
            canvas.drawRect(0, 0, biggerSize / 2 - center, size, black);
        }
        bitmap.recycle();
        bitmap = null;
    }
}
