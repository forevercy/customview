package com.cytmxk.customview.ringwave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyang on 16/4/21.
 */
public class MyRingWave extends View {

    private List<Wave> mWaveList = new ArrayList<Wave>();

    public MyRingWave(Context context) {
        super(context);
    }

    public MyRingWave(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    private float mCx;
    private float mCy;
    private float mRadius;
    private Paint mPaint;
    private float mStrokeWidth;
    private int mAlpha;

    private static float MIN_DIS = 10;

    private boolean isRunning = false;

    private void initView() {

        mCx = 0;
        mCy = 0;
        mRadius = 0;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mAlpha = 255;
        mPaint.setAlpha(mAlpha);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mStrokeWidth = mRadius / 5;
        mPaint.setStrokeWidth(mStrokeWidth);

    }

    private int[] mColors = new int[] {Color.BLUE, Color.YELLOW, Color.RED, Color.GREEN};

    private Paint initPaint() {
        Paint paint = new Paint();
        paint.setAlpha(255);
        paint.setColor(mColors[(int) (Math.random() * mColors.length)]);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0);
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = mWaveList.size();
        for (int i = 0; i < count; i++) {
            Wave wave = mWaveList.get(i);
            canvas.drawCircle(wave.mCx, wave.mCy, wave.mRadius, wave.mPaint);
        }
    }

    private float mLastX;
    private float mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mLastX = event.getX();
                mLastY = event.getY();

                addWaveList(event.getX(), event.getY());
                flushView();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - mLastX) > MIN_DIS || Math.abs(event.getY() - mLastY) > MIN_DIS) {
                    addWaveList(event.getX(), event.getY());
                    flushView();
                }
                break;
        }
        return true;
    }

    private void flushView() {

        if (!isRunning) {
            handler.sendEmptyMessage(0);
            isRunning = true;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            flushWaveList();

            if (mWaveList.size() == 0) {
                isRunning = false;
            }

            if (isRunning) {
                invalidate();
                sendEmptyMessageDelayed(0, 100);
            }
        }
    };

    private void flushWaveList() {
        for (int i = 0; i < mWaveList.size(); i++) {
            Wave wave = mWaveList.get(i);
            if (wave.mPaint.getAlpha() == 0) {
                mWaveList.remove(i);
            } else {
                updateWave(wave);
            }
        }
    }

    private void updateWave(Wave wave) {
        wave.mRadius += 15;
        wave.mPaint.setStrokeWidth(wave.mRadius / 6);
        wave.mPaint.setAlpha(Math.max(wave.mPaint.getAlpha() - 10, 0));
    }

    private void addWaveList (float x, float y) {
        Wave wave = new Wave();
        wave.mCx = x;
        wave.mCy = y;
        wave.mRadius = 0;
        wave.mPaint = initPaint();
        mWaveList.add(wave);
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    private class Wave {
        public float mCx;
        public float mCy;
        public float mRadius;
        public Paint mPaint;
    }
}
