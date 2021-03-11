package com.pj567.movie.ui.activity;

import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pj567.movie.R;
import com.pj567.movie.api.ApiConfig;
import com.pj567.movie.base.BaseActivity;
import com.pj567.movie.base.BaseLazyFragment;
import com.pj567.movie.bean.SourceBean;
import com.pj567.movie.event.HistoryStateEvent;
import com.pj567.movie.ui.adapter.HistorySourceAdapter;
import com.pj567.movie.ui.adapter.HomePageAdapter;
import com.pj567.movie.ui.fragment.HistoryFragment;
import com.pj567.movie.util.L;
import com.tv.leanback.HorizontalGridView;
import com.tv.leanback.OnChildViewHolderSelectedListener;
import com.tv.leanback.OnItemListener;
import com.tv.widget.DefaultTransformer;
import com.tv.widget.FixedSpeedScroller;
import com.tv.widget.NoScrollViewPager;
import com.tv.widget.ViewObj;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * @author pj567
 * @date :2021/1/7
 * @description:
 */
public class HistoryActivity extends BaseActivity {
    private TextView tvTitle;
    private HorizontalGridView mGridView;
    private NoScrollViewPager mViewPager;
    private HistorySourceAdapter sourceAdapter;
    private HomePageAdapter pageAdapter;
    private List<BaseLazyFragment> fragments = new ArrayList<>();
    private boolean isDownOrUp = false;
    private boolean sortChange = false;
    private int defaultSelected = 0;
    private int sortFocused = 0;
    private Handler mHandler = new Handler();
    private View focusView = null;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_history;
    }

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tvTitle);
        mGridView = findViewById(R.id.mGridView);
        mViewPager = findViewById(R.id.mViewPager);
        mGridView.setHasFixedSize(true);
        sourceAdapter = new HistorySourceAdapter();
        mGridView.setAdapter(sourceAdapter);
        sourceAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.tvTitle) {
                    if (view.getParent() != null) {
                        ((ViewGroup) view.getParent()).requestFocus();
                        sortFocused = position;
                        if (sortFocused != defaultSelected) {
                            defaultSelected = sortFocused;
                            mViewPager.setCurrentItem(sortFocused, false);
                        }
                    }
                }
            }
        });
        mGridView.setOnItemListener(new OnItemListener<HorizontalGridView>() {
            @Override
            public void onItemSelected(HorizontalGridView parent, View itemView, int position) {
                if (itemView != null) {
                    sortChange = true;
                    itemView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
                    TextView tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                    tvTitle.setTextColor(0xFFFFFFFF);
                    sortFocused = position;
                }
            }

            @Override
            public void onItemPreSelected(HorizontalGridView parent, View itemView, int position) {
                if (itemView != null) {
                    if (!isDownOrUp) {
                        itemView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new BounceInterpolator()).start();
                        TextView tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                        tvTitle.setTextColor(0xCCFFFFFF);
                    } else {
                        TextView tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                        tvTitle.setTextColor(0xFFFFFFFF);
                    }
                }
            }
        });
        mGridView.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
            @Override
            public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder viewHolder, int position, int subposition) {
                if (viewHolder != null && viewHolder.itemView != null) {
                    focusView = viewHolder.itemView;
                    focusView.setTag(position);
                    viewHolder.itemView.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                isDownOrUp = false;
                                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                                    if (((HistoryFragment) fragments.get(position)).isLoad()) {
                                        isDownOrUp = true;
                                        changeTop(true);
                                    } else {
                                        return true;
                                    }
                                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                                    isDownOrUp = true;
                                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && position == 0 || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && position == sourceAdapter.getData().size() - 1) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                }
            }
        });
    }

    private void initData() {
        EventBus.getDefault().register(this);
        sourceAdapter.setNewData(ApiConfig.get().getSourceBeanList());
        setSortDefaultPress(mGridView);
        initViewPager();
    }

    private void initViewPager() {
        if (sourceAdapter.getData().size() > 0) {
            for (SourceBean data : sourceAdapter.getData()) {
                fragments.add(HistoryFragment.newInstance(data.getApi()));
            }
            pageAdapter = new HomePageAdapter(getSupportFragmentManager(), fragments);
            try {
                Field field = ViewPager.class.getDeclaredField("mScroller");
                field.setAccessible(true);
                FixedSpeedScroller scroller = new FixedSpeedScroller(mContext, new AccelerateInterpolator());
                field.set(mViewPager, scroller);
                scroller.setmDuration(300);
            } catch (Exception e) {
            }
            mViewPager.setPageTransformer(true, new DefaultTransformer());
            mViewPager.setAdapter(pageAdapter);
            mViewPager.setCurrentItem(defaultSelected, false);
        }
    }

    private void setSortDefaultPress(final HorizontalGridView recyclerView) {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (recyclerView.getChildCount() > 0) {
                        for (int i = 0; i < recyclerView.getChildCount(); i++) {
                            if (i == defaultSelected) {
                                View view = recyclerView.getChildAt(i);
                                if (null != view) {
                                    view.requestFocus();
                                    TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                                    tvTitle.setTextColor(0xFFFFFFFF);
                                    view.animate().scaleX(1.1f).scaleY(1.1f).start();
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        });
    }

    private Runnable mDataRunnable = new Runnable() {
        @Override
        public void run() {
            if (sortChange) {
                sortChange = false;
                if (sortFocused != defaultSelected) {
                    defaultSelected = sortFocused;
                    L.e("defaultSelected = " + defaultSelected);
                    mViewPager.setCurrentItem(sortFocused, false);
                }
            }
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            mHandler.removeCallbacks(mDataRunnable);
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            mHandler.postDelayed(mDataRunnable, 200);
        }
        return super.dispatchKeyEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeTop(HistoryStateEvent event) {
        if (event.type == HistoryStateEvent.TYPE_TOP) {
            if (focusView != null && (Integer) focusView.getTag() == defaultSelected) {
                focusView.requestFocus();
            }
            changeTop(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (fragments != null && fragments.size() > defaultSelected) {
            HistoryFragment fragment = (HistoryFragment) fragments.get(defaultSelected);
            if (!fragment.isTop()) {
                fragment.scrollTop();
                if (focusView != null && (Integer) focusView.getTag() == defaultSelected) {
                    focusView.requestFocus();
                }
                changeTop(false);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void changeTop(boolean hide) {
        ViewObj viewObj = new ViewObj(mGridView, (ViewGroup.MarginLayoutParams) mGridView.getLayoutParams());
        AnimatorSet animatorSet = new AnimatorSet();
        if (hide) {
            ObjectAnimator scrollAnimator = ObjectAnimator.ofObject(viewObj, "marginTop", new IntEvaluator(), AutoSizeUtils.pt2px(mContext, 80), AutoSizeUtils.pt2px(mContext, 20));
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(tvTitle, "alpha", 1.0f, 0.0f);
            animatorSet.playTogether(scrollAnimator, alphaAnimator);
            animatorSet.setDuration(300);
            animatorSet.start();
        } else {
            viewObj.setMarginTop(AutoSizeUtils.pt2px(mContext, 80));
            tvTitle.setAlpha(1f);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}