package com.rexy.widgetlayout.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rexy.common.BaseFragment;
import com.rexy.widgets.tools.ViewUtils;
import com.rexy.widgetlayout.R;

/**
 * TODO:功能说明
 *
 * @author: rexy
 * @date: 2017-06-05 14:56
 */
public class FragmentPageScrollContainer extends BaseFragment implements View.OnClickListener {
    TextView mToggleViewPage;
    TextView mToggleOrientation;
    String[] mFragmentTags = new String[]{"ScrollView", "ViewPager"};
    int mVisibleFragmentIndex = 0;

    boolean mViewAsScrollView;
    boolean mViewAsVertical;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pagescrollview_container, container, false);
        mToggleViewPage = ViewUtils.view(root, R.id.toggleViewPager);
        mToggleOrientation = ViewUtils.view(root, R.id.toggleOrientation);
        mToggleViewPage.setOnClickListener(this);
        mToggleOrientation.setOnClickListener(this);
        switchToFragment(mVisibleFragmentIndex, 1 - mVisibleFragmentIndex);
        return root;
    }

    public boolean getDefaultViewTypeOrientation(boolean scrollView) {
        if (scrollView) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mToggleOrientation) {
            setViewOrientationInner(!mViewAsVertical, true);
        }
        if (v == mToggleViewPage) {
            int willIndex = 1 - mVisibleFragmentIndex;
            switchToFragment(willIndex, mVisibleFragmentIndex);
            mVisibleFragmentIndex = willIndex;
        }
    }

    public void setViewOrientation(boolean vertical) {
        setViewOrientationInner(vertical, false);
    }

    public void setViewOrientationInner(boolean vertical, boolean notify) {
        if (mViewAsVertical != vertical) {
            mViewAsVertical = vertical;
            mToggleOrientation.setText(vertical ? "VERTICAL" : "HORIZONTAL");
            if (notify) {
                Fragment fragment = getChildFragmentManager().findFragmentByTag(mFragmentTags[mVisibleFragmentIndex]);
                if (fragment instanceof FragmentPageBase) {
                    ((FragmentPageBase) fragment).setContentOrientation(vertical);
                }
            }
        }
    }

    private void setViewType(boolean scrollView) {
        if (mViewAsScrollView != scrollView) {
            mViewAsScrollView = scrollView;
            mToggleViewPage.setText(scrollView ? mFragmentTags[0] : mFragmentTags[1]);
        }
    }

    private void setViewTypeAndOrientation(boolean scrollView, boolean vertical) {
        mViewAsScrollView = !scrollView;
        mViewAsVertical = !vertical;
        setViewType(scrollView);
        setViewOrientationInner(vertical, false);
    }

    private void switchToFragment(int willIndex, int oldIndex) {
        boolean scrollView = willIndex == 0;
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment showFragment = fm.findFragmentByTag(mFragmentTags[willIndex]);
        Fragment hideFragment = fm.findFragmentByTag(mFragmentTags[oldIndex]);
        if (showFragment == null) {
            boolean initVertical = getDefaultViewTypeOrientation(scrollView);
            setViewTypeAndOrientation(scrollView, initVertical);
            Bundle arg = new Bundle();
            arg.putBoolean(FragmentPageBase.KEY_VERTICAL, initVertical);
            Class<? extends FragmentPageBase> fragmentClass = scrollView ? FragmentPageScrollView.class : FragmentPageViewPager.class;
            showFragment = Fragment.instantiate(getActivity(), fragmentClass.getName(), arg);
            ft.add(R.id.fragmentContainer, showFragment, mFragmentTags[willIndex]);
        } else {
            setViewType(scrollView);
            ft.show(showFragment);
        }
        if (hideFragment != null) {
            ft.hide(hideFragment);
        }
        ft.commitAllowingStateLoss();
    }
}
