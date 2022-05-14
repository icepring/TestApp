package com.example.fx;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * Create by y.tang0 on 2022/05/11
 * Description: This is DemoText
 */
public class DemoText extends AppCompatTextView {

    private Path path;


    public DemoText(Context context) {
        super(context);
        path = new Path();
    }

    public DemoText(Context context,  AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
    }

    public DemoText(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        path = new Path();
    }


    int r = 40;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getPaint().setAntiAlias(true);

        float w = (float) this.getWidth();
        float h = (float) this.getHeight();

        float tw = w * 4 / 5;
        float th = h;

        float fr = r/(float) Math.sqrt(2d);
        if ("left".equals(getTag())) {
            getLfetPoint(r,tw,fr,w,th,canvas);
        }else{
            getRightPoint(r,tw,fr,w,th,canvas);
        }
        canvas.drawPath(path, getPaint());


        double x1 = 0,y1=0;
        double x2 = tw,y2=0;
        double x3 = w,y3=h;

        double a = Math.sqrt((x2-x3)*(x2-x3)+(y2-y3)*(y2-y3));
        double b = Math.sqrt((x1-x3)*(x1-x3)+(y1-y3)*(y1-y3));
        double c = Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));

        double A = Math.toDegrees(Math.acos((a*a-b*b-c*c)/(-2*b*c)));
        double B = Math.toDegrees(Math.acos((b*b-a*a-c*c)/(-2*a*c)));
        double C = Math.toDegrees(Math.acos((c*c-a*a-b*b)/(-2*a*b)));

        System.out.println("A**"+A);
        System.out.println("B**"+B);
        System.out.println("C**"+C);
    }

    Path getLfetPoint(float r,float tw ,float fr,float w,float th,Canvas canvas){

        path.reset();

        // left top
        path.moveTo(0, r);
        path.quadTo(0, 0, r, 0);

        // right top
        path.lineTo(tw - r, 0);
        path.quadTo(tw +r/5, 0, tw + fr, r);

        // right bottom
        path.lineTo(w - r, th - r);
        path.quadTo(w-r, th, w - 2*r, th);

        // left bottom
        path.lineTo(r, th);
        path.quadTo(0, th, 0, th - r);

        path.lineTo(0, r);

        return path;
    }

    Path getRightPoint(float r,float tw ,float fr,float w,float th,Canvas canvas){
        // left top
        path.reset();
        path.moveTo(w, r);

        path.quadTo(w, 0, w-r, 0);

        // right top
        path.lineTo(w-tw + r, 0);
        path.quadTo(w-tw + r -fr/2, 0, w-tw + r - fr, r);

        // right bottom
        path.lineTo(r, th - r);
        path.quadTo(r, th,  2*r, th);

        // left bottom
        path.lineTo(w-r, th);
        path.quadTo(w, th, w, th - r);

        path.lineTo(w, r);

        return path;
    }

    private float toDeg(float start, float pi2) {
        return start / pi2 * 90f;
    }

    private float dpToPxFloat(int dp) {
        return getResources().getDisplayMetrics().density * dp;
    }
}
