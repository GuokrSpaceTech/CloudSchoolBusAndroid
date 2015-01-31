package com.Manga.Activity.ClassUpdate.Model;

/**
 * Created by YangQingSu on 2014/11/13.
 */
public class LikeDto {
    private String actionid;
    private String isstudent;
    private String adduserid;
    private String nickname;
    private String addtime;
    private boolean candelete;
    private String avatar;

    public String getActionid() {
        return actionid;
    }

    public void setActionid(String actionid) {
        this.actionid = actionid;
    }

    public String getIsstudent() {
        return isstudent;
    }

    public void setIsstudent(String isstudent) {
        this.isstudent = isstudent;
    }

    public String getAdduserid() {
        return adduserid;
    }

    public void setAdduserid(String adduserid) {
        this.adduserid = adduserid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public boolean isCandelete() {
        return candelete;
    }

    public void setCandelete(boolean candelete) {
        this.candelete = candelete;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
