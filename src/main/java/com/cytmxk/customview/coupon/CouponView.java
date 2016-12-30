package com.cytmxk.customview.coupon;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cytmxk.customview.R;

/**
 * Created by chenyang on 2016/6/29.
 */
public class CouponView extends LinearLayout {

    private Paint mPaint;

    /**
     * 圆间距
     */
    private int gap = 10;
    /**
     * 半径
     */
    private int radius = 15;
    /**
     * 圆数量
     */
    private int circleNum;

    private int remain = 0;

    public CouponView(Context context) {
        this(context, null);
    }

    public CouponView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CouponView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(android.R.color.white));

        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CouponView);
        Drawable image = ta.getDrawable(R.styleable.CouponView_cv_image);
        int background = ta.getColor(R.styleable.CouponView_cv_background, 0);
        String topic = ta.getString(R.styleable.CouponView_cv_topic);
        String number = ta.getString(R.styleable.CouponView_cv_number);
        String content = ta.getString(R.styleable.CouponView_cv_content);
        String deadline = ta.getString(R.styleable.CouponView_cv_deadline);

        setOrientation(LinearLayout.HORIZONTAL);
        setBackgroundColor(background);

        ImageView ivImage = new ImageView(getContext());
        ivImage.setImageDrawable(image);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        addView(ivImage, layoutParams);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.CENTER_VERTICAL;
        layoutParams1.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        layoutParams1.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        layoutParams1.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        layoutParams1.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics());
        addView(linearLayout, layoutParams1);

        TextView tvTopic = new TextView(getContext());
        tvTopic.setText(topic);
        LinearLayout.LayoutParams tvLayoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.addView(tvTopic, tvLayoutParams1);

        TextView tvNumber = new TextView(getContext());
        tvNumber.setText(number);
        LinearLayout.LayoutParams tvLayoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.addView(tvNumber, tvLayoutParams2);

        TextView tvContent = new TextView(getContext());
        tvContent.setText(content);
        LinearLayout.LayoutParams tvLayoutParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.addView(tvContent, tvLayoutParams3);

        TextView tvDeadline = new TextView(getContext());
        tvDeadline.setText(deadline);
        LinearLayout.LayoutParams tvLayoutParams4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.addView(tvDeadline, tvLayoutParams4);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        remain = (w - gap) % (gap + 2 * radius);
        circleNum = (w - gap) / (gap + 2 * radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 1; i <= circleNum; i++) {
            canvas.drawCircle(remain / 2 + i * gap + (2 * i - 1) * radius, 0, radius, mPaint);
            canvas.drawCircle(remain / 2 + i * gap + (2 * i - 1) * radius, getHeight(), radius, mPaint);
        }
    }
}
