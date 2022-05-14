package com.example.fx.shadow;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import static android.view.View.LAYER_TYPE_HARDWARE;

/**
 * Create by y.tang0 on 2022/04/10
 * Description:
 */
public class Shadow implements IShadowViewEvent {

    protected boolean isShowShadow = true;

    protected int shadowColor = Color.GREEN;

    protected int shadowRadius = 20;

    protected final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setDither(true);
            setFilterBitmap(true);
        }
    };

    protected final Rect mBouds = new Rect();
    protected final Canvas mCanvas = new Canvas();
    protected Bitmap mBitmap;
    protected ViewGroup mParent;

    public Shadow(ViewGroup parent) {
        mParent = parent;
        if (!(mParent instanceof IShadowLayout)) {
//            throw new IllegalArgumentException("this is not ShadowLayout");
        }
        this.mParent = parent;
        init();
    }


    protected void init() {
        if (mParent != null) {


            mParent.setLayerType(LAYER_TYPE_HARDWARE, mPaint);
            mParent.setWillNotDraw(false);
            mParent.setPadding(shadowRadius, shadowRadius, shadowRadius, shadowRadius);
        }
        mPaint.setMaskFilter(new BlurMaskFilter(shadowRadius, BlurMaskFilter.Blur.NORMAL));
        mClipPath = new Path();
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        mBouds.set(0, 0, r, b);
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        if (isShowShadow) {
            if (mBouds.width() * mBouds.height() == 0) {
                mBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
            } else {
                mBitmap = Bitmap.createBitmap(mBouds.width(), mBouds.height(), Bitmap.Config.ARGB_8888);
                mCanvas.setBitmap(mBitmap);
//                ((IShadowLayout) mParent).superdispatchDraw(mCanvas);


                Bitmap alphaBitmap = mBitmap.extractAlpha();
                mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

                mPaint.setColor(shadowColor);

                mCanvas.drawBitmap(alphaBitmap, 0f, 0f, mPaint);


                alphaBitmap.recycle();
            }
            if (mBitmap != null && !mBitmap.isRecycled()) {
                canvas.drawBitmap(mBitmap, 0f, 0f, mPaint);
            }
//            ((IShadowLayout) mParent).superdispatchDraw(mCanvas);
        }
    }

    @Override
    public void onDrawOver(Canvas canvas) {

    }

    @Override
    public void onDetachedFromWindow() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    Path mClipPath;

    @Override
    public boolean onClipCanvas(Canvas canvas, View child) {
        if (mClipPath != null && !mClipPath.isEmpty()) {
            canvas.clipPath(mClipPath);
        }
        return false;
    }

    public void setClipPath(Path clipPath) {
        mClipPath = clipPath;
        invalidate();
    }

    public Shadow setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
        return this;
    }

    public Shadow setShowShadow(boolean showShadow) {
        isShowShadow = showShadow;
        return this;
    }

    public Shadow setShadowRadius(int shadowRadius) {
        this.shadowRadius = shadowRadius;
        return this;
    }

    public void invalidate() {
        if (mParent.isInEditMode()) {
            return;
        }
        mParent.postInvalidate();
    }
}
