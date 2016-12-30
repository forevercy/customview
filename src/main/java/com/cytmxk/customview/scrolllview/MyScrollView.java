package com.cytmxk.customview.scrolllview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by chenyang on 16/4/19.
 */
public class MyScrollView extends ViewGroup {

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {

        currIndex = 0;
        //myScroller = new MyScroller();
        myScroller = new Scroller(getContext());

        gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
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
            /**
             * 响应手指在屏幕上的滑动事件
             */
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                //移动屏幕
                //System.out.println("distanceX::"+distanceX);

                /**
                 * 移动当前view的内容 移动一段距离
                 * disX	 X方向移的距离		为正是，内容左向移动，为负时，内容向右移动
                 * disY  Y方向移动的距离
                 */
                scrollBy((int) distanceX, 0);

                /**
                 * 将当前视图的基准点移动到某个点  坐标点
                 * x 水平方向X坐标
                 * Y 竖直方向Y坐标
                 *  scrollTo(x,  y);
                 */

                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            /**
             * 发生手指抛动时回调
             */
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                isFling = true;
                if (velocityX > 0 && currIndex > 0) { // 手指向右抛动
                    currIndex--;
                } else if (velocityX < 0 && currIndex < getChildCount() - 1) { // 手指向左抛动
                    currIndex++;
                }
                android.util.Log.i("chenyang", "onFling currIndex = " + currIndex);
                moveToDest(currIndex);
                return false;
            }
        });
    }

    @Override
    /**
     * 计算 控件大小，
     * 做为viewGroup 还有一个责任：计算每一个子view的大小
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);


            //child.getMeasuredWidth() // 得到测量的大小
        }
    }

    @Override
    /**
     * 对子view进行布局，确定子view的位置
     * changed  若为true ，说明布局发生了变化
     * l\t\r\b\  是指当前viewgroup 在其父view中的位置
     */
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            /**
             * 父view 会根据子view的需求，和自身的情况，来综合确定子view的位置,(确定他的大小)
             */
            //指定子view的位置  ,  左，上，右，下，是指在viewGround坐标系中的位置
            child.layout(i * getWidth(), 0, (i + 1) * getWidth(), getHeight());

            //child.getWidth();  得到child的真实的大小。
        }
    }


    private int firstY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                gestureDetector.onTouchEvent(ev);
                firstX = (int) ev.getX();
                firstY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int disX = (int) Math.abs(ev.getX() - firstX);
                int disY = (int) Math.abs(ev.getY() - firstY);
                if (disX > disY && disX > 5) {
                    result = true;
                } else {
                    result = false;
                }

                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return result;
    }

    /**
     * 手势识别的工具类
     */
    private GestureDetector gestureDetector;
    /**
     * down 事件时的x坐标
     */
    private int firstX;
    /**
     * 当前的页面索引值
     * 显示在屏幕上的子View的下标
     */
    private int currIndex;

    /**
     * 计算位移的工具类，实现移动的动画效果
     */
    //private MyScroller myScroller;
    private Scroller myScroller;
    /**
     * 判断是否发生快速滑动
     */
    private boolean isFling = false;

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
                if (!isFling) { //在没有发生快速滑动的时候，才执行按位置判断currIndex
                    int nextIndex = 0;
                    if (event.getX() - firstX > getWidth() / 2) { // 手指向右滑动，超过屏幕的1/2  当前的currIndex - 1
                        nextIndex = currIndex - 1;
                    } else if (firstX - event.getX() > getWidth() / 2) { // 手指向左滑动，超过屏幕的1/2  当前的currIndex + 1
                        nextIndex = currIndex + 1;
                    } else {
                        nextIndex = currIndex;
                    }

                    moveToDest(nextIndex);
                }
                isFling = false;
                break;

        }

        return true;
    }

    /**
     * 页面改时时的监听接口
     * @author leo
     *
     */
    public interface OnPagerChangeListener {
        public void OnPagerChange(int index);
    }

    public OnPagerChangeListener getOnPagerChangeListener() {
        return mOnPagerChangeListener;
    }

    public void setOnPagerChangeListener(OnPagerChangeListener mOnPagerChangeListener) {
        this.mOnPagerChangeListener = mOnPagerChangeListener;
    }

    private OnPagerChangeListener mOnPagerChangeListener;

    /**
     * 移动到指定的屏幕上
     * @param index	屏幕的下标
     */
    public void moveToDest(int index) {

        /*
		 * 对 index 进行判断 ，确保 是在合理的范围
		 * 即  index >=0  && index <=getChildCount()-1
		 */

        //确保 index>=0
        index = index >= 0 ? index : 0;
        //确保 currIndex<=getChildCount()-1
        currIndex = index <= getChildCount() - 1 ? index : getChildCount() - 1;

        if (null != mOnPagerChangeListener) {
            mOnPagerChangeListener.OnPagerChange(currIndex);
        }

        //瞬间移动
        //scrollTo(currIndex * getWidth(), 0);

        //myScroller.startScroll(getScrollX(), 0, currIndex * getWidth() - getScrollX(), 0);
        //设置运行的时间
        myScroller.startScroll(getScrollX(), 0, currIndex * getWidth() - getScrollX(), 0, 500);
        /*
		 * 刷新当前view   computeScroll()方法会被执行
		 */
        invalidate();
    }

    @Override
    /**
     * invalidate();  会导致  computeScroll（）这个方法的执行
     */
    public void computeScroll() {
        super.computeScroll();
        if (myScroller.computeScrollOffset()) {
            scrollTo(myScroller.getCurrX(), 0);
            invalidate();
        }
    }
}
