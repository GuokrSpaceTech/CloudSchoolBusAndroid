package com.guokrspace.cloudschoolbus.teacher.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kai on 9/17/15.
 */
public class Parent implements Serializable{
    String parentid;
    String nickname;
    String relationship;
    String pictureid;
    String mobile;
    String avatar;
    List<String> studentids;

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getPictureid() {
        return pictureid;
    }

    public void setPictureid(String pictureid) {
        this.pictureid = pictureid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<String> getStudentids() {
        return studentids;
    }

    public void setStudentids(List<String> studentids) {
        this.studentids = studentids;
    }
}
