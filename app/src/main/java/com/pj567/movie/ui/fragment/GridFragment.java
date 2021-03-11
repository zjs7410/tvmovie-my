package com.pj567.movie.ui.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pj567.movie.api.ApiConfig;
import com.tv.leanback.GridLayoutManager;
import com.tv.leanback.OnChildViewHolderSelectedListener;
import com.tv.leanback.OnItemListener;
import com.tv.leanback.VerticalGridView;
import com.tv.widget.LoadMoreView;
import com.pj567.movie.R;
import com.pj567.movie.base.BaseLazyFragment;
import com.pj567.movie.bean.AbsXml;
import com.pj567.movie.bean.Movie;
import com.pj567.movie.event.TopStateEvent;
import com.pj567.movie.ui.activity.DetailActivity;
import com.pj567.movie.ui.adapter.GridAdapter;
import com.pj567.movie.util.FastClickCheckUtil;
import com.pj567.movie.viewmodel.SourceViewModel;

import org.greenrobot.eventbus.EventBus;

/**
 * @author pj567
 * @date :2020/12/21
 * @description:
 */
public class GridFragment extends BaseLazyFragment {
    private int sortId = 0;
    private VerticalGridView mGridView;
    private SourceViewModel sourceViewModel;
    private GridAdapter gridAdapter;
    private int page = 1;
    private int maxPage = 1;
    private boolean isLoad = false;
    private boolean isTop = true;

    public static GridFragment newInstance(int id) {
        return new GridFragment().setArguments(id);
    }

    public GridFragment setArguments(int id) {
        sortId = id;
        return this;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_grid;
    }

    @Override
    protected void init() {
        initView();
        initViewModel();
        initData();
    }

    private void initView() {
        mGridView = findViewById(R.id.mGridView);
        mGridView.setHasFixedSize(true);
        gridAdapter = new GridAdapter();
        mGridView.setAdapter(gridAdapter);
        ((GridLayoutManager) mGridView.getLayoutManager()).setFocusOutAllowed(true, true);
        mGridView.setNumColumns(5);
        gridAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                gridAdapter.setEnableLoadMore(true);
                sourceViewModel.getList(sortId, page);
            }
        }, mGridView);
        gridAdapter.setLoadMoreView(new LoadMoreView());
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
                                    EventBus.getDefault().post(new TopStateEvent(TopStateEvent.TYPE_TOP));
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                }
            }
        });
        gridAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                Movie.Video video = gridAdapter.getData().get(position);
                if (video != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", video.id);
                    bundle.putString("sourceUrl", ApiConfig.get().getBaseUrl());
                    jumpActivity(DetailActivity.class, bundle);
                }
            }
        });
        setLoadSir(mGridView);
    }

    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.listResult.observe(this, new Observer<AbsXml>() {
            @Override
            public void onChanged(AbsXml absXml) {
                if (absXml != null && absXml.movie != null && absXml.movie.videoList != null && absXml.movie.videoList.size() > 0) {
                    if (page == 1) {
                        showSuccess();
                        isLoad = true;
                        gridAdapter.setNewData(absXml.movie.videoList);
                    } else {
                        gridAdapter.addData(absXml.movie.videoList);
                    }
                    page++;
                    maxPage = absXml.movie.pagecount;
                } else {
                    if (page == 1) {
                        showEmpty();
                    }
                }
                if (page > maxPage) {
                    gridAdapter.loadMoreEnd();
                } else {
                    gridAdapter.loadMoreComplete();
                }
            }
        });
    }

    public boolean isLoad() {
        return isLoad;
    }

    private void initData() {
        showLoading();
        isLoad = false;
        sourceViewModel.getList(sortId, page);
    }

    public boolean isTop() {
        return isTop;
    }

    public void scrollTop() {
        isTop = true;
        mGridView.scrollToPosition(0);
    }
}