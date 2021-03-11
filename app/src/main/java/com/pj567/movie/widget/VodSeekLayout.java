package com.pj567.movie.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tv.widget.VodSeekBar;
import com.pj567.movie.R;
import com.tv.player.PlayerUtils;

/**
 * @user acer
 * @date 2018/11/27
 */

public class VodSeekLayout extends FrameLayout implements VodSeekBar.OnVodSeekBarChangedListener {
    private VodSeekBar mSeekBar;
    private FrameLayout seekInfoLayout;
    private TextView tvCurrentPosition;
    private TextView tvDuration;
    private TextView vodName;
    private ImageView playState;
    private int delayed = 3000;
    private long mDuration;
    public static final int SEEK_START = 1;
    public static final int SEEK_STOP = 2;
    private boolean isSeekBar = false;
    private OnSeekStateListener seekStateListener;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            setVisibility(GONE);
            if (seekStateListener != null) {
                seekStateListener.onShowState(false);
            }
        }
    };
    private Runnable seekRunnable = new Runnable() {
        @Override
        public void run() {
            if (seekStateListener != null) {
                seekStateListener.onSeekState(SEEK_STOP, (int) mSeekBar.getProgress());
            }
        }
    };

    public VodSeekLayout(@NonNull Context context) {
        this(context, null);
    }

    public VodSeekLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VodSeekLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.vod_seek_layout, this);
        mSeekBar = view.findViewById(R.id.mSeekBar);
        seekInfoLayout = view.findViewById(R.id.seekInfoLayout);
        tvCurrentPosition = view.findViewById(R.id.tvCurrentPosition);
        tvDuration = view.findViewById(R.id.tvDuration);
        vodName = view.findViewById(R.id.vodName);
        playState = view.findViewById(R.id.playState);
        seekInfoLayout.setBackgroundResource(R.drawable.vod_time);
        setBackgroundResource(R.drawable.seek_layout_background);
        playState.setImageResource(R.drawable.vod_play);
        mSeekBar.setOnVodSeekBarChangedListener(this);
        mSeekBar.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    start();
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        isSeekBar = true;
                        if (seekStateListener != null) {
                            seekStateListener.onSeekState(SEEK_START, (int) mSeekBar.getProgress());
                        }
                        removeCallbacks(seekRunnable);
                        postDelayed(seekRunnable, 1000);
                    }
                }
                return false;
            }
        });
        tvCurrentPosition.setText(PlayerUtils.stringForTime(0));
        tvDuration.setText(PlayerUtils.stringForTime(0));
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            removeCallbacks(mRunnable);
            postDelayed(mRunnable, delayed);
            if (seekStateListener != null) {
                seekStateListener.onShowState(true);
            }
        }
    }


    public void setDuration(long duration) {
        mDuration = duration;
        if (duration > 3600000) {//大于3600秒
            mSeekBar.setMax(duration / 10000f);
        } else if (duration > 500000 && duration <= 3600000f) {//大于500秒小于3600秒
            mSeekBar.setMax(duration / 5000f);
        } else if (duration <= 500000) {
            mSeekBar.setMax(duration / 1000f);
        }
        tvDuration.setText(PlayerUtils.stringForTime((int) duration));
    }

    public void setCurrentPosition(long mCurrentPosition) {
        isSeekBar = false;
        tvCurrentPosition.setText(PlayerUtils.stringForTime((int) mCurrentPosition));
    }

    public void setVodName(String vodName) {
        this.vodName.setText(vodName);
    }

    public void setShowDelayed(int delayed) {
        this.delayed = delayed;
    }

    public void setProgress(int progress) {
        mSeekBar.setProgress(progress);
    }

    public int getMaxProgress() {
        return (int) mSeekBar.getMax();
    }

    private int leftMargin = 0;

    private void seekInfoLayout(int x) {
        LayoutParams layoutParams = (LayoutParams) seekInfoLayout.getLayoutParams();
        if (leftMargin == 0) {
            leftMargin = layoutParams.leftMargin;
        }
        layoutParams.leftMargin = (int) (leftMargin + x + mSeekBar.getBarRadius());
        seekInfoLayout.setLayoutParams(layoutParams);
    }

    public void start() {
        mSeekBar.setFocusable(true);
        playState.setImageResource(R.drawable.vod_play);
        removeCallbacks(mRunnable);
        postDelayed(mRunnable, delayed);
    }

    public void pause() {
        mSeekBar.setFocusable(false);
        playState.setImageResource(R.drawable.vod_pause);
        removeCallbacks(mRunnable);
        removeCallbacks(seekRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mRunnable);
        removeCallbacks(seekRunnable);
    }

    public void setOnSeekStateListener(OnSeekStateListener listener) {
        seekStateListener = listener;
    }

    @Override
    public void onProgressChanged(VodSeekBar vodSeekBar, float progress) {
        if (isSeekBar) {
            tvCurrentPosition.setText(PlayerUtils.stringForTime((int) (progress * mDuration / mSeekBar.getMax())));
        }
        seekInfoLayout((int) (progress * 1.0 / vodSeekBar.getMax() * (mSeekBar.getWidth() - 2 * vodSeekBar.getBarRadius())));
    }

    public interface OnSeekStateListener {
        void onSeekState(int state, int progress);

        void onShowState(boolean show);
    }
}
