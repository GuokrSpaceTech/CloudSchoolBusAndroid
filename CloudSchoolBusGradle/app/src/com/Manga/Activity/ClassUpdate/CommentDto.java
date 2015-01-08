package com.Manga.Activity.ClassUpdate;

/**
 * Created by YangQingSu on 2014/11/13.
 */
public class CommentDto {
    private String commentid;
    private String content;
    private String isstudent;
    private String adduserid;
    private String nickname;
    private String addtime;
    private String replynickname;
    private boolean candelete;
    private String avatar;

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getReplynickname() {
        return replynickname;
    }

    public void setReplynickname(String replynickname) {
        this.replynickname = replynickname;
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
