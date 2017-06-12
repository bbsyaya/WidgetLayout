package com.rexy.widgetlayout.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.rexy.widgetlayout.R;
import com.rexy.widgets.group.PageScrollTab;
import com.rexy.widgets.group.PageScrollView;

/**
 * TODO:功能说明
 *
 * @author: rexy
 * @date: 2017-06-05 15:02
 */
public class FragmentPageViewPager extends FragmentPageBase {

    PageScrollTab mSlideTab;
    ToggleButton mToggleHeader;
    ToggleButton mToggleFooter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pagescrollview_viewpager, container, false);
        initView(root);
        return root;
    }

    @Override
    protected void initView(View root) {
        super.initView(root);
        mSlideTab = (PageScrollTab) root.findViewById(R.id.pageTabs);
        mToggleHeader = (ToggleButton) root.findViewById(R.id.togglePageHeader);
        mToggleFooter = (ToggleButton) root.findViewById(R.id.togglePageFooter);
        mToggleHeader.setOnCheckedChangeListener(this);
        mToggleFooter.setOnCheckedChangeListener(this);
        initPageTab(mPageScrollView, mSlideTab);
    }

    private void initPageTab(final PageScrollView scrollView, final PageScrollTab tabHost) {
        scrollView.setLogTag("viewPager");
        tabHost.setLogTag("scrollTab");
        final View.OnClickListener pageClick1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = scrollView.indexOfItemView(v);
                if (index >= 0) {
                    scrollView.scrollToCentre(index, 0, -1);
                }
            }
        };
        int pageItemCount = scrollView.getItemCount();
        for (int i = 0; i < pageItemCount; i++) {
            View child = scrollView.getItemView(i);
            child.setOnClickListener(pageClick1);
            if (child instanceof TextView) {
                CharSequence text = ((TextView) child).getText();
                tabHost.addTabItem(text.subSequence(TextUtils.indexOf(text, 'i'), text.length()), true);
            }
        }

        tabHost.setTabClickListener(new PageScrollTab.ITabClickEvent() {
            @Override
            public boolean onTabClicked(PageScrollTab parent, View cur, int curPos, View pre, int prePos) {
                scrollView.scrollToCentre(curPos, 0, -1);
                return false;
            }
        });
        scrollView.setOnPageChangeListener(new PageScrollView.OnPageChangeListener() {
            @Override
            public void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            }

            @Override
            public void onScrollStateChanged(int state, int oldState) {
                tabHost.callPageScrollStateChanged(state, scrollView.getCurrentItem());
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tabHost.callPageScrolled(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position, int oldPosition) {
                tabHost.callPageSelected(position);
            }
        });
    }

    @Override
    public boolean setContentOrientationInner(boolean vertical, boolean init) {
        boolean handled = super.setContentOrientationInner(vertical, init);
        if (handled) {
            adjustPageHeaderAndFooter(vertical, true, mToggleHeader.isChecked());
            adjustPageHeaderAndFooter(vertical, false, mToggleFooter.isChecked());
        }
        return handled;
    }

    private void adjustPageHeaderAndFooter(boolean vertical, boolean header, boolean needAdded) {
        TextView pageHeaderFooter = null;
        if (needAdded) {
            pageHeaderFooter = (TextView) LayoutInflater.from(getActivity()).inflate(vertical ?
                    R.layout.pagescrollview_headerfooter_vertical : R.layout.pagescrollview_headerfooter_horizontal, mPageScrollView, false);
            if (header) {
                pageHeaderFooter.setText("I am page Header");
            } else {
                pageHeaderFooter.setText("I am page Footer");
            }
        }
        if (header) {
            mPageScrollView.setPageHeaderView(pageHeaderFooter);
        } else {
            mPageScrollView.setPageFooterView(pageHeaderFooter);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        super.onCheckedChanged(buttonView, isChecked);
        if (buttonView == mToggleHeader) {
            adjustPageHeaderAndFooter(mPageScrollView.getOrientation() == PageScrollView.VERTICAL, true, isChecked);
        }
        if (buttonView == mToggleFooter) {
            adjustPageHeaderAndFooter(mPageScrollView.getOrientation() == PageScrollView.VERTICAL, false, isChecked);
        }
    }
}
