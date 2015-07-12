package com.guokrspace.cloudschoolbus.parents.module.classes.Streaming.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangjianfeng on 14-12-28.
 */
public class Ipcparam implements Serializable{
    private String ddns;
    private String port;
    private List<Dvr> dvr;

    public String getDdns() {
        return ddns;
    }

    public void setDdns(String ddns) {
        this.ddns = ddns;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public List<Dvr> getDvr() {
        return dvr;
    }

    public void setDvr(List<Dvr> dvr) {
        this.dvr = dvr;
    }
}
