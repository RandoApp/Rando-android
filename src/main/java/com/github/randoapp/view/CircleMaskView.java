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

    private Bitmap bitmap;


    public CircleMaskView(Context context) {
        super(context);
    }

    public CircleMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initMask(int height, int width) {

        Paint whiteStroke = new Paint();
        whiteStroke.setColor(Color.WHITE);
        whiteStroke.setStrokeWidth(1);

        Paint black = new Paint();
        black.setColor(Color.BLACK);

        Paint eraser = new Paint(Paint.ANTI_ALIAS_FLAG);
        eraser.setAlpha(127);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        int center = width / 2;
        int margin = getContext().getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_left);
        int radius = center - margin;

        bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas mask = new Canvas(bitmap);

        mask.drawColor(Color.BLACK);
        mask.drawCircle(width / 2, center, radius, eraser);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        if (bitmap == null) {
            initMask(height, width);
        }

        //setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }


    public void recycle() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}
