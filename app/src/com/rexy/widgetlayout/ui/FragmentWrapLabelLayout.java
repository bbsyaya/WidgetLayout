package com.rexy.widgetlayout.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.rexy.widgets.adapter.ItemProvider;
import com.rexy.widgets.group.LabelLayout;
import com.rexy.widgets.group.WrapLayout;
import com.rexy.widgets.tools.ViewUtils;
import com.rexy.widgetlayout.R;

/**
 * TODO:功能说明
 *
 * @author: rexy
 * @date: 2017-06-05 15:03
 */
public class FragmentWrapLabelLayout extends FragmentViewPicker {
    LabelLayout mLabelLayout;
    WrapLayout mWrapLayout;
    ToggleButton mToggleOpt;
    int mMinRandomWidth = 45, mMaxRandomWidth = 90;
    int mMinRandomHeight = 35, mMaxRandomHeight = 70;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_wraplabel,container,false);
        mWrapLayout = ViewUtils.view(root, R.id.wrapLayout);
        mLabelLayout = ViewUtils.view(root, R.id.labelLayout);
        mToggleOpt = ViewUtils.view(root, R.id.toggleOptView);
        initViewProperties(mLabelLayout, mWrapLayout);
        buildRandomView(7,true);
        mToggleOpt.setChecked(!mToggleOpt.isChecked());
        return root;
    }

    private void initViewProperties(final LabelLayout labelLayout, WrapLayout wrapLayout) {
        mMinRandomWidth = (int) (mDensity * mMinRandomWidth);
        mMaxRandomWidth = (int) (mDensity * mMaxRandomWidth);
        mMinRandomHeight = (int) (mDensity * mMinRandomHeight);
        mMaxRandomHeight = (int) (mDensity * mMaxRandomHeight);
        final String[] mLabels = new String[]{
                "A", "B", "C", "D", "E", "F", "G", "H"
        };
        labelLayout.setItemProvider(new ItemProvider.ViewProvider() {
            @Override
            public int getViewType(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return buildView(getTitle(position),true);
            }

            @Override
            public CharSequence getTitle(int position) {
                return mLabels[position];
            }

            @Override
            public Object getItem(int position) {
                return mLabels[position];
            }

            @Override
            public int getCount() {
                return mLabels == null ? 0 : mLabels.length;
            }
        });
        final LabelLayout.OnLabelClickListener mLabelClicker = new LabelLayout.OnLabelClickListener() {
            @Override
            public void onLabelClick(LabelLayout parent, View labelView) {
                Object tag = labelView.getTag();
                CharSequence text = tag == null ? null : String.valueOf(tag);
                if (text == null && labelView instanceof TextView) {
                    text = ((TextView) labelView).getText();
                }
                if (text != null) {
                    Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                }
            }
        };
        mLabelLayout.setOnLabelClickListener(mLabelClicker);
    }

    private int buildRandomSize(int minSize, int maxSize) {
        if (maxSize > minSize && minSize > 0) {
            mRandom.setSeed(System.currentTimeMillis() + mRandom.nextInt(maxSize));
            return minSize + mRandom.nextInt(maxSize - minSize + 1);
        }
        return -1;
    }

    @Override
    protected View buildView(int minWidth, int maxWidth, int minHeight, int maxHeight, int gravity, int color, boolean random) {
        if (random) {
            minWidth = buildRandomSize(mMinRandomWidth, mMaxRandomWidth);
            minHeight = buildRandomSize(mMinRandomHeight, mMaxRandomHeight);
        }
        return super.buildView(minWidth, maxWidth, minHeight, maxHeight, gravity, color, random);
    }

    @Override
    protected void onAddOrRemoveView(View addView) {
        ViewGroup optView = mToggleOpt.isChecked() ? mLabelLayout : mWrapLayout;
        if (addView == null) {
            if (optView.getChildCount() > 0) {
                optView.removeViewAt(optView.getChildCount() - 1);
            }
        } else {
            optView.addView(addView);
        }
    }
}
