package com.cytmxk.customview.customshapeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.cytmxk.customview.R;

/**
 * Created by wb-cy208209 on 2016/7/4.
 */
public class RoundedImageView extends ImageView {
    /**
     * 显示的图片
     */
    private Bitmap mBitmap;
    /**
     * view 的宽度
     */
    private int mViewWidth;
    /**
     * view 的高度
     */
    private int mViewHeight;

    /**
     * view四个圆角对应的半径大小
     */
    private float topLeftRadius = 0;
    private float topRightRadius = 0;
    private float bottomLeftRadius = 0;
    private float bottomRightRadius = 0;
    private float radius = 0;

    public RoundedImageView(Context context) {
        this(context,null,0);
    }
    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setScaleType(ScaleType.CENTER_CROP);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView);
        if (ta.hasValue(R.styleable.RoundedImageView_radius)) {
            radius = ta.getDimensionPixelSize(R.styleable.RoundedImageView_radius, 0);
            topLeftRadius = radius;
            topRightRadius = radius;
            bottomLeftRadius = radius;
            bottomRightRadius = radius;
            return;
        }
        topLeftRadius = ta.getDimensionPixelSize(R.styleable.RoundedImageView_topLeftRadius, 0);
        topRightRadius = ta.getDimensionPixelSize(R.styleable.RoundedImageView_topRightRadius, 0);
        bottomLeftRadius = ta.getDimensionPixelSize(R.styleable.RoundedImageView_bottomLeftRadius, 0);
        bottomRightRadius = ta.getDimensionPixelSize(R.styleable.RoundedImageView_bottomRightRadius, 0);
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(null != mBitmap){
            canvas.drawBitmap(createImage(), 0, 0, null);
        }
    }

    private Bitmap createImage()
    {
        mBitmap = getCenterCropBitmap(mBitmap, mViewWidth, mViewHeight);
        int bmpWidth = mBitmap.getWidth();
        int bmpHeight = mBitmap.getHeight();

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap finalBmp = Bitmap.createBitmap(mViewWidth,mViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(finalBmp);
        GradientDrawable layerOneDrawable = new GradientDrawable();
        float[] radii = new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius,
                bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius};
        layerOneDrawable.setSize(mViewWidth, mViewHeight);
        layerOneDrawable.setColor(getContext().getResources().getColor(android.R.color.white));
        layerOneDrawable.setCornerRadii(radii);
        Bitmap layerOneBitmap = getBitmapFromDrawable(layerOneDrawable);
        canvas.drawBitmap(layerOneBitmap, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mBitmap, (mViewWidth - bmpWidth) / 2, (mViewHeight - bmpHeight) / 2, paint);
        return finalBmp;
    }

    /**
     * 类比ScaleType.CENTER_INSIDE
     */
    private Bitmap getCenterInsideBitmap(Bitmap src, float sideLength) {
        float srcWidth = src.getWidth();
        float srcHeight = src.getHeight();
        float scaleWidth = 0;
        float scaleHeight = 0;

        if (srcWidth > srcHeight) {
            scaleWidth = sideLength;
            scaleHeight = (sideLength / srcWidth) * srcHeight;
        } else if (srcWidth < srcHeight) {
            scaleWidth = (sideLength / srcHeight) * srcWidth;
            scaleHeight = sideLength;
        } else {
            scaleWidth = scaleHeight = sideLength;
        }

        return Bitmap.createScaledBitmap(src, (int)scaleWidth, (int)scaleHeight, false);
    }

    /**
     * 类比ScaleType.CENTER_INSIDE
     */
    private Bitmap getCenterInsideBitmap(Bitmap src, float rectWidth, float rectHeight) {

        float srcRatio = ((float) src.getWidth()) / src.getHeight();
        float rectRadio = rectWidth / rectHeight;
        if (srcRatio < rectRadio) {
            return getCenterInsideBitmap(src, rectHeight);
        } else {
            return getCenterInsideBitmap(src, rectWidth);
        }
    }

    /**
     * 类比ScaleType.CENTER_CROP
     */
    private Bitmap getCenterCropBitmap(Bitmap src, float rectWidth, float rectHeight) {

        float srcRatio = ((float) src.getWidth()) / src.getHeight();
        float rectRadio = rectWidth / rectHeight;
        if (srcRatio < rectRadio) {
            return Bitmap.createScaledBitmap(src, (int)rectWidth, (int)((rectWidth / src.getWidth()) * src.getHeight()), false);
        } else {
            return Bitmap.createScaledBitmap(src, (int)((rectHeight / src.getHeight()) * src.getWidth()), (int)rectHeight, false);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mViewWidth = getWidth();
        mViewHeight = getHeight();
        mBitmap = getBitmapFromDrawable(getDrawable());
    }

    /**
     * Drawable转Bitmap
     */
    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap = Bitmap.createBitmap(mViewWidth, mViewHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }
}
