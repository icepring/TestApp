package com.example.fx.shadow;

import android.graphics.Canvas;
import android.view.View;

/**
 * Create by y.tang0 on 2022/04/11
 * Description:
 */
public interface IShadowViewEvent {

    void onLayout(boolean changed, int l, int t, int r, int b);

    void onDraw(Canvas canvas);

    void onDrawOver(Canvas canvas);

    boolean onClipCanvas(Canvas canvas, View child);

    void onDetachedFromWindow();
}
