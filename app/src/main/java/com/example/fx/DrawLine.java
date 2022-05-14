package com.example.fx;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * Create by y.tang0 on 2022/05/12
 * Description: This is DrawLine
 */
public class DrawLine extends AppCompatTextView {

    private Path path;
    private Paint mPaint;
    private Object tag;
    private RectF mRectF;
    private Matrix matrix;

    public DrawLine(Context context) {
        this(context,null);
    }

    public DrawLine(Context context,  AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DrawLine(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tag = getTag();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mRectF = new  RectF();
        matrix = new Matrix();
        path = new Path();
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = (float) this.getWidth();
        float h = (float) this.getHeight();

        float tw = w * 4 / 5;
        float th = h;

//        if ("left".equals(tag)) {
//            getLfetPoint(r, tw, fr, w, th);
//        } else {
//            getRightPoint(r, tw, fr, w, th);
//        }





        float pi = (float) 3.141592653589793D;
        final float pi2 = pi / 2.0F;
        float a = dpToPxFloat(20);
        float xa = w - a;
        float y0 = dpToPxFloat(5);
        float r = dpToPxFloat(10);
        float y1 = h - r;
        float P = (float) Math.atan2(h, a);
        float Q = (float) 3.141592653589793D - P;
        float x1 = w - r;
        path.moveTo(x1, h);
        path.lineTo(r, h);

        path.quadTo(0.0F, h, 0.0F, y1);
        path.lineTo(0.0F, y0 + r);

        path.quadTo(0.0F, y0, r, y0);
        path.lineTo(xa - r, y0);

        float start = pi2 * 3.0F;
        mRectF.set(xa - r * (float) 2, y0, xa, y0 + r);
        mPaint.setColor(Color.BLUE);
        canvas.drawRect(mRectF,mPaint);
        path.arcTo(mRectF, toDeg(start, pi2), toDeg(P, pi2));

//        path.moveTo(r, h);
//        path.lineTo(x1, h);
//
//        float start = pi2 * 3.0F;
//        mRectF.set(x1-r, y0, x1+r, y0 + r);
//        mPaint.setColor(Color.BLUE);
//        canvas.drawRect(mRectF,mPaint);
//        path.arcTo(mRectF, toDeg(start, pi2), toDeg(P, pi2));

//        path.quadTo(w, h, w, y1);
//        path.lineTo(w, y0 + r);
//
//        path.quadTo(w, y0, x1, y0);
//        path.lineTo(a+r, y0);
//
//        float start = pi2 * 3.0F;
//        mRectF.set( a, y0, a+r, y0 + r);
//        mPaint.setColor(Color.BLUE);
//        canvas.drawRect(mRectF,mPaint);
//        path.arcTo(mRectF, toDeg(start, pi2), toDeg(P, pi2));

//
        float s2 = r / (float) 2;
        float lx = x1 + s2 * (float) Math.cos(P);
        float ly = h - s2 * (float) Math.sin(P);
        path.lineTo(lx, ly);

        start = P / (float) 2;
        mRectF.set(lx - (float) 2 * s2, h - s2 * (float) 2, lx, h);
        path.arcTo(mRectF, toDeg(start, pi2), toDeg(P, pi2));
        path.close();

        mPaint.setColor(Color.RED);
        mPaint.setStyle(Style.FILL_AND_STROKE);
        mPaint.setTextSize(50);
        mPaint.setAntiAlias(true);
        canvas.drawPath(path,mPaint);
    }

    private float dpToPxFloat(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale;
    }

    private float toDeg(float a, float pi2) {
        return a / pi2 * 90f;
    }
}
