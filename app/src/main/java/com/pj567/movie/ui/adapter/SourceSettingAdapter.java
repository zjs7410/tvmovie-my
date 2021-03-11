package com.pj567.movie.ui.adapter;

import android.graphics.Color;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pj567.movie.R;
import com.pj567.movie.bean.SourceBean;

import java.util.ArrayList;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class SourceSettingAdapter extends BaseQuickAdapter<SourceBean, BaseViewHolder> {
    public SourceSettingAdapter() {
        super(R.layout.item_source_setting_layout, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, SourceBean item) {
        TextView tvSource = helper.getView(R.id.tvSource);
        if (item.selected) {
            tvSource.setTextColor(mContext.getResources().getColor(R.color.color_02F8E1));
        } else {
            tvSource.setTextColor(Color.WHITE);
        }
        tvSource.setText(item.getName());
    }
}