package com.pj567.movie.ui.fragment;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tv.leanback.VerticalGridView;
import com.pj567.movie.R;
import com.pj567.movie.api.ApiConfig;
import com.pj567.movie.base.BaseLazyFragment;
import com.pj567.movie.bean.SourceBean;
import com.pj567.movie.ui.adapter.SourceSettingAdapter;
import com.pj567.movie.util.FastClickCheckUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class SourceSettingFragment extends BaseLazyFragment {
    private VerticalGridView mGridView;
    private SourceSettingAdapter settingAdapter;
    private List<SourceBean> mSourceBeanList = new ArrayList<>();
    private int sourceIndex = 0;

    public static SourceSettingFragment newInstance() {
        return new SourceSettingFragment().setArguments();
    }

    public SourceSettingFragment setArguments() {
        return this;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_source_grid;
    }

    @Override
    protected void init() {
        mGridView = findViewById(R.id.mGridView);
        settingAdapter = new SourceSettingAdapter();
        mGridView.setAdapter(settingAdapter);
        mGridView.setNumColumns(6);
        mSourceBeanList.addAll(ApiConfig.get().getSourceBeanList());
        for (int i = 0; i < mSourceBeanList.size(); i++) {
            if (mSourceBeanList.get(i).getId() == ApiConfig.get().getDefaultSourceBean().getId()) {
                mSourceBeanList.get(i).selected = true;
                sourceIndex = i;
                break;
            }
        }
        settingAdapter.setNewData(mSourceBeanList);
        settingAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                if (sourceIndex != position) {
                    SourceBean sourceBean = settingAdapter.getData().get(position);
                    settingAdapter.getData().get(sourceIndex).selected = false;
                    settingAdapter.notifyItemChanged(sourceIndex);
                    sourceBean.selected = true;
                    settingAdapter.notifyItemChanged(position);
                    sourceIndex = position;
                    ApiConfig.get().setSourceBean(sourceBean);
                }
            }
        });
    }
}