package com.pj567.movie.ui.activity;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.hawk.Hawk;
import com.pj567.movie.R;
import com.pj567.movie.base.BaseActivity;
import com.pj567.movie.base.BaseLazyFragment;
import com.pj567.movie.bean.AbsSortXml;
import com.pj567.movie.bean.MovieSort;
import com.pj567.movie.event.ServerEvent;
import com.pj567.movie.event.TopStateEvent;
import com.pj567.movie.server.ControlManager;
import com.pj567.movie.ui.adapter.HomePageAdapter;
import com.pj567.movie.ui.adapter.SortAdapter;
import com.pj567.movie.ui.dialog.RemoteDialog;
import com.pj567.movie.ui.dialog.UpdateHintDialog;
import com.pj567.movie.ui.fragment.GridFragment;
import com.pj567.movie.ui.fragment.UserFragment;
import com.pj567.movie.util.AppManager;
import com.pj567.movie.util.DefaultConfig;
import com.pj567.movie.util.HawkConfig;
import com.pj567.movie.util.L;
import com.pj567.movie.viewmodel.SourceViewModel;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class HomeActivity extends BaseActivity {
    private LinearLayout topLayout;
    private LinearLayout contentLayout;
    private TextView tvName;
    private TextView tvDate;
    private HorizontalGridView mGridView;
    private NoScrollViewPager mViewPager;
    private SourceViewModel sourceViewModel;
    private SortAdapter sortAdapter;
    private HomePageAdapter pageAdapter;
    private List<BaseLazyFragment> fragments = new ArrayList<>();
    private boolean isDownOrUp = false;
    private boolean sortChange = false;
    private int defaultSelected = 0;
    private int sortFocused = 0;
    private Handler mHandler = new Handler();
    private View focusView = null;
    private RemoteDialog remoteDialog;
    private long mExitTime = 0;
    private Runnable mRunnable = new Runnable() {
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void run() {
            Date date = new Date();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            tvDate.setText(timeFormat.format(date));
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_home;
    }

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        initView();
        initViewModel();
        initData();
    }

    private void initView() {
        tvName = findViewById(R.id.tvName);
        topLayout = findViewById(R.id.topLayout);
        contentLayout = findViewById(R.id.contentLayout);
        tvDate = findViewById(R.id.tvDate);
        mGridView = findViewById(R.id.mGridView);
        mViewPager = findViewById(R.id.mViewPager);
        sortAdapter = new SortAdapter();
        mGridView.setAdapter(sortAdapter);
        sortAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
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
                                    BaseLazyFragment fragment = fragments.get(position);
                                    if (fragment instanceof UserFragment) {
                                        isDownOrUp = true;
                                    } else if (fragment instanceof GridFragment) {
                                        if (((GridFragment) fragment).isLoad()) {
                                            isDownOrUp = true;
                                            changeTop(true);
                                        } else {
                                            return true;
                                        }
                                    }
                                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                                    isDownOrUp = true;
                                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && position == 0 || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && position == sortAdapter.getData().size() - 1) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                }
            }
        });
        setLoadSir(contentLayout);
    }

    private View.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    if (focusView != null && (Integer) focusView.getTag() == defaultSelected) {
                        focusView.requestFocus();
                        return true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && v.getId() == R.id.tvLive) {
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && v.getId() == R.id.tvReward) {
                    return true;
                }
            }
            return false;
        }
    };

    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.sortResult.observe(this, new Observer<AbsSortXml>() {
            @Override
            public void onChanged(AbsSortXml absXml) {
                showSuccess();
                if (absXml != null && absXml.movieSort != null && absXml.movieSort.sortList != null) {
                    sortAdapter.setNewData(DefaultConfig.adjustSort(absXml.movieSort.sortList));
                    setSortDefaultPress(mGridView);
                    initViewPager();
                } else {
                    sortAdapter.setNewData(DefaultConfig.adjustSort(new ArrayList<>()));
                    tvName.setFocusable(false);
                    tvName.setFocusableInTouchMode(false);
                }
            }
        });
    }

    private void initData() {
        ControlManager.get().startServer();
        showLoading();
        sourceViewModel.getSort();
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            L.e("有");
        } else {
            L.e("无");
        }
        if (DefaultConfig.getAppVersionCode(mContext) > Hawk.get(HawkConfig.SHOW_UPDATE_HINT, 118)) {
            new UpdateHintDialog().build(mContext).show();
            Hawk.put(HawkConfig.SHOW_UPDATE_HINT, DefaultConfig.getAppVersionCode(mContext));
        }
    }

    private void initViewPager() {
        if (sortAdapter.getData().size() > 0) {
            for (MovieSort.SortData data : sortAdapter.getData()) {
                if (data.id == 0) {
                    fragments.add(UserFragment.newInstance());
                } else {
                    fragments.add(GridFragment.newInstance(data.id));
                }
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

    @Override
    public void onBackPressed() {
        if (fragments != null && fragments.size() > defaultSelected) {
            BaseLazyFragment fragment = fragments.get(defaultSelected);
            if (fragment instanceof GridFragment) {
                if (!((GridFragment) fragment).isTop()) {
                    ((GridFragment) fragment).scrollTop();
                    if (focusView != null && (Integer) focusView.getTag() == defaultSelected) {
                        focusView.requestFocus();
                    }
                    changeTop(false);
                } else {
                    exit();
                }
            }else {
                exit();
            }
        } else {
            exit();
        }
    }

    private void exit() {
        if (System.currentTimeMillis() - mExitTime < 2000) {
            super.onBackPressed();
        } else {
            mExitTime = System.currentTimeMillis();
            Toast.makeText(mContext, "再按一次返回键退出应用", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(mRunnable);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeTop(TopStateEvent event) {
        if (event.type == TopStateEvent.TYPE_TOP) {
            if (focusView != null && (Integer) focusView.getTag() == defaultSelected) {
                focusView.requestFocus();
            }
            changeTop(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void server(ServerEvent event) {
//        if (event.type == ServerEvent.SERVER_SUCCESS) {
//            remoteDialog = new RemoteDialog().build(mContext);
//            remoteDialog.show();
//        } else if (event.type == ServerEvent.SERVER_CONNECTION) {
//            if (remoteDialog != null && remoteDialog.isShowing()) {
//                remoteDialog.dismiss();
//            }
//        }
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
                                    tvName.setFocusable(false);
                                    tvName.setFocusableInTouchMode(false);
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

    private void changeTop(boolean hide) {
        ViewObj viewObj = new ViewObj(mGridView, (ViewGroup.MarginLayoutParams) mGridView.getLayoutParams());
        AnimatorSet animatorSet = new AnimatorSet();
        if (hide) {
            ObjectAnimator scrollAnimator = ObjectAnimator.ofObject(viewObj, "marginTop", new IntEvaluator(), AutoSizeUtils.pt2px(mContext, 90), AutoSizeUtils.pt2px(mContext, 20));
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(topLayout, "alpha", 1.0f, 0.0f);
            animatorSet.playTogether(scrollAnimator, alphaAnimator);
            animatorSet.setDuration(300);
            animatorSet.start();
        } else {
            viewObj.setMarginTop(AutoSizeUtils.pt2px(mContext, 90));
            topLayout.setAlpha(1f);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        AppManager.getInstance().appExit(0);
        ControlManager.get().stopServer();
    }

}