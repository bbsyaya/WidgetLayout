package com.rexy.widgetlayout.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import com.rexy.widgetlayout.R;
import com.rexy.common.BaseFragment;
import com.rexy.widgetlayout.model.TestPageTransformer;
import com.rexy.widgets.group.PageScrollView;

/**
 * TODO:功能说明
 *
 * @author: rexy
 * @date: 2017-06-05 15:02
 */
public class FragmentPageBase extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
    public static final String KEY_VERTICAL = "KEY_VERTICAL";

    protected float mDensity;
    protected PageScrollView mPageScrollView;
    protected boolean mContentVertical;
    protected TestPageTransformer mPageTransformer = new TestPageTransformer();

    ToggleButton mToggleAnim;
    ToggleButton mToggleCenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arg = getArguments();
        if (arg != null) {
            mContentVertical = arg.getBoolean(KEY_VERTICAL);
        }
        mContentVertical = !mContentVertical;
        setContentOrientationInner(!mContentVertical, false);
    }


    private FragmentPageScrollContainer getParentContainerFragment() {
        if (getParentFragment() instanceof FragmentPageScrollContainer) {
            return (FragmentPageScrollContainer) getParentFragment();
        }
        if (getTargetFragment() instanceof FragmentPageScrollContainer) {
            return (FragmentPageScrollContainer) getTargetFragment();
        }
        return null;
    }

    protected void onFragmentVisibleChanged(boolean visible, boolean fromLifecycle) {
        if (visible) {
            FragmentPageScrollContainer parent = getParentContainerFragment();
            if (null != parent) {
                parent.setViewOrientation(mContentVertical);
            }
        }
    }

    public void setContentOrientation(boolean vertical) {
        setContentOrientationInner(vertical, false);
    }

    protected void initView(View root) {
        mPageScrollView = (PageScrollView) root.findViewById(R.id.pageScrollView);
        mToggleAnim = (ToggleButton) root.findViewById(R.id.toggleTransform);
        mToggleCenter = (ToggleButton) root.findViewById(R.id.toggleChildCenter);
        mToggleAnim.setOnCheckedChangeListener(this);
        mToggleCenter.setOnCheckedChangeListener(this);
    }

    protected boolean setContentOrientationInner(boolean vertical, boolean init) {
        if (mContentVertical != vertical) {
            mContentVertical = vertical;
            mPageScrollView.setOrientation(vertical ? PageScrollView.VERTICAL : PageScrollView.HORIZONTAL);
            if (init) {
                adjustTransformAnimation(mToggleAnim.isChecked());
                adjustChildLayoutCenter(mToggleCenter.isChecked());
            }
            return true;
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDensity = context.getResources().getDisplayMetrics().density;
    }

    public void adjustTransformAnimation(boolean haveAnim) {
        mPageScrollView.setPageTransformer(haveAnim ? mPageTransformer : null);
    }

    public void adjustChildLayoutCenter(boolean layoutCenter) {
        mPageScrollView.setChildCenter(layoutCenter);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mToggleAnim == buttonView) {
            adjustTransformAnimation(isChecked);
        }
        if (mToggleCenter == buttonView) {
            adjustChildLayoutCenter(isChecked);
        }
    }
}
