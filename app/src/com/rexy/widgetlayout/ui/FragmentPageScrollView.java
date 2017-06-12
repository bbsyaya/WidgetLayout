package com.rexy.widgetlayout.ui;
import com.rexy.widgets.group.PageScrollView;
import com.rexy.widgetlayout.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

/**
 * TODO:功能说明
 *
 * @author: rexy
 * @date: 2017-06-05 15:02
 */
public class FragmentPageScrollView extends FragmentPageBase{

    ToggleButton mToggleFloatStart;
    ToggleButton mToggleFloatEnd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pagescrollview_scrollview, container, false);
        initView(root);
        return root;
    }

    @Override
    protected void initView(View root) {
        super.initView(root);
        mToggleFloatStart = (ToggleButton) root.findViewById(R.id.toggleFloatFirst);
        mToggleFloatEnd = (ToggleButton) root.findViewById(R.id.toggleFloatEnd);
        mToggleFloatStart.setOnCheckedChangeListener(this);
        mToggleFloatEnd.setOnCheckedChangeListener(this);
        mPageScrollView.setLogTag("scrollView");
        initPageScrollViewItemClick(mPageScrollView);
    }

    @Override
    public boolean setContentOrientationInner(boolean vertical, boolean init) {
        adjustFloatViewParams(vertical);
        boolean handled = super.setContentOrientationInner(vertical, init);
        if (handled && init) {
            adjustFloatIndex(true, mToggleFloatStart.isChecked());
            adjustFloatIndex(false, mToggleFloatEnd.isChecked());
        }
        return handled;
    }

    private void initPageScrollViewItemClick(final PageScrollView scrollView) {
        final View.OnClickListener pageClick1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = scrollView.indexOfItemView(v);
                if (index >= 0) {
                    scrollView.scrollTo(index, 0, -1);
                }
            }
        };
        int pageItemCount = scrollView.getItemCount();
        for (int i = 0; i < pageItemCount; i++) {
            scrollView.getItemView(i).setOnClickListener(pageClick1);
        }
    }

    private void adjustFloatViewParams(boolean vertical) {
        int pageItemCount = mPageScrollView.getItemCount();
        if (pageItemCount >= 2) {
            PageScrollView.LayoutParams lp1 = (PageScrollView.LayoutParams) mPageScrollView.getItemView(0).getLayoutParams();
            PageScrollView.LayoutParams lp2 = (PageScrollView.LayoutParams) mPageScrollView.getItemView(pageItemCount - 1).getLayoutParams();
            int sizeShort = (int) (mDensity * 60);
            int sizeLong = (int) (mDensity * 350);
            if (vertical) {
                lp1.width = lp2.width = sizeLong;
                lp1.height = lp2.height = sizeShort;
            } else {
                lp1.width = lp2.width = sizeShort;
                lp1.height = lp2.height = sizeLong;
            }
        }
    }

    private void adjustFloatIndex(boolean header, boolean needAdded) {
        int floatIndex = -1;
        if (needAdded) {
            floatIndex = header ? 0 : mPageScrollView.getItemCount() - 1;
        }
        if (header) {
            mPageScrollView.setFloatViewStartIndex(floatIndex);
        } else {
            mPageScrollView.setFloatViewEndIndex(floatIndex);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        super.onCheckedChanged(buttonView, isChecked);
        if (buttonView == mToggleFloatStart) {
            adjustFloatIndex(true, isChecked);
        }
        if (buttonView == mToggleFloatEnd) {
            adjustFloatIndex(false, isChecked);
        }
    }
}
