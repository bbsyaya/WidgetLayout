package com.rexy.widgetlayout.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rexy.common.BaseFragment;
import com.rexy.widgetlayout.R;
import com.rexy.widgets.group.BaseViewGroup;
import com.rexy.widgets.tools.ViewUtils;
import com.rexy.widgets.view.FadeTextButton;

import java.util.Random;

/**
 * TODO:功能说明
 *
 * @author: rexy
 * @date: 2017-06-05 15:03
 */
public class FragmentViewPicker extends BaseFragment {
    int REQUEST_ADD_VIEW = 1;
    protected float mDensity;

    private int mColors[] = new int[]{0xFFFF0000, 0xDD5A6390, 0xDD7CF499, 0xDDff00ff, 0xFF0000FF, 0xEE8FC320, 0xFF295622, 0xFFF5A623};
    Random mRandom = new Random(System.currentTimeMillis());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDensity=getResources().getDisplayMetrics().density;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final View.OnClickListener clicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int viewId = v.getId();
                if (viewId == R.id.viewAdd) {
                    requestAddView();
                } else if (viewId == R.id.viewAddFast) {
                    onFastAddView();
                } else if (viewId == R.id.viewRemove) {
                    onAddOrRemoveView(null);
                }
            }
        };
        View viewAdd = ViewUtils.view(this, R.id.viewAdd);
        View viewAddFast = ViewUtils.view(this, R.id.viewAddFast);
        View viewRemove = ViewUtils.view(this, R.id.viewRemove);
        if (viewAdd != null) {
            viewAdd.setOnClickListener(clicker);
        }
        if (viewAddFast != null) {
            viewAddFast.setOnClickListener(clicker);
        }
        if (viewRemove != null) {
            viewRemove.setOnClickListener(clicker);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 0, 0, "REMOVE").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 1, 1, "ADD").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            requestAddView();
        } else {
            onAddOrRemoveView(null);
        }
        return true;
    }

    private void requestAddView() {
        Intent t = new Intent(getActivity(), ActivityCommon.class);
        t.putExtra(ActivityCommon.KEY_FRAGMENT_NAME, FragmentViewOption.class.getName());
        startActivityForResult(t, REQUEST_ADD_VIEW);
    }

    protected void onFastAddView(){
        buildRandomView(1,true);
    }

    public void buildRandomView(int count, boolean alignCenter) {
        long seek = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            mRandom.setSeed(seek + i * mRandom.nextInt(Integer.MAX_VALUE));
            onAddOrRemoveView(buildView(-1, -1, -1, -1, alignCenter ? Gravity.CENTER : 0, mColors[mRandom.nextInt(mColors.length)], true));
        }
    }

    protected View buildView(CharSequence text, boolean alignCenter) {
        mRandom.setSeed(System.currentTimeMillis() - String.valueOf(text).hashCode() * mRandom.nextInt(Integer.MAX_VALUE));
        View view = buildView(-1, -1, -1, -1, alignCenter ? Gravity.CENTER : 0, mColors[mRandom.nextInt(mColors.length)], true);
        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        }
        return view;
    }

    protected View buildView(int minWidth, int maxWidth, int minHeight, int maxHeight, int gravity, int color, boolean random) {
        BaseViewGroup.LayoutParams lp = new BaseViewGroup.LayoutParams(-2, -2, gravity);
        lp.maxWidth = maxWidth;
        lp.maxHeight = maxHeight;
        FadeTextButton view = new FadeTextButton(getActivity());
        view.setText("R");
        view.setGravity(Gravity.CENTER);
        view.setMinWidth(minWidth);
        view.setMinHeight(minHeight);
        view.setTextSize(18);
        view.setTextColor(0xFFFFFFFF);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        int paddingH=(int)(18*mDensity);
        int paddingV=(int)(10*mDensity);
        view.setPadding(paddingH,paddingV,paddingH,paddingV);
        if (color != 0) {
            view.setBackgroundColor(color);
        }
        view.setLayoutParams(lp);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_VIEW && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                int minWidth = data.getIntExtra("minWidth", -1);
                int maxWidth = data.getIntExtra("maxWidth", -1);
                int minHeight = data.getIntExtra("minHeight", -1);
                int maxHeight = data.getIntExtra("maxHeight", -1);
                int gravity = data.getIntExtra("gravity", 0);
                int color = data.getIntExtra("color", 0xFFFF0000);
                onAddOrRemoveView(buildView(minWidth, maxWidth, minHeight, maxHeight, gravity, color, false));
            }
        }
    }

    protected void onAddOrRemoveView(View addView) {
    }
}
