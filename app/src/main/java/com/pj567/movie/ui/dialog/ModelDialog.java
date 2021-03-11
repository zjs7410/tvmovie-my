package com.pj567.movie.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
public class ModelDialog {
    private View rootView;
    private Dialog mDialog;
    private EditText editText;
    private OnChangeModelListener modelListener;

    public ModelDialog() {

    }

    public ModelDialog build(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_model, null);
        mDialog = new Dialog(context, R.style.CustomDialogStyle);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setContentView(rootView);
        init(context);
        return this;
    }

    private void init(Context context) {
        editText = findViewById(R.id.etPassword);
        findViewById(R.id.tvConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                String pwd = editText.getText().toString().trim();
                String defaultPwd = Hawk.get(HawkConfig.PASSWORD);
                if (defaultPwd.equals(pwd)) {
                    if (modelListener != null) {
                        modelListener.onChangeModel();
                    }
                    mDialog.dismiss();
                } else {
                    Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
    }

    public void show() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
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

    public ModelDialog setOnChangeModelListener(OnChangeModelListener listener) {
        modelListener = listener;
        return this;
    }

    public interface OnChangeModelListener {
        void onChangeModel();
    }
}