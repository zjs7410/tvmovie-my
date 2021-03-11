package com.pj567.movie.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.IdRes;

import com.pj567.movie.R;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class UpdateHintDialog {
    private View rootView;
    private Dialog mDialog;

    public UpdateHintDialog() {

    }

    public UpdateHintDialog build(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_update_hint, null);
        mDialog = new Dialog(context, R.style.CustomDialogStyle);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(true);
        mDialog.setContentView(rootView);
        init(context);
        return this;
    }

    private void init(Context context) {

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
}