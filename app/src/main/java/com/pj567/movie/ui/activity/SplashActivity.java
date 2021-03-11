package com.pj567.movie.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;
import com.pj567.movie.R;
import com.pj567.movie.base.BaseActivity;
import com.pj567.movie.util.HawkConfig;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class SplashActivity extends BaseActivity {
    private LinearLayout rootLayout;
    private EditText etPassword;
    private EditText etConfirmPassword;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_init_pw;
    }

    @Override
    protected void init() {
        if (Hawk.get(HawkConfig.FIRST, true)) {
            Hawk.put(HawkConfig.FIRST, false);
            initView();
        } else {
            jumpActivity(HomeActivity.class);
            finish();
        }
    }

    private void initView() {
        rootLayout = findViewById(R.id.rootLayout);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        rootLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.tvConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
        findViewById(R.id.tvJump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump();
            }
        });
    }

    public void confirm() {
        String pwd = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pwd.length() != 8) {
            Toast.makeText(this, "密码必须为8位", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pwd.equals(confirm)) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        Hawk.put(HawkConfig.PASSWORD, pwd);
        Toast.makeText(this, "密码设置成功", Toast.LENGTH_SHORT).show();
        jumpActivity(HomeActivity.class);
        finish();
    }

    public void jump() {
        jumpActivity(HomeActivity.class);
        finish();
    }
}