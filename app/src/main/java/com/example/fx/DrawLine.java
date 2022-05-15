package com.example.fx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * 線を引く
 * Create by y.tang0 on 2022/05/12
 * Description: This is DrawLine
 *
 * @author 小天
 * @date 2022/05/15
 */
public class DrawLine extends AppCompatTextView {

    /**
     * 道
     */
    private Path path;
    /**
     * mペイント
     */
    private Paint mPaint;
    /**
     * 鬼ごっこ
     */
    private Object tag;
    /**
     * m rectf
     */
    private RectF mRectF;
    /**
     * マトリックス
     */
    private Matrix matrix;

    /**
     * 線を引く
     *
     * @param context コンテクスト
     */
    public DrawLine(Context context) {
        this(context,null);
    }

    /**
     * 線を引く
     *
     * @param context コンテクスト
     * @param attrs   attrs
     */
    public DrawLine(Context context,  AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 線を引く
     *
     * @param context      コンテクスト
     * @param attrs        attrs
     * @param defStyleAttr defスタイル属性
     */
    public DrawLine(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tag = getTag();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mRectF = new  RectF();
        matrix = new Matrix();
        path = new Path();
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        mPaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL));
    }

    /**
     * ドロー時
     *
     * @param canvas キャンバス
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Bitmap mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mBitmap.eraseColor(Color.YELLOW);
        Bitmap alphaBitmap = mBitmap.extractAlpha();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mPaint.setColor(Color.GREEN);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        canvas.drawBitmap(alphaBitmap, 0f, 0f, mPaint);
    }

    /**
     * dpからpxfloat
     *
     * @param dp dp
     * @return float
     */
    private float dpToPxFloat(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale;
    }

    /**
     * 度に
     *
     * @param a   a
     * @param pi2 pi2
     * @return float
     */
    private float toDeg(float a, float pi2) {
        return a / pi2 * 90f;
    }
}
