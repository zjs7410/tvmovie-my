package com.pj567.movie.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
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
public class PasswordDialog {
    private View rootView;
    private Dialog mDialog;
    private EditText oEditText;
    private EditText nEditText;
    private EditText cEditText;

    public PasswordDialog() {

    }

    public PasswordDialog build(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_password, null);
        mDialog = new Dialog(context, R.style.CustomDialogStyle);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setContentView(rootView);
        init(context);
        return this;
    }

    private void init(Context context) {
        oEditText = findViewById(R.id.etOPassword);
        nEditText = findViewById(R.id.etNPassword);
        cEditText = findViewById(R.id.etCPassword);
        findViewById(R.id.tvConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                String oPwd = oEditText.getText().toString().trim();
                String nPwd = nEditText.getText().toString().trim();
                String cPwd = cEditText.getText().toString().trim();
                String defaultPwd = Hawk.get(HawkConfig.PASSWORD);
                if (defaultPwd.equals(oPwd)) {
                    if (TextUtils.isEmpty(nPwd)) {
                        Toast.makeText(context, "新密码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (nPwd.length() != 8) {
                        Toast.makeText(context, "密码必须为8位", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (nPwd.equals(oPwd)) {
                        Toast.makeText(context, "不能和原密码一致", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!nPwd.equals(cPwd)) {
                        Toast.makeText(context, "两次密码不一致", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Hawk.put(HawkConfig.PASSWORD, nPwd);
                    Toast.makeText(context, "密码修改成功", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                } else {
                    Toast.makeText(context, "原密码错误", Toast.LENGTH_SHORT).show();
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

}