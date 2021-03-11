package com.pj567.movie.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pj567.movie.bean.VodInfo;
import com.tv.player.PlayerFactory;
import com.tv.player.VideoView;
import com.tv.player.exo.ExoMediaPlayer;

/**
 * @author pj567
 * @date :2020/12/24
 * @description:
 */
public class VodPlayView extends VideoView {
    private VodInfo mVodInfo;
    private int playIndex = 0;

    public VodPlayView(@NonNull Context context) {
        this(context, null);
    }

    public VodPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VodPlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setVodInfo(VodInfo vodInfo, int playIndex) {
        this.mVodInfo = vodInfo;
        this.playIndex = playIndex;
        play();
    }

    private void play() {
        if (mVodInfo != null && mVodInfo.seriesList != null && mVodInfo.seriesList.size() > 0 && playIndex < mVodInfo.seriesList.size()) {
            setUrl(mVodInfo.seriesList.get(playIndex).url);
            start();
        }
    }

    public boolean hasNext() {
        if (mVodInfo == null || mVodInfo.seriesList == null) {
            return false;
        }
        return playIndex + 1 < mVodInfo.seriesList.size();
    }

    public boolean hasPrevious() {
        if (mVodInfo == null || mVodInfo.seriesList == null) {
            return false;
        }
        return playIndex - 1 >= 0;
    }

    public void playNext() {
        playIndex++;
        release();
        play();
    }

    public void playPrevious() {
        playIndex--;
        release();
        play();
    }

    public int getPlayIndex() {
        return playIndex;
    }
}