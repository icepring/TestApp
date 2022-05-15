package com.example.fx.shadow;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.view.View;
import android.view.ViewGroup;

import static android.view.View.LAYER_TYPE_HARDWARE;

/**
 * ThinkSNS Plus
 * Copyright (c) 2022 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2022/05/15
 * @Email Jliuer@aliyun.com
 * @Description
 */
class PathShadow implements IShadowViewEvent{

    protected boolean isShowShadow = true;

    protected int shadowColor = Color.GREEN;

    protected ViewGroup mParent;
    private Paint mPaint;
    Path mPath;
    Path mClipPath;
    Path mShadowpath;
    float mShadowRadius;

    public PathShadow(ViewGroup parent) {
        mParent = parent;
        mParent = parent;
        if (!(mParent instanceof IShadowLayout)) {
            throw new IllegalArgumentException("this is not ShadowLayout");
        }
        this.mParent = parent;
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPath = new Path();
        mClipPath = new Path();
        mShadowpath = new Path();
        mShadowRadius = 25;
        mPaint.setMaskFilter(new BlurMaskFilter(mShadowRadius, BlurMaskFilter.Blur.NORMAL));
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public void onDraw(Canvas canvas) {

    }

    @Override
    public void onDrawOver(Canvas canvas) {
        canvas.save();
        canvas.clipPath(mPath, Region.Op.REPLACE);
        mPaint.setColor(Color.RED);
        mShadowpath.set(mClipPath);
        canvas.clipPath(mShadowpath, Region.Op.DIFFERENCE);
//        mShadowpath.offset(mOffsetDx, mOffsetDy);
        canvas.drawPath(mShadowpath, mPaint);
        canvas.restore();
    }

    @Override
    public boolean onClipCanvas(Canvas canvas, View child) {
        return false;
    }

    @Override
    public void onDetachedFromWindow() {

    }
}
