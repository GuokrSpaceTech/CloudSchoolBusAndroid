package com.guokrspace.cloudschoolbus.parents.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangjianfeng on 14-12-28.
 */
public class Ipcparam implements Serializable{
    private String serverip;
    private String port;
    private List<Channel> channels;

    public String getServerip() {
        return serverip;
    }

    public void setServerip(String serverip) {
        this.serverip = serverip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    /**
     * Created by wangjianfeng on 14-12-28.
     */
    public static class Channel implements Serializable{
        private String channelid;
        private String channeldesc;
        private String device;

        public String getChannelid() {
            return channelid;
        }

        public void setChannelid(String channelid) {
            this.channelid = channelid;
        }

        public String getChanneldesc() {
            return channeldesc;
        }

        public void setChanneldesc(String channeldesc) {
            this.channeldesc = channeldesc;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }
    }
}
