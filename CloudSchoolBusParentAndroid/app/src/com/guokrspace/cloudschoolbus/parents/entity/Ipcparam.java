package com.guokrspace.cloudschoolbus.parents.entity;

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

    /**
     * Created by wangjianfeng on 14-12-28.
     */
    public static class Dvr implements Serializable{
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
}
