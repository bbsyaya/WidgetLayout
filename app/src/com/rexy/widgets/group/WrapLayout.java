package com.rexy.widgets.group;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;

import com.rexy.widgetlayout.R;

/**
 * <!--水平方向Item 的间距-->
 * <attr name="middleMarginHorizontal" format="dimension"/>
 * <!--垂直方向Item 的间距-->
 * <attr name="middleMarginVertical" format="dimension"/>
 * <p>
 * <!--水平分割线颜色-->
 * <attr name="dividerColorHorizontal" format="color"/>
 * <!--水平分割线宽-->
 * <attr name="dividerWidthHorizontal" format="dimension"/>
 * <p>
 * <!--水平分割线开始padding-->
 * <attr name="dividerPaddingStartHorizontal" format="dimension"/>
 * <!--水平分割线结束padding-->
 * <attr name="dividerPaddingEndHorizontal" format="dimension"/>
 * <p>
 * <!--垂直分割线颜色-->
 * <attr name="dividerColorVertical" format="color"/>
 * <!--垂直分割线宽-->
 * <attr name="dividerWidthVertical" format="dimension"/>
 * <p>
 * <!--垂直分割线开始padding-->
 * <attr name="dividerPaddingStartVertical" format="dimension"/>
 * <!--垂直分割线结束padding-->
 * <attr name="dividerPaddingEndVertical" format="dimension"/>
 * <!--每行内容水平居中-->
 * <attr name="lineCenterHorizontal" format="boolean"/>
 * <!--每行内容垂直居中-->
 * <attr name="lineCenterVertical" format="boolean"/>
 *
 * <!--每一行最少的Item 个数-->
 * <attr name="lineMinItemCount" format="integer"/>
 * <!--每一行最多的Item 个数-->
 * <attr name="lineMaxItemCount" format="integer"/>
 * @author: rexy
 * @date: 2015-11-27 17:43
 */
public class WrapLayout extends PressViewGroup {
    //每行内容水平居中
    protected boolean mEachLineCenterHorizontal = false;
    //每行内容垂直居中
    protected boolean mEachLineCenterVertical = false;

    //每一行最少的Item 个数
    protected int mEachLineMinItemCount = 0;
    //每一行最多的Item 个数
    protected int mEachLineMaxItemCount = 0;

    protected int mContentMaxWidthAccess = 0;
    protected SparseIntArray mLineHeight = new SparseIntArray(2);
    protected SparseIntArray mLineWidth = new SparseIntArray(2);
    protected SparseIntArray mLineItemCount = new SparseIntArray(2);
    protected SparseIntArray mLineEndIndex = new SparseIntArray(2);

    public WrapLayout(Context context) {
        super(context);
        init(context, null);
    }

    public WrapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WrapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public WrapLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mDividerMargin = DividerMargin.from(context, attrs);
        TypedArray attr = attrs == null ? null : context.obtainStyledAttributes(attrs, R.styleable.WrapLayout);
        if (attr != null) {
            mEachLineMinItemCount = attr.getInt(R.styleable.WrapLayout_lineMinItemCount, mEachLineMinItemCount);
            mEachLineMaxItemCount = attr.getInt(R.styleable.WrapLayout_lineMaxItemCount, mEachLineMaxItemCount);
            mEachLineCenterHorizontal = attr.getBoolean(R.styleable.WrapLayout_lineCenterHorizontal, mEachLineCenterHorizontal);
            mEachLineCenterVertical = attr.getBoolean(R.styleable.WrapLayout_lineCenterVertical, mEachLineCenterVertical);
            attr.recycle();
        }
    }

    private boolean ifNeedNewLine(View child, int attemptWidth, int countInLine) {
        boolean needLine = false;
        if (countInLine > 0) {
            if (countInLine >= mEachLineMinItemCount) {
                if (mEachLineMaxItemCount > 0 && countInLine >= mEachLineMaxItemCount) {
                    needLine = true;
                } else {
                    if (attemptWidth > mContentMaxWidthAccess) {
                        needLine = true;
                    }
                }
            }
        }
        return needLine;
    }

    @Override
    protected void dispatchMeasure(int widthMeasureSpecNoPadding, int heightMeasureSpecNoPadding, int maxSelfWidthNoPadding, int maxSelfHeightNoPadding) {
        final int contentMarginHorizontal = mDividerMargin.getContentMarginLeft() + mDividerMargin.getContentMarginRight();
        final int contentMarginVertical = mDividerMargin.getContentMarginTop() + mDividerMargin.getContentMarginBottom();
        maxSelfWidthNoPadding -= contentMarginHorizontal;
        maxSelfHeightNoPadding -= contentMarginVertical;
        widthMeasureSpecNoPadding = MeasureSpec.makeMeasureSpec(maxSelfWidthNoPadding, MeasureSpec.getMode(widthMeasureSpecNoPadding));
        heightMeasureSpecNoPadding = MeasureSpec.makeMeasureSpec(maxSelfHeightNoPadding, MeasureSpec.getMode(heightMeasureSpecNoPadding));
        final boolean ignoreBeyondWidth = true;
        final int childCount = getChildCount();
        mLineHeight.clear();
        mLineEndIndex.clear();
        mLineItemCount.clear();
        mLineWidth.clear();
        mContentMaxWidthAccess=maxSelfWidthNoPadding;
        int currentLineIndex = 0;
        int currentLineMaxWidth = 0;
        int currentLineMaxHeight = 0;
        int currentLineItemCount = 0;

        int contentWidth = 0, contentHeight = 0, childState = 0;
        int middleMarginHorizontal = mDividerMargin.getContentMarginMiddleHorizontal();
        int middleMarginVertical = mDividerMargin.getContentMarginMiddleVertical();

        for (int childIndex = 0; childIndex < childCount; childIndex++) {
            final View child = getChildAt(childIndex);
            if (skipChild(child)) continue;
            WrapLayout.LayoutParams params = (WrapLayout.LayoutParams) child.getLayoutParams();
            int childMarginHorizontal = params.leftMargin + params.rightMargin;
            int childMarginVertical = params.topMargin + params.bottomMargin;
            params.measure(child, widthMeasureSpecNoPadding, heightMeasureSpecNoPadding, childMarginHorizontal, childMarginVertical + contentHeight);
            int childWidthWithMargin = child.getMeasuredWidth() + childMarginHorizontal;
            int childHeightWithMargin = child.getMeasuredHeight() + childMarginVertical;
            childState |= child.getMeasuredState();
            if (ifNeedNewLine(child, childWidthWithMargin + currentLineMaxWidth + middleMarginHorizontal, currentLineItemCount)) {
                if (ignoreBeyondWidth || currentLineMaxWidth <= mContentMaxWidthAccess) {
                    contentWidth = Math.max(contentWidth, currentLineMaxWidth);
                }
                if (middleMarginVertical > 0) {
                    contentHeight += middleMarginVertical;
                }
                contentHeight += currentLineMaxHeight;
                mLineWidth.put(currentLineIndex, currentLineMaxWidth);
                mLineHeight.put(currentLineIndex, currentLineMaxHeight);
                mLineItemCount.put(currentLineIndex, currentLineItemCount);
                mLineEndIndex.put(currentLineIndex, childIndex - 1);
                currentLineIndex += 1;
                currentLineItemCount = 1;
                currentLineMaxWidth = childWidthWithMargin;
                currentLineMaxHeight = childHeightWithMargin;
            } else {
                if (currentLineItemCount > 0 && middleMarginHorizontal > 0) {
                    currentLineMaxWidth += middleMarginHorizontal;
                }
                currentLineItemCount = currentLineItemCount + 1;
                currentLineMaxWidth += childWidthWithMargin;
                if (!ignoreBeyondWidth && currentLineMaxWidth <= mContentMaxWidthAccess) {
                    contentWidth = Math.max(contentWidth, currentLineMaxWidth);
                }
                currentLineMaxHeight = Math.max(currentLineMaxHeight, childHeightWithMargin);
            }
        }
        if (currentLineItemCount > 0) {
            if (ignoreBeyondWidth || currentLineMaxWidth <= mContentMaxWidthAccess) {
                contentWidth = Math.max(contentWidth, currentLineMaxWidth);
            }
            contentHeight += currentLineMaxHeight;
            mLineWidth.put(currentLineIndex, currentLineMaxWidth);
            mLineHeight.put(currentLineIndex, currentLineMaxHeight);
            mLineItemCount.put(currentLineIndex, currentLineItemCount);
            mLineEndIndex.put(currentLineIndex, childCount - 1);
        }
        setContentSize(contentWidth+contentMarginHorizontal, contentHeight+contentMarginVertical);
        setMeasureState(childState);
    }

    @Override
    protected void dispatchLayout(int contentLeft, int contentTop, int paddingLeft, int paddingTop, int selfWidthNoPadding, int selfHeightNoPadding) {
        final int lineCount = mLineEndIndex.size(), gravity = getGravity();
        final boolean lineVertical = mEachLineCenterVertical || ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.CENTER_VERTICAL && lineCount == 1);
        final boolean lineHorizontal = mEachLineCenterHorizontal || ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.CENTER_HORIZONTAL && lineCount == 1);
        final int middleMarginHorizontal = mDividerMargin.getContentMarginMiddleHorizontal();
        final int middleMarginVertical = mDividerMargin.getContentMarginMiddleVertical();
        final int contentMarginLeft=mDividerMargin.getContentMarginLeft();
        final int contentMarginTop=mDividerMargin.getContentMarginTop();
        final int contentWidthNoMargin=getContentWidth()-mDividerMargin.getContentMarginLeft()-mDividerMargin.getContentMarginRight();
        int lineEndIndex, lineMaxHeight, childIndex = 0, lineTop = contentTop+contentMarginTop;
        int childLeft, childTop, childRight, childBottom;
        for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            lineEndIndex = mLineEndIndex.get(lineIndex);
            lineMaxHeight = mLineHeight.get(lineIndex);
            childLeft = contentLeft+contentMarginLeft;
            if (lineHorizontal) {
                childLeft += (contentWidthNoMargin- mLineWidth.get(lineIndex)) / 2;
            }
            for (; childIndex <= lineEndIndex; childIndex++) {
                final View child = getChildAt(childIndex);
                if (skipChild(child)) continue;
                WrapLayout.LayoutParams params = (WrapLayout.LayoutParams) child.getLayoutParams();
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                childLeft += params.leftMargin;
                childTop = getContentStartV(lineTop, lineTop + lineMaxHeight, childHeight, params.topMargin, params.bottomMargin, lineVertical ? Gravity.CENTER_VERTICAL : params.gravity);
                childRight = childLeft + childWidth;
                childBottom = childTop + childHeight;
                child.layout(childLeft, childTop, childRight, childBottom);
                childLeft = childRight + params.rightMargin;
                if (middleMarginHorizontal > 0) {
                    childLeft += middleMarginHorizontal;
                }
            }
            childIndex = lineEndIndex + 1;
            lineTop += lineMaxHeight;
            if (middleMarginVertical > 0) {
                lineTop += middleMarginVertical;
            }
        }
    }

    @Override
    protected void dispatchDrawAfter(Canvas canvas) {
        boolean dividerHorizontal = mDividerMargin.isVisibleDividerHorizontal(true);
        boolean dividerVertical = mDividerMargin.isVisibleDividerVertical(true);
        if (dividerHorizontal || dividerVertical) {
            final int lineCount = mLineEndIndex.size();
            final int middleMarginHorizontal = mDividerMargin.getContentMarginMiddleHorizontal();
            final int middleMarginVertical = mDividerMargin.getContentMarginMiddleVertical();
            final int contentMarginTop=mDividerMargin.getContentMarginTop();

            int parentLeft = getPaddingLeft();
            int parentRight = getWidth() - getPaddingRight();

            int lineIndex = 0, childIndex = 0;
            int lineTop = getContentTop()+contentMarginTop, lineBottom;
            for (; lineIndex < lineCount; lineIndex++) {
                int lineEndIndex = mLineEndIndex.get(lineIndex);
                lineBottom = lineTop + mLineHeight.get(lineIndex);
                if (dividerHorizontal && lineIndex != lineCount - 1) {
                    mDividerMargin.drawDividerH(canvas, parentLeft, parentRight, lineBottom + (middleMarginVertical > 0 ? middleMarginVertical / 2 : 0));
                }
                if (dividerVertical && mLineItemCount.get(lineIndex) > 1) {
                    for (; childIndex < lineEndIndex; childIndex++) {
                        final View child = getChildAt(childIndex);
                        if (skipChild(child)) continue;
                        WrapLayout.LayoutParams params = (WrapLayout.LayoutParams) child.getLayoutParams();
                        mDividerMargin.drawDividerV(canvas, lineTop, lineBottom, child.getRight() + params.rightMargin + (middleMarginHorizontal > 0 ? middleMarginHorizontal / 2 : 0));
                    }
                }
                childIndex = lineEndIndex + 1;
                lineTop = lineBottom;
                if (middleMarginVertical > 0) {
                    lineTop += middleMarginVertical;
                }
            }
        }
        super.dispatchDrawAfter(canvas);
    }

    public int getEachLineMinItemCount() {
        return mEachLineMinItemCount;
    }

    public int getEachLineMaxItemCount() {
        return mEachLineMaxItemCount;
    }

    public boolean isEachLineCenterHorizontal() {
        return mEachLineCenterHorizontal;
    }

    public boolean isEachLineCenterVertical() {
        return mEachLineCenterVertical;
    }

    public void setEachLineMinItemCount(int eachLineMinItemCount) {
        if (mEachLineMinItemCount != eachLineMinItemCount) {
            mEachLineMinItemCount = eachLineMinItemCount;
            requestLayout();
        }
    }

    public void setEachLineMaxItemCount(int eachLineMaxItemCount) {
        if (mEachLineMaxItemCount != eachLineMaxItemCount) {
            mEachLineMaxItemCount = eachLineMaxItemCount;
            requestLayout();
        }
    }

    public void setEachLineCenterHorizontal(boolean eachLineCenterHorizontal) {
        if (mEachLineCenterHorizontal != eachLineCenterHorizontal) {
            mEachLineCenterHorizontal = eachLineCenterHorizontal;
            requestLayout();
        }
    }

    public void setEachLineCenterVertical(boolean eachLineCenterVertical) {
        if (mEachLineCenterVertical != eachLineCenterVertical) {
            mEachLineCenterVertical = eachLineCenterVertical;
            requestLayout();
        }
    }


}
