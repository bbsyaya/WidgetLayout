package com.rexy.widgetlayout.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.rexy.common.BaseFragment;
import com.rexy.widgets.group.BaseViewGroup;
import com.rexy.widgets.group.WrapLayout;
import com.rexy.widgets.tools.ViewUtils;
import com.rexy.widgets.view.FadeTextButton;
import com.rexy.widgetlayout.R;

import java.util.Random;

/**
 * TODO:功能说明
 *
 * @author: rexy
 * @date: 2017-06-05 15:03
 */
public class FragmentViewOption extends BaseFragment {
    float mDensity;
    View mExampleView;
    WrapLayout mExampleParent;

    EditText mEditMinWidth, mEditMaxWidth;
    EditText mEditMinHeight, mEditMaxHeight;

    ToggleButton[] mToggleHorizontal = new ToggleButton[3];//left,right,centerH
    ToggleButton[] mToggleVertical = new ToggleButton[3];//top,bottom,centerV
    int[] mAligns = new int[]{Gravity.LEFT, Gravity.RIGHT, Gravity.CENTER_HORIZONTAL, Gravity.TOP, Gravity.BOTTOM, Gravity.CENTER_VERTICAL};

    int mMinWidth, mMaxWidth, mMinHeight, mMaxHeight,mColor=0xFFFF0000, mGravity = Gravity.CENTER;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_viewoption, container, false);
        initView(root);
        return root;
    }

    private void initView(View root) {
        mDensity = root.getResources().getDisplayMetrics().density;
        mExampleParent = ViewUtils.view(root, R.id.exampleParent);
        mExampleView = ViewUtils.view(root, R.id.exampleView);
        mEditMinWidth = ViewUtils.view(root, R.id.editMinWidth);
        mEditMaxWidth = ViewUtils.view(root, R.id.editMaxWidth);
        mEditMinHeight = ViewUtils.view(root, R.id.editMinHeight);
        mEditMaxHeight = ViewUtils.view(root, R.id.editMaxHeight);
        mToggleHorizontal[0] = ViewUtils.view(root, R.id.toggleLeft);
        mToggleHorizontal[1] = ViewUtils.view(root, R.id.toggleRight);
        mToggleHorizontal[2] = ViewUtils.view(root, R.id.toggleCenterHorizontal);
        mToggleVertical[0] = ViewUtils.view(root, R.id.toggleTop);
        mToggleVertical[1] = ViewUtils.view(root, R.id.toggleBottom);
        mToggleVertical[2] = ViewUtils.view(root, R.id.toggleCenterVertical);
        final CompoundButton.OnCheckedChangeListener checker = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkAllAlignGravity((ToggleButton) buttonView, isChecked);
            }
        };
        for (int i = 0; i < mToggleHorizontal.length; i++) {
            mToggleHorizontal[i].setOnCheckedChangeListener(checker);
            mToggleVertical[i].setOnCheckedChangeListener(checker);
        }
        checkAllAlignGravity(null, false);

        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkAllWidthHeightLimit();
            }
        };
        mEditMinWidth.addTextChangedListener(textWatcher);
        mEditMaxWidth.addTextChangedListener(textWatcher);
        mEditMinHeight.addTextChangedListener(textWatcher);
        mEditMaxHeight.addTextChangedListener(textWatcher);
        checkAllWidthHeightLimit();

        final View.OnClickListener clicker=new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int viewId=v.getId();
                if(R.id.buttonAddView==viewId){
                    Intent t=new Intent();
                    t.putExtra("minWidth",mMinWidth);
                    t.putExtra("maxWidth",mMaxWidth);
                    t.putExtra("minHeight",mMinHeight);
                    t.putExtra("maxHeight",mMaxHeight);
                    t.putExtra("color",mColor);
                    t.putExtra("gravity",mGravity);
                    getActivity().setResult(Activity.RESULT_OK,t);
                    getActivity().finish();
                }else {
                    if(v instanceof FadeTextButton){
                        FadeTextButton fb= (FadeTextButton) v;
                        if(TextUtils.isEmpty(fb.getText())&&fb.getBackground() instanceof ColorDrawable){
                            ColorDrawable cd= (ColorDrawable) fb.getBackground();
                            mColor=cd.getColor();
                            mExampleView.setBackgroundColor(mColor);
                        }
                    }
                }
            }
        };
        ViewUtils.view(root,R.id.buttonAddView).setOnClickListener(clicker);
        ViewUtils.view(root,R.id.viewColor1).setOnClickListener(clicker);
        ViewUtils.view(root,R.id.viewColor2).setOnClickListener(clicker);
        ViewUtils.view(root,R.id.viewColor3).setOnClickListener(clicker);
        ViewUtils.view(root,R.id.viewColor4).setOnClickListener(clicker);

        int[] viewsId = new int[]{R.id.viewColor1, R.id.viewColor2, R.id.viewColor3, R.id.viewColor4};
        for (int i = 0; i < viewsId.length; i++) {
            ViewUtils.view(root, viewsId[i]).setOnClickListener(clicker);
        }
        ViewUtils.view(root, viewsId[new Random(System.currentTimeMillis()).nextInt(viewsId.length)]).performClick();
    }

    private int checkGravity(int optIndex, int shift) {
        ToggleButton[] optList = shift > 0 ? mToggleVertical : mToggleHorizontal;
        if (optIndex == -1) {
            for (int i = 0; i < optList.length; i++) {
                if (optList[i].isChecked()) {
                    return mAligns[i + shift];
                }
            }
        } else {
            ToggleButton optButton = optList[optIndex];
            if (optButton.isChecked()) {
                for (int i = 0; i < optList.length; i++) {
                    if (i != optIndex) {
                        optList[i].setChecked(false);
                    }
                }
                return mAligns[optIndex + shift];
            }
        }
        optList[0].setChecked(true);
        return mAligns[shift];
    }

    private int checkLimitSize(EditText edit) {
        String str = edit.getText().toString();
        int value, defValue = 0;
        try {
            value = Integer.parseInt(str);
            if (value < 0) {
                value = defValue;
            } else {
                value *= mDensity;
            }
        } catch (Exception e) {
            e.printStackTrace();
            value = defValue;
        }
        return value;
    }

    private void checkAllWidthHeightLimit() {
        mMinWidth = checkLimitSize(mEditMinWidth);
        mMaxWidth = checkLimitSize(mEditMaxWidth);
        mMinHeight = checkLimitSize(mEditMinHeight);
        mMaxHeight = checkLimitSize(mEditMaxHeight);
        BaseViewGroup.LayoutParams blp = null;
        if (mExampleView.getLayoutParams() instanceof BaseViewGroup.LayoutParams) {
            blp = (BaseViewGroup.LayoutParams) mExampleView.getLayoutParams();
        }
        if (blp != null) {
            blp.maxWidth = mMaxWidth;
            blp.maxHeight = mMaxHeight;
        }
        mExampleView.setMinimumWidth(mMinWidth);
        mExampleView.setMinimumHeight(mMinHeight);
        mExampleView.requestLayout();
    }

    boolean mLockAlign = false;

    private int findToggleIndex(ToggleButton button, ToggleButton[] list) {
        if (button == null || list == null) return -1;
        for (int i = 0; i < list.length; i++) {
            if (list[i] == button)
                return i;
        }
        return -1;
    }

    private void checkAllAlignGravity(ToggleButton button, boolean checked) {
        if (mLockAlign) return;
        mLockAlign = true;
        int optIndexH = findToggleIndex(button, mToggleHorizontal);
        int optIndexV = findToggleIndex(button, mToggleVertical);
        int gravity = checkGravity(optIndexH, 0) | checkGravity(optIndexV, mToggleHorizontal.length);
        boolean changed = false;
        if (mExampleView.getLayoutParams() instanceof WrapLayout.LayoutParams) {
            WrapLayout.LayoutParams lp = (WrapLayout.LayoutParams) mExampleView.getLayoutParams();
            if (lp.gravity != gravity) {
                lp.gravity = gravity;
                changed = true;
            }
        }
        if (mExampleParent.getGravity() != gravity) {
            mExampleParent.setGravity(gravity);
            changed = true;
        }
        if (changed) {
            mGravity = gravity;
            mExampleParent.requestLayout();
        }
        mLockAlign = false;
    }
}
