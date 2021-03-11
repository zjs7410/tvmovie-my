package com.pj567.movie.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pj567.movie.R;
import com.pj567.movie.base.BaseActivity;
import com.pj567.movie.bean.VodInfo;
import com.pj567.movie.event.RefreshEvent;
import com.pj567.movie.widget.VodPlayView;
import com.pj567.movie.widget.VodSeekLayout;
import com.tv.player.VideoView;

import org.greenrobot.eventbus.EventBus;

/**
 * @author pj567
 * @date :2020/12/22
 * @description:
 */
public class PlayActivity extends BaseActivity {
    private VodPlayView mVideoView;
    private TextView tvHint;
    private ProgressBar mProgressBar;
    private VodSeekLayout mVodSeekLayout;
    private VodInfo mVodInfo;
    private boolean isPause = false;
    private boolean isChangedState = true;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (isChangedState) {
                int mCurrentPosition = (int) mVideoView.getCurrentPosition();
                int mDuration = (int) mVideoView.getDuration();
                int progress = mDuration == 0 ? 0 : (int) (mCurrentPosition * 1.0 / mDuration * mVodSeekLayout.getMaxProgress());
                mVodSeekLayout.setProgress(progress);
                mVodSeekLayout.setCurrentPosition(mCurrentPosition);
                mHandler.removeCallbacks(this);
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_play;
    }

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initView() {
        mVideoView = findViewById(R.id.mVideoView);
        tvHint = findViewById(R.id.tvHint);
        mProgressBar = findViewById(R.id.mProgressBar);
        mVodSeekLayout = findViewById(R.id.mVodSeekLayout);
        mVideoView.addOnStateChangeListener(new VideoView.OnSimpleStateChangeListener() {
            @Override
            public void OnPlayerState(int state) {
                switch (state) {
                    case VideoView.STATE_IDLE:
                        break;
                    case VideoView.STATE_PREPARED:
                    case VideoView.STATE_PLAYING:
                        mHandler.post(mRunnable);
                        mVodSeekLayout.start();
                        mVodSeekLayout.setDuration(mVideoView.getDuration());
                    case VideoView.STATE_BUFFERED:
                        mProgressBar.setVisibility(View.INVISIBLE);
                        break;
                    case VideoView.STATE_PAUSED:
                        break;
                    case VideoView.STATE_BUFFERING:
                    case VideoView.STATE_PREPARING:
                        mProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case VideoView.STATE_PLAYBACK_COMPLETED:
                        if (mVideoView.hasNext()) {
                            mVideoView.playNext();
                            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_REFRESH, mVideoView.getPlayIndex()));
                            mVodSeekLayout.setVodName(String.format("%s[%s]", mVodInfo.name, mVodInfo.seriesList.get(mVideoView.getPlayIndex()).name));
                        }
                        mHandler.removeCallbacks(mRunnable);
                        mVodSeekLayout.setVisibility(View.VISIBLE);
                        mVodSeekLayout.setProgress(0);
                        mVodSeekLayout.setCurrentPosition(0);
                        mVodSeekLayout.setDuration(0);
                        mVodSeekLayout.pause();
                        break;
                    case VideoView.STATE_ERROR:
                        Toast.makeText(mContext, "播放错误", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        mVodSeekLayout.setOnSeekStateListener(new VodSeekLayout.OnSeekStateListener() {
            @Override
            public void onSeekState(int state, int progress) {
                if (state == VodSeekLayout.SEEK_START) {
                    isChangedState = false;
                    mHandler.removeCallbacks(mRunnable);
                } else if (state == VodSeekLayout.SEEK_STOP) {
                    mVideoView.seekTo(progress * mVideoView.getDuration() / mVodSeekLayout.getMaxProgress());
                    isChangedState = true;
                    mHandler.removeCallbacks(mRunnable);
                    mHandler.postDelayed(mRunnable, 1000);
                }
            }

            @Override
            public void onShowState(boolean show) {
                tvHint.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            mVodInfo = (VodInfo) bundle.getSerializable("VodInfo");
            mVideoView.setVodInfo(mVodInfo, mVodInfo.playIndex);
            mVodSeekLayout.setVodName(String.format("%s[%s]", mVodInfo.name, mVodInfo.seriesList.get(mVodInfo.playIndex).name));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (mVideoView.isPlaying()) {
                    mVodSeekLayout.setVisibility(View.VISIBLE);
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (!isPause) {
                    isPause = true;
                    mHandler.removeCallbacks(mRunnable);
                    mVideoView.pause();
                    mVodSeekLayout.setVisibility(View.VISIBLE);
                    int mCurrentPosition = (int) mVideoView.getCurrentPosition();
                    int mDuration = (int) mVideoView.getDuration();
                    int progress = mDuration == 0 ? 0 : mCurrentPosition * mVodSeekLayout.getMaxProgress() / mDuration;
                    mVodSeekLayout.setProgress(progress);
                    mVodSeekLayout.pause();
                } else {
                    isPause = false;
                    mHandler.removeCallbacks(mRunnable);
                    mHandler.postDelayed(mRunnable, 1000);
                    mVideoView.resume();
                    mVodSeekLayout.start();
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (mVideoView.hasPrevious()) {
                    mVideoView.playPrevious();
                    mVodInfo.playIndex = mVideoView.getPlayIndex();
                    EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_REFRESH, mVideoView.getPlayIndex()));
                    mVodSeekLayout.setVodName(String.format("%s[%s]", mVodInfo.name, mVodInfo.seriesList.get(mVideoView.getPlayIndex()).name));
                    mHandler.removeCallbacks(mRunnable);
                    mVodSeekLayout.setVisibility(View.VISIBLE);
                    mVodSeekLayout.setProgress(0);
                    mVodSeekLayout.setCurrentPosition(0);
                    mVodSeekLayout.setDuration(0);
                    mVodSeekLayout.pause();
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (mVideoView.hasNext()) {
                    mVideoView.playNext();
                    mVodInfo.playIndex = mVideoView.getPlayIndex();
                    EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_REFRESH, mVideoView.getPlayIndex()));
                    mVodSeekLayout.setVodName(String.format("%s[%s]", mVodInfo.name, mVodInfo.seriesList.get(mVideoView.getPlayIndex()).name));
                    mHandler.removeCallbacks(mRunnable);
                    mVodSeekLayout.setVisibility(View.VISIBLE);
                    mVodSeekLayout.setProgress(0);
                    mVodSeekLayout.setCurrentPosition(0);
                    mVodSeekLayout.setDuration(0);
                    mVodSeekLayout.pause();
                }
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.release();
        }
    }
}