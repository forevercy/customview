package com.cytmxk.customview.scrolllview;

import android.os.SystemClock;

/**
 * 计算位移距离的工具类
 * Created by chenyang on 16/4/19.
 */
public class MyScroller {

    private int mStartX;
    private int mStartY;
    private int mDistanceX;
    private int mDistanceY;

    public int getCurrX() {
        return mCurrX;
    }

    public int getCurrY() {
        return mCurrY;
    }

    /**
     * 当前的X值
     */
    private int mCurrX;
    /**
     * 当前的Y值
     */
    private int mCurrY;

    /**
     * 默认动画运行的时间
     * 毫秒值
     */
    private int mDuration = 500;
    /**
     * 开始执行动画的时间
     */
    private long mStartTime;
    /**
     * 判断是否正在执行动画
     * true 是还在运行
     * false  已经停止
     */
    private boolean isFinish = true;

    /**
     * 开移移动
     * @param startX	开始时的X坐标
     * @param startY	开始时的Y坐标
     * @param dx		X方向 要移动的距离
     * @param dy		Y方向 要移动的距离
     */
    public void startScroll(int startX, int startY, int dx, int dy) {

        this.mStartX = startX;
        this.mStartY = startY;
        this.mDistanceX = dx;
        this.mDistanceY = dy;

        mStartTime = SystemClock.uptimeMillis();
        this.isFinish = false;
    }

    /**
     * 计算一下当前的运行状况
     * 返回值：
     * true  还在运行
     * false 运行结束
     */
    public boolean computeScrollOffset() {

        if (isFinish) {
            return false;
        }

        // 获得所用的时间
        long elapseTime = SystemClock.uptimeMillis() - mStartTime;
        if (elapseTime >= mDuration) {
            mCurrX = mStartX + mDistanceX;
            mCurrY = mStartY + mDistanceY;
            isFinish = true;
        } else {
            mCurrX = (int) (mStartX + elapseTime * mDistanceX/mDuration);
            mCurrY = (int) (mStartY + elapseTime * mDistanceY/mDuration);
        }

        return true;
    }
}
