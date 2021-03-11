package com.pj567.movie.callback;

import com.kingja.loadsir.callback.Callback;
import com.pj567.movie.R;

/**
 * @author pj567
 * @date :2020/12/24
 * @description:
 */
public class EmptyCallback extends Callback {
    @Override
    protected int onCreateView() {
        return R.layout.empty_layout;
    }
}