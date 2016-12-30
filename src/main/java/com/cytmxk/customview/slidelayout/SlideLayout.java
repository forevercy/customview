package com.cytmxk.customview.slidelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.cytmxk.customview.R;

/**
 * Created by chenyang on 16/6/26.
 */
public class SlideLayout extends ViewGroup {

    private static final String TAG = SlideLayout.class.getCanonicalName();

    private ViewDragHelper mViewDragHelper = null;
    private View otherView = null;
    private View slideView = null;
    private int otherViewWidth = 100;
    private int otherViewHeight = 100;

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private int slideDirection;
    private boolean isAutoReset;
    private int screenWidth;
    private int screenHeight;
    private int maxWidth = 0;
    private int maxHeight = 0;

    public SlideLayout(Context context) {
        this(context, null);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlideLayout);
        slideDirection = ta.getInteger(R.styleable.SlideLayout_sl_slide_direction, HORIZONTAL);
        isAutoReset = ta.getBoolean(R.styleable.SlideLayout_sl_auto_reset, true);
        ta.recycle();
        Log.d(TAG, "initView slideDirection = " + slideDirection);

        WindowManager wm = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        mViewDragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        otherViewWidth = otherView.getMeasuredWidth();
        otherViewHeight = otherView.getMeasuredHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChildWithMargins(child, MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.AT_MOST), 0,
                    MeasureSpec.makeMeasureSpec(screenHeight * 2, MeasureSpec.AT_MOST), 0);
            maxWidth = maxWidth >= child.getMeasuredWidth() ? maxWidth : child.getMeasuredWidth();
            maxHeight = maxHeight >= child.getMeasuredHeight() ? maxHeight : child.getMeasuredHeight();
            Log.d(TAG, "onMeasure screenWidth = " + screenWidth + ", maxWidth = " + maxWidth);
            Log.d(TAG, "onMeasure screenHeight = " + screenHeight + ", maxHeight = " + maxHeight);
        }

        setMeasuredDimension(maxWidth, maxHeight);
        maxHeight = 0;
        maxWidth = 0;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        getChildAt(0).layout(0, 0, getChildAt(0).getMeasuredWidth(), getChildAt(0).getMeasuredHeight());
        getChildAt(1).layout(0, 0, getChildAt(1).getMeasuredWidth(), getChildAt(1).getMeasuredHeight());
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        //将触摸事件传递给ViewDragHelper,此操作必不可少
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        otherView = getChildAt(0);
        slideView = getChildAt(1);
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        // 何时开始检测触摸事件
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //如果当前触摸的child是mMainView时开始检测
            return slideView == child;
        }

        // 触摸到View后回调
        @Override
        public void onViewCaptured(View capturedChild,
                                   int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        // 当拖拽状态改变，比如idle，dragging
        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

        // 当位置改变的时候调用,常用与滑动时更改scale等
        @Override
        public void onViewPositionChanged(View changedView,
                                          int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
        }

        // 处理水平滑动
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (HORIZONTAL == slideDirection) {
                return left;
            } else if (VERTICAL == slideDirection) {
                return 0;
            }
            return 0;
        }

        // 处理垂直滑动
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (HORIZONTAL == slideDirection) {
                return 0;
            } else if (VERTICAL == slideDirection) {
                return top;
            }
            return 0;
        }

        // 拖动结束后调用
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (!isAutoReset) {
                return;
            }
            //手指抬起后缓慢移动到指定位置
            if ((slideView.getLeft() < otherViewWidth) && (slideView.getTop() < otherViewHeight)) {
                //还原到滑动前
                //相当于Scroller的startScroll方法
                mViewDragHelper.smoothSlideViewTo(slideView, 0, 0);
                ViewCompat.postInvalidateOnAnimation(SlideLayout.this);
            } else if ((slideView.getLeft() >= otherViewWidth) && (slideView.getTop() < otherViewHeight)) {
                //横向展开
                mViewDragHelper.smoothSlideViewTo(slideView, otherViewWidth, 0);
                ViewCompat.postInvalidateOnAnimation(SlideLayout.this);
            } else if ((slideView.getLeft() < otherViewWidth) && (slideView.getTop() >= otherViewHeight)) {
                //纵向展开
                mViewDragHelper.smoothSlideViewTo(slideView, 0, otherViewHeight);
                ViewCompat.postInvalidateOnAnimation(SlideLayout.this);
            }
        }

    };

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
