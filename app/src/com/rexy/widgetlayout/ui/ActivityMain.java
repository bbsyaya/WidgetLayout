package com.rexy.widgetlayout.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.rexy.common.BaseActivity;
import com.rexy.widgets.drawable.CardDrawable;
import com.rexy.widgets.tools.ViewUtils;
import com.rexy.widgetlayout.R;

/**
 * Created by rexy on 17/4/11.
 */
public class ActivityMain extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_entry);
        ViewUtils.view(this, R.id.buttonColumn).setOnClickListener(this);
        ViewUtils.view(this, R.id.buttonPageScroll).setOnClickListener(this);
        ViewUtils.view(this, R.id.buttonWrapLabel).setOnClickListener(this);
        ViewUtils.view(this, R.id.buttonNestFloat).setOnClickListener(this);
        Drawable d=CardDrawable.newBuilder(this)
                .top(5).color(0xFF666666,0xFF0000FF).radiusHalf()
                .bottom(5).color(0xFF666666,0xFF000000).radiusHalf()
                .left(5).color(0xFF666666,0xFF00FFFF).radiusHalf()
                .right(5).color(0xFF666666,0xFFFF00FF).radiusHalf()
                .build();
        ViewUtils.setBackground(ViewUtils.view(this,R.id.imageView),d);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.buttonColumn == id) {
            ActivityCommon.launch(this, FragmentColumnLayout.class);
        }
        if (R.id.buttonPageScroll == id) {
            ActivityCommon.launch(this,FragmentPageScrollContainer.class);
        }
        if (R.id.buttonWrapLabel == id) {
            ActivityCommon.launch(this, FragmentWrapLabelLayout.class);
        }
        if (R.id.buttonNestFloat == id) {
            ActivityCommon.launch(this,FragmentNestFloatLayout.class);
        }
    }
}
