package com.tv.leanback;

import android.view.View;

/**
 *
 * @author acer
 * @date 2018/8/6
 */

public interface OnItemListener<T> {
    /**
     * 获得焦点
     * @param parent
     * @param itemView
     * @param position
     */
    void onItemSelected(T parent, View itemView, int position);
    /**
     * 失去焦点
     * @param parent
     * @param itemView
     * @param position
     */
    void onItemPreSelected(T parent, View itemView, int position);

}
