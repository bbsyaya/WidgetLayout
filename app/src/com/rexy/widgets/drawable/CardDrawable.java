package com.rexy.widgets.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class CardDrawable extends Drawable {
    private static final int STATE_INDEX_NORMAL = 0;
    private static final int STATE_INDEX_PRESSED = 1;
    private static final int STATE_INDEX_CHECKED = 2;
    private static final int STATE_INDEX_DISABLED = 3;

    int mStateIndex = 0;
    int[] mColorArray;

    Paint mPaint = null;
    Rect mTempRect = new Rect();
    RectF mTempRectF = new RectF();

    CardState mLineLeft;
    CardState mLineTop;
    CardState mLineBottom;
    CardState mLineRight;

    private CardDrawable(int[] color) {
        mColorArray = color;
    }

    public static CardDrawableBuilder newBuilder(int... fillColors) {
        return new CardDrawableBuilder(fillColors);
    }

    public static CardDrawableBuilder newBuilder(Context context, int... fillColors) {
        CardDrawableBuilder builder = new CardDrawableBuilder(fillColors);
        if (context != null) {
            builder.mDensity = context.getResources().getDisplayMetrics().density;
        }
        return builder;
    }

    public static class CardDrawableBuilder {
        CardState mCardLine;
        CardDrawable mCardDrawable;
        float mDensity = 1;

        private CardDrawableBuilder(int[] colors) {
            if (colors != null && colors.length == 1 && colors[0] == 0) {
                colors = null;
            }
            mCardDrawable = new CardDrawable(colors);
        }

        public CardDrawableBuilder left(float lineWidth) {
            mCardLine = new CardState(CardState.LINE_LEFT, lineWidth * mDensity);
            mCardDrawable.mLineLeft = mCardLine;
            return CardDrawableBuilder.this;
        }

        public CardDrawableBuilder top(float lineWidth) {
            mCardLine = new CardState(CardState.LINE_TOP, lineWidth * mDensity);
            mCardDrawable.mLineTop = mCardLine;
            return CardDrawableBuilder.this;
        }

        public CardDrawableBuilder right(float lineWidth) {
            mCardLine = new CardState(CardState.LINE_RIGHT, lineWidth * mDensity);
            mCardDrawable.mLineRight = mCardLine;
            return CardDrawableBuilder.this;
        }

        public CardDrawableBuilder bottom(float lineWidth) {
            mCardLine = new CardState(CardState.LINE_BOTTOM, lineWidth * mDensity);
            mCardDrawable.mLineBottom = mCardLine;
            return CardDrawableBuilder.this;
        }

        public CardDrawableBuilder color(int color) {
            if (mCardLine != null) {
                mCardLine.mColorStart = mCardLine.mColorEnd = color;
            }
            return CardDrawableBuilder.this;
        }

        public CardDrawableBuilder color(int colorStart, int colorEnd) {
            if (mCardLine != null) {
                mCardLine.mColorStart = colorStart;
                mCardLine.mColorEnd = colorEnd;
            }
            return CardDrawableBuilder.this;
        }

        public CardDrawableBuilder colorWithPosition(int[] color, float[] positions) {
            if (mCardLine != null) {
                mCardLine.mColors = color;
                mCardLine.mColorPositions = positions;
            }
            return CardDrawableBuilder.this;
        }

        public CardDrawableBuilder radiusHalf() {
            return radius(mCardLine.mLineWidth / 2f);
        }

        public CardDrawableBuilder radius(float radius) {
            if (mCardLine != null) {
                radius *= mDensity;
                float[] r = mCardLine.mRadiusArray = new float[8];
                int lineType = mCardLine.getLineType();
                int radiusIndex1 = 0, radiusIndex2 = 0;
                if (CardState.LINE_BOTTOM == lineType) {
                    radiusIndex1 = 4;
                    radiusIndex2 = 6;
                } else if (CardState.LINE_LEFT == lineType) {
                    radiusIndex1 = 0;
                    radiusIndex2 = 6;
                } else if (CardState.LINE_RIGHT == lineType) {
                    radiusIndex1 = 2;
                    radiusIndex2 = 4;
                } else if (CardState.LINE_TOP == lineType) {
                    radiusIndex1 = 0;
                    radiusIndex2 = 2;
                }
                r[radiusIndex1] = radius;
                r[radiusIndex2] = radius;
                r[radiusIndex1 + 1] = radius;
                r[radiusIndex2 + 1] = radius;

            }
            return CardDrawableBuilder.this;
        }

        public CardDrawable build() {
            CardDrawable r = mCardDrawable;
            mCardLine = null;
            mCardDrawable = null;
            return r;
        }
    }

    private static class CardState {
        public static final int LINE_TOP = 1;
        public static final int LINE_BOTTOM = 2;
        public static final int LINE_LEFT = 3;
        public static final int LINE_RIGHT = 4;
        public static final int LINE_AVAIABLE = 8;

        public static final int DIRTY_PATH = 512;
        public static final int DIRTY_GRADIENT = 1024;
        public static final int DIRTY_AVAIABLE = 2048;

        int mFlag = 0;

        float mLineWidth;

        int mColorStart;
        int mColorEnd;

        int[] mColors;
        float[] mColorPositions;

        float[] mRadiusArray;

        Path mPath;
        Paint mPaint;

        protected CardState(int lineType, float lineWidth) {
            int lineTypes = LINE_TOP | LINE_BOTTOM | LINE_LEFT | LINE_RIGHT;
            lineType = lineTypes & lineType;
            mFlag = lineType | DIRTY_PATH | DIRTY_GRADIENT | DIRTY_AVAIABLE;
            mLineWidth = lineWidth;
        }

        protected boolean isLineAvailable() {
            if (DIRTY_AVAIABLE == (mFlag & DIRTY_AVAIABLE)) {
                mFlag = mFlag & ~DIRTY_AVAIABLE;
                boolean avaiable = mLineWidth > 0;
                if (avaiable) {
                    if (mColorStart == 0 && mColorEnd == 0 && mColors == null) {
                        avaiable = false;
                    }
                }
                if (avaiable) {
                    mFlag = mFlag | LINE_AVAIABLE;
                } else {
                    mFlag = mFlag & ~LINE_AVAIABLE;
                }
            }
            return LINE_AVAIABLE == (mFlag & LINE_AVAIABLE);
        }

        protected int getLineType() {
            int lineTypes = LINE_TOP | LINE_BOTTOM | LINE_LEFT | LINE_RIGHT;
            return lineTypes & mFlag;
        }

        protected void drawCardLine(Canvas canvas, Rect bounds, RectF rectTemp) {
            boolean dirtyPath = (mFlag & DIRTY_PATH) == DIRTY_PATH;
            boolean dirtyGradient = (mFlag & DIRTY_GRADIENT) == DIRTY_GRADIENT;
            if (dirtyPath || dirtyGradient) {
                rectTemp.set(bounds);
                int lineType = getLineType();
                float x0 = rectTemp.left, y0 = rectTemp.top;
                float x1 = rectTemp.right, y1 = rectTemp.bottom;
                if (LINE_BOTTOM == lineType) {
                    y0 = rectTemp.top = rectTemp.bottom - mLineWidth;
                    x0 = x1;
                } else if (LINE_LEFT == lineType) {
                    y0 = x0;
                    x0 = rectTemp.right = rectTemp.left + mLineWidth;
                    x1 = y0;
                    y0 = y1;
                } else if (LINE_TOP == lineType) {
                    x0 = y0;
                    y0 = rectTemp.bottom = rectTemp.top + mLineWidth;
                    y1 = x0;
                    x0 = x1;
                } else if (LINE_RIGHT == lineType) {
                    x0 = rectTemp.left = rectTemp.right - mLineWidth;
                    y0 = y1;
                }
                if (dirtyPath) {
                    mFlag = mFlag & ~DIRTY_PATH;
                    if (mPath == null) {
                        mPath = new Path();
                    } else {
                        mPath.reset();
                    }
                    if (mRadiusArray == null) {
                        mPath.addRect(rectTemp, Path.Direction.CW);
                    } else {
                        mPath.addRoundRect(rectTemp, mRadiusArray, Path.Direction.CW);
                    }
                }
                if (dirtyGradient) {
                    mFlag = mFlag & ~DIRTY_GRADIENT;
                    if (mPaint == null) {
                        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        mPaint.setStyle(Paint.Style.FILL);
                    }
                    LinearGradient gradient = null;
                    if (mColors == null) {
                        if (mColorStart == mColorEnd) {
                            mPaint.setColor(mColorStart);
                        } else {
                            gradient = new LinearGradient(x0, y0, x1, y1, mColorStart, mColorEnd, Shader.TileMode.CLAMP);
                        }
                    } else {
                        gradient = new LinearGradient(x0, y0, x1, y1, mColors, mColorPositions, Shader.TileMode.CLAMP);
                    }
                    mPaint.setShader(gradient);
                }
            }
            canvas.drawPath(mPath, mPaint);
        }
    }

    private static boolean hasState(int[] state, int s) {
        int n = state == null ? 0 : state.length;
        for (int i = 0; i < n; i++) {
            if (state[i] == s) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean onStateChange(int[] state) {
        boolean r = super.onStateChange(state);
        int willState = STATE_INDEX_NORMAL;
        if (hasState(state, android.R.attr.state_enabled)) {
            if (hasState(state, android.R.attr.state_pressed)) {
                willState = STATE_INDEX_PRESSED;
            } else if (hasState(state, android.R.attr.state_checked)) {
                willState = STATE_INDEX_CHECKED;
            }
        } else {
            willState = STATE_INDEX_DISABLED;
        }
        if (willState != mStateIndex) {
            mStateIndex = willState;
            r = true;
            invalidateSelf();
        }
        return r;
    }

    @Override
    public void draw(Canvas can) {
        final Rect bounds = getBounds();
        if (!bounds.isEmpty()) {
            if (mLineLeft != null && mLineLeft.isLineAvailable()) {
                mLineLeft.drawCardLine(can, bounds, mTempRectF);
            }
            if (mLineTop != null && mLineTop.isLineAvailable()) {
                mLineTop.drawCardLine(can, bounds, mTempRectF);
            }
            if (mLineRight != null && mLineRight.isLineAvailable()) {
                mLineRight.drawCardLine(can, bounds, mTempRectF);
            }
            if (mLineBottom != null && mLineBottom.isLineAvailable()) {
                mLineBottom.drawCardLine(can, bounds, mTempRectF);
            }
            if (mColorArray != null && mStateIndex >= 0 && mColorArray.length>0) {
                int colorIndex=mStateIndex>=mColorArray.length?0:mStateIndex;
                int fillColor = mColorArray[colorIndex];
                if (fillColor != 0) {
                    Paint fillPaint = getPaint();
                    mTempRect.setEmpty();
                    applyCardLinePadding(mTempRect);
                    int left = bounds.left + mTempRect.left;
                    int top = bounds.top + mTempRect.top;
                    int right = bounds.right - mTempRect.right;
                    int bottom = bounds.bottom - mTempRect.bottom;
                    fillPaint.setColor(fillColor);
                    can.drawRect(left, top, right, bottom, fillPaint);
                }
            }
        }
    }

    private Paint getPaint() {
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.FILL);
        }
        return mPaint;
    }

    private boolean applyCardLinePadding(Rect padding) {
        boolean changedPadding = false;
        if (mLineLeft != null && mLineLeft.isLineAvailable()) {
            padding.left = (int) mLineLeft.mLineWidth;
            changedPadding = true;
        }
        if (mLineTop != null && mLineTop.isLineAvailable()) {
            padding.top = (int) mLineTop.mLineWidth;
            changedPadding = true;
        }
        if (mLineRight != null && mLineRight.isLineAvailable()) {
            padding.right = (int) mLineRight.mLineWidth;
            changedPadding = true;
        }
        if (mLineBottom != null && mLineBottom.isLineAvailable()) {
            padding.bottom = (int) mLineBottom.mLineWidth;
            changedPadding = true;
        }
        return changedPadding;
    }

    @Override
    public boolean getPadding(Rect padding) {
        return applyCardLinePadding(padding) || super.getPadding(padding);
    }

    private void markCardLineDirty(CardState lineState, int dirty) {
        if (lineState != null && lineState.isLineAvailable()) {
            lineState.mFlag = lineState.mFlag | dirty;
        }
    }

    private void markCardLineDirty(int dirty) {
        markCardLineDirty(mLineLeft, dirty);
        markCardLineDirty(mLineTop, dirty);
        markCardLineDirty(mLineRight, dirty);
        markCardLineDirty(mLineBottom, dirty);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        markCardLineDirty(CardState.DIRTY_PATH | CardState.DIRTY_GRADIENT);
    }


    @Override
    protected boolean onLevelChange(int level) {
        super.onLevelChange(level);
        markCardLineDirty(CardState.DIRTY_PATH | CardState.DIRTY_GRADIENT);
        invalidateSelf();
        return true;
    }

    @Override
    public boolean isStateful() {
        return mColorArray != null && mColorArray.length > 1;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        if (mPaint != null) {
            mPaint.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (mPaint != null) {
            mPaint.setColorFilter(cf);
        }
    }
}
