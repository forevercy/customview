package com.cytmxk.customview.radiogroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by wb-cy208209 on 2016/7/22.
 */
public class CRadioGroup extends LinearLayout implements View.OnClickListener {

    private static final String TAG = CRadioGroup.class.getCanonicalName();

    public CRadioGroup(Context context) {
        this(context, null);
    }

    public CRadioGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CRadioGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(HORIZONTAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            childView.setOnClickListener(this);
            childView.setSelected(false);
        }
        getChildAt(0).setSelected(true);
    }

    @Override
    public void onClick(View v) {
        mOnItemClickListener.onItemClick(v, v.getId());
        updateButtonState(v);
    }

    private void updateButtonState(View clicked) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            if (childView.isSelected()) {
                childView.setSelected(false);
            }
        }
        clicked.setSelected(true);
    }

    private OnItemClickListener mOnItemClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(View view, long id);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
