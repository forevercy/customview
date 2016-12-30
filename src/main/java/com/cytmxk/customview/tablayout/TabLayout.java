package com.cytmxk.customview.tablayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cytmxk.customview.R;

/**
 * Created by chenyang on 2016/11/17.
 */

public class TabLayout extends LinearLayout {

    private static final int DEFAULT_TABLAYOUT_ITEM_COUNT = 3;
    private int tablayoutItemCount = DEFAULT_TABLAYOUT_ITEM_COUNT;

    private LinearLayout.LayoutParams layoutParams;

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context, attrs);
    }

    private void initLayout(Context context, AttributeSet attrs) {
        layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        setOrientation(HORIZONTAL);
    }

    public void addTab(Tab tab, boolean isSelected) {
        addView(tab, layoutParams);
    }

    public static class Tab extends LinearLayout{

        private ImageView imageView;
        private TextView textView;

        public Tab(Context context) {
            this(context, null);
        }

        public Tab(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public Tab(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            initLayout(context);
        }

        private void initLayout(Context context) {
            setOrientation(VERTICAL);
            View tabView = LayoutInflater.from(context).inflate(R.layout.customview_tablayout_item, this, false);
            imageView = (ImageView) tabView.findViewById(R.id.icon);
            textView = (TextView) tabView.findViewById(R.id.text);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            addView(tabView, layoutParams);
        }

        public Tab setText(int resId) {
            imageView.setImageResource(resId);
            return this;
        }

        public Tab setIcon(int resId) {
            textView.setText(resId);
            return this;
        }
    }


}
