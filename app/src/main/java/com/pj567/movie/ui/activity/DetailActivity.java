package com.pj567.movie.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.hawk.Hawk;
import com.pj567.movie.R;
import com.pj567.movie.base.BaseActivity;
import com.pj567.movie.bean.AbsXml;
import com.pj567.movie.bean.Movie;
import com.pj567.movie.bean.VodInfo;
import com.pj567.movie.cache.RoomDataManger;
import com.pj567.movie.event.RefreshEvent;
import com.pj567.movie.picasso.RoundTransformation;
import com.pj567.movie.ui.adapter.SeriesAdapter;
import com.pj567.movie.util.FastClickCheckUtil;
import com.pj567.movie.util.HawkConfig;
import com.pj567.movie.viewmodel.SourceViewModel;
import com.squareup.picasso.Picasso;
import com.tv.leanback.GridLayoutManager;
import com.tv.leanback.VerticalGridView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * @author pj567
 * @date :2020/12/22
 * @description:
 */
public class DetailActivity extends BaseActivity {
    private LinearLayout llLayout;
    private ImageView ivThumb;
    private TextView tvName;
    private TextView tvYear;
    private TextView tvArea;
    private TextView tvLang;
    private TextView tvType;
    private TextView tvActor;
    private TextView tvDirector;
    private TextView tvDes;
    private TextView tvPlay;
    private VerticalGridView mGridView;
    private SourceViewModel sourceViewModel;
    private Movie.Video mVideo;
    private VodInfo vodInfo;
    private SeriesAdapter seriesAdapter;
    private int playIndex = -1;
    private String sourceUrl;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_detail;
    }

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        initView();
        initViewModel();
        initData();
    }

    private void initView() {
        llLayout = findViewById(R.id.llLayout);
        ivThumb = findViewById(R.id.ivThumb);
        tvName = findViewById(R.id.tvName);
        tvYear = findViewById(R.id.tvYear);
        tvArea = findViewById(R.id.tvArea);
        tvLang = findViewById(R.id.tvLang);
        tvType = findViewById(R.id.tvType);
        tvActor = findViewById(R.id.tvActor);
        tvDirector = findViewById(R.id.tvDirector);
        tvDes = findViewById(R.id.tvDes);
        tvPlay = findViewById(R.id.tvPlay);
        mGridView = findViewById(R.id.mGridView);
        mGridView.setHasFixedSize(true);
        mGridView.setNumColumns(6);
        ((GridLayoutManager) mGridView.getLayoutManager()).setFocusOutAllowed(true, true);
        seriesAdapter = new SeriesAdapter();
        mGridView.setAdapter(seriesAdapter);
        tvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if (vodInfo != null && vodInfo.seriesList.size() > 0) {
                    Bundle bundle = new Bundle();
                    seriesAdapter.getData().get(vodInfo.playIndex).selected = true;
                    seriesAdapter.notifyItemChanged(vodInfo.playIndex);
                    //保存历史
                    RoomDataManger.insertVodRecord(sourceUrl, vodInfo);
                    if (vodInfo.isX5) {
                        bundle.putString("html", vodInfo.seriesList.get(vodInfo.playIndex).url);
                        jumpActivity(PraseActivity.class, bundle);
                    } else {
                        bundle.putSerializable("VodInfo", vodInfo);
                        jumpActivity(PlayActivity.class, bundle);
                    }
                }
            }
        });
        seriesAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                if (vodInfo != null && vodInfo.seriesList.size() > 0) {
                    if (vodInfo.playIndex != position) {
                        seriesAdapter.getData().get(vodInfo.playIndex).selected = false;
                        seriesAdapter.notifyItemChanged(vodInfo.playIndex);
                        seriesAdapter.getData().get(position).selected = true;
                        seriesAdapter.notifyItemChanged(position);
                        vodInfo.playIndex = position;
                    }
                    //保存历史
                    RoomDataManger.insertVodRecord(sourceUrl, vodInfo);
                    Bundle bundle = new Bundle();
                    if (vodInfo.isX5) {
                        bundle.putString("html", vodInfo.seriesList.get(vodInfo.playIndex).url);
                        jumpActivity(PraseActivity.class, bundle);
                    } else {
                        bundle.putSerializable("VodInfo", vodInfo);
                        jumpActivity(PlayActivity.class, bundle);
                    }
                }
            }
        });
        setLoadSir(llLayout);
    }

    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.detailResult.observe(this, new Observer<AbsXml>() {
            @Override
            public void onChanged(AbsXml absXml) {
                if (absXml != null && absXml.movie != null && absXml.movie.videoList != null && absXml.movie.videoList.size() > 0) {
                    showSuccess();
                    mVideo = absXml.movie.videoList.get(0);
                    vodInfo = new VodInfo();
                    vodInfo.setVideo(mVideo);
                    vodInfo.playIndex = Math.max(playIndex, 0);
                    if (vodInfo.seriesList != null && vodInfo.seriesList.size() > playIndex && playIndex != -1) {
                        vodInfo.seriesList.get(playIndex).selected = true;
                    }
                    seriesAdapter.setNewData(vodInfo.seriesList);
                    mGridView.scrollToPosition(vodInfo.playIndex);
                    tvName.setText(mVideo.name);
                    tvYear.setText(Html.fromHtml(getHtml("年份：", String.valueOf(mVideo.year))));
                    tvArea.setText(Html.fromHtml(getHtml("地区：", mVideo.area)));
                    tvLang.setText(Html.fromHtml(getHtml("语言：", mVideo.lang)));
                    tvType.setText(Html.fromHtml(getHtml("类型：", mVideo.type)));
                    tvActor.setText(Html.fromHtml(getHtml("演员：", mVideo.actor)));
                    tvDirector.setText(Html.fromHtml(getHtml("导演：", mVideo.director)));
                    tvDes.setText(Html.fromHtml(getHtml("内容简介：", mVideo.des)));
                    if (!TextUtils.isEmpty(mVideo.pic)) {
                        Picasso.get()
                                .load(mVideo.pic)
                                .transform(new RoundTransformation(mVideo.pic)
                                        .centerCorp(true)
                                        .override(AutoSizeUtils.pt2px(mContext, 224), AutoSizeUtils.pt2px(mContext, 315))
                                        .roundRadius(AutoSizeUtils.pt2px(mContext, 5), RoundTransformation.RoundType.ALL))
                                .placeholder(R.drawable.error_all_loading)
                                .error(R.drawable.error_all_loading)
                                .into(ivThumb);
                    } else {
                        ivThumb.setImageResource(R.drawable.error_all_loading);
                    }
                } else {
                    showEmpty();
                }
            }
        });
    }

    private String getHtml(String label, String content) {
        return label + "<font color=\"#FFFFFF\">" + content + "</font>";
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            int id = bundle.getInt("id", -1);
            sourceUrl = bundle.getString("sourceUrl");
            VodInfo vodInfo = RoomDataManger.getVodInfo(sourceUrl, id);
            if (vodInfo != null) {
                playIndex = vodInfo.playIndex;
            }
            if (id != -1) {
                showLoading();
                sourceViewModel.getDetail(sourceUrl, id);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_REFRESH) {
            if (event.obj != null) {
                int index = (int) event.obj;
                if (index != vodInfo.playIndex) {
                    seriesAdapter.getData().get(vodInfo.playIndex).selected = false;
                    seriesAdapter.notifyItemChanged(vodInfo.playIndex);
                    seriesAdapter.getData().get(index).selected = true;
                    seriesAdapter.notifyItemChanged(index);
                    mGridView.scrollToPosition(index);
                    vodInfo.playIndex = index;
                    //保存历史
                    RoomDataManger.insertVodRecord(sourceUrl, vodInfo);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}