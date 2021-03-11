package com.tv.player;

/**
 * @author pj567
 * @date :2020/12/24
 * @description:
 */
public class VideoViewManager {
    private static VideoViewManager instance;
    private static PlayerConfig mConfig;

    private VideoViewManager() {

    }

    public static VideoViewManager get() {
        if (instance == null) {
            synchronized (VideoViewManager.class) {
                if (instance == null) {
                    instance = new VideoViewManager();
                }
            }
        }
        return instance;
    }

    public static PlayerConfig getConfig() {
        setConfig(null);
        return mConfig;
    }

    public static void setConfig(PlayerConfig config) {
        if (mConfig == null) {
            synchronized (PlayerConfig.class) {
                if (mConfig == null) {
                    mConfig = config == null ? PlayerConfig.newBuilder().build() : config;
                }
            }
        }
    }
}