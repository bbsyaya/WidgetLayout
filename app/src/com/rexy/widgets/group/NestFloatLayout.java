package com.rexy.widgets.group;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.OverScroller;

import com.rexy.widgetlayout.R;

import java.lang.ref.WeakReference;

/**
 * 支持NestScrollView 方式的嵌套滑动。要求嵌套滑动的child 必须实现NestedScrollingChild接口。
 * 如可使用 RecyclerView 作为列表视图，NestScrollView 作为滑动子视图。
 * 作用上
 * 1. xml 中可用nestViewIndex指定实现了NestedScrollingChild接口的子 View 所在的直接 child 的索引。
 * floatViewIndex指定需要悬停的 View 所在的直接 Child 的索引。
 * 2. java 中建议使用setNestViewId setFloatViewId 来指定，也可通过setNestViewIndex,setFloatViewIndex来分别指定能嵌套滑动的view和悬停 View.
 *
 * <declare-styleable name="NestFloatLayout">
 * <!--实现了嵌套滑动NestScrollingChild 接口的滑动的 View 所在的直接子 View 索引-->
 * <attr name="nestViewIndex" format="integer"/>
 * <!--需要吸顶到顶部的 View 所在的直接子 View 索引-->
 * <attr name="floatViewIndex" format="integer"/>
 * </declare-styleable>
 * @date: 2017-05-27 17:36
 */
public class NestFloatLayout extends BaseViewGroup implements NestedScrollingParent {
    WeakReference<View> mNestChild;
    WeakReference<View> mFloatView;

    int mFloatViewId, mFloatViewIndex = -1;
    int mNestChildId, mNestChildIndex = -1;

    int mTouchSlop;
    int mMaximumVelocity;
    int mOverFlingDistance;

    boolean mIsBeingDragged = false;
    boolean mCancleDragged = false;

    PointF mPointDown = new PointF();
    PointF mPointLast = new PointF();

    OverScroller mScroller = null;
    VelocityTracker mVelocityTracker = null;


    public NestFloatLayout(Context context) {
        super(context);
        init(context, null);
    }

    public NestFloatLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NestFloatLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public NestFloatLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mOverFlingDistance = configuration.getScaledOverflingDistance() * 2;
        TypedArray attr = attrs == null ? null : context.obtainStyledAttributes(attrs, R.styleable.NestFloatLayout);
        if (attr != null) {
            mFloatViewIndex = attr.getInt(R.styleable.NestFloatLayout_floatViewIndex, mFloatViewIndex);
            mNestChildIndex = attr.getInt(R.styleable.NestFloatLayout_nestViewIndex, mNestChildIndex);
            attr.recycle();
        }
    }

    public void setNestViewId(int nestChildId) {
        if (mNestChildId != nestChildId) {
            mNestChildId = nestChildId;
        }
    }

    public void setFloatViewId(int floatViewId) {
        if (mFloatViewId != floatViewId) {
            mFloatViewId = floatViewId;
        }
    }

    public void setNestViewIndex(int index) {
        if (mNestChildIndex != index) {
            mNestChildIndex = index;
        }
    }

    public void setFloatViewIndex(int index) {
        if (mFloatViewIndex != index) {
            mFloatViewIndex = index;
        }
    }

    private View findDirectChildView(View view) {
        ViewParent parent = view.getParent();
        while (parent != null && parent != NestFloatLayout.this) {
            if (parent instanceof View) {
                view = (View) parent;
                parent = view.getParent();
            } else {
                break;
            }
        }
        if (parent == NestFloatLayout.this) {
            return view;
        }
        return null;
    }

    private View findViewByIndexAndId(int index, int id) {
        View view = index >= 0 ? getChildAt(index) : null;
        if (view == null && id != 0) {
            view = findViewById(id);
        }
        return view;
    }

    private void ensureNestFloatView() {
        if (mFloatViewId != 0 || mFloatViewIndex != -1) {
            View floatView = findViewByIndexAndId(mFloatViewIndex, mFloatViewId);
            if (floatView != null) {
                mFloatView = null;
                mFloatViewId = 0;
                mFloatViewIndex = -1;
                floatView = findDirectChildView(floatView);
                if (floatView != null) {
                    mFloatView = new WeakReference(floatView);
                }
            }
        }
        if (mNestChildId != 0 || mNestChildIndex != -1) {
            View nestChild = findViewByIndexAndId(mNestChildIndex, mNestChildId);
            if (nestChild != null) {
                mNestChildId = 0;
                mNestChild = null;
                nestChild = findDirectChildView(nestChild);
                if (nestChild != null) {
                    mNestChild = new WeakReference(nestChild);
                }
            }
        }
    }

    public View getFloatView() {
        if (mFloatViewId != 0 || mFloatViewIndex != -1) {
            ensureNestFloatView();
        }
        return mFloatView == null ? null : mFloatView.get();
    }

    public View getNestView() {
        if (mNestChildId != 0 || mNestChildIndex != -1) {
            ensureNestFloatView();
        }
        return mNestChild == null ? null : mNestChild.get();
    }

    @Override
    protected void dispatchMeasure(int widthMeasureSpecNoPadding, int heightMeasureSpecNoPadding, int maxSelfWidthNoPadding, int maxSelfHeightNoPadding) {
        final int childCount = getChildCount();
        int contentWidth = 0, contentHeight = 0;
        int childState = 0, vitureHeight=0;
        View nestView = getNestView();
        View floatView = getFloatView();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (skipChild(child)) continue;
            NestFloatLayout.LayoutParams params = (NestFloatLayout.LayoutParams) child.getLayoutParams();
            int childMarginHorizontal = params.leftMargin + params.rightMargin;
            int childMarginVertical = params.topMargin + params.bottomMargin;
            int childWidthSpec = getChildMeasureSpec(widthMeasureSpecNoPadding, childMarginHorizontal, params.width);
            int childHeightSpec = getChildMeasureSpec(heightMeasureSpecNoPadding, childMarginVertical, params.height);
            if (nestView == child && vitureHeight > 0) {
                int mode = params.height == -1 ? View.MeasureSpec.EXACTLY : View.MeasureSpec.AT_MOST;
                childHeightSpec = View.MeasureSpec.makeMeasureSpec(maxSelfHeightNoPadding - vitureHeight-params.getMarginVertical(), mode);
            }
            params.measure(child,childWidthSpec, childHeightSpec);
            int itemWidth = child.getMeasuredWidth() + childMarginHorizontal;
            int itemHeight = (child.getMeasuredHeight() + childMarginVertical);
            if (floatView == child) {
                vitureHeight = itemHeight;
            }else {
                if(vitureHeight>0){
                    vitureHeight+=itemHeight;
                }
            }
            contentHeight += itemHeight;
            if (contentWidth < itemWidth) {
                contentWidth = itemWidth;
            }
            childState |= child.getMeasuredState();
        }
        setContentSize(contentWidth, contentHeight);
        setMeasureState(childState);
    }

    @Override
    protected void dispatchLayout(int contentLeft, int contentTop, int paddingLeft, int paddingTop, int selfWidthNoPadding, int selfHeightNoPadding) {
        contentTop = Math.max(contentTop, paddingTop);
        int childLeft, childTop, childRight, childBottom;
        final int baseRight = contentLeft + getContentWidth();
        childTop = contentTop;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (skipChild(child)) continue;
            NestFloatLayout.LayoutParams params = (NestFloatLayout.LayoutParams) child.getLayoutParams();
            childTop += params.topMargin;
            childBottom = childTop + child.getMeasuredHeight();
            childLeft = getContentStartH(contentLeft, baseRight, child.getMeasuredWidth(),params.leftMargin , params.rightMargin, params.gravity);
            childRight = childLeft + child.getMeasuredWidth();
            child.layout(childLeft, childTop, childRight, childBottom);
            childTop = childBottom + params.bottomMargin;
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        View floatView = getFloatView();
        boolean acceptedNestedScroll = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && floatView != null;
        if (isLogAccess()) {
            print(String.format("onStartNestedScroll(child=%s,target=%s,nestedScrollAxes=%d,accepted=%s)", String.valueOf(child.getClass().getSimpleName()), String.valueOf(target.getClass().getSimpleName()), nestedScrollAxes, acceptedNestedScroll));
        }
        return acceptedNestedScroll;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        if (isLogAccess()) {
            print(String.format("onNestedScrollAccepted(child=%s,target=%s,nestedScrollAxes=%d)", String.valueOf(child.getClass().getSimpleName()), String.valueOf(target.getClass().getSimpleName()), nestedScrollAxes));
        }
    }

    @Override
    public void onStopNestedScroll(View target) {
        if (isLogAccess()) {
            print(String.format("onStopNestedScroll(target=%s)", String.valueOf(target.getClass().getSimpleName())));
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (isLogAccess()) {
            print(String.format("onNestedScroll(target=%s,dxConsumed=%d,dyConsumed=%d,dxUnconsumed=%d,dyUnconsumed=%d)", String.valueOf(target.getClass().getSimpleName()), dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed));
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        View floatView = getFloatView();
        if (floatView != null) {
            int maxSelfScrolled = getVerticalScrollRange();
            int curSelfScrolled = getScrollY();
            int consumedY = 0;
            if (dy > 0 && curSelfScrolled < maxSelfScrolled) {
                consumedY = Math.min(dy, maxSelfScrolled - curSelfScrolled);
            }
            if (dy < 0 && curSelfScrolled > 0 && !ViewCompat.canScrollVertically(target, dy)) {
                consumedY = Math.max(dy, -curSelfScrolled);
            }
            if (consumedY != 0) {
                scrollBy(0, consumedY);
            }
            consumed[1] = consumedY;
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (isLogAccess()) {
            print(String.format("onNestedFling(target=%s,vx=%.1f,vy=%.1f,consumed=%s)", String.valueOf(target.getClass().getSimpleName()), velocityX, velocityY, consumed));
        }
        boolean watched = false;
        //以下是对快速滑动NestView 的补偿。
        if (velocityY > 1 && getScrollY() >= 0) {
            flingToWhere(0, -1, 0, (int) velocityY);
            watched = true;
        }
        if (velocityY < -1 && getScrollY() <= getVerticalScrollRange()) {
            flingToWhere(0, 1, 0, (int) velocityY);
            watched = true;
        }
        return watched;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (isLogAccess()) {
            print(String.format("onNestedPreFling(target=%s,vx=%.1f,vy=%.1f)", String.valueOf(target.getClass().getSimpleName()), velocityX, velocityY));
        }
        //如果列表可快速滑动返回 false,否则返回true.  down - //up+
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        if (isLogAccess()) {
            print("getNestedScrollAxes");
        }
        return 0;
    }

    public OverScroller getScroller() {
        if (mScroller == null) {
            mScroller = new OverScroller(getContext());
        }
        return mScroller;
    }

    private boolean ifNeedInterceptTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View nestView = getNestView();
            mCancleDragged = nestView == null;
            if (!mCancleDragged) {
                float y = event.getY() + getScrollY();
                mCancleDragged = y >= getVerticalScrollRange() && y <= nestView.getBottom();
            }
        }
        return mCancleDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (ifNeedInterceptTouch(event) || getFloatView() == null || !isEnabled() || mCancleDragged) {
            return super.onTouchEvent(event);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        final int action = event.getAction() & MotionEventCompat.ACTION_MASK;
        if (action == MotionEvent.ACTION_MOVE) {
            handleTouchActionMove(event);
        } else {
            if (action == MotionEvent.ACTION_DOWN) {
                handleTouchActionDown(event);
            }
            if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                handleTouchActionUp(event);
            }
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ifNeedInterceptTouch(ev) || getFloatView() == null || !isEnabled() || mCancleDragged) {
            mIsBeingDragged = false;
        } else {
            final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
            if (action == MotionEvent.ACTION_MOVE) {
                handleTouchActionMove(ev);
            } else {
                if (action == MotionEvent.ACTION_DOWN) {
                    handleTouchActionDown(ev);
                }
                if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                    handleTouchActionUp(ev);
                }
            }
        }
        return mIsBeingDragged;
    }

    private void handleTouchActionMove(MotionEvent ev) {
        float x = ev.getX(), y = ev.getY();
        if (mIsBeingDragged) {
            scrollDxDy((int) (mPointLast.x - x), (int) (mPointLast.y - y));
            mPointLast.set(x, y);
        } else {
            int dx = (int) (mPointDown.x - x), dy = (int) (mPointDown.y - y);
            int dxAbs = Math.abs(dx), dyAbs = Math.abs(dy);
            boolean dragged;
            if (dragged = (dyAbs > mTouchSlop && (dyAbs * 0.6f) > dxAbs)) {
                dy = (dy > 0 ? mTouchSlop : -mTouchSlop) >> 2;
                dx = 0;
            }
            if (!dragged) {
                if (Math.max(dxAbs, dyAbs) > (mTouchSlop << 2)) {
                    dx = (int) (mPointLast.x - x);
                    dy = (int) (mPointLast.y - y);
                    dxAbs = Math.abs(dx);
                    dyAbs = Math.abs(dy);
                    if ((dyAbs * 0.6f) > dxAbs) {
                        mPointDown.set(mPointLast);
                    }
                }
            }
            if (dragged) {
                mIsBeingDragged = true;
                markAsWillDragged(true);
                scrollDxDy(dx, dy);
            }
            mPointLast.set(x, y);
        }
    }

    private void handleTouchActionUp(MotionEvent ev) {
        if (mIsBeingDragged) {
            mIsBeingDragged = false;
            mPointLast.set(ev.getX(), ev.getY());
            int velocityX = 0, velocityY = 0;
            final VelocityTracker velocityTracker = mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                velocityX = (int) velocityTracker.getXVelocity();
                velocityY = (int) velocityTracker.getYVelocity();
            }
            if (!flingToWhere((int) (mPointLast.x - mPointDown.x), (int) (mPointLast.y - mPointDown.y), -velocityX, -velocityY)) {
                markAsWillIdle();
            }
        }
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void handleTouchActionDown(MotionEvent ev) {
        mPointDown.set(ev.getX(), ev.getY());
        mPointLast.set(mPointDown);
        if (SCROLL_STATE_DRAGGING == mScrollState) {
            OverScroller scroller = getScroller();
            scroller.computeScrollOffset();
            if (!scroller.isFinished()) {
                scroller.abortAnimation();
                mIsBeingDragged = true;
                markAsWillDragged(true);
            }
        }
    }

    @Override
    public void computeScroll() {
        boolean scrollWorking = SCROLL_STATE_SETTLING == mScrollState;
        OverScroller scroller = scrollWorking ? getScroller() : null;
        if (scroller != null && scroller.computeScrollOffset()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = scroller.getCurrX();
            int y = scroller.getCurrY();
            if (oldX != x || oldY != y) {
                scrollTo(x, y);
            }
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            if (scrollWorking) {
                markAsWillIdle();
            }
        }
    }

    private void scrollDxDy(int scrollDx, int scrollDy) {
        View floatView = getFloatView();
        if (floatView != null) {
            int scrollWant = getScrollY() + scrollDy;
            int scrollRange = getVerticalScrollRange();
            if (scrollWant < 0) scrollWant = 0;
            if (scrollWant > scrollRange) scrollWant = scrollRange;
            scrollTo(getScrollX(), scrollWant);
        }
    }

    private boolean isFlingAllowed(int scrolled, int scrollRange, int velocity) {
        return !(velocity == 0 || (velocity < 0 && scrolled <= 0) || (velocity > 0 && scrolled >= scrollRange));
    }

    private boolean flingToWhere(int movedX, int movedY, int velocityX, int velocityY) {
        boolean willScroll = false;
        View floatView = getFloatView();
        if (floatView != null) {
            int scrolled, scrollRange, velocity;
            scrolled = getScrollY();
            scrollRange = getVerticalScrollRange();
            velocity = velocityY;
            if (willScroll = isFlingAllowed(scrolled, scrollRange, velocity)) {
                if (isLogAccess()) {
                    print(String.format("flingWhere: movedY=%d,velocityY=%d,[scrollY=%d,scrollRange=%d]", movedY, velocityY, scrolled, scrollRange));
                }
                getScroller().fling(getScrollX(), scrolled, 0, velocity, 0, 0, 0, scrollRange, 0, mOverFlingDistance);
                markAsWillScroll();
            }
        }
        return willScroll;
    }
}
