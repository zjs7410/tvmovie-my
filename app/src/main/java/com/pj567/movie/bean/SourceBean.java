package com.pj567.movie.bean;

/**
 * @author pj567
 * @date :2020/12/18
 * @description:
 */
public class SourceBean {

    /**
     * name : 最大资源网
     * api : http://www.zdziyuan.com/inc/api.php
     * download : http://www.zdziyuan.com/inc/apidown.php
     */
    private int id;
    private String name;
    private String api;
    private String download;
    public boolean selected = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }
}