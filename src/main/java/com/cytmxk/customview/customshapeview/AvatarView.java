package com.cytmxk.customview.customshapeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by chenyang on 2016/7/4.
 */
public class AvatarView extends ImageView {
    private static final String TAG = AvatarView.class.getCanonicalName();
    /**
     * 用于图片背景
     */
    private Bitmap mBackgroundBmp;
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

    public AvatarView(Context context) {
        this(context,null,0);
    }
    public AvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public AvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(TAG, "AvatarView()");
        super.setScaleType(ScaleType.CENTER_CROP);
        /*buildDrawingCache();
        Bitmap drawingCache = getDrawingCache();
        Log.d(TAG, "drawingCache = " + drawingCache);
        if(drawingCache!=null){
            mBitmap = Bitmap.createBitmap(drawingCache);
            setBackgroundBmp();
        }*/
    }

    private void setBackgroundBmp(){
        if(null==getBackground()){
            throw new IllegalArgumentException(String.format("background is null."));
        }else{
            mBackgroundBmp = getBitmapFromDrawable(getBackground());
            invalidate();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mBitmap!=null && mBackgroundBmp!=null){
            canvas.drawBitmap(createImage(), 0, 0, null);
        }
    }

    private Bitmap createImage()
    {
        mBackgroundBmp = getCenterCropBitmap(mBackgroundBmp, mViewWidth, mViewHeight);
        mBitmap = getCenterCropBitmap(mBitmap, mViewWidth, mViewHeight);

        int bmpWidth = mBitmap.getWidth();
        int bmpHeight = mBitmap.getHeight();

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap finalBmp = Bitmap.createBitmap(mViewWidth,mViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(finalBmp);
        canvas.drawBitmap(mBackgroundBmp, 0, 0, paint);
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
        setBackgroundBmp();
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
