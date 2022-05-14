package com.example.fx.shadow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.example.fx.R;

/**
 * Create by y.tang0 on 2022/05/12
 * Description: This is SpeedButton
 */
public class SpeedButton extends RelativeLayout {

    private Path path;
    private Paint mPaint;
    private Object tag;
    private RectF mRectF;
    private Matrix matrix;

    public SpeedButton(Context context) {
        this(context, null);
    }

    public SpeedButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        tag = getTag();
        LayoutInflater.from(getContext()).inflate("left".equals(tag) ?
                R.layout.speed_left : R.layout.speed_right, this);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mRectF = new  RectF();
        matrix = new Matrix();
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = (float) this.getWidth();
        float h = (float) this.getHeight();

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
        path.arcTo(mRectF, toDeg(start, pi2), toDeg(P, pi2));

        float s2 = r / (float) 2;
        float lx = x1 + s2 * (float) Math.cos(P);
        float ly = h - s2 * (float) Math.sin(P);
        path.lineTo(lx, ly);

        start = P / (float) 2;
        mRectF.set(lx - (float) 2 * s2, h - s2 * (float) 2, lx, h);
        path.arcTo(mRectF, toDeg(start, pi2), toDeg(P, pi2));
        path.close();
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Style.FILL);
        if ("right".equals(tag)) {

            matrix.preScale(-1.0F, 1.0F);
            matrix.preTranslate(w, 0.0F);
            path.transform(matrix);

            mPaint.setColor(Color.RED);

//            canvas.drawPoint(50,50,mPaint);
            mPaint.setTextSize(50);
//            canvas.drawText("tttt",50,50,mPaint);
//            canvas.drawCircle(100,100,50,mPaint);








        }
        canvas.drawPath(path, mPaint);




    }

    private float dpToPxFloat(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale;
    }

    private float toDeg(float a, float pi2) {
        return a / pi2 * 90f;
    }

    Path getLfetPoint(float r, float tw, float fr, float w, float th) {
        path.reset();
        path.moveTo(0, r);


        path.quadTo(0, 0, r, 0);

        // right top
        path.lineTo(tw - r, 0);
        path.quadTo(tw + fr / 2, 0, tw + fr, r);

        // right bottom
        path.lineTo(w - r, th - r);
        path.quadTo(w - r, th, w - 2 * r, th);

        // left bottom
        path.lineTo(r, th);
        path.quadTo(0, th, 0, th - r);

        path.lineTo(0, r);

        return path;
    }

    Path getRightPoint(float r, float tw, float fr, float w, float th) {
        // left top

        path.reset();
        path.moveTo(w, r);


        path.quadTo(w, 0, w - r, 0);

        // right top
        path.lineTo(w - tw + r, 0);
        path.quadTo(w - tw + r - fr / 2, 0, w - tw + r - fr, r);

        // right bottom
        path.lineTo(r, th - r);
        path.quadTo(r, th, 2 * r, th);

        // left bottom
        path.lineTo(w - r, th);
        path.quadTo(w, th, w, th - r);

        path.lineTo(w, r);

        return path;
    }
}
