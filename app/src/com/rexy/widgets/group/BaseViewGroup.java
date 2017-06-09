package com.rexy.widgets.group;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * 支持容器内容自身的gravity,maxWidth,maxHeight.
 * 支持直接子 View 的 layout_gravity,maxWidth,maxHeight 等。
 * 支持水平和垂直视图的滑动计算 Api 和滑动事件。
 * 随时可监听当前视图可见区域的变法。
 * onMeasure 和 onLayout 内部做了一定的通用处理，不可重载，可打印他们执行的结果和耗费时间。
 * <p>
 * <p>
 * 实现子类需要重写dispatchMeasure和dispatchLayout 两个方法。
 * 其中dispatchMeasure来实现child 的测量，最终需要调用setContentSize 方法。
 *
 * @author: rexy
 * @date: 2017-04-25 09:32
 */
public abstract class BaseViewGroup extends ViewGroup {
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;
    private static final int[] ATTRS_PROPERTIES = new int[]
            {android.R.attr.gravity, android.R.attr.maxWidth, android.R.attr.maxHeight};
    private static final int[] ATTRS_LAYOUTPARAMS = new int[]{android.R.attr.layout_gravity, android.R.attr.maxWidth, android.R.attr.maxHeight};

    private int mGravity;
    private int mMaxWidth = -1;
    private int mMaxHeight = -1;
    private int mContentWidth = 0;
    private int mContentHeight = 0;
    private int mChildMeasureState = 0;
    protected Rect mVisibleBounds = new Rect();
    private OnScrollChangeListener mScrollListener;

    protected int mScrollState = SCROLL_STATE_IDLE;
    protected boolean mAttachLayout = false;

    private String mLogTag;
    private long mTimeMeasureStart, mTimeLayoutStart;

    public BaseViewGroup(Context context) {
        super(context);
        init(context, null);
    }

    public BaseViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BaseViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public BaseViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, ATTRS_PROPERTIES);
            mGravity = a.getInt(0, mGravity);
            mMaxWidth = a.getDimensionPixelSize(1, mMaxWidth);
            mMaxHeight = a.getDimensionPixelSize(2, mMaxHeight);
            a.recycle();
        }
    }

    public void setLogTag(String logTag) {
        mLogTag = logTag;
    }

    public boolean isLogAccess() {
        return mLogTag != null;
    }

    protected void print(CharSequence msg) {
        if (mLogTag != null && mLogTag.length() > 0) {
            Log.d(mLogTag, String.valueOf(msg));
        }
    }

    public int getContentWidth() {
        return mContentWidth;
    }

    public int getContentHeight() {
        return mContentHeight;
    }

    public int getMeasureState() {
        return mChildMeasureState;
    }

    protected void setContentSize(int contentWidth, int contentHeight) {
        mContentWidth = contentWidth;
        mContentHeight = contentHeight;
    }

    protected void setMeasureState(int childMeasureState) {
        mChildMeasureState = childMeasureState;
    }

    public int getGravity() {
        return mGravity;
    }

    public int getMaxWidth() {
        return mMaxWidth;
    }

    public int getMaxHeight() {
        return mMaxHeight;
    }

    public int getScrollState() {
        return mScrollState;
    }

    public OnScrollChangeListener getScrollChangeListener() {
        return mScrollListener;
    }

    public boolean isAttachLayoutFinished() {
        return mAttachLayout;
    }

    public void setGravity(int gravity) {
        if (mGravity != gravity) {
            mGravity = gravity;
            requestLayout();
        }
    }

    public void setMaxWidth(int maxWidth) {
        if (mMaxWidth != maxWidth) {
            mMaxWidth = maxWidth;
            requestLayout();
        }
    }

    public void setMaxHeight(int maxHeight) {
        if (mMaxHeight != maxHeight) {
            mMaxHeight = maxHeight;
            requestLayout();
        }
    }

    public int getWidthWithoutPadding() {
        return mVisibleBounds.width();
    }

    public int getHeightWithoutPadding() {
        return mVisibleBounds.height();
    }

    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        mScrollListener = l;
    }

    public Rect getVisibleBounds() {
        return mVisibleBounds;
    }

    protected void markAsWillDragged(boolean disallowParentTouch) {
        if (disallowParentTouch) {
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
        setScrollState(SCROLL_STATE_DRAGGING);
    }

    protected void markAsWillScroll() {
        setScrollState(SCROLL_STATE_SETTLING);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    protected void markAsWillIdle() {
        setScrollState(SCROLL_STATE_IDLE);
    }

    private void setScrollState(int newState) {
        if (mScrollState != newState) {
            int preState = mScrollState;
            mScrollState = newState;
            if (isLogAccess()) {
                print(String.format("stateChanged: %d to %d", preState, newState));
            }
            onScrollStateChanged(newState, preState);
            if (mScrollListener != null) {
                mScrollListener.onScrollStateChanged(mScrollState, preState);
            }
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int ol, int ot) {
        super.onScrollChanged(l, t, ol, ot);
        if (mScrollState != SCROLL_STATE_IDLE) {
            awakenScrollBars();
        }
        computeVisibleBounds(l, t, true);
        if (mScrollListener != null) {
            mScrollListener.onScrollChanged(l, t, ol, ot);
        }
    }

    protected void onScrollStateChanged(int newState, int prevState) {
    }

    protected void onScrollChanged(int scrollX, int scrollY, Rect visibleBounds, boolean fromScrollChanged) {
        if (isLogAccess()) {
            StringBuilder sb = new StringBuilder(32);
            sb.append("scrollChanged: scrollX=").append(scrollX);
            sb.append(",scrollY=").append(scrollY).append(",visibleBounds=").append(visibleBounds);
            sb.append(",scrollChanged=").append(fromScrollChanged);
            print(sb);
        }
    }

    private int getMeasureSize(int minSize, int maxSize, int contentSize, int padding) {
        int finalSize = Math.max(minSize, contentSize + padding);
        if (maxSize > minSize && maxSize > 0 && finalSize > maxSize) {
            finalSize = maxSize;
        }
        return finalSize;
    }

    private int getMeasureSizeWithoutPadding(int minSize, int maxSize, int measureSpec, int padding) {
        int finalSize = MeasureSpec.getSize(measureSpec);
        if (minSize > finalSize) {
            finalSize = minSize;
        }
        if (finalSize > maxSize && maxSize > minSize && maxSize > 0) {
            finalSize = maxSize;
        }
        return Math.max(finalSize - padding, 0);
    }

    @Override
    protected final void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mTimeMeasureStart = System.currentTimeMillis();
        mContentWidth = mContentHeight = mChildMeasureState = 0;
        final int minWidth = getSuggestedMinimumWidth();
        final int minHeight = getSuggestedMinimumHeight();
        final int paddingHorizontal = getPaddingLeft() + getPaddingRight();
        final int paddingVertical = getPaddingTop() + getPaddingBottom();
        final int mostWidthNoPadding = getMeasureSizeWithoutPadding(minWidth, mMaxWidth, widthMeasureSpec, paddingHorizontal);
        final int mostHeightNoPadding = getMeasureSizeWithoutPadding(minHeight, mMaxHeight, heightMeasureSpec, paddingVertical);
        dispatchMeasure(
                MeasureSpec.makeMeasureSpec(mostWidthNoPadding, MeasureSpec.getMode(widthMeasureSpec)),
                MeasureSpec.makeMeasureSpec(mostHeightNoPadding, MeasureSpec.getMode(heightMeasureSpec)),
                mostWidthNoPadding,
                mostHeightNoPadding
        );
        int contentWidth = mContentWidth, contentHeight = mContentHeight, childState = mChildMeasureState;
        int finalWidth = getMeasureSize(minWidth, mMaxWidth, contentWidth, paddingHorizontal);
        int finalHeight = getMeasureSize(minHeight, mMaxHeight, contentHeight, paddingVertical);
        setMeasuredDimension(resolveSizeAndState(finalWidth, widthMeasureSpec, childState),
                resolveSizeAndState(finalHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
        doAfterMeasure(getMeasuredWidth(), getMeasuredHeight(), contentWidth, contentHeight);
    }

    @Override
    protected final void onLayout(boolean changed, int l, int t, int r, int b) {
        mTimeLayoutStart = System.currentTimeMillis();
        boolean firstAttachLayout = false;
        if (!mAttachLayout) {
            firstAttachLayout = mAttachLayout = true;
            int scrollX = getScrollX(), scrollY = getScrollY();
            computeVisibleBounds(scrollX, scrollY, false);
        }
        int left = getPaddingLeft(), top = getPaddingTop();
        int right = getPaddingRight(), bottom = getPaddingBottom();
        int selfWidthNoPadding = r - l - left - right;
        int selfHeightNoPadding = b - t - top - bottom;
        int contentLeft = getContentStartH(left, selfWidthNoPadding + left, getContentWidth(), mGravity);
        int contentTop = getContentStartV(top, selfHeightNoPadding + top, getContentHeight(), mGravity);
        dispatchLayout(contentLeft, contentTop, left, top, selfWidthNoPadding, selfHeightNoPadding);
        doAfterLayout(firstAttachLayout);
    }

    /**
     * in this function must call setContentSize(contentWidth,contentHeight);
     *
     * @param widthMeasureSpecNoPadding  widthMeasureSpec with out padding
     * @param heightMeasureSpecNoPadding heightMeasureSpec with out padding
     * @param maxSelfWidthNoPadding      max width with out padding can allowed by its child.
     * @param maxSelfHeightNoPadding     max height with out padding can allowed by its child.
     */
    protected abstract void dispatchMeasure(int widthMeasureSpecNoPadding, int heightMeasureSpecNoPadding, int maxSelfWidthNoPadding, int maxSelfHeightNoPadding);

    protected void doAfterMeasure(int measuredWidth, int measuredHeight, int contentWidth, int contentHeight) {
        if (isLogAccess()) {
            print(String.format("measure(%d ms): [measuredWidth=%d , measuredHeight=%d],[contentWidth=%d,contentHeight=%d]", (System.currentTimeMillis() - mTimeMeasureStart), measuredWidth, measuredHeight, contentWidth, contentHeight));
        }
    }

    protected abstract void dispatchLayout(int contentLeft, int contentTop, int paddingLeft, int paddingTop, int selfWidthNoPadding, int selfHeightNoPadding);

    protected void doAfterLayout(boolean firstAttachLayout) {
        if (isLogAccess()) {
            print(String.format("layout(%d ms): firstAttachLayout=%s", (System.currentTimeMillis() - mTimeLayoutStart), firstAttachLayout));
        }
    }

    protected boolean isChildNotGone(View child) {
        return child != null && child.getVisibility() != View.GONE && child.getParent() == BaseViewGroup.this;
    }

    protected boolean skipChild(View child) {
        return child == null || child.getVisibility() == View.GONE;
    }

    protected int getContentLeft() {
        return getContentStartH(getPaddingLeft(), getWidth() - getPaddingRight(), getContentWidth(), mGravity);
    }

    protected int getContentTop() {
        return getContentStartH(getPaddingTop(), getHeight() - getPaddingBottom(), getContentHeight(), mGravity);
    }

    protected int getContentStartH(int containerLeft, int containerRight, int contentWillSize, int gravity) {
        return getContentStartH(containerLeft, containerRight, contentWillSize, 0, 0, gravity);
    }

    protected int getContentStartV(int containerTop, int containerBottom, int contentWillSize, int gravity) {
        return getContentStartV(containerTop, containerBottom, contentWillSize, 0, 0, gravity);
    }

    protected int getContentStartH(int containerLeft, int containerRight, int contentWillSize, int contentMarginLeft, int contentMarginRight, int gravity) {
        if (gravity != -1 || gravity != 0) {
            int start;
            final int mask = Gravity.HORIZONTAL_GRAVITY_MASK;
            final int maskCenter = Gravity.CENTER_HORIZONTAL;
            final int maskEnd = Gravity.RIGHT;
            final int okGravity = gravity & mask;
            if (maskCenter == okGravity) {//center
                start = containerLeft + (containerRight - containerLeft - (contentWillSize + contentMarginRight - contentMarginLeft)) / 2;
            } else if (maskEnd == okGravity) {//end
                start = containerRight - contentWillSize - contentMarginRight;
            } else {//start
                start = containerLeft + contentMarginLeft;
            }
            return start;
        }
        return containerLeft + contentMarginLeft;
    }

    protected int getContentStartV(int containerTop, int containerBottom, int contentWillSize, int contentMarginTop, int contentMarginBottom, int gravity) {
        if (gravity != -1 || gravity != 0) {
            int start;
            final int mask = Gravity.VERTICAL_GRAVITY_MASK;
            final int maskCenter = Gravity.CENTER_VERTICAL;
            final int maskEnd = Gravity.BOTTOM;
            final int okGravity = gravity & mask;
            if (maskCenter == okGravity) {//center
                start = containerTop + (containerBottom - containerTop - (contentWillSize + contentMarginBottom - contentMarginTop)) / 2;
            } else if (maskEnd == okGravity) {//end
                start = containerBottom - contentWillSize - contentMarginBottom;
            } else {//start
                start = containerTop + contentMarginTop;
            }
            return start;
        }
        return containerTop + contentMarginTop;
    }

    protected int offsetX(View child, boolean centreInVisibleBounds, boolean marginInclude) {
        int current;
        MarginLayoutParams marginLp = (marginInclude && child.getLayoutParams() instanceof MarginLayoutParams) ? (MarginLayoutParams) child.getLayoutParams() : null;
        if (centreInVisibleBounds) {
            current = (child.getLeft() + child.getRight()) >> 1;
            if (marginLp != null) {
                current = current + (marginLp.rightMargin - marginLp.leftMargin) / 2;
            }
            return current - mVisibleBounds.centerX() + mVisibleBounds.left - getPaddingLeft();
        } else {
            current = child.getLeft();
            if (marginLp != null) {
                current = current - marginLp.leftMargin;
            }
            return current - getPaddingLeft();
        }
    }

    protected int offsetY(View child, boolean centreInVisibleBounds, boolean marginInclude) {
        int current;
        MarginLayoutParams marginLp = (marginInclude && child.getLayoutParams() instanceof MarginLayoutParams) ? (MarginLayoutParams) child.getLayoutParams() : null;
        if (centreInVisibleBounds) {
            current = (child.getTop() + child.getBottom()) >> 1;
            if (marginLp != null) {
                current = current + (marginLp.bottomMargin - marginLp.topMargin) / 2;
            }
            return current - mVisibleBounds.centerY() + mVisibleBounds.top - getPaddingTop();
        } else {
            current = child.getTop();
            if (marginLp != null) {
                current = current - marginLp.topMargin;
            }
            return current - getPaddingTop();
        }
    }

    protected int getVerticalScrollRange() {
        int scrollRange = 0, contentSize = getContentHeight();
        if (contentSize > 0) {
            scrollRange = contentSize - mVisibleBounds.height();
            if (scrollRange < 0) {
                scrollRange = 0;
            }
        }
        return scrollRange;
    }

    protected int getHorizontalScrollRange() {
        int scrollRange = 0, contentSize = getContentWidth();
        if (contentSize > 0) {
            scrollRange = contentSize - mVisibleBounds.width();
            if (scrollRange < 0) {
                scrollRange = 0;
            }
        }
        return scrollRange;
    }

    protected int getScrollRange(boolean horizontal) {
        return horizontal ? getHorizontalScrollRange() : getVerticalScrollRange();
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return Math.max(0, super.computeVerticalScrollOffset());
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        return Math.max(0, super.computeHorizontalScrollOffset());
    }

    @Override
    protected int computeVerticalScrollRange() {
        final int count = getChildCount();
        final int paddingTop = getPaddingTop();
        final int contentHeight = mVisibleBounds.height();
        if (count == 0) {
            return contentHeight;
        }
        int scrollRange = paddingTop + getContentHeight();
        final int scrollY = getScrollY();
        final int overScrollBottom = Math.max(0, scrollRange - contentHeight);
        if (scrollY < 0) {
            scrollRange -= scrollY;
        } else if (scrollY > overScrollBottom) {
            scrollRange += scrollY - overScrollBottom;
        }
        return scrollRange;
    }

    @Override
    protected int computeHorizontalScrollRange() {
        final int count = getChildCount();
        final int paddingLeft = getPaddingLeft();
        final int contentWidth = mVisibleBounds.width();
        if (count == 0) {
            return contentWidth;
        }
        int scrollRange = paddingLeft + getContentWidth();
        final int scrollX = getScrollX();
        final int overScrollRight = Math.max(0, scrollRange - contentWidth);
        if (scrollX < 0) {
            scrollRange -= scrollX;
        } else if (scrollX > overScrollRight) {
            scrollRange += scrollX - overScrollRight;
        }
        return scrollRange;
    }

    protected void computeVisibleBounds(int scrollX, int scrollY, boolean scrollChanged) {
        int beforeHash = mVisibleBounds.hashCode(), width = getWidth(), height = getHeight();
        if (width <= 0) {
            width = getMeasuredWidth();
        }
        if (height <= 0) {
            height = getMeasuredHeight();
        }
        mVisibleBounds.left = getPaddingLeft() + scrollX;
        mVisibleBounds.top = getPaddingTop() + scrollY;
        mVisibleBounds.right = mVisibleBounds.left + width - getPaddingLeft() - getPaddingRight();
        mVisibleBounds.bottom = mVisibleBounds.top + height - getPaddingTop() - getPaddingBottom();
        if (beforeHash != mVisibleBounds.hashCode()) {
            onScrollChanged(scrollX, scrollY, mVisibleBounds, scrollChanged);
        }
    }

    @Override
    public void removeAllViewsInLayout() {
        super.removeAllViewsInLayout();
        mAttachLayout = false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachLayout = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachLayout = false;
    }

    @Override
    public BaseViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new BaseViewGroup.LayoutParams(getContext(), attrs);
    }

    @Override
    protected BaseViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new BaseViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected BaseViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new BaseViewGroup.LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof BaseViewGroup.LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int gravity = -1;
        public int maxWidth = -1;
        public int maxHeight = -1;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, ATTRS_LAYOUTPARAMS);
            gravity = a.getInt(0, gravity);
            maxWidth = a.getDimensionPixelSize(1, maxWidth);
            maxHeight = a.getDimensionPixelSize(2, maxHeight);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
            if (source instanceof BaseViewGroup.LayoutParams) {
                BaseViewGroup.LayoutParams lp = (BaseViewGroup.LayoutParams) source;
                gravity = lp.gravity;
                maxWidth = lp.maxWidth;
                maxHeight = lp.maxHeight;
            } else {
                if (source instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) source;
                    gravity = lp.gravity;
                }
                if (source instanceof FrameLayout.LayoutParams) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) source;
                    gravity = lp.gravity;
                }
            }
        }

        public int getMarginHorizontal() {
            return leftMargin + rightMargin;
        }

        public int getMarginVertical() {
            return topMargin + bottomMargin;
        }

        public void measure(View child, int childWidthMeasureSpec, int childHeightMeasureSpec) {
            if (maxWidth > 0) {
                childWidthMeasureSpec = getLimitMeasureSpec(childWidthMeasureSpec, maxWidth);
            }
            if (maxHeight > 0) {
                childHeightMeasureSpec = getLimitMeasureSpec(childHeightMeasureSpec, maxHeight);
            }
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }

        public void measure(View view, int parentWidthMeasureSpec, int parentHeightMeasureSpec, int widthUsed, int heightUsed) {
            measure(view
                    , BaseViewGroup.getChildMeasureSpec(parentWidthMeasureSpec, widthUsed, width)
                    , BaseViewGroup.getChildMeasureSpec(parentHeightMeasureSpec, heightUsed, height));
        }

        private int getLimitMeasureSpec(int measureSpec, int maxSize) {
            int size = MeasureSpec.getSize(measureSpec);
            int mode = MeasureSpec.getMode(measureSpec);
            if (size > maxSize) {
                size = maxSize;
            }
            if (mode == MeasureSpec.UNSPECIFIED) {
                mode = MeasureSpec.AT_MOST;
            }
            return MeasureSpec.makeMeasureSpec(size, mode);
        }
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY);

        void onScrollStateChanged(int state, int oldState);
    }
}
