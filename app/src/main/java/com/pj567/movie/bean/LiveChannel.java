package com.pj567.movie.bean;

import java.io.Serializable;

/**
 * @author pj567
 * @date :2021/1/12
 * @description:
 */
public class LiveChannel implements Serializable {

    /**
     * channelNum : 1
     * channelName : CCTV-1 综合
     * channelUrl : http://117.148.187.37/PLTV/88888888/224/3221226154/index.m3u8
     * channelLogo : https://upload.wikimedia.org/wikipedia/zh/6/65/CCTV-1_Logo.png
     */

    private int channelNum;
    private String channelName;
    private String channelUrl;
    private String channelLogo;
    public boolean selected;

    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public void setChannelUrl(String channelUrl) {
        this.channelUrl = channelUrl;
    }

    public String getChannelLogo() {
        return channelLogo;
    }

    public void setChannelLogo(String channelLogo) {
        this.channelLogo = channelLogo;
    }
}