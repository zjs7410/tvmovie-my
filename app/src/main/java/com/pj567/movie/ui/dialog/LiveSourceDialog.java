package com.pj567.movie.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.orhanobut.hawk.Hawk;
import com.pj567.movie.R;
import com.pj567.movie.util.FastClickCheckUtil;
import com.pj567.movie.util.HawkConfig;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class LiveSourceDialog {
    private View rootView;
    private Dialog mDialog;
    private OnChangeLiveListener liveListener;

    public LiveSourceDialog() {

    }

    public LiveSourceDialog build(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_live_source, null);
        mDialog = new Dialog(context, R.style.CustomDialogStyle);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(true);
        mDialog.setContentView(rootView);
        init(context);
        return this;
    }

    private void init(Context context) {
        //直播源1 ip多余域名
        TextView tvLive1 = findViewById(R.id.tvLive1);
        //直播源2
        TextView tvLive2 = findViewById(R.id.tvLive2);
        int live = Hawk.get(HawkConfig.LIVE_SOURCE, 0);
        if (live == 0) {
            tvLive1.requestFocus();
            tvLive1.setTextColor(context.getResources().getColor(R.color.color_058AF4));
        } else {
            tvLive2.requestFocus();
            tvLive2.setTextColor(context.getResources().getColor(R.color.color_058AF4));
        }
        tvLive1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if (live != 0 && liveListener != null) {
                    Hawk.put(HawkConfig.LIVE_SOURCE, 0);
                    liveListener.onChange();
                }
                dismiss();
            }
        });
        tvLive2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if (live != 1 && liveListener != null) {
                    Hawk.put(HawkConfig.LIVE_SOURCE, 1);
                    liveListener.onChange();
                }
                dismiss();
            }
        });
    }

    public void show() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends View> T findViewById(@IdRes int viewId) {
        View view = null;
        if (rootView != null) {
            view = rootView.findViewById(viewId);
        }
        return (T) view;
    }

    public LiveSourceDialog setOnChangeLiveListener(OnChangeLiveListener listener) {
        liveListener = listener;
        return this;
    }

    public interface OnChangeLiveListener {
        void onChange();
    }
}