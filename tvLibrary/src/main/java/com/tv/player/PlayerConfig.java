package com.tv.player;

import androidx.annotation.Nullable;

import com.tv.player.android.AndroidMediaPlayerFactory;
import com.tv.player.render.RenderViewFactory;
import com.tv.player.render.TextureRenderViewFactory;

/**
 * @author pj567
 * @date :2020/12/24
 * @description:
 */
public class PlayerConfig {
    public final boolean mIsEnableLog;
    public final boolean mEnableAudioFocus;
    public final ProgressManager mProgressManager;
    public final PlayerFactory mPlayerFactory;
    public final boolean isEnableMediaCodec;
    public final int mScreenScaleType;
    public final RenderViewFactory mRenderViewFactory;

    private PlayerConfig(Builder builder) {
        mIsEnableLog = builder.mIsEnableLog;
        mEnableAudioFocus = builder.mEnableAudioFocus;
        isEnableMediaCodec = builder.isEnableMediaCodec;
        mProgressManager = builder.mProgressManager;
        mScreenScaleType = builder.mScreenScaleType;
        if (builder.mPlayerFactory == null) {
            //默认为AndroidMediaPlayer
            mPlayerFactory = AndroidMediaPlayerFactory.create();
        } else {
            mPlayerFactory = builder.mPlayerFactory;
        }
        if (builder.mRenderViewFactory == null) {
            //默认使用TextureView渲染视频
            mRenderViewFactory = TextureRenderViewFactory.create();
        } else {
            mRenderViewFactory = builder.mRenderViewFactory;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public final static class Builder {
        private boolean mIsEnableLog;
        private boolean mEnableAudioFocus = true;
        private ProgressManager mProgressManager;
        private PlayerFactory mPlayerFactory;
        private int mScreenScaleType;
        private RenderViewFactory mRenderViewFactory;
        private boolean isEnableMediaCodec;

        /**
         * 是否开启AudioFocus监听， 默认开启
         */
        public Builder setEnableAudioFocus(boolean enableAudioFocus) {
            mEnableAudioFocus = enableAudioFocus;
            return this;
        }

        /**
         * 设置进度管理器，用于保存播放进度
         */
        public Builder setProgressManager(@Nullable ProgressManager progressManager) {
            mProgressManager = progressManager;
            return this;
        }

        /**
         * 是否打印日志
         */
        public Builder setLogEnabled(boolean enableLog) {
            mIsEnableLog = enableLog;
            return this;
        }

        /**
         * 是否打印日志
         */
        public Builder setEnableMediaCodec(boolean enableMediaCodec) {
            isEnableMediaCodec = enableMediaCodec;
            return this;
        }

        /**
         * 自定义播放核心
         */
        public Builder setPlayerFactory(PlayerFactory playerFactory) {
            mPlayerFactory = playerFactory;
            return this;
        }

        /**
         * 设置视频比例
         */
        public Builder setScreenScaleType(int screenScaleType) {
            mScreenScaleType = screenScaleType;
            return this;
        }

        /**
         * 自定义RenderView
         */
        public Builder setRenderViewFactory(RenderViewFactory renderViewFactory) {
            mRenderViewFactory = renderViewFactory;
            return this;
        }

        public PlayerConfig build() {
            return new PlayerConfig(this);
        }
    }
}