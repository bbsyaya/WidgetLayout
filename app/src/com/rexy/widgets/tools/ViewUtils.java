package com.rexy.widgets.tools;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ViewUtils {


    public static <T extends View> T view(Activity aty, int id) {
        if (aty != null) {
            return (T) aty.findViewById(id);
        }
        return null;
    }

    public static <T extends View> T view(Fragment frag, int id) {
        if (frag != null && frag.getView() != null) {
            return (T) frag.getView().findViewById(id);
        }
        return null;
    }

    public static <T extends View> T view(View container, int id) {
        if (container != null) {
            return (T) container.findViewById(id);
        }
        return null;
    }

    private static <T extends View> T view(View p, Class<? extends View> cls) {
        View result = null;
        if (cls.isAssignableFrom(p.getClass())) {
            result = p;
        } else if (p instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) p;
            int size = parent.getChildCount();
            for (int i = 0; i < size; i++) {
                if (null != (result = view(parent.getChildAt(i), cls))) {
                    break;
                }
            }
        }
        return result == null ? null : (T) result;
    }


    public static Activity findActivityContext(Context context, int remainDeep) {
        if (context instanceof Activity) return (Activity) context;
        if (remainDeep > 0 && context instanceof ContextWrapper) {
            return findActivityContext(((ContextWrapper) context).getBaseContext(), remainDeep - 1);
        }
        return null;
    }


    public static Context findActivityContext(Context context) {
        Activity aty = findActivityContext(context, 100);
        return aty == null ? context : aty;
    }


    public static boolean isContextAlive(Context context) {
        Activity aty = findActivityContext(context, 100);
        if (aty instanceof FragmentActivity) {
            return !((FragmentActivity) aty).getSupportFragmentManager().isDestroyed();
        } else if (aty != null) {
            if (Build.VERSION.SDK_INT < 17) {
                return !aty.isFinishing();
            } else {
                return !aty.isDestroyed();
            }
        }
        return false;
    }

    public static Drawable loadDrawable(Context context, int drawableResId) {
        Drawable drawable = null;
        if (context != null && drawableResId != 0) {
            drawable = context.getResources().getDrawable(drawableResId);
            if (drawable instanceof BitmapDrawable) {
                Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
                if (bmp == null || bmp.isRecycled()) {
                    drawable = null;
                }
            }
        }
        return drawable;
    }

    public static Drawable recycleDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
            }
            return null;
        }
        return drawable;
    }

    public static boolean isDrawableAvaiable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
            if (bmp == null || bmp.isRecycled()) {
                return false;
            }
        }
        return drawable != null;
    }

    /**
     * Runs a piece of code after the next layout run
     */
    @SuppressLint("NewApi")
    public static void doAfterLayout(final View view, final Runnable runnable) {
        OnGlobalLayoutListener listener = new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Layout pass done, unregister for further events
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                runnable.run();
            }
        };
        view.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    /**
     * WHEN HDIE DIDN'T WORK,TRY TO CALL IT IN A POST RUNNABLE.
     *
     * @param v
     * @param show
     * @return
     */
    public static boolean showKeybord(final View v, final boolean show) {
        if (v == null) return false;
        v.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                if (show) {
                    imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        });
        return true;
    }

    /**
     * @param child
     * @param maxWidth  unknown for 0.
     * @param maxHeight unknown for 0.
     * @param result    accept the width and height ,clound not be null.
     * @return int[] allow to be null.
     * @throws
     */
    public static void measureView(View child, int maxWidth, int maxHeight, int result[]) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (null == p) {
            p = new ViewGroup.LayoutParams(-2, -2);
        }
        int heightSpec;// = ViewGroup.getChildMeasureSpec(0, 0, p.height);
        int widthSpec;
        if (p.width > 0) {// exactly size
            widthSpec = MeasureSpec.makeMeasureSpec(p.width, MeasureSpec.EXACTLY);
        } else if (p.width == -2 || maxWidth <= 0) {// wrapcontent
            widthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        } else if (p.width == -1) {
            widthSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY);
        } else {// fillparent
            widthSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST);
        }
        if (p.height > 0) {
            heightSpec = MeasureSpec.makeMeasureSpec(p.height, MeasureSpec.EXACTLY);
        } else if (p.height == -2 || maxHeight <= 0) {
            heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        } else if (p.height == -1) {
            heightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
        } else {
            heightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }
        child.measure(widthSpec, heightSpec);
        result[0] = child.getMeasuredWidth();
        result[1] = child.getMeasuredHeight();
    }

    public static void setLayerType(View v, int layerType) {
        ViewCompat.setLayerType(v, layerType, null);
    }

    @SuppressLint("NewApi")
    public static void setBackground(View v, Drawable d) {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackgroundDrawable(d);
        } else {
            v.setBackground(d);
        }
    }

    /**
     * set view visible state,this will not set a same state twice or more.
     *
     * @param visible View.GONE View.VISIBLE View.INVISIBLE
     */
    public static void setVisibility(View view, int visible) {
        if (view != null && view.getVisibility() != visible) {
            view.setVisibility(visible);
        }
    }

    public static int setListViewHeightBasedOnChildren(ListView lv) {
        ListAdapter listAdapter = lv.getAdapter();
        int n = listAdapter == null ? 0 : listAdapter.getCount();
        if (n == 0) {
            return -1;
        }
        int totalHeight = 0, result[] = new int[2];
        LinearLayout ll = new LinearLayout(lv.getContext());
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View v = listAdapter.getView(i, null, lv);
            if (v instanceof RelativeLayout) {
                ll.removeAllViews();
                ll.addView(v);
                measureView(ll, 0, 0, result);
            } else {
                measureView(v, 0, 0, result);
            }
            totalHeight += result[1];
        }
        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = totalHeight + (lv.getDividerHeight() * (listAdapter.getCount() - 1));
        lv.setLayoutParams(params);
        return params.height;
    }

    public static Bitmap getBitmap(View v, boolean includeBar) {
        Rect r = new Rect(0, 0, 0, 0);
        if (!includeBar) {
            v.getWindowVisibleDisplayFrame(r);
        }
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight() - r.top, Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(bitmap);
        canvas.translate(0, -r.top);
        v.draw(canvas);
        return bitmap;
    }

    public static void scrollToView(final ScrollView slv, final View dest, final int offset,
                                    int deay) {
        dest.postDelayed(new Runnable() {
            @Override
            public void run() {
                int targetScrollY = 0;
                int contentBtm = slv.getBottom();
                int targetBtm = dest.getBottom();
                targetScrollY = targetBtm - contentBtm;
                slv.smoothScrollTo(0, targetScrollY + offset);
            }
        }, Math.max(deay, 100));
    }

    public static void weightScrollChild(final ScrollView p, final float weight,
                                         final boolean adjustPadding) {
        if (p != null && p.getChildCount() > 0) {
            final Rect r = new Rect();
            p.getHitRect(r);
            if (r.isEmpty() == false) {
                r.inset((p.getPaddingLeft() + p.getPaddingRight()) / 2,
                        (p.getPaddingTop() + p.getPaddingBottom()) / 2);
                int padding = (int) ((1 - weight) * r.width()) / 2;
                if (adjustPadding) {
                    p.setPadding(padding, p.getPaddingTop(), padding, p.getPaddingBottom());
                    p.invalidate();
                } else {
                    View v = p.getChildAt(0);
                    LayoutParams lp = (LayoutParams) v.getLayoutParams();
                    lp.gravity = Gravity.CENTER_HORIZONTAL;
                    lp.rightMargin = lp.leftMargin = padding;
                    v.requestLayout();
                }
            } else {
                p.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            p.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            p.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        p.getHitRect(r);
                        int padding = (int) ((1 - weight) * r.width()) / 2;
                        if (adjustPadding) {
                            p.setPadding(padding, p.getPaddingTop(), padding, p.getPaddingBottom());
                            p.invalidate();
                        } else {
                            View v = p.getChildAt(0);
                            r.inset((p.getPaddingLeft() + p.getPaddingRight()) / 2,
                                    (p.getPaddingTop() + p.getPaddingBottom()) / 2);
                            LayoutParams lp = (LayoutParams) v.getLayoutParams();
                            lp.gravity = Gravity.CENTER_HORIZONTAL;
                            lp.rightMargin = lp.leftMargin = padding;
                            v.requestLayout();
                        }
                    }
                });
            }
        }
    }

    public static int getContentStart(int containerStart, int containerEnd, int contentWillSize, int contentGravity, boolean horizontalDirection) {
        int start = containerStart;
        if (contentGravity != -1) {
            final int mask = horizontalDirection ? Gravity.HORIZONTAL_GRAVITY_MASK : Gravity.VERTICAL_GRAVITY_MASK;
            final int maskCenter = horizontalDirection ? Gravity.CENTER_HORIZONTAL : Gravity.CENTER_VERTICAL;
            final int maskEnd = horizontalDirection ? Gravity.RIGHT : Gravity.BOTTOM;
            final int okGravity = contentGravity & mask;
            if (maskCenter == okGravity) {
                start = containerStart + (containerEnd - containerStart - contentWillSize) / 2;
            } else if (maskEnd == okGravity) {
                start = containerEnd - contentWillSize;
            }
        }
        return start;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void fixedWebViewInnerBug(View view) {
        if (view != null) {
            int sdkInt = Build.VERSION.SDK_INT;
            if (sdkInt > 10 && sdkInt < 17) {
                if (view instanceof WebView) {
                    ((WebView) view).removeJavascriptInterface("searchBoxJavaBridge_");
                } else if (view instanceof ViewGroup) {
                    ViewGroup p = (ViewGroup) view;
                    for (int i = 0; i < p.getChildCount(); i++) {
                        view = p.getChildAt(i);
                        if (view != null) {
                            //为了减少递归深度预先判断。
                            if (view instanceof WebView) {
                                fixedWebViewInnerBug(view);
                            } else if (view instanceof ViewGroup) {
                                fixedWebViewInnerBug(view);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 多行设left
     * 否则设center
     *
     * @param view
     */
    public static void setGravityForMultiLine(final TextView view) {
        final ViewTreeObserver observer = view.getViewTreeObserver();
        // 设置提示的显示规则，如果大于一行居左，如果小于2行，居中
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                try {
                    if (view.getLineCount() > 1) {
                        view.setGravity(Gravity.LEFT);
                    } else {
                        view.setGravity(Gravity.CENTER);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (observer != null && observer.isAlive()) {
                        observer.removeOnPreDrawListener(this);
                    }
                }
                return true;
            }
        });
    }

    public static int getPointerIndex(int action) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            return (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        else
            return (action & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
    }


}
