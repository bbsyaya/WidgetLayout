package com.rexy.widgets.group;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.rexy.widgets.drawable.FloatDrawable;
import com.rexy.widgetlayout.R;

/**
 * 支持Hover Drawable 动画渐变,类似ios 效果.
 * <p>
 * <declare-styleable name="PressViewGroup">
 * <p>
 * <!--hover drawable 忽略手势滑动到自身之外取消按下状态-->
 * <attr name="ignoreForegroundStateWhenTouchOut" format="boolean"/>
 * <!--hover drawable 颜色-->
 * <attr name="foregroundColor" format="color"/>
 * <!--hover drawable  圆角-->
 * <attr name="foregroundRadius" format="dimension"/>
 * <!--hover drawable 动画时间-->
 * <attr name="foregroundDuration" format="integer"/>
 * <!--hover drawable 最小不透明度-->
 * <attr name="foregroundAlphaMin" format="integer"/>
 * <!--hover drawable 最大不透明度-->
 * <attr name="foregroundAlphaMax" format="integer"/>
 * <p>
 * </declare-styleable>
 *
 * @author: rexy
 * @date: 2017-06-01 09:25
 */
public abstract class PressViewGroup extends BaseViewGroup {
    protected int mTouchSlop = 0;
    protected boolean mIgnoreForegroundStateWhenTouchOut = false;
    protected FloatDrawable mForegroundDrawable = null;

    //分割线和item 间距信息类。
    protected DividerMargin mDividerMargin;

    public PressViewGroup(Context context) {
        super(context);
        init(context, null);
    }

    public PressViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PressViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public PressViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mDividerMargin = DividerMargin.from(context, attrs);
        TypedArray attr = attrs == null ? null : context.obtainStyledAttributes(attrs, R.styleable.PressViewGroup);
        if (attr != null) {
            int floatColor = attr.getColor(R.styleable.PressViewGroup_foregroundColor, 0);
            if (floatColor != 0) {
                int floatRadius = attr.getDimensionPixelSize(R.styleable.PressViewGroup_foregroundRadius, 0);
                int floatDuration = attr.getInt(R.styleable.PressViewGroup_foregroundDuration, 120);
                int floatMinAlpha = attr.getInt(R.styleable.PressViewGroup_foregroundAlphaMin, 0);
                int floatMaxAlpha = attr.getInt(R.styleable.PressViewGroup_foregroundAlphaMax, 50);
                FloatDrawable floatDrawable = new FloatDrawable(floatColor, floatMinAlpha, floatMaxAlpha).duration(floatDuration).radius(floatRadius);
                setForegroundDrawable(floatDrawable);
                setClickable(true);
            }
            attr.recycle();
        }
    }

    public void setForegroundDrawable(FloatDrawable foregroundDrawable) {
        if (mForegroundDrawable != foregroundDrawable) {
            if (mForegroundDrawable != null) {
                mForegroundDrawable.setCallback(null);
                unscheduleDrawable(mForegroundDrawable);
            }
            if (foregroundDrawable == null) {
                mForegroundDrawable = null;
            } else {
                mForegroundDrawable = foregroundDrawable;
                foregroundDrawable.setCallback(this);
                foregroundDrawable.setVisible(getVisibility() == VISIBLE, false);
            }
        }
    }

    public void setIgnoreForegroundStateWhenTouchOut(boolean ignoreForegroundStateWhenTouchOut) {
        mIgnoreForegroundStateWhenTouchOut = ignoreForegroundStateWhenTouchOut;
    }

    public FloatDrawable setForegroundDrawable(int color, int minAlpha, int maxAlpha) {
        FloatDrawable drawable = new FloatDrawable(color, minAlpha, maxAlpha);
        setForegroundDrawable(drawable);
        return drawable;
    }

    public boolean isIgnoreForegroundStateWhenTouchOut() {
        return mIgnoreForegroundStateWhenTouchOut;
    }

    public FloatDrawable getForegroundDrawable() {
        return mForegroundDrawable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mForegroundDrawable != null && isClickable()) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                mForegroundDrawable.start(true);
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                mForegroundDrawable.start(false);
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (!mIgnoreForegroundStateWhenTouchOut && !pointInView(event.getX(), event.getY(), mTouchSlop)) {
                    mForegroundDrawable.start(false);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean pointInView(float localX, float localY, float slop) {
        return localX >= -slop && localY >= -slop && localX < ((getRight() - getLeft()) + slop) &&
                localY < ((getBottom() - getTop()) + slop);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        boolean result = super.verifyDrawable(who);
        if (!result && mForegroundDrawable == who) {
            result = true;
        }
        return result;
    }

    @Override
    protected final void dispatchDraw(Canvas canvas) {
        if (!dispatchDrawPrevious(canvas)) {
            super.dispatchDraw(canvas);
        }
        dispatchDrawAfter(canvas);
        if (mForegroundDrawable != null) {
            mForegroundDrawable.setBounds(0, 0, getWidth(), getHeight());
            mForegroundDrawable.draw(canvas);
        }
    }

    protected boolean dispatchDrawPrevious(Canvas canvas) {
        return false;
    }

    protected void dispatchDrawAfter(Canvas canvas) {
        mDividerMargin.drawBorder(canvas, getWidth(), getHeight());
    }

    public int getBorderLeftColor() {
        return mDividerMargin.getBorderLeftColor();
    }

    public int getBorderLeftWidth() {
        return mDividerMargin.getBorderLeftWidth();
    }

    public int getBorderLeftMarginStart() {
        return mDividerMargin.getBorderLeftMarginStart();
    }

    public int getBorderLeftMarginEnd() {
        return mDividerMargin.getBorderLeftMarginEnd();
    }

    public int getBorderTopColor() {
        return mDividerMargin.getBorderTopColor();
    }

    public int getBorderTopWidth() {
        return mDividerMargin.getBorderTopWidth();
    }

    public int getBorderTopMarginStart() {
        return mDividerMargin.getBorderTopMarginStart();
    }

    public int getBorderTopMarginEnd() {
        return mDividerMargin.getBorderTopMarginEnd();
    }

    public int getBorderRightColor() {
        return mDividerMargin.getBorderRightColor();
    }

    public int getBorderRightWidth() {
        return mDividerMargin.getBorderRightWidth();
    }

    public int getBorderRightMarginStart() {
        return mDividerMargin.getBorderRightMarginStart();
    }

    public int getBorderRightMarginEnd() {
        return mDividerMargin.getBorderRightMarginEnd();
    }

    public int getBorderBottomColor() {
        return mDividerMargin.getBorderBottomColor();
    }

    public int getBorderBottomWidth() {
        return mDividerMargin.getBorderBottomWidth();
    }

    public int getBorderBottomMarginStart() {
        return mDividerMargin.getBorderBottomMarginStart();
    }

    public int getBorderBottomMarginEnd() {
        return mDividerMargin.getBorderBottomMarginEnd();
    }

    public int getMiddleMarginHorizontal() {
        return mDividerMargin.getMiddleMarginHorizontal();
    }

    public int getMiddleMarginVertical() {
        return mDividerMargin.getMiddleMarginVertical();
    }

    public int getContentMarginLeft() {
        return mDividerMargin.getContentMarginLeft();
    }

    public int getContentMarginTop() {
        return mDividerMargin.getContentMarginTop();
    }

    public int getContentMarginRight() {
        return mDividerMargin.getContentMarginRight();
    }

    public int getContentMarginBottom() {
        return mDividerMargin.getContentMarginBottom();
    }

    public int getDividerWidthHorizontal() {
        return mDividerMargin.getDividerWidthHorizontal();
    }

    public int getDividerWidthVertical() {
        return mDividerMargin.getDividerWidthVertical();
    }

    public int getDividerColorHorizontal() {
        return mDividerMargin.getDividerColorHorizontal();
    }

    public int getDividerColorVertical() {
        return mDividerMargin.getDividerColorVertical();
    }

    public int getDividerPaddingStartHorizontal() {
        return mDividerMargin.getDividerPaddingStartHorizontal();
    }

    public int getDividerPaddingStartVertical() {
        return mDividerMargin.getDividerPaddingStartVertical();
    }

    public int getDividerPaddingEndHorizontal() {
        return mDividerMargin.getDividerPaddingEndHorizontal();
    }

    public int getDividerPaddingEndVertical() {
        return mDividerMargin.getDividerPaddingEndVertical();
    }

    public void setBorderLeftColor(int color) {
        mDividerMargin.setBorderLeftColor(color);
        invalidate();
    }

    public void setBorderLeftWidth(int width) {
        mDividerMargin.setBorderLeftWidth(width);
        invalidate();
    }

    public void setBorderLeftMarginStart(int marginStart) {
        mDividerMargin.setBorderLeftMarginStart(marginStart);
        invalidate();
    }

    public void setBorderLeftMarginEnd(int marginEnd) {
        mDividerMargin.setBorderLeftMarginEnd(marginEnd);
        invalidate();
    }

    public void setBorderTopColor(int color) {
        mDividerMargin.setBorderTopColor(color);
        invalidate();
    }

    public void setBorderTopWidth(int width) {
        mDividerMargin.setBorderTopWidth(width);
        invalidate();
    }

    public void setBorderTopMarginStart(int marginStart) {
        mDividerMargin.setBorderTopMarginStart(marginStart);
        invalidate();
    }

    public void setBorderTopMarginEnd(int marginEnd) {
        mDividerMargin.setBorderTopMarginEnd(marginEnd);
        invalidate();
    }

    public void setBorderRightColor(int color) {
        mDividerMargin.setBorderRightColor(color);
        invalidate();
    }

    public void setBorderRightWidth(int width) {
        mDividerMargin.setBorderRightWidth(width);
        invalidate();
    }

    public void setBorderRightMarginStart(int marginStart) {
        mDividerMargin.setBorderRightMarginStart(marginStart);
        invalidate();
    }

    public void setBorderRightMarginEnd(int marginEnd) {
        mDividerMargin.setBorderRightMarginEnd(marginEnd);
        invalidate();
    }

    public void setBorderBottomColor(int color) {
        mDividerMargin.setBorderBottomColor(color);
        invalidate();
    }

    public void setBorderBottomWidth(int width) {
        mDividerMargin.setBorderBottomWidth(width);
        invalidate();
    }

    public void setBorderBottomMarginStart(int marginStart) {
        mDividerMargin.setBorderBottomMarginStart(marginStart);
        invalidate();
    }

    public void setBorderBottomMarginEnd(int marginEnd) {
        mDividerMargin.setBorderBottomMarginEnd(marginEnd);
        invalidate();
    }

    public void setMiddleMarginHorizontal(int middleMarginHorizontal) {
        if (middleMarginHorizontal != mDividerMargin.getMiddleMarginHorizontal()) {
            mDividerMargin.setMiddleMarginHorizontal(middleMarginHorizontal);
            requestLayout();
        }
    }

    public void setMiddleMarginVertical(int middleMarginVertical) {
        if (middleMarginVertical != mDividerMargin.getMiddleMarginVertical()) {
            mDividerMargin.setMiddleMarginVertical(middleMarginVertical);
            requestLayout();
        }
    }

    public void setContentMarginLeft(int contentMarginLeft) {
        if (contentMarginLeft != mDividerMargin.getContentMarginLeft()) {
            mDividerMargin.setContentMarginLeft(contentMarginLeft);
            requestLayout();
        }
    }

    public void setContentMarginTop(int contentMarginTop) {
        if (contentMarginTop != mDividerMargin.getContentMarginTop()) {
            mDividerMargin.setContentMarginTop(contentMarginTop);
            requestLayout();
        }
    }

    public void setContentMarginRight(int contentMarginRight) {
        if (contentMarginRight != mDividerMargin.getContentMarginRight()) {
            mDividerMargin.setContentMarginRight(contentMarginRight);
            requestLayout();
        }
    }

    public void setContentMarginBottom(int contentMarginBottom) {
        if (contentMarginBottom != mDividerMargin.getContentMarginBottom()) {
            mDividerMargin.setContentMarginBottom(contentMarginBottom);
            requestLayout();
        }
    }

    public void setDividerWidthHorizontal(int dividerWidthHorizontal) {
        mDividerMargin.setDividerWidthHorizontal(dividerWidthHorizontal);
        invalidate();
    }

    public void setDividerWidthVertical(int dividerWidthVertical) {
        mDividerMargin.setDividerWidthVertical(dividerWidthVertical);
        invalidate();
    }

    public void setDividerColorHorizontal(int dividerColorHorizontal) {
        mDividerMargin.setDividerColorHorizontal(dividerColorHorizontal);
        invalidate();
    }

    public void setDividerColorVertical(int dividerColorVertical) {
        mDividerMargin.setDividerColorVertical(dividerColorVertical);
        invalidate();
    }

    public void setDividerPaddingStartHorizontal(int dividerPaddingStartHorizontal) {
        mDividerMargin.setDividerPaddingStartHorizontal(dividerPaddingStartHorizontal);
        invalidate();
    }

    public void setDividerPaddingStartVertical(int dividerPaddingStartVertical) {
        mDividerMargin.setDividerPaddingStartVertical(dividerPaddingStartVertical);
        invalidate();
    }

    public void setDividerPaddingEndHorizontal(int dividerPaddingEndHorizontal) {
        mDividerMargin.setDividerPaddingEndHorizontal(dividerPaddingEndHorizontal);
        invalidate();
    }

    public void setDividerPaddingEndVertical(int dividerPaddingEndVertical) {
        mDividerMargin.setDividerPaddingEndVertical(dividerPaddingEndVertical);
        invalidate();
    }
}
