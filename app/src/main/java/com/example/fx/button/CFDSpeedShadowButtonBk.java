package com.example.fx.button;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.fx.R;

import java.util.Locale;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * ThinkSNS Plus
 * Copyright (c) 2022 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2022/05/12
 * @Email Jliuer@aliyun.com  CFDSpeedShadowButton
 * @Description
 */
public class CFDSpeedShadowButtonBk extends AppCompatTextView {

    private boolean debug = true;

    public static final String SELL = "sell";
    public static final String BUY = "buy";

    private static long lastClickTime;

    private final PriceParts priceParts;
    private final Bitmap upImage;
    private final Bitmap downImage;
    private final Bitmap upImageDisabled;
    private final Bitmap downImageDisabled;
    private final Path mPath = new Path();
    private final Paint mPaint = new Paint();
    private final Paint shadowPaint = new Paint();
    private final RectF mRectF = new RectF();
    private final RectF mUpDownRectF = new RectF();
    private final RectF mBuySellRect = new RectF();
    private final Matrix matrix = new Matrix();
    private final RectF priceRect = new RectF();
    protected final Canvas mCanvas = new Canvas();

    private double price;
    private TickIndicator indicator;
    private boolean isOrderLocked;
    private int bgColor;
    private int buySellMarkBGColor;
    private int buySellMarkTextColor;
    private int priceColor;
    private int decimalDigits;
    private int shadowColor;
    private boolean isShowShadow = true;
    private float shadowRadios;
    private CFDSpeedButtonClickListener mClickListener;

    private Bitmap shadowBitmap;
    private Path mCachePath;

    public CFDSpeedShadowButtonBk(Context context) {
        this(context, null);
    }

    public CFDSpeedShadowButtonBk(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CFDSpeedShadowButtonBk(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Style.FILL);

        setBackgroundColor(0);

        upImage = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow_rate_up);
        upImageDisabled = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow_rate_up_desable);
        downImage = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow_rate_down);
        downImageDisabled = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow_rate_up_desable);

        bgColor = Color.YELLOW;
        shadowColor = Color.GREEN;
        buySellMarkBGColor = Color.BLUE;
        priceColor = Color.BLUE;
        buySellMarkTextColor = Color.WHITE;
        price = Double.NaN;
        indicator = TickIndicator.UP;
        priceParts = new PriceParts();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        shadowRadios = Math.max(20, shadowRadios);
        shadowPaint.setMaskFilter(new BlurMaskFilter(shadowRadios, BlurMaskFilter.Blur.NORMAL));
        shadowPaint.setColor(shadowColor);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (isShowShadow) {
            if (shadowBitmap == null) {
                shadowBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            }
            mCanvas.setBitmap(shadowBitmap);
            onCustomDraw(mCanvas, true);
            Bitmap alphaBitmap = shadowBitmap.extractAlpha();
            mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            mCanvas.drawBitmap(alphaBitmap, 0f, 0f, shadowPaint);
            alphaBitmap.recycle();
            if (shadowBitmap != null && !shadowBitmap.isRecycled()) {
                canvas.drawBitmap(shadowBitmap, 0f, 0f, shadowPaint);
            }
        }
        onCustomDraw(canvas, false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int delayTime = 500;
            if (isOrderLocked || System.currentTimeMillis() - lastClickTime < delayTime) {
                return false;
            }
            if (mCachePath.isEmpty()) {
                return super.dispatchTouchEvent(event);
            } else {
                RectF rect = new RectF();
                mCachePath.computeBounds(rect, true);
                Region region = new Region();
                region.setPath(mPath, new Region((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom));
                boolean isInArea = region.contains((int) event.getX(), (int) event.getY());
                if (isInArea && mClickListener != null) {
                    mClickListener.onClick(isBuy());
                }
                return isInArea;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL) {
            lastClickTime = System.currentTimeMillis();
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (shadowBitmap != null) {
            shadowBitmap.recycle();
            shadowBitmap = null;
        }
    }

    private void onCustomDraw(Canvas canvas, boolean isDrawShadow) {
        float w = getWidth();
        float h = getHeight();

        if (w * h == 0) {
            return;
        }
        //------------------draw 梯形---------------------------------------------------------
        float radios = dpToPxFloat(10);
        float space = dpToPxFloat(20);
        float x0 = 0;
        float y0 = 0;
        // 角度
        float p = (float) Math.atan2(h, space);
        calculateShape(w, h, radios, space, x0, y0, p);
        if (isDrawShadow) {
            canvas.drawPath(mCachePath, mPaint);
            return;
        }
        float scalcX = (w - shadowRadios) / w;
        float scalcY = (h - shadowRadios) / h;
        canvas.scale(scalcX, scalcY);
        canvas.translate(shadowRadios / 2f, shadowRadios / 2f);
        canvas.drawPath(mCachePath, mPaint);

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

        if (debug) {
            mPaint.setStyle(Style.STROKE);
            mPaint.setColor(Color.RED);
            canvas.drawRect(mBuySellRect, mPaint);
        }

        mPaint.setColor(buySellMarkBGColor);
        canvas.drawCircle(mRectF.centerX(), mRectF.centerY(), markR, mPaint);

        getPaint().setColor(buySellMarkTextColor);
        getPaint().setTextSize(markR * 1.1F);
        drawTextInRect(canvas, text, mRectF, getPaint(), TextAlign.CENTER);
        getPaint().setColor(priceColor);
        //--------------------------draw up down 箭头--------------------------------------
        if (isBuy()) {
            mRectF.set(space + markX, markX, w - h / 7.0F, h - markX);
        } else {
            mRectF.set(h / 7.0F, markX, w - space - markX, h - markX);
        }

        priceRect.set(mRectF);

        if (debug) {
            canvas.drawRect(priceRect, mPaint);
        }

        float upperH;
        float left;
        if ((double) priceRect.height() * 1.3D < (double) priceRect.width()) {
            upperH = priceRect.height() * 1.3F;
            left = priceRect.centerX();
            priceRect.left = left - upperH / (float) 2;
            priceRect.right = left + upperH / (float) 2;
        }

        if (!Double.isNaN(price) && this.priceParts.getPrice() != 0.0D) {

            upperH = priceRect.height() * 2.0F / 5.0F;
            left = priceRect.width() * 4.0F / 5.0F;
            float U;

            if (this.indicator != TickIndicator.NEUTRAL) {
                float size = mBuySellRect.width() * 2 / 3;
                if (isBuy()) {
                    mUpDownRectF.set(priceRect.left, mBuySellRect.top,
                            size + priceRect.left, mBuySellRect.top + size);

                } else {
                    mUpDownRectF.set(priceRect.right - size, mBuySellRect.top, priceRect.right, mBuySellRect.top + size);
                }
                if (debug) {
                    canvas.drawRect(mUpDownRectF, mPaint);
                }
                Bitmap bitmap;
                if (isOrderLocked) {
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
            getPaint().setTextSize(upperH * 0.6F);
            if (isBuy()) {
                mRectF.set(mUpDownRectF.right, priceRect.top + y0, mBuySellRect.left, mBuySellRect.bottom);
            } else {
                mRectF.set(mBuySellRect.right, priceRect.top + y0, mUpDownRectF.left, mBuySellRect.bottom);
            }

            drawTextInRect(canvas, this.priceParts.getLeftPart(), mRectF, getPaint(), TextAlign.BOTTOM);
            if (debug) {
                canvas.drawRect(mRectF, mPaint);
            }

            //--------------------------draw CenterPart--------------------------------------

            getPaint().setTypeface(Typeface.DEFAULT_BOLD);
            U = priceRect.bottom - upperH;
            getPaint().setTextSize(Math.min(left, U));
            mRectF.set(priceRect.left, upperH + y0, priceRect.left + left, priceRect.bottom - upperH / 6);
            if (debug) {
                canvas.drawRect(mRectF, mPaint);
            }
            drawTextInRect(canvas, this.priceParts.getCenterPart(), mRectF, getPaint(), TextAlign.BOTTOM);
            //--------------------------draw RightPart--------------------------------------

            getPaint().setTextSize(left * 0.5F);
            mRectF.set(priceRect.left + left,
                    upperH + y0,
                    priceRect.right + dpToPxFloat(5), priceRect.bottom - upperH / 6);
            if (debug) {
                canvas.drawRect(mRectF, mPaint);
            }
            drawTextInRect(canvas, this.priceParts.getRightPart(), mRectF, getPaint(), TextAlign.BOTTOM);
        }
    }

    private void calculateShape(float w, float h, float radios, float space, float x0, float y0, float p) {
        if (mCachePath == null) {
            mPaint.setColor(bgColor);
            mCachePath = new Path();
            mPath.reset();
            mPath.moveTo(x0, y0);
            mPath.lineTo(w - space - radios, y0);
            float x = (float) Math.cos(p) * radios;
            float y = (float) Math.sin(p) * radios;
            mPath.quadTo(w - space, y0, w - space + x, y);
            mPath.lineTo(w - x, h - y);
            mPath.quadTo(w, h, w - radios, h);
            mPath.lineTo(radios, h);
            mPath.quadTo(x0, h, x0, h - radios);
            mPath.lineTo(x0, radios);
            mPath.quadTo(x0, y0, radios, y0);
            if (isBuy()) {
                matrix.setScale(-1.0F, 1.0F);
                matrix.postTranslate(w, 0.0F);
                mPath.transform(matrix);
            }
            mCachePath.set(mPath);
        }
    }

    private void drawTextInRect(Canvas canvas, String text, RectF rect, Paint paint, TextAlign align) {
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
                default:
            }

            canvas.drawText(text, x, y, paint);
            if (paint.getTextSize() != firstTextSize) {
                paint.setTextSize(firstTextSize);
            }
        }
    }

    private void setPrice(double value) {
        if (price == value) {
            return;
        }
        if (!Double.isNaN(price) && !Double.isNaN(value)) {
            if (price < value) {
                indicator = TickIndicator.UP;
            } else if (value < price) {
                indicator = TickIndicator.DOWN;
            }
        }
        price = value;
        priceParts.setDecimalDigits(decimalDigits);
        priceParts.setPrice(value);
    }

    public void setOnClickListener(CFDSpeedButtonClickListener clickListener) {
        mClickListener = clickListener;
    }

    private boolean isBuy() {
        return BUY.equals(getTag());
    }

    private float dpToPxFloat(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale;
    }

    public enum TickIndicator {
        // up: 上昇 down: 下落 neutral: 変化なし
        UP,
        DOWN,
        NEUTRAL,
    }

    public interface CFDSpeedButtonClickListener {
        void onClick(boolean isBuy);
    }

    enum TextAlign {
        // 文字对齐方式
        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
        CENTER
    }

    static final class PriceParts {

        // 小数位数
        private int decimalDigits;

        // 无效数值  Not-a-Number (NaN)
        private double price = Double.NaN;

        // 整数
        private String leftPart = "";

        // 中间小数
        private String centerPart = "";

        // 右边小数
        private String rightPart = "";


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

    public static class Builder {
        private double price;
        private TickIndicator indicator;
        private boolean isOrderLocked;
        private boolean isShowShadow;
        private int bgColor;
        private int buySellMarkBGColor;
        private int buySellMarkTextColor;
        private int decimalDigits;
        private int shadowColor;
        private float shadowRadios;
        private int priceColor;

        public Builder() {
        }

        public Builder(Builder builder) {
            bgColor = builder.bgColor;
            priceColor = builder.priceColor;
            shadowRadios = builder.shadowRadios;
            shadowColor = builder.shadowColor;
            decimalDigits = builder.decimalDigits;
            buySellMarkTextColor = builder.buySellMarkTextColor;
            buySellMarkBGColor = builder.buySellMarkBGColor;
            isOrderLocked = builder.isOrderLocked;
            indicator = builder.indicator;
            price = builder.price;
        }

        public Builder buildPrice(double price) {
            this.price = price;
            return this;
        }

        public Builder buildIndicator(TickIndicator indicator) {
            this.indicator = indicator;
            return this;
        }

        public Builder buildOrderLocked(boolean orderLocked) {
            isOrderLocked = orderLocked;
            return this;
        }

        public Builder buildBgColor(int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public Builder buildBuySellMarkBGColor(int buySellMarkBGColor) {
            this.buySellMarkBGColor = buySellMarkBGColor;
            return this;
        }

        public Builder buildBuySellMarkTextColor(int buySellMarkTextColor) {
            this.buySellMarkTextColor = buySellMarkTextColor;
            return this;
        }

        public Builder buildDecimalDigits(int decimalDigits) {
            this.decimalDigits = decimalDigits;
            return this;
        }

        public Builder buildShadowColor(int shadowColor) {
            this.shadowColor = shadowColor;
            return this;
        }

        public Builder buildShadowRadios(float shadowRadios) {
            this.shadowRadios = shadowRadios;
            return this;
        }

        public Builder buildPriceColor(int priceColor) {
            this.priceColor = priceColor;
            return this;
        }

        public Builder buildShowShadow(boolean showShadow) {
            isShowShadow = showShadow;
            return this;
        }

        public void updateData(CFDSpeedShadowButtonBk button) {
            button.isShowShadow = isShowShadow;
            button.bgColor = bgColor;
            button.priceColor = priceColor;
            button.shadowRadios = shadowRadios;
            button.shadowColor = shadowColor;
            button.decimalDigits = decimalDigits;
            button.buySellMarkTextColor = buySellMarkTextColor;
            button.buySellMarkBGColor = buySellMarkBGColor;
            button.isOrderLocked = isOrderLocked;
            button.indicator = indicator;
            button.setPrice(price);
            button.postInvalidate();
        }
    }

}