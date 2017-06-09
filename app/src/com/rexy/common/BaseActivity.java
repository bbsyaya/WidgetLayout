package com.rexy.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.Stack;

/**
 * TODO:功能说明
 *
 * @author: rexy
 * @date: 2017-06-05 14:45
 */
public class BaseActivity extends FragmentActivity {
    public static final String KEY_ATY_STYLE = "KEY_ATY_STYLE";
    private boolean mExitNoAnim = false;
    private static int mVisibleSignal = 0;
    protected Intent mNewIntent;

    private static Stack<Activity> mActivities = new Stack<Activity>();

    public static Stack<Activity> getActivities() {
        return mActivities;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT > 10) {
            getWindow().setFlags(0x01000000, 0x01000000);
        }
        Intent i = getIntent();
        int atytheme = (i == null ? 0 : i.getIntExtra(KEY_ATY_STYLE, 0));
        if (atytheme != 0) {
            setTheme(atytheme);
        }
        super.onCreate(savedInstanceState);
        mActivities.push(this);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mNewIntent != null) {
            Bundle extras = mNewIntent.getExtras();
            if (!handleNewIntent(mNewIntent, extras)) {
            }
            mNewIntent = null;
        }
    }

    @Override
    protected final void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mNewIntent = intent;
    }

    /**
     * @param newIntent 从onNewIntent而来的Intent.
     * @param extras Intent里面的getExtras  注意这里的extras是一个深拷贝。
     * @return 返回true后将不会给父类处理。
     */
    protected boolean handleNewIntent(Intent newIntent, Bundle extras) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mVisibleSignal++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mVisibleSignal--;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivities.remove(this);
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
            if (mExitNoAnim) {
                overridePendingTransition(0, 0);
            }
        } catch (Exception e) {
            //防止手动重复调用onBackPressed可能会异常crash.
            e.printStackTrace();
            finish();
        }
    }

    public static boolean hasActivityVisible() {
        return mVisibleSignal == 1;
    }

    public static final void exitApp(boolean kill) {
        while (!mActivities.isEmpty()) {
            mActivities.pop().finish();
        }
        if (kill) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
