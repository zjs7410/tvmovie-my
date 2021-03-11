package com.pj567.movie.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.hawk.Hawk;
import com.pj567.movie.bean.LiveChannel;
import com.pj567.movie.util.HawkConfig;
import com.tv.player.VideoView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pj567
 * @date :2021/1/12
 * @description:
 */
public class LivePlayView extends VideoView {
    private List<LiveChannel> channelList = new ArrayList<>();
    private LiveChannel liveChannel;
    private int playIndex = 0;
    private OnPlayIndexListener listener;

    public LivePlayView(@NonNull Context context) {
        this(context, null);
    }

    public LivePlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LivePlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChannel(List<LiveChannel> list) {
        channelList.clear();
        channelList.addAll(list);
        int index = Hawk.get(HawkConfig.LIVE_CHANNEL, 0);
        liveChannel = getLiveChannelIndex(index);
        play();
    }

    private LiveChannel getLiveChannelNum(int changeChannel) {
        LiveChannel tempChannel = null;
        for (int i = 0; i < channelList.size(); i++) {
            LiveChannel channel = channelList.get(i);
            if (channel.getChannelNum() == changeChannel) {
                playIndex = i;
                tempChannel = channel;
                break;
            }
        }
        if (tempChannel == null) {
            playIndex = 0;
            tempChannel = channelList.get(playIndex);
        }
        Hawk.put(HawkConfig.LIVE_CHANNEL, playIndex);
        return tempChannel;
    }

    private LiveChannel getLiveChannelIndex(int index) {
        LiveChannel tempChannel = null;
        if (index >= 0 && index < channelList.size()) {
            playIndex = index;
            tempChannel = channelList.get(index);
        } else {
            playIndex = 0;
            tempChannel = channelList.get(playIndex);
        }
        Hawk.put(HawkConfig.LIVE_CHANNEL, playIndex);
        return tempChannel;
    }

    public boolean hasContains(int changeChannel) {
        for (LiveChannel channel : channelList) {
            if (channel.getChannelNum() == changeChannel) {
                return true;
            }
        }
        return false;
    }

    public int getPlayIndex(int changeChannel) {
        for (int i = 0; i < channelList.size(); i++) {
            LiveChannel channel = channelList.get(i);
            if (channel.getChannelNum() == changeChannel) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param changeChannel 频道号
     */
    public void changeChannel(int changeChannel) {
        liveChannel = getLiveChannelNum(changeChannel);
        if (listener != null) {
            listener.onPlayIndex(playIndex);
        }
        release();
        play();
    }

    public void changeChannelIndex(int index) {
        liveChannel = getLiveChannelIndex(index);
        if (listener != null) {
            listener.onPlayIndex(playIndex);
        }
        release();
        play();
    }

    public void playNext() {
        playIndex++;
        if (playIndex >= channelList.size()) {
            playIndex = 0;
        }
        liveChannel = getLiveChannelIndex(playIndex);
        if (listener != null) {
            listener.onPlayIndex(playIndex);
        }
        release();
        play();
    }

    public void playPrevious() {
        playIndex--;
        if (playIndex < 0) {
            playIndex = channelList.size() - 1;
        }
        liveChannel = getLiveChannelIndex(playIndex);
        if (listener != null) {
            listener.onPlayIndex(playIndex);
        }
        release();
        play();
    }

    public void play() {
        if (liveChannel != null) {
            setUrl(liveChannel.getChannelUrl());
            start();
        }
    }

    public int getPlayIndex() {
        return playIndex;
    }

    public LiveChannel getLiveChannel() {
        return liveChannel;
    }

    public void setOnPlayIndexListener(OnPlayIndexListener listener) {
        this.listener = listener;
    }

    public interface OnPlayIndexListener {
        void onPlayIndex(int playIndex);
    }
}