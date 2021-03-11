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
public class PraseDialog {
    private View rootView;
    private Dialog mDialog;
    private OnPraseListener playListener;

    public PraseDialog() {

    }

    public PraseDialog build(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_prase, null);
        mDialog = new Dialog(context, R.style.CustomDialogStyle);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(true);
        mDialog.setContentView(rootView);
        init(context);
        return this;
    }

    private void init(Context context) {
        TextView tvPrase1 = findViewById(R.id.tvPrase1);
        TextView tvPrase2 = findViewById(R.id.tvPrase2);
        TextView tvPrase3 = findViewById(R.id.tvPrase3);
        TextView tvPrase4 = findViewById(R.id.tvPrase4);
        int id = Hawk.get(HawkConfig.DEFAULT_PRASE_ID, 1);
        if (id == 1) {
            tvPrase1.requestFocus();
            tvPrase1.setTextColor(context.getResources().getColor(R.color.color_058AF4));
        } else if (id == 2) {
            tvPrase2.requestFocus();
            tvPrase2.setTextColor(context.getResources().getColor(R.color.color_058AF4));
        } else if (id == 3) {
            tvPrase3.requestFocus();
            tvPrase3.setTextColor(context.getResources().getColor(R.color.color_058AF4));
        } else {
            tvPrase4.requestFocus();
            tvPrase4.setTextColor(context.getResources().getColor(R.color.color_058AF4));
        }
        tvPrase1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if (id != 1 && playListener != null) {
                    Hawk.put(HawkConfig.DEFAULT_PRASE_ID, 1);
                    playListener.onChange();
                }
                dismiss();
            }
        });
        tvPrase2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if (id != 2 && playListener != null) {
                    Hawk.put(HawkConfig.DEFAULT_PRASE_ID, 2);
                    playListener.onChange();
                }
                dismiss();
            }
        });
        tvPrase3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if (id != 3 && playListener != null) {
                    Hawk.put(HawkConfig.DEFAULT_PRASE_ID, 3);
                    playListener.onChange();
                }
                dismiss();
            }
        });
        tvPrase4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if (id != 4 && playListener != null) {
                    Hawk.put(HawkConfig.DEFAULT_PRASE_ID, 4);
                    playListener.onChange();
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

    public PraseDialog setOnPraseListener(OnPraseListener listener) {
        playListener = listener;
        return this;
    }

    public interface OnPraseListener {
        void onChange();
    }
}