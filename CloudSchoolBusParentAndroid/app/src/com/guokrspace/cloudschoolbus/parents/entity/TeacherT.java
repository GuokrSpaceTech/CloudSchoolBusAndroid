package com.guokrspace.cloudschoolbus.parents.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by kai on 9/17/15.
 */
public class TeacherT implements Serializable{
    String teacherid;
    String dutyid;
    String realname;
    String nickname;
    String sex;
    String mobile;
    String pictureid;
    String schoolid;
    String avatar;
    List<TeacherClassInfo> classes;
    String duty;

    public String getTeacherid() {
        return teacherid;
    }

    public void setTeacherid(String teacherid) {
        this.teacherid = teacherid;
    }

    public String getDutyid() {
        return dutyid;
    }

    public void setDutyid(String dutyid) {
        this.dutyid = dutyid;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPictureid() {
        return pictureid;
    }

    public void setPictureid(String pictureid) {
        this.pictureid = pictureid;
    }

    public String getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<TeacherClassInfo> getClasses() {
        return classes;
    }

    public void setClasses(List<TeacherClassInfo> classes) {
        this.classes = classes;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }
}

