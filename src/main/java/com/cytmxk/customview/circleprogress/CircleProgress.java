package com.cytmxk.customview.circleprogress;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.cytmxk.customview.R;

/**
 * Created by wb-cy208209 on 2016/8/8.
 */
public class CircleProgress extends View {

    private static final String TAG = CircleProgress.class.getCanonicalName();
    private int mWidth = 200;
    private int mHeight = 200;
    private int sweepAngle;
    private int sweepValue;

    private Paint paint;
    private ObjectAnimator animator;

    public CircleProgress(Context context) {
        super(context);
        init();
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(getResources().getColor(android.R.color.white));
    }

    private void init(AttributeSet attrs) {
        init();
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        sweepAngle = ta.getInteger(R.styleable.CircleProgress_cp_sweep_value, 0);
        ta.recycle();

        animator = ObjectAnimator.ofInt(this, "sweepValue", 0, sweepAngle).setDuration(3000);
        animator.setInterpolator(new DecelerateInterpolator());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mHeight);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    /**
     * 该方法在当前View或其祖先的可见性改变时被调用
     */
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        Log.d(TAG, "onVisibilityChanged this.isShown() = " + this.isShown());
        if (this.isShown()) {
            startSweep();
        } else {
            cancelSweep();
        }
    }

    /**
     * 该方法在包含当前View的window可见性改变时被调用
     */
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        Log.d(TAG, "onWindowVisibilityChanged visibility = " + visibility);
        if (View.GONE == visibility) {
            cancelSweep();
        } else if (View.VISIBLE == visibility) {
            startSweep();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(new RectF(getPaddingLeft(), getPaddingTop(), mWidth - getPaddingRight(), mHeight - getPaddingBottom()), 0, sweepValue, false, paint);

    }

    private void startSweep() {
        Log.d(TAG, "startSweep isStarted() = " + animator.isStarted() + ", isRunning() = " + animator.isRunning());
        if (animator.isStarted() || animator.isRunning()) {
            return;
        }
        animator.start();
    }

    private void cancelSweep() {
        Log.d(TAG, "cancelSweep isStarted() = " + animator.isStarted() + ", isRunning() = " + animator.isRunning());
        if (animator.isStarted() || animator.isRunning()) {
            animator.cancel();
        }
    }

    public int getSweepValue() {
        return sweepValue;
    }

    public void setSweepValue(int sweepValue) {
        this.sweepValue = sweepValue;
        invalidate();
    }
}
