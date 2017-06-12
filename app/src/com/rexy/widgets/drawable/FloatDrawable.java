package com.rexy.widgets.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;

/**
 * TODO:功能说明
 *
 * @author: rexy
 * @date: 2017-02-16 14:00
 */
public class FloatDrawable extends Drawable implements Runnable, Animatable {
    int mCurrentAlpha = 0;
    long mAnimStartTime = 0;
    boolean mDevDebug = true;
    boolean mRunning = false;
    boolean mAnimating = false;
    RectF mRectRound = new RectF();
    Paint mPaint = new Paint();


    private int mMinAlpha = 0;
    private int mMaxAlpha = 50;
    private int mDuration = 120;
    private int mColorWithOutAlpha = 0xFF000000;
    private int mRoundRadius = 0;
    private boolean mAnimToVisible = false;

    private void print(String msg) {
        System.out.println(msg);
    }

    public FloatDrawable(int color) {
        mPaint.setColor((mColorWithOutAlpha & 0x00FFFFFF) | (mCurrentAlpha << 24));
        color(color);
    }

    public FloatDrawable(int color, int minAlpha, int maxAlpha) {
        mPaint.setColor((mColorWithOutAlpha & 0x00FFFFFF) | (mCurrentAlpha << 24));
        alpha(minAlpha, maxAlpha);
        color(color);
    }

    public FloatDrawable alpha(int alpha) {
        if (mCurrentAlpha != alpha && alpha >= mMinAlpha && alpha <= mMaxAlpha) {
            mCurrentAlpha = alpha;
            if (mRunning) {
                mAnimStartTime = SystemClock.uptimeMillis() - mDuration + (int) ((mAnimToVisible ? (mCurrentAlpha - mMinAlpha) : (mMaxAlpha - mCurrentAlpha)) * mDuration / (float) (mMaxAlpha - mMinAlpha));
            }
            mPaint.setColor((mColorWithOutAlpha & 0x00FFFFFF) | (mCurrentAlpha << 24));
            invalidateSelf();
        }
        return FloatDrawable.this;
    }

    public FloatDrawable alpha(int minAlpha, int maxAlpha) {
        if (minAlpha < 0 || minAlpha > 255) {
            minAlpha = mMinAlpha;
        }
        if (maxAlpha < 0 || maxAlpha > 255) {
            maxAlpha = mMinAlpha;
        }
        if (minAlpha <= maxAlpha && (minAlpha != mMinAlpha || maxAlpha != mMaxAlpha)) {
            mMinAlpha = minAlpha;
            mMaxAlpha = maxAlpha;
            mCurrentAlpha = mAnimToVisible ? mMaxAlpha : mMinAlpha;
            if (mRunning) {
                float percent = (SystemClock.uptimeMillis() - mAnimStartTime) / (float) mDuration;
                int startAlpha = mAnimToVisible ? mMinAlpha : mMaxAlpha;
                mCurrentAlpha = startAlpha + Math.round((mCurrentAlpha - startAlpha) * percent);
            }
            mPaint.setColor((mColorWithOutAlpha & 0x00FFFFFF) | (mCurrentAlpha << 24));
            invalidateSelf();
        }
        return FloatDrawable.this;
    }

    public FloatDrawable color(int color) {
        int alpha = color >>> 24;
        int colorWithoutAlpha = (color & 0x00FFFFFF) | 0xFF000000;
        boolean changed = false;
        if (alpha != mCurrentAlpha && alpha >= mMinAlpha && alpha <= mMaxAlpha) {
            mCurrentAlpha = alpha;
            changed = true;
        }
        if (colorWithoutAlpha != mColorWithOutAlpha) {
            mColorWithOutAlpha = colorWithoutAlpha;
            changed = true;
        }
        if (changed) {
            mPaint.setColor((mColorWithOutAlpha & 0x00FFFFFF) | (mCurrentAlpha << 24));
            invalidateSelf();
        }
        return FloatDrawable.this;
    }

    public FloatDrawable duration(int duration) {
        if (duration > 0) {
            int deta = mDuration - duration;
            if (mRunning) {
                mAnimStartTime += deta;
            }
            mDuration = duration;
        }
        return FloatDrawable.this;
    }

    public FloatDrawable radius(int roundRadius) {
        if (mRoundRadius != roundRadius) {
            mRoundRadius = roundRadius;
            if (mRoundRadius > 0) {
                mPaint.setAntiAlias(true);
                mPaint.setDither(true);
            } else {
                mPaint.setAntiAlias(false);
                mPaint.setDither(false);
            }
            invalidateSelf();
        }
        return FloatDrawable.this;
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        final boolean changed = super.setVisible(visible, restart);
        if (mDevDebug) {
            print(String.format("setVisible(visible=%s,restart=%s)", visible, restart));
        }
        if (visible) {
            if (restart || changed) {
                int targetAlpha = mCurrentAlpha;
                if (mAnimating) {
                    if (restart) {
                        mAnimStartTime = SystemClock.uptimeMillis();
                        targetAlpha = mAnimToVisible ? mMinAlpha : mMaxAlpha;
                    } else {
                        targetAlpha = mCurrentAlpha + (mAnimToVisible ? 1 : -1);
                        mAnimStartTime = SystemClock.uptimeMillis() + (int) (mDuration * (mAnimToVisible ? (mMinAlpha - targetAlpha) : (targetAlpha - mMaxAlpha)) / (float) (mMaxAlpha - mMinAlpha));
                    }
                }
                setFrame(targetAlpha, true, mColorWithOutAlpha != 0 && mMaxAlpha > mMinAlpha && mAnimating);
            }
        } else {
            unscheduleSelf(this);
        }
        return changed;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void start(boolean toVisible) {
        mAnimating = true;
        if (mRunning) {
            if (mAnimToVisible != toVisible) {
                mAnimToVisible = toVisible;
                mAnimStartTime = SystemClock.uptimeMillis() + (int) (mDuration * (toVisible ? (mMinAlpha - mCurrentAlpha) : (mCurrentAlpha - mMaxAlpha)) / (float) (mMaxAlpha - mMinAlpha));
            }
        } else {
            mAnimToVisible = toVisible;
            int targetFrame = mCurrentAlpha;
            if (targetFrame < mMinAlpha || targetFrame > mMaxAlpha) {
                targetFrame = mAnimToVisible ? mMinAlpha : mMaxAlpha;
            } else {
                mAnimStartTime = SystemClock.uptimeMillis() + (int) (mDuration * (toVisible ? (mMinAlpha - targetFrame) : (targetFrame - mMaxAlpha)) / (float) (mMaxAlpha - mMinAlpha));
            }
            if (mDevDebug) {
                print(String.format("start(toVisible=%s)", mAnimToVisible));
            }
            setFrame(targetFrame, false, mMaxAlpha > mMinAlpha && mColorWithOutAlpha != 0);
        }
    }

    @Override
    public void start() {
        start(mRunning ? mAnimToVisible : !mAnimToVisible);
    }

    @Override
    public void stop() {
        mAnimating = false;
        if (isRunning()) {
            if (mDevDebug) {
                print(String.format("stop(lastVisible=%s)", mAnimToVisible));
            }
            unscheduleSelf(this);
        }
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    @Override
    public void run() {
        float percent = (SystemClock.uptimeMillis() - mAnimStartTime) / (float) mDuration;
        boolean animFinished = percent > 1f || percent < 0;
        int endAlpha = mAnimToVisible ? mMaxAlpha : mMinAlpha;
        if (animFinished) {
            if (mDevDebug) {
                print(String.format("runfinish(toVisible=%s,targetAlpha=%d)", mAnimToVisible, endAlpha));
            }
            setFrame(endAlpha, false, false);
            stop();
        } else {
            int startAlpha = mAnimToVisible ? mMinAlpha : mMaxAlpha;
            int targetAlpha = startAlpha + Math.round((endAlpha - startAlpha) * percent);
            if (mDevDebug) {
                print(String.format("running(toVisible=%s,percent=%3f,targetAlpha=%d)", mAnimToVisible, percent, targetAlpha));
            }
            setFrame(targetAlpha, false, true);
        }
    }

    private void setFrame(int frame, boolean unschedule, boolean animate) {
        if (animate) {
            if (frame < mMinAlpha) {
                frame = mMinAlpha;
            }
            if (frame > mMaxAlpha) {
                frame = mMaxAlpha;
            }
        }
        mAnimating = animate;
        if (frame != mCurrentAlpha) {
            mCurrentAlpha = frame;
            mPaint.setColor((mColorWithOutAlpha & 0x00FFFFFF) | (mCurrentAlpha << 24));
            invalidateSelf();
        }
        if (unschedule) {
            unscheduleSelf(this);
        }
        if (animate) {
            mRunning = true;
            scheduleSelf(this, SystemClock.uptimeMillis() + 16);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (mCurrentAlpha > 0 && mCurrentAlpha <= 255) {
            if (mRoundRadius <= 0) {
                canvas.drawRect(getBounds(), mPaint);
            } else {
                mRectRound.set(getBounds());
                canvas.drawRoundRect(mRectRound, mRoundRadius, mRoundRadius, mPaint);
            }
        }
    }

    @Override
    public void unscheduleSelf(Runnable what) {
        if (what == this) {
            mRunning = false;
        }
        super.unscheduleSelf(what);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

}
