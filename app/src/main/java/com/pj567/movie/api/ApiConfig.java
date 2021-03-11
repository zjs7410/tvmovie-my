package com.pj567.movie.api;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.pj567.movie.bean.LiveChannel;
import com.pj567.movie.bean.PraseBean;
import com.pj567.movie.bean.SearchRequest;
import com.pj567.movie.bean.SourceBean;
import com.pj567.movie.util.HawkConfig;
import com.pj567.movie.util.L;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pj567
 * @date :2020/12/18
 * @description:
 */
public class ApiConfig {
    public static String PinYinUrl = "https://www.yuming8.com/pinyin/jieshi/";
    private static ApiConfig instance;
    private List<SourceBean> sourceBeanList;
    private List<SearchRequest> searchRequestList;
    private SourceBean mSourceBean;
    //直播源2
    private List<LiveChannel> channelList;
    //直播源1
    private List<LiveChannel> channelIpList;
    private List<PraseBean> praseBeanList;

    private ApiConfig() {
        sourceBeanList = new ArrayList<>();
        searchRequestList = new ArrayList<>();
        channelList = new ArrayList<>();
        channelIpList = new ArrayList<>();
        praseBeanList = new ArrayList<>();
    }

    public static ApiConfig get() {
        if (instance == null) {
            synchronized (ApiConfig.class) {
                if (instance == null) {
                    instance = new ApiConfig();
                }
            }
        }
        return instance;
    }

    public void loadSource(Context context) {
        String json = getAssetText(context, "resources.json");
        sourceBeanList = new Gson().fromJson(json, new TypeToken<List<SourceBean>>() {
        }.getType());
        if (sourceBeanList != null && sourceBeanList.size() > 0) {
            int id = Hawk.get(HawkConfig.DEFAULT_SOURCE_ID, 0);
            L.e("id = " + id);
            for (SourceBean sourceBean : sourceBeanList) {
                if (sourceBean.getId() == id) {
                    setSourceBean(sourceBean);
                    break;
                }
            }
            if (mSourceBean == null) {
                mSourceBean = sourceBeanList.get(0);
                Hawk.put(HawkConfig.DEFAULT_SOURCE_ID, mSourceBean.getId());
            }
            for (int i = 0; i < sourceBeanList.size(); i++) {
                SourceBean sourceBean = sourceBeanList.get(i);
                if (sourceBean.getName().equals("123资源") || sourceBean.getName().equals("速播资源")) {
                    continue;
                }
                searchRequestList.add(new SearchRequest(i, sourceBean.getApi(), sourceBean.getName()));
            }
        }
        loadLiveSource(context);
        loadPraseSource(context);
    }

    private void loadLiveSource(Context context) {
        String json = getAssetText(context, "liveChannel.json");
        channelList = new Gson().fromJson(json, new TypeToken<List<LiveChannel>>() {
        }.getType());
        json = getAssetText(context, "liveChannelIp.json");
        channelIpList = new Gson().fromJson(json, new TypeToken<List<LiveChannel>>() {
        }.getType());
    }

    private void loadPraseSource(Context context) {
        String json = getAssetText(context, "praseUrl.json");
        praseBeanList = new Gson().fromJson(json, new TypeToken<List<PraseBean>>() {
        }.getType());
        int id = Hawk.get(HawkConfig.DEFAULT_PRASE_ID, 0);
        if (id != 0) {
            boolean selected = false;
            for (PraseBean praseBean : praseBeanList) {
                if (praseBean.getId() == id) {
                    praseBean.selected = true;
                    selected = true;
                    break;
                }
            }
            if (!selected) {
                PraseBean praseBean = praseBeanList.get(0);
                praseBean.selected = true;
                Hawk.put(HawkConfig.DEFAULT_PRASE_ID, praseBean.getId());
            }
        } else {
            PraseBean praseBean = praseBeanList.get(0);
            praseBean.selected = true;
            Hawk.put(HawkConfig.DEFAULT_PRASE_ID, praseBean.getId());
        }
    }

    private String getAssetText(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assets = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assets.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setSourceBean(SourceBean sourceBean) {
        this.mSourceBean = sourceBean;
        Hawk.put(HawkConfig.DEFAULT_SOURCE_ID, mSourceBean.getId());
    }

    public List<SourceBean> getSourceBeanList() {
        return sourceBeanList;
    }

    public List<SearchRequest> getSearchRequestList() {
        return searchRequestList;
    }

    public List<PraseBean> getPraseBeanList() {
        return praseBeanList;
    }

    public SourceBean getDefaultSourceBean() {
        return mSourceBean;
    }

    public List<LiveChannel> getChannelList() {
        int type = Hawk.get(HawkConfig.LIVE_SOURCE, 0);
        return type == 0 ? channelIpList : channelList;
    }

    public String getBaseUrl() {
        return mSourceBean.getApi();
    }
}