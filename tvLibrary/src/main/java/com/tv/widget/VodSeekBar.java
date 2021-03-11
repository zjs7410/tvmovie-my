package com.tv.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.tv.R;


/**
 * 最小高度20
 *
 * @user acer
 * @date 2018/12/21
 */
public class VodSeekBar extends View {
    private Paint mBarPaint;
    private Paint mBackPaint;
    private int mBackColor = 0xa30996c5;
    private int mBarColor = Color.WHITE;
    private float mBackStrokeWidth;
    private float startY;
    private float barRadius;
    private float mProgress;
    private float mMax;
    private int speed = 1;
    private long startTime;
    private long endTime;
    private OnVodSeekBarChangedListener seekBarChangedListener;

    public VodSeekBar(Context context) {
        this(context, null);
    }

    public VodSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VodSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VodSeekBar, defStyleAttr, 0);
        mMax = a.getFloat(R.styleable.VodSeekBar_max, 100.0f);
        mProgress = a.getFloat(R.styleable.VodSeekBar_progress, 0);
        a.recycle();
        init();
    }

    private void init() {
        //初始化背景画笔
        mBackPaint = new Paint();
        mBackPaint.setAntiAlias(true);
        mBackPaint.setColor(mBackColor);
        mBackStrokeWidth = pt2px(getContext(), 3);
        mBackPaint.setStrokeWidth(mBackStrokeWidth);
        //初始化bar画笔
        mBarPaint = new Paint();
        mBarPaint.setAntiAlias(true);
        mBarPaint.setColor(mBarColor);
        mBarPaint.setStrokeWidth(pt2px(getContext(), 1));

        barRadius = pt2px(getContext(), 4);
        startY = pt2px(getContext(), 14);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        //画背景
        startY = (startY + mBackStrokeWidth + pt2px(getContext(), 4)) >= height ?
                startY : height - mBackStrokeWidth - pt2px(getContext(), 4);
        canvas.drawLine(barRadius, startY, width - barRadius, startY, mBackPaint);
        //绘制bar
        float barStartX = (float) (mProgress * 1.0 / mMax * (width - barRadius * 2));
        @SuppressLint("DrawAllocation")
        RectF barRectF = new RectF(barStartX, getPaddingTop(), barStartX + 2 * barRadius, height - getPaddingBottom());
        canvas.drawRoundRect(barRectF, barRadius, barRadius, mBarPaint);
    }

    public void setBackColor(@ColorInt int color) {
        mBackColor = color;
        mBackPaint.setColor(color);
        postInvalidate();
    }

    public void setBackStrokeWidth(float width) {
        mBackStrokeWidth = pt2px(getContext(), width);
        mBackPaint.setStrokeWidth(mBackStrokeWidth);
        postInvalidate();
    }

    public void setOnVodSeekBarChangedListener(OnVodSeekBarChangedListener listener) {
        this.seekBarChangedListener = listener;
    }

    public void setProgress(float progress) {
        if (progress >= mMax) {
            mProgress = mMax;
        } else if (progress <= 0) {
            mProgress = 0;
        } else {
            mProgress = progress;
        }
        if (seekBarChangedListener != null) {
            seekBarChangedListener.onProgressChanged(this, mProgress);
        }
        postInvalidate();
    }

    public float getProgress() {
        return mProgress;
    }

    public void setMax(float mMax) {
        this.mMax = mMax;
    }

    public float getMax() {
        return mMax;
    }

    public float getBarRadius() {
        return barRadius;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        startTime = 0;
        speed = 1;
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            } else {
                endTime = System.currentTimeMillis();
            }
            if (endTime - startTime > 1000 && endTime - startTime < 2500) {
                speed = 2;
            } else if (endTime - startTime >= 2500 && endTime - startTime < 4000) {
                speed = 4;
            } else if (endTime - startTime >= 4000) {
                speed = 8;
            }
            float progress = getProgress();
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (progress + speed <= getMax()) {
                    setProgress(progress + speed);
                } else if (progress + speed >= getMax() && progress != getMax()) {
                    setProgress(getMax());
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (progress - speed >= 0) {
                    setProgress(progress - speed);
                } else if (progress - speed <= 0 && progress != 0) {
                    setProgress(0);
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    public interface OnVodSeekBarChangedListener {
        void onProgressChanged(VodSeekBar vodSeekBar, float progress);
    }

    public int pt2px(Context context, float value) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, value, context.getResources().getDisplayMetrics()) + 0.5f);
    }
}
