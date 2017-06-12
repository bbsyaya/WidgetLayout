package com.rexy.widgets.group;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.rexy.widgets.adapter.ItemProvider;
import com.rexy.widgets.tools.ViewUtils;
import com.rexy.widgets.view.CheckText;
import com.rexy.widgetlayout.R;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * TODO:功能说明
 *
 * @author: rexy
 * @date: 2017-06-02 15:47
 */
public class LabelLayout extends WrapLayout {
    private static int TAG_VIEW_TYPE = R.id.key_special_0;
    private static int TAG_SPECIAL = TAG_VIEW_TYPE;

    private int mTextSize = 15;
    private Drawable mLabelBackground;
    private ColorStateList mTextColorList;

    private ItemProvider mItemProvider;

    private OnLabelClickListener mOnLabelClickListener;

    private LinkedList<View> mCachedView = new LinkedList<View>();

    private ViewGroup.OnHierarchyChangeListener mHierarchyChangeListener = new ViewGroup.OnHierarchyChangeListener() {
        @Override
        public void onChildViewAdded(View parent, View child) {
            if (parent == LabelLayout.this && child != null) {
                child.setOnClickListener(mInnerClicker);
            }
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {
            if (parent == LabelLayout.this && child != null) {
                child.setOnClickListener(null);
                if ((child.getTag(TAG_VIEW_TYPE) instanceof Integer)) {
                    ViewUtils.setBackground(child, null);
                    mCachedView.add(child);
                }
            }
        }
    };

    private View.OnClickListener mInnerClicker = new View.OnClickListener() {
        @Override
        public void onClick(View child) {
            if(mOnLabelClickListener!=null){
                mOnLabelClickListener.onLabelClick(LabelLayout.this,child);
            }
        }
    };

    public LabelLayout(Context context) {
        super(context);
        init(context, null);
    }

    public LabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LabelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public LabelLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        float density = context.getResources().getDisplayMetrics().density;
        mTextSize *= density;
        TypedArray attr = attrs == null ? null : context.obtainStyledAttributes(attrs, new int[]{android.R.attr.textSize,
                android.R.attr.textColor});
        if (attr != null) {
            mTextSize = attr.getDimensionPixelSize(0, mTextSize);
            try {
                mTextColorList = attr.getColorStateList(1);
                int textColor = attr.getColor(1, 0xFF333333);
                if (mTextColorList != null && textColor != 0) {
                    mTextColorList = ColorStateList.valueOf(textColor);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            attr.recycle();
        }
        setOnHierarchyChangeListener(mHierarchyChangeListener);
    }

    private void buildLabels(ItemProvider provider, int itemCount) {
        ItemProvider.ViewProvider viewProvider = (provider instanceof ItemProvider.ViewProvider) ? (ItemProvider.ViewProvider) provider : null;
        boolean viewType = viewProvider != null;
        for (int i = 0; i < itemCount; i++) {
            View view;
            if (viewType) {
                int itemType = viewProvider.getViewType(i);
                View cacheView = getCacheView(itemType);
                view = viewProvider.getView(i, cacheView, LabelLayout.this);
                if (view != cacheView) {
                    view.setTag(TAG_VIEW_TYPE, itemType);
                }
            } else {
                view = makeLabel(provider.getTitle(i), null, null, 0);
            }
            view.setTag(provider.getItem(i));
            addView(view);
        }
    }

    private View getCacheView(int viewType) {
        Iterator<View> its = mCachedView.iterator();
        while (its.hasNext()) {
            View view = its.next();
            if ((view.getTag(TAG_VIEW_TYPE) instanceof Integer) && ((Integer) view.getTag(TAG_VIEW_TYPE) == viewType)) {
                its.remove();
                return view;
            }
        }
        return null;
    }

    private CheckText makeLabel(CharSequence label, Drawable background, ColorStateList textColor, int textSize) {
        CheckText tab = (CheckText) getCacheView(TAG_SPECIAL);
        if (tab == null) {
            tab = new CheckText(getContext());
            tab.setGravity(Gravity.CENTER);
            tab.setSingleLine();
            tab.setIncludeFontPadding(false);
            tab.setTag(TAG_VIEW_TYPE, TAG_SPECIAL);
        }
        tab.setText(label);
        background = background == null ? mLabelBackground : background;
        textColor = textColor == null ? mTextColorList : textColor;
        textSize = textSize <= 0 ? mTextSize : textSize;
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        if (textColor!=null&&tab.getTextColors() != textColor) {
            tab.setTextColor(textColor);
        }
        if (tab.getBackground() != background) {
            ViewUtils.setBackground(tab, background);
        }
        return tab;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setOnHierarchyChangeListener(mHierarchyChangeListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setOnHierarchyChangeListener(null);
        mCachedView.clear();
        removeAllViewsInLayout();
    }

    public ItemProvider getItemProvider() {
        return mItemProvider;
    }

    public void setItemProvider(ItemProvider provider) {
        if (mItemProvider != provider) {
            removeAllViewsInLayout();
            mItemProvider = provider;
            if (provider != null) {
                buildLabels(provider, provider.getCount());
            }
        }
    }

    public int getTextSizePixel() {
        return mTextSize;
    }

    public ColorStateList getTextColorList() {
        return mTextColorList;
    }

    public Drawable getLabelBackground() {
        return mLabelBackground;
    }

    public OnLabelClickListener getLabelClickListener() {
        return mOnLabelClickListener;
    }

    public void setOnLabelClickListener(OnLabelClickListener l) {
        mOnLabelClickListener = l;
    }

    public void setTextSizePixel(int textSize) {
        if (mTextSize != textSize) {
            mTextSize = textSize;
        }
    }

    public void setTextColor(int textColor) {
        if (textColor != 0 && (mTextColorList == null || mTextColorList.getDefaultColor() != textColor)) {
            mTextColorList = ColorStateList.valueOf(textColor);
        }
    }

    public void setTextColorList(ColorStateList colorList) {
        if (mTextColorList != colorList) {
            mTextColorList = colorList;
        }
    }

    public void setLabelBackground(Drawable labelBackground) {
        if (mLabelBackground != labelBackground) {
            mLabelBackground = labelBackground;
        }
    }

    public interface OnLabelClickListener {
        void onLabelClick(LabelLayout parent, View labelView);
    }

}