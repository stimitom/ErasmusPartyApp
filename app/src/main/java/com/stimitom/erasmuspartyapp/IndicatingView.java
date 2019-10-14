package com.stimitom.erasmuspartyapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class IndicatingView extends View {
    public static final int NOTEXECUTED = 0;
    public static final int SUCCESS = 1;
    public static final int FAILED = 2;
    public static final int DRAWLOGO = 3;

    int state = NOTEXECUTED;

    public IndicatingView(Context context) {
        super(context);
    }

    public IndicatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndicatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        Paint paint;
        switch (state) {
            case SUCCESS:
                paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(20f);
                canvas.drawLine(0, height, width / 2, 0, paint);
                canvas.drawLine(width/2, 0, width, height, paint);
                canvas.drawLine(width, height, 0, height, paint);
                break;
            case FAILED:
                paint = new Paint();
                paint.setColor(Color.RED);
                paint.setStrokeWidth(20f);
                canvas.drawLine(0, 0, width, height, paint);
                canvas.drawLine(0, height, width, 0, paint);
                break;

            case DRAWLOGO:
                paint = new Paint();
                paint.setColor(getResources().getColor(R.color.colorTextOnPrimary));
                paint.setStrokeWidth(40f);

                //E
                //Long E stroke
                canvas.drawLine(width/2,height,0,height/2,paint);
                //Upper E stroke
                canvas.drawLine(0,height/2,width/3,height/3,paint);
                //Middle E stroke
                canvas.drawLine(width/5,2*(height/3),width/2,height/2,paint);

                //P
                //Long P stroke
                canvas.drawLine(width/2,height,width/2,height/5,paint);
                //Upper P stroke (left to right)
                canvas.drawLine(width/2,height/5,2*(width/3),height/5,paint);
                //Short P stroke (from top to bottom)
                canvas.drawLine(2*(width/3),height/5,2*(width/3),2*(height/5),paint);
                //Lower P stroke
                canvas.drawLine(2*(width/3),2*(height/5),width/2,2*(height/5),paint);

                //A
                //Left long A stroke
                canvas.drawLine(width/2,height,7*(width/8),1*(height/8),paint);
                //Middle  A stroke
                canvas.drawLine(2*(width/3),3*(height/5),11*(width/12),3*(height/5),paint);
                //Right long A stroke
                canvas.drawLine(7*(width/8),1*(height/8),width,height,paint);
                break;
            default:
                break;
        }
    }
}
