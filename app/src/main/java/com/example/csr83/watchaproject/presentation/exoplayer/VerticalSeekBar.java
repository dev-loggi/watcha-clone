package com.example.csr83.watchaproject.presentation.exoplayer;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.support.v7.widget.AppCompatSeekBar;

public class VerticalSeekBar extends AppCompatSeekBar {

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);
    }

    private OnSeekBarChangeListener onChangeListener;
    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onChangeListener){
        this.onChangeListener = onChangeListener;
    }

    private int lastProgress = 0;
    private int mLastProgress = 0;
    private int mProgressWhenScrollingStarted = 0;
    public boolean onExoTouchEvent(int action, float offset) {
        if (!isEnabled()) {
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                Log.d(getClass().getSimpleName(), "onExoTouchEvent(ACTION_DOWN), " + "getProgress()" + getProgress());
                onChangeListener.onStartTrackingTouch(this);
                mProgressWhenScrollingStarted = getProgress();
                setPressed(true);
                setSelected(true);
                break;

            case MotionEvent.ACTION_SCROLL:
//                Log.d(getClass().getSimpleName(), "offset=" + offset + ", mProgressWhenScrollingStarted=" + mProgressWhenScrollingStarted);

                int progress = mProgressWhenScrollingStarted + (int) offset;

                // Ensure progress stays within boundaries
                if (progress < 0) {
                    progress = 0;
                }
                if (progress > getMax()) {
                    progress = getMax();
                }
                setProgress(progress);  // Draw progress
                if (progress != mLastProgress) {
                    // Only enact listener if the progress has actually changed
                    mLastProgress = progress;
                    onChangeListener.onProgressChanged(this, progress, false);
                }

                onSizeChanged(getWidth(), getHeight(), 0, 0);
                setPressed(true);
                setSelected(true);
                return true;

            case MotionEvent.ACTION_UP:
                onChangeListener.onStopTrackingTouch(this);
                setPressed(false);
                setSelected(false);
                mProgressWhenScrollingStarted = 0;
                break;
        }
        return false;
    }

    public synchronized void setProgressAndThumb(int progress) {
        setProgress(progress);
        onSizeChanged(getWidth(), getHeight() , 0, 0);
        if(progress != lastProgress) {
            // Only enact listener if the progress has actually changed
            lastProgress = progress;
            onChangeListener.onProgressChanged(this, progress, true);
        }
    }

    public synchronized void setMaximum(int maximum) {
        setMax(maximum);
    }

    public synchronized int getMaximum() {
        return getMax();
    }
}