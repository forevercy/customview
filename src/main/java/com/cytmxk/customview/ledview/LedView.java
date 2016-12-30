package com.cytmxk.customview.ledview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.cytmxk.utils.common.FontUtils;
import com.cytmxk.utils.common.FontUtils.DotMatrixFontType;
import com.cytmxk.customview.R;

import java.io.UnsupportedEncodingException;

/**
 * Created by chenyang on 2016/7/7.
 */
public class LedView extends View {

    private static final String TAG = LedView.class.getCanonicalName();
    private DotMatrixFontType mDotMatrixFontType = DotMatrixFontType.SIXTEEN_TYPE;

    /**
     * 显示的文本
     */
    private String mText = "";
    /**
     * 点的颜色
     */
    private int mPointColor = 0;
    /**
     *点之间的距离
     */
    private float mPointSpace = 0;
    /**
     * 点的半径
     */
    private float mPaintRadius = 0;

    /**
     * Led的滚动速度
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
                case 300:
                    return SLOW;
                case 200:
                    return NORMAL;
                case 100:
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
    private Speed mScrollSpeed = Speed.NORMAL;

    /**
     * Led的滚动方向
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
    private Direction mScrollDirection = Direction.LEFT;

    /**
     * 用来绘制空心点和实心点的画笔
     */
    private Paint mHollowPaint = null;
    private Paint mFillPaint = null;

    /**
     *保存文字文本的boolean点阵
     */
    private boolean[][] mWordsMatrix;
    /**
     * 文字个数
     */
    private int mWordNumber;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch ((Direction)msg.obj) {
                case LEFT:
                    matrixMoveToLeft();
                    invalidate();
                    break;
                case RIGHT:
                    matrixMoveToRight();
                    invalidate();
                    break;
            }
        }
    };

    private void matrixMoveToRight() {
        for (int row = 0; row < mDotMatrixFontType.getValue(); row++) {
            boolean temp = mWordsMatrix[row][mDotMatrixFontType.getValue() * mWordNumber - 1];
            System.arraycopy(mWordsMatrix[row], 0, mWordsMatrix[row], 1, mDotMatrixFontType.getValue() * mWordNumber - 1);
            mWordsMatrix[row][0] = temp;
        }
    }

    private void matrixMoveToLeft() {
        for (int row = 0; row < mDotMatrixFontType.getValue(); row++) {
            boolean temp = mWordsMatrix[row][0];
            System.arraycopy(mWordsMatrix[row], 1, mWordsMatrix[row], 0, mDotMatrixFontType.getValue() * mWordNumber - 1);
            mWordsMatrix[row][mDotMatrixFontType.getValue() * mWordNumber - 1] = temp;
        }
    }

    public LedView(Context context) {
        super(context);
    }

    public LedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LedView);
        mText = ta.getString(R.styleable.LedView_lv_text);
        mPointColor = ta.getColor(R.styleable.LedView_lv_point_color, getContext().getResources().getColor(R.color.ledview_default_paint_color));
        mPointSpace = ta.getDimension(R.styleable.LedView_lv_point_space, getContext().getResources().getDimension(R.dimen.ledview_point_space));
        mScrollSpeed = Speed.valueOf(ta.getInteger(R.styleable.LedView_lv_scroll_speed, Speed.NORMAL.getValue()));
        mScrollDirection = Direction.valueOf(ta.getInteger(R.styleable.LedView_lv_scroll_direction, Direction.LEFT.getValue()));
        mHollowPaint = new Paint();
        mHollowPaint.setAntiAlias(true);
        mHollowPaint.setColor(mPointColor);
        mHollowPaint.setStyle(Paint.Style.STROKE);
        mFillPaint = new Paint();
        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(mPointColor);
        mFillPaint.setStyle(Paint.Style.FILL);
        ta.recycle();

        this.mWordsMatrix = new FontUtils().getWordsMatrix(getContext(), mText);
        try {
            this.mWordNumber = FontUtils.countWordNumber(mText);
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "UnsupportedEncodingException e = " + e.getMessage());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPaintRadius = (h - mPointSpace * (mDotMatrixFontType.getValue() + 1)) / (mDotMatrixFontType.getValue() * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        for (int row = 0; row < mDotMatrixFontType.getValue(); row++) {
            for (int col = 0; col < mDotMatrixFontType.getValue() * this.mWordNumber; col++) {
                if (mWordsMatrix[row][col]) {
                    canvas.drawCircle(col * (mPointSpace + mPaintRadius * 2) + mPointSpace + mPaintRadius,
                            row * (mPointSpace + mPaintRadius * 2) + mPointSpace + mPaintRadius,
                            mPaintRadius, mFillPaint);
                } else {
                    canvas.drawCircle(col * (mPointSpace + mPaintRadius * 2) + mPointSpace + mPaintRadius,
                            row * (mPointSpace + mPaintRadius * 2) + mPointSpace + mPaintRadius,
                            mPaintRadius, mHollowPaint);
                }
            }
        }
        Message message = new Message();
        message.obj = this.mScrollDirection;
        mHandler.sendMessageDelayed(message, mScrollSpeed.getValue());
    }
}
