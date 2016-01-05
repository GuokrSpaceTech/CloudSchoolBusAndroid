package com.guokrspace.cloudschoolbus.parents.event;

/**
 * Created by wangjianfeng on 16/1/5.
 */
public class BaseinfoErrorEvent {
    private int errCode;
    private String errMsg;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
