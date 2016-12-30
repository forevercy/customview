package com.cytmxk.customview.waterfallimage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by chenyang on 16/4/20.
 */
public class WaterfallImageView extends LinearLayout {

    public WaterfallImageView(Context context) {
        super(context);
    }

    public WaterfallImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int width = getWidth() / getChildCount();
        int height = getHeight();

        if (event.getX() <= width) { //滑动左边的 listView
            event.setLocation(width / 2, event.getY());
            getChildAt(0).dispatchTouchEvent(event);
        } else if (event.getX() > width && event.getX() <= 2 * width) {
            if (event.getY() < height / 2) {
                event.setLocation(width / 2, event.getY());
                for (int i = 0; i < getChildCount(); i++) { //滑动中间的 listView
                    getChildAt(i).dispatchTouchEvent(event);
                }
            } else {
                event.setLocation(width / 2, event.getY());
                getChildAt(1).dispatchTouchEvent(event);
            }
        } else if (event.getX() > 2 * width) {
            event.setLocation(width / 2, event.getY());
            getChildAt(2).dispatchTouchEvent(event);
        }
        return true;
    }
}
