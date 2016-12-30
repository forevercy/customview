package com.cytmxk.customview.swipelayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by chenyang on 2016/7/14.
 */
public class SwipeLayout extends LinearLayout {

    private static final String TAG = SwipeLayout.class.getCanonicalName();

    private static final int THRESHOLD_SCROLL = 5;

    private int mRightViewWidth = 0;
    private int disX = 0;
    private boolean isOpen = false;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(HORIZONTAL);
        mScroller = new Scroller(getContext());
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener(){

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                disX += (int)distanceX;
                if (disX > mRightViewWidth) {
                    scrollBy(mRightViewWidth - (disX - (int) distanceX), 0);
                    disX = mRightViewWidth;
                } else if (disX < 0) {
                    scrollBy( -(disX - (int) distanceX), 0);
                    disX = 0;
                } else {
                    scrollBy((int) distanceX, 0);
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                isFling = true;
                if (velocityX > 0) {// 手指向右抛动
                    close();
                } else if (velocityX < 0) {// 手指向左抛动
                    expansion();
                }
                return false;
            }
        });
    }

    private void expansion() {
        mScroller.startScroll(getScrollX(), 0, mRightViewWidth - getScrollX(), 0, 500);
        invalidate();
        disX = mRightViewWidth;
        isOpen = true;
        if (null != onSwipeListener) {
            onSwipeListener.onExpansion(this);
        }
    }

    public void close() {
        mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 500);
        invalidate();
        disX = 0;
        isOpen = false;
        if (null != onSwipeListener) {
            onSwipeListener.onClose(this);
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }

    /**
     * 手势识别的工具类
     */
    private GestureDetector gestureDetector;

    /**
     * 计算位移的工具类，实现移动的动画效果
     */
    private Scroller mScroller;

    private boolean isFling = false;
    private int firstX = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (!isFling) {
                    if ((!isOpen) && firstX - (int) event.getX() > mRightViewWidth / 2) {// 手指向左滑动，超过RightView的1/2,展开RightView
                        expansion();
                    } else if ((!isOpen) && firstX - (int) event.getX() >= 0) {// 手指向左滑动，小于RightView的1/2,关闭RightView
                        close();
                    } else if (isOpen && firstX - (int) event.getX() == 0) {// 手指点击屏幕但是没有滑动，如果RightView处于打开状态,则关闭RightView
                        close();
                    } else if (isOpen && firstX - (int) event.getX() < -mRightViewWidth / 2) {// 手指向右滑动，超过RightView的1/2,关闭RightView
                        close();
                    } else if (isOpen && firstX - (int) event.getX() < 0) {// 手指向右滑动，小于RightView的1/2,展开RightView
                        expansion();
                    }
                }
                isFling = false;
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRightViewWidth = getChildAt(1).getWidth();
    }

    private int firstX1 = 0;
    private int firstY1 = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstX1 = (int) ev.getX();
                firstY1 = (int) ev.getY();
                disallowParentsInterceptTouchEvent(getParent(), true);
                break;
            case MotionEvent.ACTION_MOVE:
                int disX = (int) Math.abs(ev.getX() - firstX1);
                int disY = (int) Math.abs(ev.getY() - firstY1);
                if (disX * disX + disY * disY > THRESHOLD_SCROLL * THRESHOLD_SCROLL) {
                    if (disY > disX) {
                        disallowParentsInterceptTouchEvent(getParent(), false);
                        close();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void disallowParentsInterceptTouchEvent(ViewParent parent, boolean disallow) {
        if (null == parent) {
            return;
        }
        // requestDisallowInterceptTouchEvent不仅设置了parent，而且会递归设置parent所有的上层视图
        parent.requestDisallowInterceptTouchEvent(disallow);
    }

    private OnSwipeListener onSwipeListener;
    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }
    public interface OnSwipeListener {
        public void onExpansion(SwipeLayout swipeLayout);
        public void onClose(SwipeLayout swipeLayout);
    }
}
