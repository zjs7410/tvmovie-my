package com.pj567.movie.ui.fragment;

import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.pj567.movie.R;
import com.pj567.movie.base.BaseLazyFragment;
import com.pj567.movie.event.ServerEvent;
import com.pj567.movie.event.TopStateEvent;
import com.pj567.movie.ui.activity.HistoryActivity;
import com.pj567.movie.ui.activity.LivePlayActivity;
import com.pj567.movie.ui.activity.RewardActivity;
import com.pj567.movie.ui.activity.SearchActivity;
import com.pj567.movie.ui.activity.SettingActivity;
import com.pj567.movie.ui.dialog.RemoteDialog;
import com.pj567.movie.util.FastClickCheckUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author pj567
 * @date :2021/3/9
 * @description:
 */
public class UserFragment extends BaseLazyFragment implements View.OnClickListener {
    private TextView tvLive;
    private TextView tvSearch;
    private TextView tvSetting;
    private TextView tvHistory;
    private TextView tvReward;
    private TextView tvProjection;
    private RemoteDialog remoteDialog;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_user_layout;
    }

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        tvLive = findViewById(R.id.tvLive);
        tvSearch = findViewById(R.id.tvSearch);
        tvSetting = findViewById(R.id.tvSetting);
        tvHistory = findViewById(R.id.tvHistory);
        tvReward = findViewById(R.id.tvReward);
        tvProjection = findViewById(R.id.tvProjection);
        tvLive.setOnKeyListener(onKeyListener);
        tvSearch.setOnKeyListener(onKeyListener);
        tvSetting.setOnKeyListener(onKeyListener);
        tvHistory.setOnKeyListener(onKeyListener);
        tvReward.setOnKeyListener(onKeyListener);
        tvLive.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        tvHistory.setOnClickListener(this);
        tvReward.setOnClickListener(this);
        tvProjection.setOnClickListener(this);
    }

    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    EventBus.getDefault().post(new TopStateEvent(TopStateEvent.TYPE_TOP));
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    public void onClick(View v) {
        FastClickCheckUtil.check(v);
        if (v.getId() == R.id.tvLive) {
            jumpActivity(LivePlayActivity.class);
        } else if (v.getId() == R.id.tvSearch) {
            jumpActivity(SearchActivity.class);
        } else if (v.getId() == R.id.tvSetting) {
            jumpActivity(SettingActivity.class);
        } else if (v.getId() == R.id.tvHistory) {
            jumpActivity(HistoryActivity.class);
        } else if (v.getId() == R.id.tvReward) {
            jumpActivity(RewardActivity.class);
        } else if (v.getId() == R.id.tvProjection) {
            remoteDialog = new RemoteDialog().build(mContext);
            remoteDialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void server(ServerEvent event) {
        if (event.type == ServerEvent.SERVER_CONNECTION) {
            if (remoteDialog != null && remoteDialog.isShowing()) {
                remoteDialog.dismiss();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}