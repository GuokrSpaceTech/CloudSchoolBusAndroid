package com.Manga.Activity.myChildren.Streaming.entity;

import java.io.Serializable;

/**
 * Created by wangjianfeng on 14-12-28.
 */
public class Dvr implements Serializable{
    private String dvrid;
    private String channelid;
    private String dvr_name;
    private String channeldesc;

    public String getDvrid() {
        return dvrid;
    }

    public void setDvrid(String dvrid) {
        this.dvrid = dvrid;
    }

    public String getChannelid() {
        return channelid;
    }

    public void setChannelid(String channelid) {
        this.channelid = channelid;
    }

    public String getDvr_name() {
        return dvr_name;
    }

    public void setDvr_name(String dvr_name) {
        this.dvr_name = dvr_name;
    }

    public String getChanneldesc() {
        return channeldesc;
    }

    public void setChanneldesc(String channeldesc) {
        this.channeldesc = channeldesc;
    }
}
