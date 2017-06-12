package com.rexy.widgets.group;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.Checkable;
import android.widget.TextView;

import com.rexy.widgets.adapter.ItemProvider;
import com.rexy.widgets.view.CheckText;
import com.rexy.widgetlayout.R;

import java.util.Locale;


public class PageScrollTab extends PageScrollView {

    private static int TAB_INDEX = R.id.key_special_0;
    private static final int[] ATTRS = new int[]{android.R.attr.textSize,
            android.R.attr.textColor};

    /**
     * 以下和
     */
    private int mCurrentPosition = 0; //当前选中的tab 索引。
    private float mCurrentPositionOffset = 0f;//选中tab 的偏移量子。



    /**
     * 以下是设置tab item 的最小padding 值。
     */
    private int mItemMinPaddingHorizontal = 10;
    private int mItemMinPaddingTop = 0;
    private int mItemMinPaddingBottom = 0;

    /**
     * tab item 的背景
     */
    private int mItemBackgroundFirst = 0, mItemBackground = 0, mItemBackgroundLast = 0, mItemBackgroundFull = 0;

    /**
     * 如果item 是 TextView 会应用以下属性。
     */

    private boolean mTextAllCaps = false;
    private Typeface mTextTypeFace = null;
    private int mTextTypefaceStyle = Typeface.NORMAL;
    private int mTextSize = 14;
    private int mTextColor = 0xFF666666;
    private int mTextColorResId = 0;

    private Paint mRectPaint;
    private Paint mDividerPaint;

    /**
     * item 之间垂直分割线。
     */
    private int mDividerWidth = 1;
    private int mDividerPadding = 6;
    private int mDividerColor = 0x1A000000;


    /**
     * 选中item 底部指示线。
     */
    private int mIndicatorHeight = 2;
    private int mIndicatorOffset = 0;
    private int mIndicatorColor = 0xffff9500;
    private float mIndicatorWidthPercent = 1;

    /**
     * 顶部水平分界线。
     */
    private int mTopLineHeight = 0;
    private int mTopLineColor = 0xffd8e2e9;

    /**
     * 底部水平分界线
     */
    private int mBottomLineHeight = 0;
    private int mBottomLineColor = 0x1A000000;

    private Locale mLocalInfo;
    private boolean mAutoCheckState = true;
    private View mPreCheckView = null;
    private PageScrollView.LayoutParams mItemLayoutParams;

    //c
    private ViewPager mViewPager = null;
    private final PageListener mViewPageListener = new PageListener();
    public ViewPager.OnPageChangeListener mDelegatePageListener;

    protected ItemProvider mITabProvider = null;
    protected ITabClickEvent mTabClick = null;
    private View.OnClickListener mTabItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Object tag = view.getTag(TAB_INDEX);
            int cur = (tag instanceof Integer) ? (Integer) tag : mCurrentPosition;
            int pre = mCurrentPosition;
            boolean handled = mTabClick == null ? false : mTabClick.onTabClicked(PageScrollTab.this, view, cur, mPreCheckView, pre);
            handTabClick(cur, pre, handled);
        }
    };

    protected void handTabClick(int cur, int pre, boolean handled) {
        if (!handled) {
            if (cur != pre) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(cur);
                } else {
                    setSelectedTab(cur, false, false);
                }
            }
        } else {
            mCurrentPosition = cur;
        }
    }

    public PageScrollTab(Context context) {
        this(context, null);
    }

    public PageScrollTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageScrollTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        setOrientation(HORIZONTAL);
        setChildFillParent(true);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mIndicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mIndicatorHeight, dm);
        mTopLineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mTopLineHeight, dm);
        mBottomLineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mBottomLineHeight, dm);
        mDividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mDividerPadding, dm);
        mItemMinPaddingHorizontal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mItemMinPaddingHorizontal, dm);
        mItemMinPaddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mItemMinPaddingTop, dm);
        mItemMinPaddingBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mItemMinPaddingBottom, dm);
        mDividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDividerWidth,
                dm);
        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, dm);
        // get system attrs (android:textSize and android:textColor)
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        mTextSize = a.getDimensionPixelSize(0, mTextSize);
        mTextColor = a.getColor(1, mTextColor);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.PageScrollTab);
        mItemBackground = a.getResourceId(R.styleable.PageScrollTab_tabItemBackground,
                mItemBackground);
        mItemBackgroundFirst = a.getResourceId(R.styleable.PageScrollTab_tabItemBackgroundFirst,
                mItemBackgroundFirst);
        mItemBackgroundLast = a.getResourceId(R.styleable.PageScrollTab_tabItemBackgroundLast,
                mItemBackgroundLast);
        mItemBackgroundFull = a.getResourceId(R.styleable.PageScrollTab_tabItemBackgroundFull,
                mItemBackgroundFull);


        mIndicatorColor = a.getColor(R.styleable.PageScrollTab_tabIndicatorColor,
                mIndicatorColor);
        mIndicatorHeight = a.getDimensionPixelSize(
                R.styleable.PageScrollTab_tabIndicatorHeight, mIndicatorHeight);
        mIndicatorOffset = a.getDimensionPixelSize(
                R.styleable.PageScrollTab_tabIndicatorOffset, mIndicatorOffset);
        float atrIndicatorWidthPercent = a.getFloat(R.styleable.PageScrollTab_tabIndicatorWidthPercent, mIndicatorWidthPercent);


        mTopLineColor = a.getColor(R.styleable.PageScrollTab_tabTopLineColor,
                mTopLineColor);
        mTopLineHeight = a.getDimensionPixelSize(
                R.styleable.PageScrollTab_tabTopLineHeight, mTopLineHeight);

        mBottomLineColor = a.getColor(R.styleable.PageScrollTab_tabBottomLineColor,
                mBottomLineColor);
        mBottomLineHeight = a.getDimensionPixelSize(
                R.styleable.PageScrollTab_tabBottomLineHeight, mBottomLineHeight);


        mDividerColor = a.getColor(R.styleable.PageScrollTab_tabItemDividerColor, mDividerColor);
        mDividerWidth = a.getDimensionPixelSize(R.styleable.PageScrollTab_tabItemDividerWidth, mDividerWidth);
        mDividerPadding = a.getDimensionPixelSize(
                R.styleable.PageScrollTab_tabItemDividerPadding, mDividerPadding);


        mItemMinPaddingHorizontal = a.getDimensionPixelSize(
                R.styleable.PageScrollTab_tabItemMinPaddingHorizontal, mItemMinPaddingHorizontal);
        mItemMinPaddingTop = a.getDimensionPixelSize(
                R.styleable.PageScrollTab_tabItemMinPaddingTop, mItemMinPaddingTop);
        mItemMinPaddingBottom = a.getDimensionPixelSize(
                R.styleable.PageScrollTab_tabItemMinPaddingBottom, mItemMinPaddingBottom);

        mTextAllCaps = a.getBoolean(R.styleable.PageScrollTab_tabItemTextCaps, mTextAllCaps);
        mTextColorResId = a.getResourceId(R.styleable.PageScrollTab_tabItemTextColor,
                mTextColorResId);
        a.recycle();

        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStyle(Style.FILL);

        mDividerPaint = new Paint();
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setStrokeWidth(mDividerWidth);

        if (mLocalInfo == null) {
            mLocalInfo = getResources().getConfiguration().locale;
        }
        mItemLayoutParams=new PageScrollView.LayoutParams(-2,-2,Gravity.CENTER_VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL|getGravity());
        setIndicatorWidthPercent(atrIndicatorWidthPercent);
    }

    public int getTabItemCount() {
        if (mITabProvider != null) {
            return mITabProvider.getCount();
        }
        if (mViewPager != null && mViewPager.getAdapter() != null) {
            return mViewPager.getAdapter().getCount();
        }
        return getItemCount();
    }

    public ItemProvider getTabProvider() {
        if (mITabProvider != null) {
            return mITabProvider;
        }
        if (mViewPager != null && mViewPager.getAdapter() instanceof ItemProvider) {
            return (ItemProvider) mViewPager.getAdapter();
        }
        return null;
    }

    public void setViewPager(ViewPager pager) {
        mViewPager = pager;
        PagerAdapter adp = pager == null ? null : pager.getAdapter();
        if (adp != null) {
            if (adp instanceof ItemProvider) {
                mITabProvider = (ItemProvider) adp;
            }
            pager.setOnPageChangeListener(mViewPageListener);
        }
        notifyDataSetChanged();
    }

    public void setTabProvider(ItemProvider provider, int currentPosition) {
        mITabProvider = provider;
        this.mCurrentPosition = currentPosition;
        notifyDataSetChanged();
    }

    public void setTabClickListener(ITabClickEvent l) {
        mTabClick = l;
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.mDelegatePageListener = listener;
    }

    public void notifyDataSetChanged() {
        removeAllViews();
        int tabItemCount = getTabItemCount();
        boolean accessToTabProvider = mITabProvider != null;
        boolean accessToViewPage = mViewPager != null;
        boolean isViewTab;
        if (!accessToTabProvider && !accessToViewPage) {
            return;
        } else {
            isViewTab = (accessToTabProvider && mITabProvider instanceof ItemProvider.ViewProvider);
        }
        for (int i = 0; i < tabItemCount; i++) {
            if (isViewTab) {
                addTab(i, ((ItemProvider.ViewProvider) mITabProvider).getView(i,null,PageScrollTab.this));
            } else {
                CharSequence label = accessToTabProvider ? (mITabProvider.getTitle(i)) : (mViewPager.getAdapter().getPageTitle(i));
                addTextTab(i, label);
            }
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                if (mViewPager != null) {
                    mCurrentPosition = mViewPager.getCurrentItem();
                }
                int n = getItemCount();
                if (mCurrentPosition >= 0 && mCurrentPosition < n) {
                    mPreCheckView = getVirtualChildAt(mCurrentPosition, true);
                    if (mPreCheckView instanceof Checkable && mPreCheckView.isEnabled()) {
                        ((Checkable) mPreCheckView).setChecked(true);
                    }
                    scrollToChild(mCurrentPosition, 0, false);
                } else {
                    scrollToChild(0, 0, false);
                }
            }
        });
    }

    private void addTextTab(final int position, CharSequence title) {
        CheckText tab = new CheckText(getContext());
        tab.setEnabled(true);
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        tab.setIncludeFontPadding(false);
        addTab(position, tab);
    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setTag(TAB_INDEX, position);
        tab.setOnClickListener(mTabItemClick);
        int left = Math.max(mItemMinPaddingHorizontal, tab.getPaddingLeft());
        int top = Math.max(mItemMinPaddingTop, tab.getPaddingTop());
        int right = Math.max(mItemMinPaddingHorizontal, tab.getPaddingRight());
        int bottom = Math.max(mItemMinPaddingBottom, tab.getPaddingBottom());
        tab.setPadding(left, top, right, bottom);
        addView(tab, position,mItemLayoutParams);
    }

    public void addTabItem(CharSequence title, boolean updateStyle) {
        addTextTab(getChildCount(), title);
        if (updateStyle) {
            updateTabStyles();
        }
    }

    private void updateTabStyles() {
        int itemCount = getChildCount();
        boolean hasMutiBackground = mItemBackgroundFirst != 0 && mItemBackgroundLast != 0;
        for (int i = 0; i < itemCount; i++) {
            int backgroundRes = mItemBackground;
            View v = getChildAt(i);
            if (hasMutiBackground) {
                if (i == 0) {
                    if (itemCount == 1) {
                        if (mItemBackgroundFull != 0) {
                            backgroundRes = mItemBackgroundFull;
                        }
                    } else {
                        backgroundRes = mItemBackgroundFirst;
                    }
                } else if (i == itemCount - 1) {
                    backgroundRes = mItemBackgroundLast;
                }
            }
            if (backgroundRes != 0) {
                v.setBackgroundResource(backgroundRes);
            }
            if (v instanceof TextView) {
                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
                tab.setTypeface(mTextTypeFace, mTextTypefaceStyle);
                if (mTextColorResId != 0) {
                    tab.setTextColor(getContext().getResources().getColorStateList(mTextColorResId));
                } else {
                    tab.setTextColor(mTextColor);
                }
                // setAllCaps() is only available from API 14, so the upper case
                // is made manually if we are on a
                // pre-ICS-build
                if (mTextAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(mLocalInfo));
                    }
                }
            }
        }
    }

    private void scrollToChild(int position, int offset, boolean anim) {
        scrollToCentre(position, offset, anim ? -1 : 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int itemCount = getTabItemCount();
        if (isInEditMode() || itemCount == 0) {
            return;
        }
        int width = getWidth(),height = getHeight();

        // draw divider
        if (mDividerWidth > 0) {
            mDividerPaint.setColor(mDividerColor);
            float dividerXOffset = mDividerPaint.getStrokeWidth() / 2;
            for (int i = 0; i < itemCount - 1; i++) {
                View tab = getVirtualChildAt(i, true);
                float startX = tab.getRight() + dividerXOffset;
                canvas.drawLine(startX, mDividerPadding, startX, height - mDividerPadding, mDividerPaint);
            }
        }

        // draw top or bottom line.
        if (mBottomLineHeight > 0) {
            mRectPaint.setColor(mBottomLineColor);
            canvas.drawRect(0, height - mBottomLineHeight, width, height, mRectPaint);
        }
        if (mTopLineHeight > 0) {
            mRectPaint.setColor(mTopLineColor);
            canvas.drawRect(0, 0, width, mTopLineHeight, mRectPaint);
        }

        // draw indicator line
        if (mIndicatorHeight > 0 && mIndicatorWidthPercent > 0) {
            mRectPaint.setColor(mIndicatorColor);
            // default: line below current tab
            View currentTab = getVirtualChildAt(mCurrentPosition, true);
            float lineLeft = currentTab.getLeft();
            float lineRight = currentTab.getRight();
            // if there is an offset, start interpolating left and right coordinates   between current and next tab
            if (mCurrentPositionOffset > 0f && mCurrentPosition < itemCount - 1) {
                View nextTab = getVirtualChildAt(mCurrentPosition + 1, true);
                final float nextTabLeft = nextTab.getLeft();
                final float nextTabRight = nextTab.getRight();
                lineLeft = (mCurrentPositionOffset * nextTabLeft + (1f - mCurrentPositionOffset)
                        * lineLeft);
                lineRight = (mCurrentPositionOffset * nextTabRight + (1f - mCurrentPositionOffset)
                        * lineRight);
            }
            if (mIndicatorOffset != 0) {
                height -= mIndicatorOffset;
            }
            if (mIndicatorWidthPercent >= 1) {
                canvas.drawRect(lineLeft, height - mIndicatorHeight, lineRight, height, mRectPaint);
            } else {
                float offsetSize = (lineRight - lineLeft) * (1 - mIndicatorWidthPercent);
                canvas.drawRect(lineLeft + offsetSize, height - mIndicatorHeight, lineRight - offsetSize, height, mRectPaint);
            }
            // draw underline
        }
    }

    private class PageListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            callPageScrolled(position, positionOffset);
            if (mDelegatePageListener != null) {
                mDelegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            callPageScrollStateChanged(state, mViewPager.getCurrentItem());
            if (mDelegatePageListener != null) {
                mDelegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            callPageSelected(position);
            if (mDelegatePageListener != null) {
                mDelegatePageListener.onPageSelected(position);
            }
        }
    }


    public void callPageScrolled(int position, float positionOffset) {
        mCurrentPosition = position;
        mCurrentPositionOffset = positionOffset;
        scrollToChild(position, (int) (positionOffset * getVirtualChildAt(position, true).getWidth()), false);
        invalidate();
    }

    public void callPageSelected(int position) {
        setSelectedTab(position, true);
    }

    public void callPageScrollStateChanged(int state, int viewPageItem) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (viewPageItem != mCurrentPosition) {
                mCurrentPosition = viewPageItem;
                mCurrentPositionOffset = 0;
            }
            scrollToChild(viewPageItem, 0, false);
        }
    }

    public void setSelectedTab(int position, boolean fromViewPageListener, boolean animToCur) {
        setSelectedTab(position, fromViewPageListener);
        scrollToChild(mCurrentPosition, 0, animToCur);
    }

    protected void setSelectedTab(int position, boolean fromViewPageListener) {
        if (!fromViewPageListener) {
            mCurrentPosition = position;
            mCurrentPositionOffset = 0;
        }
        View v = getVirtualChildAt(position, true);
        if (mPreCheckView == null || mPreCheckView != v) {
            if (mAutoCheckState) {
                if (mPreCheckView instanceof Checkable) {
                    ((Checkable) mPreCheckView).setChecked(false);
                }
            }
            mPreCheckView = v;
            if (v instanceof Checkable) {
                ((Checkable) v).setChecked(true);
            }
        }
        invalidate();
    }

    public boolean setCheckedAtPosition(int pos, boolean checked) {
        if (pos < 0) {
            pos = mCurrentPosition;
        }
        int itemCount = getTabItemCount();
        if (pos >= 0 && pos < itemCount) {
            View v = getVirtualChildAt(pos, true);
            if (v instanceof Checkable) {
                Checkable cv = (Checkable) v;
                if (cv.isChecked() != checked) {
                    cv.setChecked(checked);
                    return true;
                }
            }
        }
        return false;
    }

    public int getSelectedPosition() {
        return mCurrentPosition;
    }

    public View getSelectedView() {
        if (mCurrentPosition >= 0 && mCurrentPosition < getTabItemCount()) {
            return getVirtualChildAt(mCurrentPosition,true);
        }
        return null;
    }

    public <T extends View> SparseArray<T> findTabViewByClass(Class<T> cls, int from, int endExclude) {
        int size = getChildCount();
        int start = from > 0 ? from : 0;
        int end = (endExclude <= 0 || endExclude > size) ? size : endExclude;
        SparseArray<T> result = new SparseArray(size + 1);
        while (start < end) {
            View itemView = getChildAt(start);
            if (cls.isAssignableFrom(itemView.getClass())) {
                result.put(start, (T) itemView);
            }
            start++;
        }
        return result;
    }

    public void setIndicatorWidthPercent(float widthPercent) {
        if (widthPercent != mIndicatorWidthPercent) {
            if (widthPercent >= 1) {
                widthPercent = 1;
            }
            if (widthPercent < 0) {
                widthPercent = 0;
            }
            if (widthPercent != mIndicatorWidthPercent) {
                mIndicatorWidthPercent = widthPercent;
                invalidate();
            }
        }
    }

    public void setIndicatorOffset(int indicatorOffsetPx) {
        this.mIndicatorOffset = indicatorOffsetPx;
        invalidate();
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.mIndicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public void setIndicatorColor(int indicatorColor) {
        this.mIndicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorId(int resId) {
        this.mIndicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public void setDividerWidth(int dividerWidth) {
        this.mDividerWidth = dividerWidth;
        invalidate();
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.mDividerPadding = dividerPaddingPx;
        invalidate();
    }

    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorId(int resId) {
        this.mDividerColor = getResources().getColor(resId);
        invalidate();
    }

    public void setTopLineHeight(int topLineHeightPx) {
        this.mTopLineHeight = topLineHeightPx;
        invalidate();
    }

    public void setTopLineColor(int color) {
        this.mTopLineColor = color;
        invalidate();
    }

    public void setTopLineColorId(int resId) {
        this.mTopLineColor = getResources().getColor(resId);
        invalidate();
    }

    public void setBottomLineHeight(int underlineHeightPx) {
        this.mBottomLineHeight = underlineHeightPx;
        invalidate();
    }

    public void setBottomLineColor(int bottomLineColor) {
        this.mBottomLineColor = bottomLineColor;
        invalidate();
    }

    public void setBottomLineColorId(int resId) {
        this.mBottomLineColor = getResources().getColor(resId);
        invalidate();
    }

    public void setAutoCheckState(boolean autoCheckState) {
        this.mAutoCheckState = autoCheckState;
    }

    public boolean isAutoCheckState() {
        return mAutoCheckState;
    }

    public void setTextAllCaps(boolean textAllCaps) {
        this.mTextAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.mTextSize = textSizePx;
        updateTabStyles();
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorId(int resId) {
        this.mTextColorResId = resId;
        updateTabStyles();
    }

    public void setTextTypeface(Typeface typeface, int style) {
        this.mTextTypeFace = typeface;
        this.mTextTypefaceStyle = style;
        updateTabStyles();
    }

    /**
     * {first middle last full} or {normal}
     *
     * @param resIds
     */
    public void setItemBackground(int... resIds) {
        int size = resIds == null ? 0 : resIds.length;
        if (size == 1) {
            this.mItemBackground = resIds[0];
        } else {
            if (size > 0) {
                this.mItemBackgroundFirst = resIds[0];
            }
            if (size > 1) {
                this.mItemBackground = resIds[1];
            }
            if (size > 2) {
                this.mItemBackgroundLast = resIds[2];
            }
            if (size > 3) {
                this.mItemBackgroundFull = resIds[4];
            }
        }
    }

    public void setItemPaddingHorizonal(int paddingHorizonalPixel) {
        this.mItemMinPaddingHorizontal = paddingHorizonalPixel;
    }

    public void setItemPaddingTop(int paddingTopPixel) {
        this.mItemMinPaddingTop = paddingTopPixel;
    }

    public void setItemPaddingBottom(int paddingBottomPixel) {
        this.mItemMinPaddingBottom = paddingBottomPixel;
    }

    public void smoothScroll(int from, int to, Animation.AnimationListener l) {
        int childCount = getItemCount();
        if (from >= 0 && to >= 0 && (from < childCount && to < childCount)) {
            if (getAnimation() != null) {
                getAnimation().cancel();
                clearAnimation();
            }
            boolean horizontal = mOrientation == HORIZONTAL;
            int scrollFrom = computeScrollOffset(getVirtualChildAt(from, true), 0, false, horizontal);
            int scrollTo = computeScrollOffset(getVirtualChildAt(to, true), 0, false, horizontal);
            if (scrollTo != scrollFrom) {
                int absDx = Math.abs(scrollTo - scrollFrom);
                ScrollAnimation anim = new ScrollAnimation(scrollFrom, scrollTo);
                int measureWidth = getMeasuredWidth();
                if (measureWidth == 0) {
                    measureWidth = Math.max(getSuggestedMinimumWidth(), 1);
                }
                anim.setDuration(Math.min(4000, absDx * 1800 / measureWidth));
                anim.setInterpolator(new LinearInterpolator());
                anim.setAnimationListener(l);
                startAnimation(anim);
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mCurrentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = mCurrentPosition;
        return savedState;
    }

    static class SavedState extends View.BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public interface ITabClickEvent {
        boolean onTabClicked(PageScrollTab parent, View cur, int curPos, View pre, int prePos);
    }

    class ScrollAnimation extends Animation {
        private int mScrollFrom, mScrollTo;

        public ScrollAnimation(int from, int to) {
            mScrollFrom = from;
            mScrollTo = to;
        }
        @Override
        protected void applyTransformation(float time, Transformation t) {
            int current = (int) (mScrollFrom + (mScrollTo - mScrollFrom) * time);
            scrollTo(current, 0);
        }
    }
}