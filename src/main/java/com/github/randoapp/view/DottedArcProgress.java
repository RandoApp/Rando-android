package com.github.randoapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.github.randoapp.R;

public class DottedArcProgress extends View {

    private Paint paint=new Paint();
    private int startAngle=120;
    private int startAngle2=240;
    private RectF oval=new RectF();
    private int sweepAngle=100;

    private float out_rad;
    private int colorArc;

    public DottedArcProgress(Context context, float radius) {
        super(context);
        paint.setStyle(Paint.Style.STROKE);
        this.out_rad= radius==0 ? 50 : radius;
        this.colorArc = Color.parseColor("#5C6BC0");
       post(animator);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((int)(getResources().getDimensionPixelSize(R.dimen.rando_padding_portrait_column_left) * 0.6));
        oval.set(getWidth()/2-out_rad,getHeight()/2-out_rad,getWidth()/2+out_rad,getHeight()/2+out_rad);
        paint.setColor(getResources().getColor(R.color.dark_gray_button_background_normal));
        canvas.drawArc(oval,0,360,false,paint);
        paint.setColor(colorArc);
        canvas.drawArc(oval,startAngle2,sweepAngle,false,paint);
    }
    Runnable animator=new Runnable() {
        @Override
        public void run() {
            if(startAngle2>=1){
                startAngle2-=15;
            }
            else{
                startAngle2=360;
            }
            invalidate();
            postDelayed(this,100);
        }
    };

}
