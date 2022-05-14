package com.example.fx.shadow;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;


/**
 * Create by y.tang0 on 2022/04/10
 * Description:
 */
public class ShadowLinearLayout extends LinearLayout implements IShadowLayout {

    private IShadowViewEvent mShadowViewEvent;


    private Object Tag;


    public ShadowLinearLayout(Context context) {
        super(context);
        init();
    }

    public ShadowLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShadowLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mShadowViewEvent = new Shadow(this);
        Tag = getTag();
    }

    @Override
    public void superdispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mShadowViewEvent.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mShadowViewEvent.onDetachedFromWindow();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mShadowViewEvent.onDraw(canvas);
        mShadowViewEvent.onDrawOver(canvas);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        try {
            canvas.save();
            return mShadowViewEvent.onClipCanvas(canvas, child) & super.drawChild(canvas, child, drawingTime);
        } finally {
            canvas.restore();
        }
    }

    public IShadowViewEvent getShadowViewEvent() {
        return mShadowViewEvent;
    }
}
