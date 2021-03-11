package com.tv.player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author pj567
 * @date :2020/12/24
 * @description:
 */
public class BaseController extends FrameLayout {
    private MediaPlayerControl playerControl;

    public BaseController(@NonNull Context context) {
        super(context);
    }

    public BaseController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}