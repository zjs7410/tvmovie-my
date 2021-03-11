package com.pj567.movie.ui.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pj567.movie.R;
import com.pj567.movie.base.BaseLazyFragment;
import com.pj567.movie.bean.VodInfo;
import com.pj567.movie.cache.RoomDataManger;
import com.pj567.movie.event.HistoryStateEvent;
import com.pj567.movie.event.TopStateEvent;
import com.pj567.movie.ui.activity.DetailActivity;
import com.pj567.movie.ui.adapter.HistoryAdapter;
import com.pj567.movie.util.FastClickCheckUtil;
import com.tv.leanback.GridLayoutManager;
import com.tv.leanback.OnChildViewHolderSelectedListener;
import com.tv.leanback.OnItemListener;
import com.tv.leanback.VerticalGridView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author pj567
 * @date :2020/12/21
 * @description:
 */
public class HistoryFragment extends BaseLazyFragment {
    private VerticalGridView mGridView;
    private HistoryAdapter historyAdapter;
    private boolean isLoad = false;
    private boolean isTop = true;
    private String sourceUrl;

    public static HistoryFragment newInstance(String url) {
        return new HistoryFragment().setArguments(url);
    }

    public HistoryFragment setArguments(String url) {
        sourceUrl = url;
        return this;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_history_grid;
    }

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initView() {
        mGridView = findViewById(R.id.mGridView);
        mGridView.setHasFixedSize(true);
        historyAdapter = new HistoryAdapter();
        mGridView.setAdapter(historyAdapter);
        ((GridLayoutManager) mGridView.getLayoutManager()).setFocusOutAllowed(true, true);
        mGridView.setNumColumns(5);
        mGridView.setOnItemListener(new OnItemListener<VerticalGridView>() {
            @Override
            public void onItemSelected(VerticalGridView parent, View itemView, int position) {
                itemView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }

            @Override
            public void onItemPreSelected(VerticalGridView parent, View itemView, int position) {
                itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
            }
        });
        mGridView.addOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
            @Override
            public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder viewHolder, int position, int subposition) {
                if (viewHolder != null && viewHolder.itemView != null) {
                    if (position < 5) {
                        isTop = true;
                    } else {
                        isTop = false;
                    }
                    viewHolder.itemView.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                if (keyCode == KeyEvent.KEYCODE_DPAD_UP && position < 5) {
                                    EventBus.getDefault().post(new HistoryStateEvent(TopStateEvent.TYPE_TOP));
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                }
            }
        });
        historyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                VodInfo vodInfo = historyAdapter.getData().get(position);
                if (vodInfo != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", vodInfo.id);
                    bundle.putString("sourceUrl", sourceUrl);
                    jumpActivity(DetailActivity.class, bundle);
                }
            }
        });
        setLoadSir(mGridView);
    }

    public boolean isLoad() {
        return isLoad;
    }

    private void initData() {
        isLoad = false;
        showLoading();
        List<VodInfo> infoList = RoomDataManger.getAllVodRecord(sourceUrl);
        if (infoList.size() > 0) {
            showSuccess();
            historyAdapter.setNewData(infoList);
            isLoad = true;
        } else {
            isLoad = false;
            showEmpty();
        }
    }

    public boolean isTop() {
        return isTop;
    }

    public void scrollTop() {
        isTop = true;
        mGridView.scrollToPosition(0);
    }
}