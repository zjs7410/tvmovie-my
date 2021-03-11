package com.pj567.movie.bean;

import java.io.Serializable;

/**
 * @author pj567
 * @date :2021/3/8
 * @description:
 */
public class PraseBean implements Serializable {

    /**
     * id : 4
     * praseName : 备用3
     * praseUrl : https://www.administratorw.com/video.php?url=
     */

    private int id;
    private String praseName;
    private String praseUrl;
    public boolean selected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPraseName() {
        return praseName;
    }

    public void setPraseName(String praseName) {
        this.praseName = praseName;
    }

    public String getPraseUrl() {
        return praseUrl;
    }

    public void setPraseUrl(String praseUrl) {
        this.praseUrl = praseUrl;
    }
}