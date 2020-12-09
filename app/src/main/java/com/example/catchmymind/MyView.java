package com.example.catchmymind;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MyView extends View implements SocketInterface {

    public boolean changed = false;

    Canvas mCanvas;
    Bitmap mBitmap;
    Paint mPaint;

    float lastX;
    float lastY;

    Path mPath = new Path();

    float mCurveEndX;
    float mCurveEndY;

    int mInvalidateExtraBorder = 10;

    static final float TOUCH_TOLERANCE = 8;

    private String userName;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String roomId;

    public MyView(Context context, String userName, ObjectOutputStream oos, ObjectInputStream ois) {
        super(context);
        init(context, userName, oos, ois);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, userName, oos, ois);
    }

    private void init(Context context, String userName, ObjectOutputStream oos, ObjectInputStream ois) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);

        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3.0F);

        this.lastX = -1;
        this.lastY = -1;

        this.userName = userName;
        this.oos = oos;
        this.ois = ois;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Bitmap img = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);
        canvas.drawColor(Color.WHITE);

        mBitmap = img;
        mCanvas = canvas;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        ChatMsg cm = new ChatMsg();
        switch (action) {
            case MotionEvent.ACTION_UP: // 뗐을 때
                changed = true;
                Rect rect = touchUp(event, false);
                if (rect != null) {
                    invalidate(rect);
                    cm.setCode("600");
                    cm.setUserName(userName);
                    cm.setRect(rect);
                    SendChatMsg(cm);
                }
                mPath.rewind();
                return true;
            case MotionEvent.ACTION_DOWN: // 눌렸을 때
                rect = touchDown(event);
                if (rect != null) {
                    invalidate(rect);
                    cm.setCode("600");
                    cm.setUserName(userName);
                    cm.setRect(rect);
                    SendChatMsg(cm);
                }
                return true;
            case MotionEvent.ACTION_MOVE: // 그리는 중
                rect = touchMove(event);
                if (rect != null) {
                    invalidate(rect);
                    cm.setCode("600");
                    cm.setUserName(userName);
                    cm.setRect(rect);
                    SendChatMsg(cm);
                }
                return true;

        }
        return false;
    }

    private Rect touchUp(MotionEvent event, boolean b) {
        Rect rect = processMove(event);
        return rect;
    }

    private Rect touchMove(MotionEvent event) {
        Rect rect = processMove(event);
        return rect;
    }

    private Rect processMove(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        final float dx = Math.abs(x - lastX);
        final float dy = Math.abs(y - lastY);

        Rect mInvalidateRect = new Rect();

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            final int border = mInvalidateExtraBorder;

            mInvalidateRect.set((int) mCurveEndX - border, (int) mCurveEndY - border, (int) mCurveEndX + border, (int) mCurveEndY + border);

            float cx = mCurveEndX = (x + lastX) / 2;
            float cy = mCurveEndY = (y + lastY) / 2;

            mPath.quadTo(lastX, lastY, cx, cy);

            mInvalidateRect.union((int) lastX - border, (int) lastY - border, (int) lastX + border, (int) lastY + border);
            mInvalidateRect.union((int) cx - border, (int) cy - border, (int) cx, (int) cy + border);

            lastX = x;
            lastY = y;

            mCanvas.drawPath(mPath, mPaint);
        }

        return mInvalidateRect;
    }

    private Rect touchDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        lastX = x;
        lastY = y;

        Rect mInvalidateRect = new Rect();
        mPath.moveTo(x, y);

        final int border = mInvalidateExtraBorder;
        mInvalidateRect.set((int) x - border, (int) y - border, (int) x + border, (int) y + border);
        mCurveEndX = x;
        mCurveEndY = y;

        mCanvas.drawPath(mPath, mPaint);
        return mInvalidateRect;
    }

    public void setStrokeWidth(int width) {
        mPaint.setStrokeWidth(width);
    }


    public void setColor(String color) {
        mPaint.setColor(Color.parseColor(color));

    }

    public void clearCanvas() {
        Paint clearPaint = new Paint();
        Xfermode xmode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        clearPaint.setXfermode(xmode);

        int iCnt = mCanvas.save();
        mCanvas.drawBitmap(mBitmap, 0, 0, clearPaint);
        mCanvas.restoreToCount(iCnt);
    }

    // Server Message 수신

    // SendChatMsg()
    @Override
    public synchronized void SendChatMsg(ChatMsg cm) {
        new Thread() {
            public void run() {
                // Java 호환성을 위해 각각의 Field를 따로따로 보낸다.
                try {
                    oos.writeObject(cm.getCode());
                    oos.writeObject(cm.getUserName());
                    oos.writeObject(cm.getData());
                    oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public synchronized void DoReceive() {
        new Thread() {
            public void run() {
                ReadChatMsg();
            }
        }.start();
    }

    @Override
    public synchronized ChatMsg ReadChatMsg() {
        ChatMsg cm = new ChatMsg("", "", "");
        try {
            cm.setCode((String) ois.readObject());
            Log.d("Edit : ", cm.getCode());

           if (cm.getCode().equals("600")) {
                cm.setUserName((String) ois.readObject());
                cm.setRect((Rect) ois.readObject());
                invalidate(cm.getRect());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cm;
    }
}