package com.guokrspace.cloudschoolbus.parents.event;

/**
 * Created by wangjianfeng on 15/7/30.
 */
public class NetworkStatusEvent {
    private boolean isWifiConnected;
    private boolean isMobileNetworkConnected;
    private boolean isNetworkConnected;

    public NetworkStatusEvent(boolean isWifiConnected, boolean isMobileNetworkConnected, boolean isNetworkConnected) {
        this.isWifiConnected = isWifiConnected;
        this.isMobileNetworkConnected = isMobileNetworkConnected;
        this.isNetworkConnected = isNetworkConnected;
    }

    public boolean isWifiConnected() {
        return isWifiConnected;
    }

    public void setIsWifiConnected(boolean isWifiConnected) {
        this.isWifiConnected = isWifiConnected;
    }

    public boolean isMobileNetworkConnected() {
        return isMobileNetworkConnected;
    }

    public void setIsMobileNetworkConnected(boolean isMobileNetworkConnected) {
        this.isMobileNetworkConnected = isMobileNetworkConnected;
    }

    public boolean isNetworkConnected() {
        return isNetworkConnected;
    }

    public void setIsNetworkConnected(boolean isNetworkConnected) {
        this.isNetworkConnected = isNetworkConnected;
    }
}
