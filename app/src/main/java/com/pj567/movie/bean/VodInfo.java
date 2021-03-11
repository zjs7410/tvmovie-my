package com.pj567.movie.bean;

import com.pj567.movie.util.DefaultConfig;
import com.pj567.movie.util.L;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pj567
 * @date :2020/12/22
 * @description:
 */
public class VodInfo implements Serializable {
    public String last;//时间
    //内容id
    public int id;
    //父级id
    public int tid;
    //影片名称 <![CDATA[老爸当家]]>
    public String name;
    //类型名称
    public String type;
    //视频分类zuidam3u8,zuidall
    public String dt;
    //图片
    public String pic;
    //语言
    public String lang;
    //地区
    public String area;
    //年份
    public int year;
    public String state;
    //描述集数或者影片信息<![CDATA[共40集]]>
    public String note;
    //演员<![CDATA[张国立,蒋欣,高鑫,曹艳艳,王维维,韩丹彤,孟秀,王新]]>
    public String actor;
    //导演<![CDATA[陈国星]]>
    public String director;
    public List<VodSeries> seriesList;
    public String des;// <![CDATA[权来]
    public boolean isX5;
    public int playIndex = 0;

    public void setVideo(Movie.Video video) {
        last = video.last;
        id = video.id;
        tid = video.tid;
        name = video.name;
        type = video.type;
        dt = video.dt;
        pic = video.pic;
        lang = video.lang;
        area = video.area;
        year = video.year;
        state = video.state;
        note = video.note;
        actor = video.actor;
        director = video.director;
        des = video.des;
        if (video.urlBean != null && video.urlBean.infoList != null && video.urlBean.infoList.size() > 0) {
            seriesList = new ArrayList<>();
            Movie.Video.UrlBean.UrlInfo mUrlInfo = null;
            for (Movie.Video.UrlBean.UrlInfo urlInfo : video.urlBean.infoList) {
                if (urlInfo.beanList != null && urlInfo.beanList.size() > 0) {
                    Movie.Video.UrlBean.UrlInfo.InfoBean infoBean = urlInfo.beanList.get(0);
                    String name = DefaultConfig.getFileName(infoBean.url);
                    if (name.contains(".") && !name.endsWith("html")) {
                        this.isX5 = false;
                        mUrlInfo = urlInfo;
                        break;
                    }
                }
            }
            if (mUrlInfo == null) {
                this.isX5 = true;
                mUrlInfo = video.urlBean.infoList.get(0);
            }
            if (mUrlInfo.beanList != null && mUrlInfo.beanList.size() > 0) {
                for (Movie.Video.UrlBean.UrlInfo.InfoBean infoBean : mUrlInfo.beanList) {
                    seriesList.add(new VodSeries(infoBean.name, infoBean.url));
                }
            }
        }
    }

    public static class VodSeries implements Serializable {

        public String name;
        public String url;
        public boolean selected;

        public VodSeries() {
        }

        public VodSeries(String name, String url) {
            this.name = name;
            this.url = url;
        }
    }
}