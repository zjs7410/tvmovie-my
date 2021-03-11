package com.pj567.movie.ui.fragment;

import android.view.View;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;
import com.pj567.movie.R;
import com.pj567.movie.api.ApiConfig;
import com.pj567.movie.base.BaseLazyFragment;
import com.pj567.movie.bean.PraseBean;
import com.pj567.movie.ui.dialog.ChangePlayDialog;
import com.pj567.movie.ui.dialog.LiveSourceDialog;
import com.pj567.movie.ui.dialog.ModelDialog;
import com.pj567.movie.ui.dialog.PasswordDialog;
import com.pj567.movie.ui.dialog.PraseDialog;
import com.pj567.movie.util.FastClickCheckUtil;
import com.pj567.movie.util.HawkConfig;
import com.tv.player.PlayerConfig;
import com.tv.player.PlayerFactory;
import com.tv.player.VideoViewManager;
import com.tv.player.android.AndroidMediaPlayerFactory;
import com.tv.player.exo.ExoMediaPlayerFactory;
import com.tv.player.ijk.IjkPlayerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class ModelSettingFragment extends BaseLazyFragment {
    private TextView tvOpen;
    private TextView tvMediaCodec;
    private TextView tvPlay;
    private TextView tvLive;
    private boolean isModel;
    private boolean isMediaCodec;

    public static ModelSettingFragment newInstance() {
        return new ModelSettingFragment().setArguments();
    }

    public ModelSettingFragment setArguments() {
        return this;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_model;
    }

    @Override
    protected void init() {
        tvOpen = findViewById(R.id.tvOpen);
        tvMediaCodec = findViewById(R.id.tvMediaCodec);
        tvPlay = findViewById(R.id.tvPlay);
        tvLive = findViewById(R.id.tvLive);
        isModel = Hawk.get(HawkConfig.ADOLESCENT_MODEL, true);
        tvOpen.setText(isModel ? "已打开" : "已关闭");
        isMediaCodec = Hawk.get(HawkConfig.MEDIA_CODEC, false);
        tvMediaCodec.setText(isMediaCodec ? "硬解码" : "软解码");
        changePlay();
        tvLive.setText(Hawk.get(HawkConfig.LIVE_SOURCE, 0) == 0 ? "直播源1" : "直播源2");
        findViewById(R.id.llModel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if (!isModel) {
                    isModel = !isModel;
                    Hawk.put(HawkConfig.ADOLESCENT_MODEL, isModel);
                    tvOpen.setText(isModel ? "已打开" : "已关闭");
                } else {
                    ModelDialog dialog = new ModelDialog().setOnChangeModelListener(new ModelDialog.OnChangeModelListener() {
                        @Override
                        public void onChangeModel() {
                            isModel = !isModel;
                            Hawk.put(HawkConfig.ADOLESCENT_MODEL, isModel);
                            tvOpen.setText(isModel ? "已打开" : "已关闭");
                        }
                    }).build(mActivity);
                    dialog.show();
                }
            }
        });
        findViewById(R.id.llPwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                PasswordDialog dialog = new PasswordDialog().build(mActivity);
                dialog.show();
            }
        });
        findViewById(R.id.llMediaCodec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                isMediaCodec = !isMediaCodec;
                Hawk.put(HawkConfig.MEDIA_CODEC, isMediaCodec);
                tvMediaCodec.setText(isMediaCodec ? "硬解码" : "软解码");
                PlayerConfig config = VideoViewManager.getConfig();
                try {
                    Field mPlayerFactoryField = config.getClass().getDeclaredField("isEnableMediaCodec");
                    mPlayerFactoryField.setAccessible(true);
                    mPlayerFactoryField.set(config, isMediaCodec);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.llPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                new ChangePlayDialog().setOnChangePlayListener(new ChangePlayDialog.OnChangePlayListener() {
                    @Override
                    public void onChange() {
                        int playType = Hawk.get(HawkConfig.PLAY_TYPE, 0);
                        PlayerConfig config = VideoViewManager.getConfig();
                        try {
                            Field mPlayerFactoryField = config.getClass().getDeclaredField("mPlayerFactory");
                            mPlayerFactoryField.setAccessible(true);
                            PlayerFactory playerFactory = null;
                            switch (playType) {
                                case 0:
                                default:
                                    tvPlay.setText("系统播放器");
                                    playerFactory = AndroidMediaPlayerFactory.create();
                                    break;
                                case 1:
                                    tvPlay.setText("Ijk播放器");
                                    playerFactory = IjkPlayerFactory.create();
                                    break;
                                case 2:
                                    tvPlay.setText("Exo播放器");
                                    playerFactory = ExoMediaPlayerFactory.create();
                                    break;
                            }
                            mPlayerFactoryField.set(config, playerFactory);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).build(mContext).show();
            }
        });
        findViewById(R.id.llLive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                new LiveSourceDialog().setOnChangeLiveListener(new LiveSourceDialog.OnChangeLiveListener() {
                    @Override
                    public void onChange() {
                        tvLive.setText(Hawk.get(HawkConfig.LIVE_SOURCE, 0) == 0 ? "直播源1" : "直播源2");
                    }
                }).build(mContext).show();
            }
        });
    }

    private void changePlay() {
        int playType = Hawk.get(HawkConfig.PLAY_TYPE, 0);
        if (playType == 1) {
            tvPlay.setText("Ijk播放器");
        } else if (playType == 2) {
            tvPlay.setText("Exo播放器");
        } else {
            tvPlay.setText("系统播放器");
        }
    }
}