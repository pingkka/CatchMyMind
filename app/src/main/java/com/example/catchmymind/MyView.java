package com.example.catchmymind;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View {
    private Paint paint = new Paint();
    //private Canvas canvas;

    private Path path = new Path();

    private int x, y;

    private int penColor = Color.BLACK;
    private float size = 10;

    public MyView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(penColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(size);

        canvas.drawPath(path, paint);
    }

    public void setPaintInfo(int color, float size) {
        Log.d("penSetting : ", color + "+" + size);
        penColor = color;
        this.size = size;

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = (int) event.getX();
        y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                x = (int) event.getX();
                y = (int) event.getY();

                path.lineTo(x, y);
                break;
        }

        invalidate();

        return true;
    }
}
