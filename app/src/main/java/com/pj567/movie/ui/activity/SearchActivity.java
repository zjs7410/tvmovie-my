package com.pj567.movie.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.pj567.movie.R;
import com.pj567.movie.api.ApiConfig;
import com.pj567.movie.base.BaseActivity;
import com.pj567.movie.bean.AbsXml;
import com.pj567.movie.bean.Movie;
import com.pj567.movie.bean.SearchRequest;
import com.pj567.movie.event.ServerEvent;
import com.pj567.movie.server.RemoteServer;
import com.pj567.movie.ui.adapter.SearchAdapter;
import com.pj567.movie.util.DefaultConfig;
import com.pj567.movie.util.FastClickCheckUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.tv.QRCodeGen;
import com.tv.leanback.VerticalGridView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class SearchActivity extends BaseActivity {
    private LinearLayout llLayout;
    private VerticalGridView mGridView;
    private TextView tvName;
    private EditText etSearch;
    private TextView tvSearch;
    private TextView tvClear;
    private TextView tvAddress;
    private ImageView ivQRCode;
    private SearchAdapter searchAdapter;
    private int sourceIndex = 0;
    private int sourceTotal = 0;

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_search;
    }

    @Override
    protected void init() {
        initView();
        initViewModel();
        initData();
    }

    private void initView() {
        EventBus.getDefault().register(this);
        llLayout = findViewById(R.id.llLayout);
        etSearch = findViewById(R.id.etSearch);
        tvSearch = findViewById(R.id.tvSearch);
        tvClear = findViewById(R.id.tvClear);
        tvAddress = findViewById(R.id.tvAddress);
        ivQRCode = findViewById(R.id.ivQRCode);
        mGridView = findViewById(R.id.mGridView);
        tvName = findViewById(R.id.tvName);
        mGridView.setHasFixedSize(true);
        mGridView.setNumColumns(1);
        searchAdapter = new SearchAdapter();
        mGridView.setAdapter(searchAdapter);
        searchAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FastClickCheckUtil.check(view);
                Movie.Video video = searchAdapter.getData().get(position);
                if (video != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", video.id);
                    bundle.putString("sourceUrl", video.api);
                    jumpActivity(DetailActivity.class, bundle);
                }
            }
        });
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                String wd = etSearch.getText().toString().trim();
                if (!TextUtils.isEmpty(wd)) {
                    search(wd);
                } else {
                    Toast.makeText(mContext, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                etSearch.setText("");
            }
        });
        setLoadSir(llLayout);
    }

    private void initViewModel() {
    }

    private void initData() {
        refreshQRCode();
        sourceTotal = ApiConfig.get().getSearchRequestList().size();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("title")) {
            String title = intent.getStringExtra("title");
            showLoading();
            search(title);
        }
    }

    private void refreshQRCode() {
        String address = RemoteServer.getServerAddress(mContext);
        tvAddress.setText(String.format("远程搜索使用手机/电脑扫描下面二维码或者直接浏览器访问地址\n%s", address));
        ivQRCode.setImageBitmap(QRCodeGen.generateBitmap(address, 300, 300));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void server(ServerEvent event) {
        if (event.type == ServerEvent.SERVER_SEARCH) {
            String title = (String) event.obj;
            showLoading();
            search(title);
        }
    }

    private void search(String title) {
        tvName.setText(title);
        sourceIndex = 0;
        cancel();
        showLoading();
        mGridView.setVisibility(View.INVISIBLE);
        searchAdapter.setNewData(new ArrayList<>());
        for (SearchRequest request : ApiConfig.get().getSearchRequestList()) {
            searchResult(request.api, title, request.name);
        }
    }

    private void searchResult(String api, String wd, String sourceName) {
        OkGo.<String>get(api)
                .params("wd", wd)
                .tag("search")
                .execute(new AbsCallback<String>() {
                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        if (response.body() != null) {
                            return response.body().string();
                        } else {
                            throw new IllegalStateException("网络请求错误");
                        }
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        String xml = response.body();
                        xml(xml, api, sourceName);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        searchData(null);
                    }
                });
    }

    private void xml(String xml, String api, String sourceName) {
        try {
            XStream xstream = new XStream(new DomDriver());//创建Xstram对象
            xstream.autodetectAnnotations(true);
            xstream.processAnnotations(AbsXml.class);
            xstream.ignoreUnknownElements();
            if (xml.contains("<year></year>")) {
                xml = xml.replace("<year></year>", "<year>0</year>");
            }
            if (xml.contains("<state></state>")) {
                xml = xml.replace("<state></state>", "<state>0</state>");
            }
            AbsXml data = (AbsXml) xstream.fromXML(xml);
            data.api = api;
            if (data.movie != null && data.movie.videoList != null) {
                for (Movie.Video video : data.movie.videoList) {
                    if (video.urlBean != null && video.urlBean.infoList != null) {
                        for (Movie.Video.UrlBean.UrlInfo urlInfo : video.urlBean.infoList) {
                            String[] str = null;
                            if (urlInfo.urls.contains("#")) {
                                str = urlInfo.urls.split("#");
                            } else {
                                str = new String[]{urlInfo.urls};
                            }
                            List<Movie.Video.UrlBean.UrlInfo.InfoBean> infoBeanList = new ArrayList<>();
                            for (String s : str) {
                                if (s.contains("$")) {
                                    infoBeanList.add(new Movie.Video.UrlBean.UrlInfo.InfoBean(s.substring(0, s.indexOf("$")), s.substring(s.indexOf("$") + 1)));
                                }
                            }
                            urlInfo.beanList = infoBeanList;
                        }
                    }
                    video.api = api;
                    video.sourceName = sourceName;
                }
            }
            searchData(data);
        } catch (Exception e) {
            searchData(null);
        }
    }

    private void searchData(AbsXml absXml) {
        if (absXml != null && absXml.movie != null && absXml.movie.videoList != null && absXml.movie.videoList.size() > 0) {
            List<Movie.Video> data = new ArrayList<>();
            for (Movie.Video video : absXml.movie.videoList) {
                if (!DefaultConfig.isContains(video.type)) {
                    data.add(video);
                }
            }
            if (searchAdapter.getData().size() > 0) {
                searchAdapter.addData(data);
            } else {
                showSuccess();
                mGridView.setVisibility(View.VISIBLE);
                searchAdapter.setNewData(data);
            }
        }
        if (++sourceIndex == sourceTotal) {
            if (searchAdapter.getData().size() <= 0) {
                showEmpty();
            }
            cancel();
        }
    }

    private void cancel() {
        OkGo.getInstance().cancelTag("search");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancel();
        EventBus.getDefault().unregister(this);
    }
}