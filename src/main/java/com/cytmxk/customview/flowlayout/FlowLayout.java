package com.cytmxk.customview.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.cytmxk.customview.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyang on 16/6/18.
 */
public class FlowLayout extends ViewGroup {

    private static final String TAG = FlowLayout.class.getCanonicalName();

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mGravity = ta.getInt(R.styleable.FlowLayout_gravity,LEFT);
        ta.recycle();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams()
    {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p)
    {
        return new MarginLayoutParams(p);
    }

    private List<Integer> mLineWidths = new ArrayList<Integer>();
    private List<Integer> mLineHeights = new ArrayList<Integer>();
    private List<Integer> mLineViewNums = new ArrayList<Integer>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        mLineWidths.clear();
        mLineHeights.clear();
        mLineViewNums.clear();
        int width = 0;
        int lineWidth = 0;
        int height = 0;
        int lineHeight = 0;
        int lineViewNum = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);

            if (View.GONE == childView.getVisibility()) {
                if (i == childCount - 1) {
                    lineViewNum++;
                    mLineViewNums.add(lineViewNum);
                    mLineWidths.add(lineWidth);
                    width = Math.max(width, lineWidth);
                    mLineHeights.add(lineHeight);
                    height += lineHeight;
                }
                continue;
            }

            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            if (lineWidth + childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin > widthSpecSize - getPaddingLeft() - getPaddingRight()) {
                mLineViewNums.add(lineViewNum);
                lineViewNum = 1;
                mLineWidths.add(lineWidth);
                lineWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                mLineHeights.add(lineHeight);
                height += lineHeight;
                lineHeight = Math.max(lineHeight, childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
            } else {
                lineViewNum++;
                lineWidth += childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                width = Math.max(width, lineWidth);
                lineHeight = Math.max(lineHeight, childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
            }
        }
        mLineViewNums.add(lineViewNum);
        mLineWidths.add(lineWidth);
        mLineHeights.add(lineHeight);
        height += lineHeight;

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(), height + getPaddingTop() + getPaddingBottom());
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(), heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, height + getPaddingTop() + getPaddingBottom());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private int mGravity;

    private static final int LEFT = -1;
    private static final int CENTER = 0;
    private static final int RIGHT = 1;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lineNum = mLineWidths.size();
        int paddingTop = getPaddingTop();
        int startIndex = 0;
        int endIndex = 0;
        for (int line = 0; line < lineNum; line++) {
            int paddingLeft = 0;
            int currentLineWidth = mLineWidths.get(line);
            switch (mGravity) {
                case LEFT:
                    paddingLeft = getPaddingLeft();
                    break;
                case CENTER:
                    paddingLeft = (getWidth() - currentLineWidth)/2;
                    break;
                case RIGHT:
                    paddingLeft = getWidth() - currentLineWidth - getPaddingRight();
                    break;
            }

            endIndex += mLineViewNums.get(line);
            for (; startIndex < endIndex; startIndex++) {
                View childView = getChildAt(startIndex);

                if (View.GONE == childView.getVisibility()) {
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                int lc = paddingLeft + lp.leftMargin;
                int tc = paddingTop + lp.topMargin;
                int rc = childView.getMeasuredWidth() + lc;
                int bc = childView.getMeasuredHeight() + tc;
                childView.layout(lc, tc, rc, bc);
                paddingLeft += childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            paddingTop += mLineHeights.get(line);
        }
    }
}
