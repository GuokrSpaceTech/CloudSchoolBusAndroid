package com.guokrspace.cloudschoolbus.teacher.event;

/**
 * Created by wangjianfeng on 15/7/30.
 */
public class SidExpireEvent {
    private String old_sid;

    public SidExpireEvent(String sid) {
        old_sid = sid;
    }

    public String getOld_sid() {
        return old_sid;
    }

    public void setOld_sid(String old_sid) {
        this.old_sid = old_sid;
    }
}
