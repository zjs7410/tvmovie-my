package com.pj567.movie.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.orhanobut.hawk.Hawk;
import com.pj567.movie.bean.MovieSort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author pj567
 * @date :2020/12/21
 * @description:
 */
public class DefaultConfig {

    public static List<MovieSort.SortData> adjustSort(List<MovieSort.SortData> list) {
        List<MovieSort.SortData> data = new ArrayList<>();
        for (MovieSort.SortData sortData : list) {
            if (!isContains(sortData.name)) {
                data.add(sortData);
            }
        }
        data.add(0, new MovieSort.SortData(0, "我的"));
        Collections.sort(data);
        return data;
    }

    public static boolean isContains(String s) {
        boolean contains = false;
        String[] remove;
        if (!Hawk.get(HawkConfig.ADOLESCENT_MODEL, true)) {
            remove = new String[]{"连续剧", "电影", "剧集"};
        } else {
            remove = getRemove();
        }
        for (String temp : remove) {
            if (s.contains(temp)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    private static String[] getRemove() {
        return new String[]{"连续剧", "电影", "剧集", "伦理", "论理", "倫理", "福利", "激情", "理论", "写真", "情色", "美女", "街拍", "赤足", "性感", "里番", "VIP"};
    }

    public static int getAppVersionCode(Context mContext) {
        //包管理操作管理类
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(mContext.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getAppVersionName(Context mContext) {
        //包管理操作管理类
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(mContext.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取路径的文件名
     *
     * @param url
     * @return
     */
    public static String getFileName(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        Uri uri = Uri.parse(url);
        String path = uri.getPath();
        if (path != null) {
            int index = path.lastIndexOf("/") + 1;
            return path.substring(index);
        }
        return url;
    }

    /**
     * 后缀
     *
     * @param name
     * @return
     */
    public static String getFileSuffix(String name) {
        if (TextUtils.isEmpty(name)) {
            return "";
        }
        int endP = name.lastIndexOf(".");
        return endP > -1 ? name.substring(endP) : "";
    }

    /**
     * 获取文件的前缀
     *
     * @param fileName
     * @return
     */
    public static String getFilePrefixName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        int start = fileName.lastIndexOf(".");
        return start > -1 ? fileName.substring(0, start) : fileName;
    }
}