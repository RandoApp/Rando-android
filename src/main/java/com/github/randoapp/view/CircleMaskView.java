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

    private boolean drawGrid = true;
    private int numColumns = 3;
    private int numRows = 3;

    public CircleMaskView(Context context) {
        super(context);
    }

    public CircleMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public void setDrawGrid(boolean drawGrid) {
        this.drawGrid = drawGrid;
    }

    private void initGridLines(Canvas canvas, int radius, int xOffset, int yOffset) {
        float width = radius * 2;
        float height = radius * 2;
        float cellWidth = width / 3;
        float cellHeight = height / 3;

        float lineLengthDelta = (float) (radius - Math.sqrt(radius * radius - cellWidth * cellWidth / 4));

        Paint gray = new Paint();
        gray.setColor(Color.WHITE);
        gray.setStrokeWidth(1);


        for (int i = 1; i < numColumns; i++) {
            canvas.drawLine(i * cellWidth + xOffset, 0 + yOffset + lineLengthDelta, i * cellWidth + xOffset, height + yOffset - lineLengthDelta, gray);
        }

        for (int i = 1; i < numRows; i++) {
            canvas.drawLine(0 + xOffset + lineLengthDelta, i * cellHeight + yOffset, width + xOffset - lineLengthDelta, i * cellHeight + yOffset, gray);
        }
    }

    private Bitmap initPaints(int size, int center, int radius) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ALPHA_8);

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
        int size = Math.min(width, height);
        int center = size / 2;
        int biggerSize = Math.max(width, height);

        Paint black = new Paint();
        black.setColor(Color.BLACK);

        Paint bitmapPaint = new Paint();
        bitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        int margin = getContext().getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_left);
        int radius = center - margin;

        Bitmap bitmap = initPaints(size, center, radius);
        if (height > width) {
            canvas.drawBitmap(bitmap, 0, biggerSize / 2 - center, bitmapPaint);
            canvas.drawRect(0, biggerSize / 2 + center, size, biggerSize, black);
            canvas.drawRect(0, 0, size, biggerSize / 2 - center, black);
            if (drawGrid) {
                initGridLines(canvas, radius, margin, margin + biggerSize / 2 - center);
            }
        } else {
            canvas.drawBitmap(bitmap, biggerSize / 2 - center, 0, bitmapPaint);
            canvas.drawRect(biggerSize / 2 + center, 0, biggerSize, size, black);
            canvas.drawRect(0, 0, biggerSize / 2 - center, size, black);
        }
        bitmap.recycle();
        bitmap = null;
    }
}
