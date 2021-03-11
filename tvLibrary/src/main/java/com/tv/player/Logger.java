package com.tv.player;

import android.util.Log;

/**
 * @author pj567
 * @date :2020/12/18
 * @description:
 */
public class Logger {
    private static String TAG = "TVMovie_Player";

    public static void e(String msg) {
        Log.e(TAG, "" + msg);
    }
}