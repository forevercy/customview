package com.cytmxk.customview.togglebutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cytmxk.customview.R;

/**
 * Created by chenyang on 16/4/17.
 */
public class MyToggleButton extends View implements View.OnClickListener {

    /**
     * 做为背景的图片
     */
    private Bitmap backgroundBitmap;
    /**
     * 可以滑动的图片
     */
    private Bitmap slideButtonBitmap;
    private Paint paint;

    /**
     * 当前开关的状态
     * true 为开
     */
    private boolean currentState = false;
    /**
     * 滑动按钮的左边届
     */
    private int slideButtonLeft = 0;
    private int maxLeft;

    /**
     * down 事件时的x值
     */
    private int firstX;
    /**
     * touch 事件的上一个x值
     */
    private int lastX;

    /**
     * 判断是否发生拖动，
     * 如果拖动了，就不再响应 onclick 事件
     */
    private boolean isDrag;

    /**
     * 在代码里面创建对象的时候，使用此构造方法
     *
     * @param context
     */
    public MyToggleButton(Context context) {
        super(context);
    }

    /**
     * 在布局文件中声名的view，创建时由系统自动调用。
     *
     * @param context 上下文对象
     * @param attrs   属性集
     */
    public MyToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray tr = context.obtainStyledAttributes(attrs, R.styleable.MyToggleButton);
        int count = tr.getIndexCount();
        for (int i = 0; i < count; i++) {
            int index = tr.getIndex(i);
            if (R.styleable.MyToggleButton_tb_background == index) {
                int backgroundId = tr.getResourceId(index, -1);
                if (-1 == backgroundId) {
                    backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.switch_background);
                }
                backgroundBitmap = BitmapFactory.decodeResource(getResources(), backgroundId);
            } else if (R.styleable.MyToggleButton_tb_slide_btn == index) {
                int slideBtnId = tr.getResourceId(index, -1);
                if (-1 == slideBtnId) {
                    slideButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.slide_button);
                }
                slideButtonBitmap = BitmapFactory.decodeResource(getResources(), slideBtnId);
            } else if (R.styleable.MyToggleButton_tb_curr_state == index) {
                currentState = tr.getBoolean(index, false);
            }
        }

        initView();
        flushState();
    }

    /**
     * 初始化
     */
    private void initView() {

        //初始化图片
        //backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.switch_background);
        //slideButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.slide_button);

        //
        maxLeft = backgroundBitmap.getWidth() - slideButtonBitmap.getWidth();

        //初始化画笔
        paint = new Paint();
        paint.setAntiAlias(true); //打开抗矩齿


        //添加点击事件监听
        setOnClickListener(this);
    }

    /*
     * view 对象显示的屏幕上，有几个重要步骤：
	 * 1、构造方法 创建 对象。
	 * 2、测量view的大小。	onMeasure(int,int);
	 * 3、确定view的位置 ，view自身有一些建议权，决定权在 父view手中。  onLayout();
	 * 4、绘制 view 的内容 。 onDraw(Canvas)
	 */

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 设置当前view的大小
         * width  :view的宽度
         * height :view的高度   （单位：像素）
         */
        setMeasuredDimension(backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
    }

    //确定位置的时候调用此方法
    //自定义view的时候，作用不大
//	@Override
//	protected void onLayout(boolean changed, int left, int top, int right,
//			int bottom) {
//		super.onLayout(changed, left, top, right, bottom);
//	}

    @Override
    /**
     * 绘制当前view的内容
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制 背景
		/*
		 * backgroundBitmap	要绘制的图片
		 * left	图片的左边届
		 * top	图片的上边届
		 * paint 绘制图片要使用的画笔
		 */
        canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
        //绘制 可滑动的按钮
        canvas.drawBitmap(slideButtonBitmap, slideButtonLeft, 0, paint);
    }

    @Override
    /**
     * onclick 事件在View.onTouchEvent 中被解析。
     * 系统对onclick 事件的解析，过于简陋，只要有down 事件  up 事件，系统即认为 发生了click 事件
     *
     */
    public void onClick(View v) {
        /*
		 * 如果没有拖动，才执行改变状态的动作
		 */
        if (!isDrag) {
            currentState = !currentState;
            flushState();
        }
    }

    /**
     * 刷新当前状态
     */
    private void flushState() {
        if (currentState) {
            slideButtonLeft = maxLeft;
        } else {
            slideButtonLeft = 0;
        }

        flushView();
    }

    /**
     * 刷新当前视图
     */
    private void flushView() {

        /*
		 * 对 slideBtn_left  的值进行判断 ，确保其在合理的位置 即       0<=slideBtn_left <=  maxLeft
		 *
		 */
        slideButtonLeft = (slideButtonLeft > 0) ? slideButtonLeft : 0;
        slideButtonLeft = (slideButtonLeft < maxLeft) ? slideButtonLeft : maxLeft;

        /*
		 * 刷新当前视图  导致 执行onDraw执行
		 */
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstX = lastX = (int) event.getX();
                isDrag = false;
                break;
            case MotionEvent.ACTION_MOVE:
                //判断是否发生拖动
                if (Math.abs(event.getX() - firstX) > 5) {
                    isDrag = true;
                }
                //计算 手指在屏幕上移动的距离
                int dis = (int) (event.getX() - lastX);
                //根据手指移动的距离，改变slideBtn_left 的值
                slideButtonLeft = slideButtonLeft + dis;
                //将本次的位置 设置给lastX
                lastX = (int) event.getX();

                flushView();
                break;
            case MotionEvent.ACTION_UP:
                //在发生拖动的情况下，根据最后的位置，判断当前开关的状态
                if (isDrag) {
                    /*
                     * 根据 slideBtn_left 判断，当前应是什么状态
                     */
                    if (slideButtonLeft > maxLeft / 2) { // 此时应为打开的状态
                        currentState = true;
                    } else {
                        currentState = false;
                    }
                    flushState();
                }
                break;

        }
        return true;
    }
}
