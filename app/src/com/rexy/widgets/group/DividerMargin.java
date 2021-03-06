package com.rexy.widgets.group;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.rexy.widgetlayout.R;

/**
 * 描述 Divider 和 margin 的信息类,可独立画divider。目前只支持纯色divider。
 * 具体属性见
 <!--左边线的颜色，宽度，和边线padding-->
 <attr name="borderLeftColor" format="color"/>
 <attr name="borderLeftWidth" format="dimension"/>
 <attr name="borderLeftMargin" format="dimension"/>
 <attr name="borderLeftMarginStart" format="dimension"/>
 <attr name="borderLeftMarginEnd" format="dimension"/>

 <!--上边线的颜色，宽度，和边线padding-->
 <attr name="borderTopColor" format="color"/>
 <attr name="borderTopWidth" format="dimension"/>
 <attr name="borderTopMargin" format="dimension"/>
 <attr name="borderTopMarginStart" format="dimension"/>
 <attr name="borderTopMarginEnd" format="dimension"/>

 <!--右边线的颜色，宽度，和边线padding-->
 <attr name="borderRightColor" format="color"/>
 <attr name="borderRightWidth" format="dimension"/>
 <attr name="borderRightMargin" format="dimension"/>
 <attr name="borderRightMarginStart" format="dimension"/>
 <attr name="borderRightMarginEnd" format="dimension"/>

 <!--下边线的颜色，宽度，和边线padding-->
 <attr name="borderBottomColor" format="color"/>
 <attr name="borderBottomWidth" format="dimension"/>
 <attr name="borderBottomMargin" format="dimension"/>
 <attr name="borderBottomMarginStart" format="dimension"/>
 <attr name="borderBottomMarginEnd" format="dimension"/>

 <!--内容四边的间距，不同于padding -->
 <attr name="contentMarginLeft" format="dimension"/>
 <attr name="contentMarginTop" format="dimension"/>
 <attr name="contentMarginRight" format="dimension"/>
 <attr name="contentMarginBottom" format="dimension"/>
 <!--水平方向和垂直方向Item 的间距-->
 <attr name="contentMarginMiddleHorizontal" format="dimension"/>
 <attr name="contentMarginMiddleVertical" format="dimension"/>


 <!--水平分割线颜色-->
 <attr name="dividerColorHorizontal" format="color"/>
 <!--水平分割线宽-->
 <attr name="dividerWidthHorizontal" format="dimension"/>
 <!--水平分割线开始和结束padding-->
 <attr name="dividerPaddingHorizontal" format="dimension"/>
 <attr name="dividerPaddingHorizontalStart" format="dimension"/>
 <attr name="dividerPaddingHorizontalEnd" format="dimension"/>

 <!--垂直分割线颜色-->
 <attr name="dividerColorVertical" format="color"/>
 <!--垂直分割线宽-->
 <attr name="dividerWidthVertical" format="dimension"/>
 <!--垂直分割线开始 和结束padding-->
 <attr name="dividerPaddingVertical" format="dimension"/>
 <attr name="dividerPaddingVerticalStart" format="dimension"/>
 <attr name="dividerPaddingVerticalEnd" format="dimension"/>
 *
 * @author: rexy
 * @date: 2017-06-02 10:26
 */
public class DividerMargin {
    private Paint mPaintHorizontal;
    private Paint mPaintVertical;
    private Paint mPaintBorder;

    private boolean mResetPaintHorizontal = true;
    private boolean mResetPaintVertical = true;

    float mDensity=1;

    //左边线的颜色，宽度，和边线padding
    int mBorderLeftColor;
    int mBorderLeftWidth;
    int mBorderLeftMarginStart;
    int mBorderLeftMarginEnd;

    //上边线的颜色，宽度，和边线padding
    int mBorderTopColor;
    int mBorderTopWidth;
    int mBorderTopMarginStart;
    int mBorderTopMarginEnd;

    //右边线的颜色，宽度，和边线padding
    int mBorderRightColor;
    int mBorderRightWidth;
    int mBorderRightMarginStart;
    int mBorderRightMarginEnd;

    //下边线的颜色，宽度，和边线padding
    int mBorderBottomColor;
    int mBorderBottomWidth;
    int mBorderBottomMarginStart;
    int mBorderBottomMarginEnd;

    private int mContentMarginLeft=0;
    private int mContentMarginTop=0;
    private int mContentMarginRight=0;
    private int mContentMarginBottom=0;
    //水平方向Item 的间距
    private int mContentMarginMiddleHorizontal = 0;
    //垂直方向Item 的间距
    private int mContentMarginMiddlVertical = 0;

    //水平分割线颜色
    private int mDividerColorHorizontal = 0;
    //水平分割线宽
    private int mDividerWidthHorizontal = 0;

    //水平分割线开始padding
    private int mDividerPaddingHorizontalStart = 0;
    //水平分割线结束padding
    private int mDividerPaddingHorizontalEnd = 0;

    //垂直分割线颜色
    private int mDividerColorVertical = 0;
    //垂直分割线宽
    private int mDividerWidthVertical = 0;
    //垂直分割线开始padding
    private int mDividerPaddingVerticalStart = 0;
    //垂直分割线结束padding
    private int mDividerPaddingVerticalEnd = 0;

    private DividerMargin(float density) {
        mDensity=density;
        mContentMarginMiddleHorizontal *= density;
        mContentMarginMiddlVertical *= density;
        mDividerWidthHorizontal = (int) (0.5f + density * 0.5f);
        mDividerWidthVertical = (int) (0.5f + density * 0.5f);
        mDividerPaddingHorizontalStart *= density;
        mDividerPaddingVerticalStart *= density;
        mDividerPaddingHorizontalEnd *= density;
        mDividerPaddingVerticalEnd *= density;
        mBorderLeftWidth = (int) (0.5f + density * 0.5f);
        mBorderLeftMarginStart *= density;
        mBorderLeftMarginEnd *= density;
        mBorderTopWidth = (int) (0.5f + density * 0.5f);
        mBorderTopMarginStart *= density;
        mBorderTopMarginEnd *= density;
        mBorderRightWidth = (int) (0.5f + density * 0.5f);
        mBorderRightMarginStart *= density;
        mBorderRightMarginEnd *= density;
        mBorderBottomWidth = (int) (0.5f + density * 0.5f);
        mBorderBottomMarginStart *= density;
        mBorderBottomMarginEnd *= density;
        mContentMarginLeft*=density;
        mContentMarginTop*=density;
        mContentMarginRight*=density;
        mContentMarginBottom*=density;
    }

    public int dip(float dip){
       return  (int) (0.5f + dip * mDensity);
    }

    public void setBorderLeftColor(int color) {
        if (mBorderLeftColor != color) {
            mBorderLeftColor = color;
        }
    }

    public void setBorderLeftWidth(int width) {
        if (mBorderLeftWidth != width) {
            mBorderLeftWidth = width;
        }
    }

    public void setBorderLeftMarginStart(int marginStart) {
        if (mBorderLeftMarginStart != marginStart) {
            mBorderLeftMarginStart = marginStart;
        }
    }

    public void setBorderLeftMarginEnd(int marginEnd) {
        if (mBorderLeftMarginEnd != marginEnd) {
            mBorderLeftMarginEnd = marginEnd;
        }
    }

    public void setBorderTopColor(int color) {
        if (mBorderTopColor != color) {
            mBorderTopColor = color;
        }
    }

    public void setBorderTopWidth(int width) {
        if (mBorderTopWidth != width) {
            mBorderTopWidth = width;
        }
    }

    public void setBorderTopMarginStart(int marginStart) {
        if (mBorderTopMarginStart != marginStart) {
            mBorderTopMarginStart = marginStart;
        }
    }

    public void setBorderTopMarginEnd(int marginEnd) {
        if (mBorderTopMarginEnd != marginEnd) {
            mBorderTopMarginEnd = marginEnd;
        }
    }

    public void setBorderRightColor(int color) {
        if (mBorderRightColor != color) {
            mBorderRightColor = color;
        }
    }

    public void setBorderRightWidth(int width) {
        if (mBorderRightWidth != width) {
            mBorderRightWidth = width;
        }
    }

    public void setBorderRightMarginStart(int marginStart) {
        if (mBorderRightMarginStart != marginStart) {
            mBorderRightMarginStart = marginStart;
        }
    }

    public void setBorderRightMarginEnd(int marginEnd) {
        if (mBorderRightMarginEnd != marginEnd) {
            mBorderRightMarginEnd = marginEnd;
        }
    }

    public void setBorderBottomColor(int color) {
        if (mBorderBottomColor != color) {
            mBorderBottomColor = color;
        }
    }

    public void setBorderBottomWidth(int width) {
        if (mBorderBottomWidth != width) {
            mBorderBottomWidth = width;
        }
    }

    public void setBorderBottomMarginStart(int marginStart) {
        if (mBorderBottomMarginStart != marginStart) {
            mBorderBottomMarginStart = marginStart;
        }
    }

    public void setBorderBottomMarginEnd(int marginEnd) {
        if (mBorderBottomMarginEnd != marginEnd) {
            mBorderBottomMarginEnd = marginEnd;
        }
    }

    public void setDividerColorHorizontal(int color) {
        if (mDividerColorHorizontal != color) {
            mDividerColorHorizontal = color;
            mResetPaintHorizontal = true;
        }
    }

    public void setDividerWidthHorizontal(int width) {
        if (mDividerWidthHorizontal != width) {
            mDividerWidthHorizontal = width;
            mResetPaintHorizontal = true;
        }
    }

    public void setDividerColorVertical(int color) {
        if (mDividerColorVertical != color) {
            mDividerColorVertical = color;
            mResetPaintVertical = true;
        }
    }

    public void setDividerWidthVertical(int width) {
        if (mDividerWidthVertical != width) {
            mDividerWidthVertical = width;
            mResetPaintVertical = true;
        }
    }

    public void setDividerPaddingHorizontalStart(int paddingStart) {
        mDividerPaddingHorizontalStart = paddingStart;
    }

    public void setDividerPaddingHorizontalEnd(int paddingEnd) {
        mDividerPaddingHorizontalEnd = paddingEnd;
    }

    public void setDividerPaddingVerticalStart(int paddingStart) {
        mDividerPaddingVerticalStart = paddingStart;
    }

    public void setDividerPaddingVerticalEnd(int paddingEnd) {
        mDividerPaddingVerticalEnd = paddingEnd;
    }

    public void setContentMarginLeft(int contentMarginLeft){
        if(mContentMarginLeft!=contentMarginLeft){
            mContentMarginLeft=contentMarginLeft;
        }
    }

    public void setContentMarginTop(int contentMarginTop){
        if(mContentMarginTop!=contentMarginTop){
            mContentMarginTop=contentMarginTop;
        }
    }

    public void setContentMarginRight(int contentMarginRight){
        if(mContentMarginRight!=contentMarginRight){
            mContentMarginRight=contentMarginRight;
        }
    }

    public void setContentMarginBottom(int contentMarginBottom){
        if(mContentMarginBottom!=contentMarginBottom){
            mContentMarginBottom=contentMarginBottom;
        }
    }

    public void setContentMarginMiddleHorizontal(int contentMarginMiddleHorizontal) {
        mContentMarginMiddleHorizontal = contentMarginMiddleHorizontal;
    }

    public void setContentMarginMiddleVertical(int contentMarginMiddleVertical) {
        mContentMarginMiddlVertical = contentMarginMiddleVertical;
    }

    public int getBorderLeftColor() {
        return mBorderLeftColor;
    }

    public int getBorderLeftWidth() {
        return mBorderLeftWidth;
    }

    public int getBorderLeftMarginStart() {
        return mBorderLeftMarginStart;
    }

    public int getBorderLeftMarginEnd() {
        return mBorderLeftMarginEnd;
    }

    public int getBorderTopColor() {
        return mBorderTopColor;
    }

    public int getBorderTopWidth() {
        return mBorderTopWidth;
    }

    public int getBorderTopMarginStart() {
        return mBorderTopMarginStart;
    }

    public int getBorderTopMarginEnd() {
        return mBorderTopMarginEnd;
    }

    public int getBorderRightColor() {
        return mBorderRightColor;
    }

    public int getBorderRightWidth() {
        return mBorderRightWidth;
    }

    public int getBorderRightMarginStart() {
        return mBorderRightMarginStart;
    }

    public int getBorderRightMarginEnd() {
        return mBorderRightMarginEnd;
    }

    public int getBorderBottomColor() {
        return mBorderBottomColor;
    }

    public int getBorderBottomWidth() {
        return mBorderBottomWidth;
    }

    public int getBorderBottomMarginStart() {
        return mBorderBottomMarginStart;
    }

    public int getBorderBottomMarginEnd() {
        return mBorderBottomMarginEnd;
    }

    public int getDividerWidthHorizontal() {
        return mDividerWidthHorizontal;
    }

    public int getContentMarginLeft(){
        return mContentMarginLeft;
    }

    public int getContentMarginTop(){
        return mContentMarginTop;
    }

    public int getContentMarginRight(){
        return mContentMarginRight;
    }

    public int getContentMarginBottom(){
        return mContentMarginBottom;
    }

    public int getContentMarginMiddleHorizontal() {
        return mContentMarginMiddleHorizontal;
    }

    public int getContentMarginMiddleVertical() {
        return mContentMarginMiddlVertical;
    }

    public int getDividerWidthVertical() {
        return mDividerWidthVertical;
    }

    public int getDividerColorHorizontal() {
        return mDividerColorHorizontal;
    }

    public int getDividerColorVertical() {
        return mDividerColorVertical;
    }

    public int getDividerPaddingHorizontalStart() {
        return mDividerPaddingHorizontalStart;
    }

    public int getDividerPaddingVerticalStart() {
        return mDividerPaddingVerticalStart;
    }

    public int getDividerPaddingHorizontalEnd() {
        return mDividerPaddingHorizontalEnd;
    }

    public int getDividerPaddingVerticalEnd() {
        return mDividerPaddingVerticalEnd;
    }

    public boolean isVisibleDividerHorizontal(boolean initPaintIfNeed) {
        boolean result = mDividerColorHorizontal != 0 && mDividerWidthHorizontal > 0;
        if (initPaintIfNeed && result) {
            if (mPaintHorizontal == null) {
                mPaintHorizontal = new Paint();
                mPaintHorizontal.setStyle(Paint.Style.FILL);
            }
            if (mResetPaintHorizontal) {
                mPaintHorizontal.setStrokeWidth(mDividerWidthHorizontal);
                mPaintHorizontal.setColor(mDividerColorHorizontal);
                mResetPaintHorizontal = false;
            }
        }
        return result;
    }

    public boolean isVisibleDividerVertical(boolean initPaintIfNeed) {
        boolean result = mDividerColorVertical != 0 && mDividerWidthVertical > 0;
        if (initPaintIfNeed && result) {
            if (mPaintVertical == null) {
                mPaintVertical = new Paint();
                mPaintVertical.setStyle(Paint.Style.FILL);
            }
            if (mResetPaintVertical) {
                mPaintVertical.setStrokeWidth(mDividerWidthVertical);
                mPaintVertical.setColor(mDividerColorVertical);
                mResetPaintVertical = false;
            }
        }
        return result;
    }

    public void drawBorder(Canvas canvas, int viewWidth, int viewHeight) {
        boolean drawLeft = mBorderLeftColor != 0 && mBorderLeftWidth > 0;
        boolean drawTop = mBorderTopColor != 0 && mBorderTopWidth > 0;
        boolean drawRight = mBorderRightColor != 0 && mBorderRightWidth > 0;
        boolean drawBottom = mBorderBottomColor != 0 && mBorderBottomWidth > 0;
        if (drawLeft || drawTop || drawRight || drawBottom) {
            if (mPaintBorder == null) {
                mPaintBorder = new Paint();
                mPaintBorder.setStyle(Paint.Style.FILL);
            }
            float startX, startY, endX, endY;
            if (drawLeft) {
                mPaintBorder.setColor(mBorderLeftColor);
                mPaintBorder.setStrokeWidth(mBorderLeftWidth);
                startX = endX = mBorderLeftWidth / 2;
                startY = mBorderLeftMarginStart > 0 ? mBorderLeftMarginStart : 0;
                endY = viewHeight - (mBorderLeftMarginEnd > 0 ? mBorderLeftMarginEnd : 0);
                canvas.drawLine(startX, startY, endX, endY, mPaintBorder);
            }
            if (drawRight) {
                mPaintBorder.setColor(mBorderRightColor);
                mPaintBorder.setStrokeWidth(mBorderRightWidth);
                startX = endX = viewWidth - mBorderRightWidth / 2;
                startY = mBorderRightMarginStart > 0 ? mBorderRightMarginStart : 0;
                endY = viewHeight - (mBorderRightMarginEnd > 0 ? mBorderRightMarginEnd : 0);
                canvas.drawLine(startX, startY, endX, endY, mPaintBorder);
            }
            if (drawTop) {
                mPaintBorder.setColor(mBorderTopColor);
                mPaintBorder.setStrokeWidth(mBorderTopWidth);
                startY = endY = mBorderTopWidth / 2;
                startX = mBorderTopMarginStart > 0 ? mBorderTopMarginStart : 0;
                endX = viewWidth - (mBorderTopMarginEnd > 0 ? mBorderTopMarginEnd : 0);
                canvas.drawLine(startX, startY, endX, endY, mPaintBorder);
            }
            if (drawBottom) {
                mPaintBorder.setColor(mBorderBottomColor);
                mPaintBorder.setStrokeWidth(mBorderBottomWidth);
                startY = endY = viewHeight - mBorderBottomWidth / 2;
                startX = mBorderBottomMarginStart > 0 ? mBorderBottomMarginStart : 0;
                endX = viewWidth - (mBorderBottomMarginEnd > 0 ? mBorderBottomMarginEnd : 0);
                canvas.drawLine(startX, startY, endX, endY, mPaintBorder);
            }
        }
    }

    public void drawDividerH(Canvas canvas, float xStart, float xEnd, float y) {
        xStart += mDividerPaddingHorizontalStart;
        xEnd -= mDividerPaddingHorizontalEnd;
        if (xEnd > xStart && mPaintHorizontal != null) {
            canvas.drawLine(xStart, y, xEnd, y, mPaintHorizontal);
        }
    }

    public void drawDividerV(Canvas canvas, float yStart, float yEnd, float x) {
        yStart += mDividerPaddingVerticalStart;
        yEnd -= mDividerPaddingVerticalEnd;
        if (yEnd > yStart && mPaintVertical != null) {
            canvas.drawLine(x, yStart, x, yEnd, mPaintVertical);
        }
    }

    public static DividerMargin from(Context context, AttributeSet attrs) {
        DividerMargin dm = new DividerMargin(context.getResources().getDisplayMetrics().density);
        TypedArray attr = attrs == null ? null : context.obtainStyledAttributes(attrs, R.styleable.DividerMargin);
        if (attr != null) {
            dm.mDividerColorHorizontal = attr.getColor(R.styleable.DividerMargin_dividerColorHorizontal, dm.mDividerColorHorizontal);
            dm.mDividerWidthHorizontal = attr.getDimensionPixelSize(R.styleable.DividerMargin_dividerWidthHorizontal, dm.mDividerWidthHorizontal);
            boolean hasDividerPaddingHorizontal = attr.hasValue(R.styleable.DividerMargin_dividerPaddingHorizontal);
            int dividerPaddingHorizontal = hasDividerPaddingHorizontal ? attr.getDimensionPixelSize(R.styleable.DividerMargin_dividerPaddingHorizontal, 0) : 0;
            dm.mDividerPaddingHorizontalStart = attr.getDimensionPixelSize(R.styleable.DividerMargin_dividerPaddingHorizontalStart, hasDividerPaddingHorizontal ? dividerPaddingHorizontal : dm.mDividerPaddingHorizontalStart);
            dm.mDividerPaddingHorizontalEnd = attr.getDimensionPixelSize(R.styleable.DividerMargin_dividerPaddingHorizontalEnd, hasDividerPaddingHorizontal ? dividerPaddingHorizontal : dm.mDividerPaddingHorizontalEnd);

            dm.mDividerColorVertical = attr.getColor(R.styleable.DividerMargin_dividerColorVertical, dm.mDividerColorVertical);
            dm.mDividerWidthVertical = attr.getDimensionPixelSize(R.styleable.DividerMargin_dividerWidthVertical, dm.mDividerWidthVertical);
            boolean hasDividerPaddingVertical = attr.hasValue(R.styleable.DividerMargin_dividerPaddingVertical);
            int dividerPaddingVertical = hasDividerPaddingVertical ? attr.getDimensionPixelSize(R.styleable.DividerMargin_dividerPaddingVertical, 0) : 0;
            dm.mDividerPaddingVerticalStart = attr.getDimensionPixelSize(R.styleable.DividerMargin_dividerPaddingVerticalStart, hasDividerPaddingVertical ? dividerPaddingVertical : dm.mDividerPaddingVerticalStart);
            dm.mDividerPaddingVerticalEnd = attr.getDimensionPixelSize(R.styleable.DividerMargin_dividerPaddingVerticalEnd, hasDividerPaddingVertical ? dividerPaddingVertical : dm.mDividerPaddingVerticalEnd);


            dm.mBorderLeftColor = attr.getColor(R.styleable.DividerMargin_borderLeftColor, dm.mBorderLeftColor);
            dm.mBorderLeftWidth = attr.getDimensionPixelSize(R.styleable.DividerMargin_borderLeftWidth, dm.mBorderLeftWidth);
            boolean hasBorderLeftMargin = attr.hasValue(R.styleable.DividerMargin_borderLeftMargin);
            int borderLeftMargin = hasBorderLeftMargin ? attr.getDimensionPixelSize(R.styleable.DividerMargin_borderLeftMargin, 0) : 0;
            dm.mBorderLeftMarginStart = attr.getDimensionPixelSize(R.styleable.DividerMargin_borderLeftMarginStart, hasBorderLeftMargin ? borderLeftMargin : dm.mBorderLeftMarginStart);
            dm.mBorderLeftMarginEnd = attr.getDimensionPixelSize(R.styleable.DividerMargin_borderLeftMarginEnd, hasBorderLeftMargin ? borderLeftMargin : dm.mBorderLeftMarginEnd);

            dm.mBorderTopColor = attr.getColor(R.styleable.DividerMargin_borderTopColor, dm.mBorderTopColor);
            dm.mBorderTopWidth = attr.getDimensionPixelSize(R.styleable.DividerMargin_borderTopWidth, dm.mBorderTopWidth);
            boolean hasBorderTopMargin = attr.hasValue(R.styleable.DividerMargin_borderTopMargin);
            int borderTopMargin = hasBorderTopMargin ? attr.getDimensionPixelSize(R.styleable.DividerMargin_borderTopMargin, 0) : 0;
            dm.mBorderTopMarginStart = attr.getDimensionPixelSize(R.styleable.DividerMargin_borderTopMarginStart, hasBorderTopMargin ? borderTopMargin : dm.mBorderTopMarginStart);
            dm.mBorderTopMarginEnd = attr.getDimensionPixelSize(R.styleable.DividerMargin_borderTopMarginEnd, hasBorderTopMargin ? borderTopMargin : dm.mBorderTopMarginEnd);

            dm.mBorderRightColor = attr.getColor(R.styleable.DividerMargin_borderRightColor, dm.mBorderRightColor);
            dm.mBorderRightWidth = attr.getDimensionPixelSize(R.styleable.DividerMargin_borderRightWidth, dm.mBorderRightWidth);
            boolean hasBorderRightMargin = attr.hasValue(R.styleable.DividerMargin_borderRightMargin);
            int borderRightMargin = hasBorderRightMargin ? attr.getDimensionPixelSize(R.styleable.DividerMargin_borderRightMargin, 0) : 0;
            dm.mBorderRightMarginStart = attr.getDimensionPixelSize(R.styleable.DividerMargin_borderRightMarginStart, hasBorderRightMargin ? borderRightMargin : dm.mBorderRightMarginStart);
            dm.mBorderRightMarginEnd = attr.getDimensionPixelSize(R.styleable.DividerMargin_borderRightMarginEnd, hasBorderRightMargin ? borderRightMargin : dm.mBorderRightMarginEnd);

            dm.mBorderBottomColor = attr.getColor(R.styleable.DividerMargin_borderBottomColor, dm.mBorderBottomColor);
            dm.mBorderBottomWidth = attr.getDimensionPixelSize(R.styleable.DividerMargin_borderBottomWidth, dm.mBorderBottomWidth);
            boolean hasBorderBottomMargin = attr.hasValue(R.styleable.DividerMargin_borderBottomMargin);
            int borderBottomMargin = hasBorderBottomMargin ? attr.getDimensionPixelSize(R.styleable.DividerMargin_borderBottomMargin, 0) : 0;
            dm.mBorderBottomMarginStart = attr.getDimensionPixelSize(R.styleable.DividerMargin_borderBottomMarginStart, hasBorderBottomMargin ? borderBottomMargin : dm.mBorderBottomMarginStart);
            dm.mBorderBottomMarginEnd = attr.getDimensionPixelSize(R.styleable.DividerMargin_borderBottomMarginEnd, hasBorderBottomMargin ? borderBottomMargin : dm.mBorderBottomMarginEnd);

            dm.mContentMarginLeft = attr.getDimensionPixelSize(R.styleable.DividerMargin_contentMarginLeft, dm.mContentMarginLeft);
            dm.mContentMarginTop = attr.getDimensionPixelSize(R.styleable.DividerMargin_contentMarginTop, dm.mContentMarginTop);
            dm.mContentMarginRight = attr.getDimensionPixelSize(R.styleable.DividerMargin_contentMarginRight, dm.mContentMarginRight);
            dm.mContentMarginBottom = attr.getDimensionPixelSize(R.styleable.DividerMargin_contentMarginBottom, dm.mContentMarginBottom);
            dm.mContentMarginMiddleHorizontal = attr.getDimensionPixelSize(R.styleable.DividerMargin_contentMarginMiddleHorizontal, dm.mContentMarginMiddleHorizontal);
            dm.mContentMarginMiddlVertical = attr.getDimensionPixelSize(R.styleable.DividerMargin_contentMarginMiddleVertical, dm.mContentMarginMiddlVertical);
            attr.recycle();
        }
        return dm;
    }
}
