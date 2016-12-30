package com.cytmxk.customview.waveprogressview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import com.cytmxk.customview.R;

/**
 * Created by chenyang on 2016/7/11.
 */
public class WaveProgressView extends View {

    private static final String TAG = WaveProgressView.class.getCanonicalName();

    /**
     * WaveProgressView的波动速度
     */
    private enum Speed {
        SLOW(300), NORMAL(200), FAST(100);

        private int mSpeed;
        private Speed(int speed) {
            this.mSpeed = speed;
        }

        public int getValue() {
            return this.mSpeed;
        }

        public static Speed valueOf(int value) {
            switch (value) {
                case 15:
                    return SLOW;
                case 10:
                    return NORMAL;
                case 5:
                    return FAST;
                default:
                    return NORMAL;
            }
        }

        @Override
        public String toString() {
            return String.valueOf(mSpeed);
        }
    }
    private Speed mFluctuationSpeed = Speed.NORMAL;

    /**
     * WaveProgressView的波动方向
     */
    private enum Direction {
        LEFT(0), RIGHT(1);

        private int mDirection;
        private Direction(int direction) {
            this.mDirection = direction;
        }

        public int getValue() {
            return this.mDirection;
        }

        public static Direction valueOf(int value) {
            switch (value) {
                case 0:
                    return LEFT;
                case 1:
                    return RIGHT;
                default:
                    return LEFT;
            }
        }

        @Override
        public String toString() {
            return String.valueOf(mDirection);
        }
    }
    private Direction mFluctuationDirection = Direction.LEFT;

    private int mMaxProgress;
    private int mCurrentProgress;
    private String mProgressUnit;
    private int mTextColor;
    private float mTextSize;
    private int mWaveColor;
    private float mWaveWidth;
    private float mWaveHeight;
    private int mViewWidth;
    private int mViewHeight;
    private float offset = 0;
    private float steps = 0;
    private float refreshRate = 5;

    private Bitmap mBackGroundBmp = null;
    private Paint mWavePaint;
    private Paint mTextPaint;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            waveMoveToNext();
            invalidate();
            mHandler.sendEmptyMessageDelayed(0, mFluctuationSpeed.getValue());
        }
    };

    private void waveMoveToNext() {
        offset += steps;
        if (offset < -mWaveWidth) {
            offset += mWaveWidth;
        } else if (offset > 0) {
            offset -= mWaveWidth;
        }

        if (refreshRate > 0) {
            mCurrentWaveY -= (1.0f / mMaxProgress) * mViewHeight / 5;
            refreshRate--;
        } else {
            mCurrentProgress ++;
            if (mCurrentProgress > 100) {
                mCurrentProgress = 0;
                mCurrentWaveY = mViewHeight;
            }
            refreshRate = 5;
        }
    }

    public WaveProgressView(Context context) {
        super(context);
    }

    public WaveProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    public int getCurrentProgress() {
        return mCurrentProgress;
    }

    public void setmCurrentProgress(int currentProgress) {
        this.mCurrentProgress = currentProgress;
    }

    public String getProgressUnit() {
        return mProgressUnit;
    }

    public void setProgressUnit(String progressUnit) {
        this.mProgressUnit = progressUnit;
    }

    public int getWaveColor() {
        return mWaveColor;
    }

    public void setWaveColor(int waveColor) {
        this.mWaveColor = waveColor;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaveProgressView);
        mMaxProgress = ta.getInteger(R.styleable.WaveProgressView_wpv_max_progress, 100);
        mCurrentProgress = ta.getInteger(R.styleable.WaveProgressView_wpv_current_progress, 10);
        mProgressUnit = ta.getString(R.styleable.WaveProgressView_wpv_progress_unit);
        if (null == mProgressUnit) {
            mProgressUnit = "%";
        }
        mTextColor = ta.getColor(R.styleable.WaveProgressView_wpv_text_color, context.getResources().getColor(R.color.waveprogressview_text_color));
        mTextSize = ta.getDimension(R.styleable.WaveProgressView_wpv_text_size, context.getResources().getDimension(R.dimen.waveprogressview_text_size));
        mWaveColor = ta.getColor(R.styleable.WaveProgressView_wpv_wave_color, context.getResources().getColor(R.color.waveprogressview_wave_color));
        mWaveWidth = ta.getDimension(R.styleable.WaveProgressView_wpv_wave_width, context.getResources().getDimension(R.dimen.waveprogressview_wave_width));
        mWaveHeight = ta.getDimension(R.styleable.WaveProgressView_wpv_wave_height, context.getResources().getDimension(R.dimen.waveprogressview_wave_height));
        mFluctuationSpeed = Speed.valueOf(ta.getInteger(R.styleable.WaveProgressView_wpv_fluctuation_speed, Speed.NORMAL.getValue()));
        mFluctuationDirection = Direction.valueOf(ta.getInteger(R.styleable.WaveProgressView_wpv_fluctuation_direction, Direction.LEFT.getValue()));
        ta.recycle();

        switch (mFluctuationDirection) {
            case LEFT:
                offset = 0;
                steps = -TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                break;
            case RIGHT:
                offset = -mWaveWidth;
                steps = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());;
                break;
        }

        mWavePaint = new Paint();
        mWavePaint.setAntiAlias(true);
        mWavePaint.setColor(mWaveColor);
        mWavePaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        if (null != getBackground()) {
            mBackGroundBmp = getBitmapFromDrawable(getBackground());
            setBackgroundDrawable(null);
        } else {
            throw new IllegalArgumentException(String.format("background is null."));
        }

        mHandler.sendEmptyMessageDelayed(0, mFluctuationSpeed.getValue());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mCurrentWaveY = (((float) mCurrentProgress) / mMaxProgress) * mViewHeight;
        mWaveNumber = (int) (mViewWidth / mWaveWidth + 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        canvas.drawBitmap(createImage(), 0, 0, null);
    }

    private float mCurrentWaveY;
    private int mWaveNumber;
    private Bitmap createImage()
    {
        Bitmap finalBmp = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(finalBmp);
        Path path = new Path();
        path.moveTo(offset, mCurrentWaveY);
        for (int i = 0; i < mWaveNumber; i++) {
            path.quadTo(mWaveWidth * i + offset + mWaveWidth / 4, mCurrentWaveY - mWaveHeight, mWaveWidth * i + offset + mWaveWidth / 2, mCurrentWaveY);
            path.quadTo(mWaveWidth * i + offset + mWaveWidth * 3 / 4, mCurrentWaveY + mWaveHeight, mWaveWidth * i + offset + mWaveWidth, mCurrentWaveY);
        }
        path.lineTo(mViewWidth, mViewHeight);
        path.lineTo(0,mViewHeight);
        path.close();
        canvas.drawPath(path, mWavePaint);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        canvas.drawBitmap(Bitmap.createScaledBitmap(mBackGroundBmp, mViewWidth, mViewHeight, false), 0, 0, paint);

        canvas.drawText(mCurrentProgress + mProgressUnit, mViewWidth / 2,
                mViewHeight / 2 - (mTextPaint.getFontMetricsInt().ascent + mTextPaint.getFontMetricsInt().descent) / 2, mTextPaint);

        return finalBmp;
    }

    /**
     * Drawable转Bitmap
     */
    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}
