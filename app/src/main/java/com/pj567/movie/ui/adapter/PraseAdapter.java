package com.pj567.movie.ui.adapter;

import android.graphics.Color;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pj567.movie.R;
import com.pj567.movie.bean.PraseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pj567
 * @date :2021/3/9
 * @description:
 */
public class PraseAdapter extends BaseQuickAdapter<PraseBean, BaseViewHolder> {
    public PraseAdapter() {
        super(R.layout.item_prase_layout, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, PraseBean item) {
        TextView tvPrase = helper.getView(R.id.tvPrase);
        if (item.selected) {
            tvPrase.setTextColor(mContext.getResources().getColor(R.color.color_02F8E1));
        } else {
            tvPrase.setTextColor(Color.WHITE);
        }
        tvPrase.setText(item.getPraseName());
        helper.addOnClickListener(R.id.tvPrase);
    }
}