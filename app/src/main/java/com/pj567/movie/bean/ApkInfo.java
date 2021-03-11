package com.pj567.movie.bean;

import java.io.Serializable;

/**
 * @author pj567
 * @date :2021/1/5
 * @description:
 */
public class ApkInfo implements Serializable {

    /**
     * versionCode : 104
     * apkUrl : https://gitee.com/pj567/TVMovie/blob/master/tv_movie_release_v1.0.4.apk
     */

    private int versionCode;
    private String apkUrl;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }
}