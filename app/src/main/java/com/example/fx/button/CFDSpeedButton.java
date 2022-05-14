package com.example.fx.button;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.fx.R;

import java.util.Locale;

/**
 * ThinkSNS Plus
 * Copyright (c) 2022 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2022/05/12
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CFDSpeedButton extends AppCompatTextView {
    private double rate;
    private PriceParts priceParts;
    private TickIndicator indicator;
    private boolean isOrderLocked;

    private Bitmap upImage;
    private Bitmap downImage;
    private Bitmap upImageDisabled;
    private Bitmap downImageDisabled;
    private int bgColor;
    private int buySellMarkBGColor;
    private int buySellMarkTextColor;
    private int decimalDigits;

    public CFDSpeedButton(Context context) {
        this(context, null);
    }

    public CFDSpeedButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CFDSpeedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Style.FILL);
        mPaint.setStrokeWidth(5);

        setBackgroundColor(0);

        upImage = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow_rate_up);
        upImageDisabled = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow_rate_up_desable);
        downImage = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow_rate_down);
        downImageDisabled = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow_rate_up_desable);

        bgColor = Color.YELLOW;
        buySellMarkBGColor = Color.BLUE;
        buySellMarkTextColor = Color.RED;
        rate = Double.NaN;
        indicator = TickIndicator.UP;
        priceParts = new PriceParts();
    }

    Path mPath = new Path();
    Paint mPaint = new Paint();

    RectF mRectF = new RectF();
    RectF mUpDownRectF = new RectF();
    RectF mBuySellRect = new RectF();
    Matrix matrix = new Matrix();
    RectF rateRect = new RectF();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();

        if (w * h == 0) {
            return;
        }

        mPath.reset();
        //------------------draw 梯形---------------------------------------------------------
        float radios = dpToPxFloat(10);
        float pi = (float) Math.PI;
        final float pi2 = pi / 2.0F;
        float space = dpToPxFloat(20);


        float x0 = 0;
        float y0 = dpToPxFloat(5);

        float topRightX = w - space;
        float bottomRightX = w - radios;

        // 角度
        float P = (float) Math.atan2(h, space);

        // rightBottom
        mPath.moveTo(bottomRightX, h);

        // leftBottom
        mPath.lineTo(radios, h);
        mPath.quadTo(x0, h, x0, h - radios);

        // leftTop
        mPath.lineTo(x0, y0 + radios);
        mPath.quadTo(x0, y0, radios, y0);

        // rightTop
        mPath.lineTo(topRightX - radios, y0);
        float start = pi2 * 3.0F;
        mRectF.set(topRightX - radios * 2, y0, topRightX, y0 + radios);
        mPath.arcTo(mRectF, (float) Math.toDegrees(start), (float) Math.toDegrees(P));

        // rightBottom
        float radios2 = radios / 2;
        float lx = bottomRightX + radios2 * (float) Math.cos(P);
        float ly = h - radios2 * (float) Math.sin(P);
        mPath.lineTo(lx, ly);
        start = P / 2;
        mRectF.set(lx - 2 * radios2, h - radios2 * 2, lx, h);
        mPath.arcTo(mRectF, (float) Math.toDegrees(start), (float) Math.toDegrees(P));

        mPath.close();
        if (isBuy()) {
            matrix.setScale(-1.0F, 1.0F);
            matrix.postTranslate(w, 0.0F);
            mPath.transform(matrix);
        }
        mPaint.setColor(bgColor);
        canvas.drawPath(mPath, mPaint);

        //-----------------------draw 买卖标识-----------------------------------------
        float markR;
        float markX;
        markX = dpToPxFloat(4);
        markR = h / 8.0F;
        String text;
        if (isBuy()) {
            text = "買";
            mRectF.set(w - (markX + markR * 2.0F), markX + y0, w - markX, markX + markR * 2.0F + y0);
        } else {
            text = "売";
            mRectF.set(markX, markX + y0, markX + markR * 2.0F, markX + markR * 2.0F + y0);
        }
        mBuySellRect.set(mRectF);


        mPaint.setStyle(Style.STROKE);
        canvas.drawRect(mBuySellRect,mPaint);

        mPaint.setColor(buySellMarkBGColor);
        canvas.drawCircle(mRectF.centerX(), mRectF.centerY(), markR, mPaint);

        mPaint.setColor(buySellMarkTextColor);
        mPaint.setTextSize(markR * 1.1F);
        drawTextInRect(canvas, text, mRectF, mPaint, TextAlign.CENTER);

        //--------------------------draw up down 箭头--------------------------------------
        if (isBuy()) {
            mRectF.set(space + markX, markX, w - h / 7.0F, h - markX);
        } else {
            mRectF.set(h / 7.0F, markX, w - space - markX, h - markX);
        }

        rateRect.set(mRectF);
        canvas.drawRect(rateRect,mPaint);
        float upperH;
        float left;
        if ((double) rateRect.height() * 1.3D < (double) rateRect.width()) {
            upperH = rateRect.height() * 1.3F;
            left = rateRect.centerX();
            rateRect.left = left - upperH / (float) 2;
            rateRect.right = left + upperH / (float) 2;
        }

        if (!Double.isNaN(rate) && this.priceParts.getPrice() != 0.0D) {

            upperH = rateRect.height() * 2.0F / 5.0F;
            left = rateRect.width() * 4.0F / 5.0F;
            float U;

            if (this.indicator != TickIndicator.NEUTRAL) {
                float SIZE = mBuySellRect.width() * 2 / 3;
                if (isBuy()) {
                    mUpDownRectF.set(rateRect.left, mBuySellRect.top,
                            SIZE + rateRect.left, mBuySellRect.top + SIZE);

                } else {
                    mUpDownRectF.set(rateRect.right - SIZE, mBuySellRect.top, rateRect.right, mBuySellRect.top + SIZE);
                }
                canvas.drawRect(mUpDownRectF,mPaint);
                Bitmap bitmap;
                if (isOrderLocked()) {
                    bitmap = this.indicator == TickIndicator.UP ? this.upImageDisabled :
                            this.indicator == TickIndicator.DOWN ? this.downImageDisabled : null;
                } else {
                    bitmap = this.indicator == TickIndicator.UP ? this.upImage :
                            this.indicator == TickIndicator.DOWN ? this.downImage : null;
                }
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, null, mUpDownRectF, mPaint);
                }
            }

            //--------------------------draw LeftPart--------------------------------------
            mPaint.setTextSize(upperH * 0.6F);
            if (isBuy()) {
                mRectF.set(mUpDownRectF.right, rateRect.top + y0, mBuySellRect.left, mBuySellRect.bottom);
            } else {
                mRectF.set(mBuySellRect.right, rateRect.top + y0, mUpDownRectF.left, mBuySellRect.bottom);
            }

            drawTextInRect(canvas, this.priceParts.getLeftPart(), mRectF, mPaint, TextAlign.BOTTOM);
            canvas.drawRect(mRectF,mPaint);


            //--------------------------draw CenterPart--------------------------------------

            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            U = rateRect.bottom - upperH;
            mPaint.setTextSize(Math.min(left, U));
            mRectF.set(rateRect.left, upperH + y0, rateRect.left + left, rateRect.bottom - upperH / 6);
            canvas.drawRect(mRectF,mPaint);

            drawTextInRect(canvas, this.priceParts.getCenterPart(), mRectF, mPaint, TextAlign.BOTTOM);
            //--------------------------draw RightPart--------------------------------------

            mPaint.setTextSize(left * 0.5F);
            mRectF.set(rateRect.left + left,
                    upperH + y0,
                    rateRect.right + dpToPxFloat(5), rateRect.bottom - upperH / 6);
            canvas.drawRect(mRectF,mPaint);

            drawTextInRect(canvas, this.priceParts.getRightPart(), mRectF, mPaint, TextAlign.BOTTOM);
        }
    }

    public final boolean isOrderLocked() {
        return isOrderLocked;
    }

    public final void setOrderLocked(boolean value) {
        isOrderLocked = value;
        invalidate();
    }

    public final double getRate() {
        return rate;
    }

    public final void setRate(double value) {
        if (rate == value) {
            return;
        }
        if (!Double.isNaN(rate) && !Double.isNaN(value)) {
            if (rate < value) {
                indicator = TickIndicator.UP;
            } else if (value < rate) {
                indicator = TickIndicator.DOWN;
            }
        }
        rate = value;
        priceParts.setDecimalDigits(3);
        priceParts.setPrice(value);
        invalidate();
    }

    private boolean isBuy() {
        return "buy".equals(getTag());
    }

    private float dpToPxFloat(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale;
    }

    private static void drawTextInRect(Canvas canvas, String text, RectF rect, Paint paint, TextAlign align) {
        float x, y;
        if (text.length() != 0) {
            float firstTextSize = paint.getTextSize();
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            float originalTextSize;
            float finalTextSize;
            if (rect.width() < (float) bounds.width()) {
                originalTextSize = paint.getTextSize();
                finalTextSize = originalTextSize * rect.width() / (float) bounds.width() * 0.95F;
                paint.setTextSize(finalTextSize);
                paint.getTextBounds(text, 0, text.length(), bounds);
            }

            if (rect.height() < (float) bounds.height()) {
                originalTextSize = paint.getTextSize();
                finalTextSize = originalTextSize * rect.height() / (float) bounds.height() * 0.95F;
                paint.setTextSize(finalTextSize);
                paint.getTextBounds(text, 0, text.length(), bounds);
            }

            paint.getTextBounds(text, 0, text.length(), bounds);
            int textWidth = bounds.width();
            int textHeight = bounds.height();

            x = rect.centerX() - (float) (textWidth / 2);
            y = rect.centerY() - (float) (textHeight / 2) + (float) textHeight - paint.getFontMetrics().descent / 2;
            switch (align) {
                case LEFT:
                    x = rect.left;
                    break;
                case RIGHT:
                    x = rect.right - textWidth;
                    break;
                case TOP:
                    y = rect.top + textHeight;
                    break;
                case BOTTOM:
                    y = rect.bottom;
                    break;
            }

            canvas.drawText(text, x, y, paint);
            if (paint.getTextSize() != firstTextSize) {
                paint.setTextSize(firstTextSize);
            }
        }
    }


    enum TickIndicator {
        // up: 上昇 down: 下落 neutral: 変化なし
        UP,
        DOWN,
        NEUTRAL,
    }

    enum TextAlign {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
        CENTER
    }

    static final class PriceParts {

        // 小数点
        private int decimalDigits;


        private double price = Double.NaN;

        private String leftPart = "";

        private String rightPart = "";

        private String centerPart = "";

        public final int getDecimalDigits() {
            return decimalDigits;
        }

        public final void setDecimalDigits(int value) {
            decimalDigits = value;
            if (Double.isNaN(price)) {
                return;
            }
            setPrice(price);
        }

        public final double getPrice() {
            return price;
        }

        public final void setPrice(double value) {
            price = value;
            if (Double.isNaN(price)) {
                rightPart = "";
                centerPart = "";
                leftPart = "";
            } else {
                String format = "%." + decimalDigits + 'f';
                String s = String.format(Locale.JAPAN, format, value);
                int len = s.length();
                if (len < 4) {
                    s = "0000";
                    len = 4;
                }

                // 123.456
                // 0123456
                // R=6,7
                // C=4,5
                rightPart = s.substring(len - 1, len);
                centerPart = s.substring(len - 3, len - 1);
                leftPart = s.substring(0, len - 3);
            }
        }

        public final boolean isNaN() {
            double var1 = price;
            return Double.isNaN(var1);
        }

        public final String getLeftPart() {
            return leftPart;
        }

        public final String getRightPart() {
            return rightPart;
        }

        public final String getCenterPart() {
            return centerPart;
        }
    }

}